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


    //Возраст истечения срока действия объявления
    private final int adExpirationAgeMinutes;
    private final String defUrlForParse;

    @Autowired
    public ParserConfig(Environment environment) {
        this.adExpirationAgeMinutes = Integer.parseInt(Objects.requireNonNull(environment.getProperty("parser.adExpirationAgeMinutes")));
        LOGGER.info("adExpirationAgeMinutes: {}", adExpirationAgeMinutes);
        this.defUrlForParse = environment.getProperty("parser.defUrlForParse");
        LOGGER.info("defUrlForParse: {}", defUrlForParse);
    }

    public int getAdExpirationAgeMinutes() {
        return adExpirationAgeMinutes;
    }

    public String getDefUrlForParse() {
        return defUrlForParse;
    }
}
