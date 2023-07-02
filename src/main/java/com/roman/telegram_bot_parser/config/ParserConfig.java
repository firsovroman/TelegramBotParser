package com.roman.telegram_bot_parser.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import java.util.Objects;

@Configuration
public class ParserConfig {

    private static final Logger LOGGER = LoggerFactory.getLogger(ParserConfig.class);


    private final String adAgeRegExp;
    private final String defUrlForParse;

    @Autowired
    public ParserConfig(Environment environment) {
        this.adAgeRegExp = Objects.requireNonNull(environment.getProperty("parser.adAgeRegExp"));
        LOGGER.info("adAgeRegExp: {}", adAgeRegExp);
        this.defUrlForParse = Objects.requireNonNull(environment.getProperty("parser.defUrlForParse"));
        LOGGER.info("defUrlForParse: {}", defUrlForParse);
    }

    public String getAdAgeRegExp() {
        return adAgeRegExp;
    }

    public String getDefUrlForParse() {
        return defUrlForParse;
    }
}
