package com.roman.telegram_bot_parser.logic;

import com.roman.telegram_bot_parser.config.DriverConfigurator;
import com.roman.telegram_bot_parser.config.ParserConfig;
import com.roman.telegram_bot_parser.dao.Ads;
import com.roman.telegram_bot_parser.dao.AdsRepository;
import com.roman.telegram_bot_parser.utils.ParsingUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.openqa.selenium.WebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Component
public class ParserAdapter {

    private static final Logger LOGGER = LoggerFactory.getLogger(ParserAdapter.class);

    private final int adExpirationAge;
    private String urlForParsing;
    private final String regExpByPostTime;
    private final Pattern filteredByPostTime;

    private final DriverConfigurator driverConfigurator;
    private final AdsRepository adsRepository;

    @Autowired
    public ParserAdapter(AdsRepository adsRepository, DriverConfigurator driverConfigurator, ParserConfig parserConfig) {
        this.adsRepository = adsRepository;
        this.driverConfigurator = driverConfigurator;
        this.urlForParsing = parserConfig.getDefUrlForParse();
        this.adExpirationAge = parserConfig.getAdExpirationAgeMinutes();
        this.regExpByPostTime = "\\b[1-"+ adExpirationAge +"]\\b минут.*";
        this.filteredByPostTime = Pattern.compile(regExpByPostTime);
    }

    public void parseAndSaveAds()  {

        WebDriver webDriver = null;
        List<Ads> afterFiltering = null;
        try {
            webDriver = driverConfigurator.getChromeDriver();

            Document page = getFirstPage(webDriver);
            Thread.sleep(1000);
            List<Ads> tempList = ParsingUtils.parseToList(page);

            afterFiltering = tempList.stream().filter(it -> {
                Matcher matcher = filteredByPostTime.matcher(it.getDate());
                return matcher.find();
            }).collect(Collectors.toList());

            adsRepository.saveAll(afterFiltering);
            LOGGER.info("total saved size: {}", afterFiltering.size());
        } catch (Exception e) {
            LOGGER.error("executeByOrdered().exception ", e);
        }

    }

    public Document getFirstPage(WebDriver webDriver) {
        webDriver.get(urlForParsing);
        return Jsoup.parse(webDriver.getPageSource());
    }


    public void setUrlForParsing(String urlForParsing) {
        this.urlForParsing = urlForParsing;
        LOGGER.info("URL was changed for {}", urlForParsing);
    }

    public AdsRepository getAdsRepository() {
        return adsRepository;
    }
}
