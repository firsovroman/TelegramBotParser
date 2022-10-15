package com.roman.telegram_bot_parser.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

@Configuration
public class ParserConfig {

    private static final Logger LOGGER = LoggerFactory.getLogger(ParserConfig.class);

    private int testRange;
    private String urlForParse;

    @Autowired
    public ParserConfig(Environment environment) {
        this.testRange = Integer.parseInt(environment.getProperty("parser.testRange"));
        LOGGER.info("testRange: {}", testRange);
        this.urlForParse = environment.getProperty("parser.urlForParsing");
        LOGGER.info("urlForParse: {}", urlForParse);
    }

    public int getTestRange() {
        return testRange;
    }

    public String getUrlForParse() {
        return urlForParse;
    }
}
