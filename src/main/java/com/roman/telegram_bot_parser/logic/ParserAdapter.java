package com.roman.telegram_bot_parser.logic;

import com.roman.telegram_bot_parser.config.DriverConfigurator;
import com.roman.telegram_bot_parser.config.ParserConfig;
import com.roman.telegram_bot_parser.dao.Ad;
import com.roman.telegram_bot_parser.dao.AdsRepository;
import com.roman.telegram_bot_parser.utils.ParsingUtils;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.openqa.selenium.WebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PreDestroy;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Component
public class ParserAdapter {

    private static final Logger LOGGER = LoggerFactory.getLogger(ParserAdapter.class);

    private final AtomicReference<String> urlForParsing;

    private final ConcurrentLinkedDeque<String> unwantedWords = new ConcurrentLinkedDeque<>();
    private final Pattern filteredByPostTime;

    private final DriverConfigurator driverConfigurator;
    private final AdsRepository adsRepository;

    private final ExecutorService executorParser = Executors.newCachedThreadPool();

    @Autowired
    public ParserAdapter(AdsRepository adsRepository, DriverConfigurator driverConfigurator, ParserConfig parserConfig) {
        this.adsRepository = adsRepository;
        this.driverConfigurator = driverConfigurator;
        this.urlForParsing = new AtomicReference<>(parserConfig.getDefUrlForParse());
        String regExpByPostTime = parserConfig.getAdAgeRegExp();
        this.filteredByPostTime = Pattern.compile(regExpByPostTime);
    }

    public void parseFilterAndSaveAds() {
        WebDriver webDriver = null;
        
        try {
            webDriver = driverConfigurator.getChromeDriver();
            if (webDriver == null) {
                LOGGER.error("Failed to initialize WebDriver");
                return;
            }

            Callable<Document> pageTask = getFirstPage(webDriver);
            Future<Document> futurePage = executorParser.submit(pageTask);
            
            // Добавляем timeout для предотвращения зависания
            Document page = futurePage.get(30, TimeUnit.SECONDS);
            
            if (page == null) {
                LOGGER.error("Failed to load page or page is empty");
                return;
            }

            List<Ad> tempList = ParsingUtils.parseToList(page);
            if (tempList.isEmpty()) {
                LOGGER.warn("No ads found during parsing");
                return;
            }

            List<Ad> afterFiltering = filterByTimeAndDescription(tempList);
            
            if (!afterFiltering.isEmpty()) {
                adsRepository.saveAll(afterFiltering);
                LOGGER.info("Successfully saved {} ads (filtered from {} total)", 
                           afterFiltering.size(), tempList.size());
            } else {
                LOGGER.info("No ads remained after filtering (from {} total)", tempList.size());
            }
            
        } catch (TimeoutException e) {
            LOGGER.error("Page loading timeout exceeded: {}", e.getMessage());
        } catch (InterruptedException e) {
            LOGGER.error("Parsing was interrupted: {}", e.getMessage());
            Thread.currentThread().interrupt(); // Восстанавливаем флаг прерывания
        } catch (ExecutionException e) {
            LOGGER.error("Error during page execution: {}", e.getCause() != null ? e.getCause().getMessage() : e.getMessage());
        } catch (Exception e) {
            LOGGER.error("Unexpected error during parsing and saving ads: {}", e.getMessage(), e);
        } finally {
            // Всегда закрываем WebDriver для освобождения ресурсов
            if (webDriver != null) {
                try {
                    webDriver.quit();
                    LOGGER.debug("WebDriver closed successfully");
                } catch (Exception e) {
                    LOGGER.warn("Error closing WebDriver: {}", e.getMessage());
                }
            }
        }
    }

    /**
     * Фильтруем старые записи по (adExpirationAgeMinutes) и исключаем записи с нежелательными словами по unwantedWords
     */
    public List<Ad> filterByTimeAndDescription(List<Ad> tempList) {
        List<Ad> afterFiltering;
        afterFiltering = tempList.stream()
                .filter(it -> {
                    Matcher matcher = filteredByPostTime.matcher(it.getDate());
                    return matcher.find();
                })
                .filter(this::notIncludedUnwantedWords)
                .collect(Collectors.toList());
        return afterFiltering;
    }

    public Callable<Document> getFirstPage(WebDriver webDriver) {
        webDriver.get(urlForParsing.get());
        return () -> Jsoup.parse(webDriver.getPageSource());
    }

    public void setUrlForParsing(String urlForParsing) {
        this.urlForParsing.set(urlForParsing);
        LOGGER.info("URL was changed for {}", urlForParsing);
    }

    /**
     * Добавить список запрещенных слов
     */
    public void addWordsToExcluded(List<String> words) {
        unwantedWords.addAll(words);
        LOGGER.info("excludedWords was changed");
    }

    /**
     * Сбросить список запрещенных слов
     */
    public void resetWordsToExcluded() {
        unwantedWords.clear();
        LOGGER.info("unwantedWords was cleared");
    }

    /**
     * Истина если в строке нет ни одного слова из списка.
     */
    private boolean notIncludedUnwantedWords(Ad ad) {
        if (unwantedWords.isEmpty()) {
            return true;
        }
        return unwantedWords.stream().noneMatch(it -> isContains(ad, it));
    }

    private static boolean isContains(Ad ad, String it) {
        if(it.isEmpty()) {
            return false;
        }
        return StringUtils.containsIgnoreCase(ad.getDescription(), it);
    }


    public String reportConfig() {
        return  "Список нежелательных слов=" + Arrays.toString(unwantedWords.toArray())
                + System.lineSeparator() +
                "URL для поиска=" + urlForParsing.get();
    }
    
    /**
     * Корректно закрываем ExecutorService при завершении работы приложения
     */
    @PreDestroy
    public void cleanup() {
        LOGGER.info("Shutting down ParserAdapter executor service...");
        executorParser.shutdown();
        try {
            if (!executorParser.awaitTermination(10, TimeUnit.SECONDS)) {
                LOGGER.warn("Executor did not terminate gracefully, forcing shutdown");
                executorParser.shutdownNow();
                if (!executorParser.awaitTermination(5, TimeUnit.SECONDS)) {
                    LOGGER.error("Executor did not terminate after forced shutdown");
                }
            }
        } catch (InterruptedException e) {
            LOGGER.error("Interrupted while shutting down executor", e);
            executorParser.shutdownNow();
            Thread.currentThread().interrupt();
        }
        LOGGER.info("ParserAdapter cleanup completed");
    }

}
