package com.roman.telegram_bot_parser.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import java.util.Objects;

@Configuration
public class ProcessorConfig {

    private static final Logger LOGGER = LoggerFactory.getLogger(ProcessorConfig.class);

    private final int siteScanningIntervalMillis;

    @Autowired
    public ProcessorConfig(Environment environment) {
        int siteScanningIntervalMinutes = Integer.parseInt(Objects.requireNonNull(environment.getProperty("processor.siteScanningIntervalMinutes")));
        this.siteScanningIntervalMillis = siteScanningIntervalMinutes * 1000 * 60;
        LOGGER.info("siteScanningIntervalMinutes: {}", siteScanningIntervalMinutes);
    }

    public int getSiteScanningIntervalMillis() {
        return siteScanningIntervalMillis;
    }

    @Override
    public String toString() {
        return "ProcessorConfig{" +
                ", siteScanningIntervalMillis=" + siteScanningIntervalMillis +
                '}';
    }
}
