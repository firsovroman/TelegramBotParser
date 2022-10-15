package com.roman.telegram_bot_parser.logic;

import com.roman.telegram_bot_parser.config.ProcessorConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
public class Processor {

    private static final Logger LOGGER = LoggerFactory.getLogger(Processor.class);

    private final ProcessorConfig config;

    private final ParserAdapter parserAdapter;

    private final TelegramAdapter telegramAdapter;

    @Autowired
    public Processor(ProcessorConfig processorConfig, ParserAdapter parserAdapter, TelegramAdapter telegramAdapter) {
        this.config = processorConfig;
        this.parserAdapter = parserAdapter;
        this.telegramAdapter = telegramAdapter;
    }

    public ProcessorConfig getConfig() {
        return config;
    }


    public void execute() {
        LOGGER.info("avito parsing started ");
        long started = System.nanoTime();
        parserAdapter.parseAndSaveAds();
        long elapsed = TimeUnit.NANOSECONDS.toSeconds(System.nanoTime() - started);
        LOGGER.info("avito parsing completed for {} seconds ", elapsed);

        LOGGER.info("telegram sending started ");
        long started2 = System.nanoTime();
        telegramAdapter.sendAllUsers();
        long elapsed2 = TimeUnit.NANOSECONDS.toSeconds(System.nanoTime() - started2);
        LOGGER.info("telegram sent messages and clear DB for {} seconds ", elapsed2);
    }

    public ParserAdapter getParserAdapter() {
        return parserAdapter;
    }

    public TelegramAdapter getTelegramAdapter() {
        return telegramAdapter;
    }
}
