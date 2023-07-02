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

import java.util.List;
import java.util.concurrent.ConcurrentLinkedDeque;
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
        List<Ad> afterFiltering = null;
        try {
            webDriver = driverConfigurator.getChromeDriver();

            Document page = getFirstPage(webDriver);
            //todo убрать сон
            Thread.sleep(1000);
            List<Ad> tempList = ParsingUtils.parseToList(page);

            afterFiltering = filterByTimeAndDescription(tempList);

            adsRepository.saveAll(afterFiltering);
            LOGGER.info("total saved size: {}", afterFiltering.size());
        } catch (Exception e) {
            LOGGER.error("parseAndSaveAds().exception {}", e, e);
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

    public Document getFirstPage(WebDriver webDriver) {
        webDriver.get(urlForParsing.get());
        return Jsoup.parse(webDriver.getPageSource());
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
}
