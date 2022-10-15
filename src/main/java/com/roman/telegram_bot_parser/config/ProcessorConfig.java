package com.roman.telegram_bot_parser.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

@Configuration
public class ProcessorConfig {

    private static final Logger LOGGER = LoggerFactory.getLogger(ProcessorConfig.class);

    private int fixedRate;

    @Autowired
    public ProcessorConfig(Environment environment) {
        int fixedRateMinute = Integer.parseInt(environment.getProperty("processor.fixedRateMinute"));
        this.fixedRate = fixedRateMinute * 1000 * 60;
        LOGGER.info("fixedRateMinute: {}", fixedRateMinute);
    }

    public int getFixedRate() {
        return fixedRate;
    }

    @Override
    public String toString() {
        return "ProcessorConfig{" +
                ", fixedRate=" + fixedRate +
                '}';
    }
}
