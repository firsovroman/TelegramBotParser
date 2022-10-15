package com.roman.telegram_bot_parser.jobs;

import com.roman.telegram_bot_parser.dao.UserRepository;
import com.roman.telegram_bot_parser.logic.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Collection;


@Component
public class ProcessorJob {

    private static final Logger LOGGER = LoggerFactory.getLogger(ProcessorJob.class);

    private final Processor processor;
    private final UserRepository userRepository;

    @Autowired
    public ProcessorJob(Processor processor, UserRepository userRepository) {
        this.processor = processor;
        this.userRepository = userRepository;
    }

    @Scheduled(fixedRateString = "#{@processor.config.fixedRate}")
    public void execute() {
        LOGGER.info("job() started");
        if (listOfUsersIsEmpty(userRepository.findAll())) {
            LOGGER.info("job() failed because there are no subscribers");
            return;
        }
        LOGGER.info("job() execute started");
        processor.execute();
        LOGGER.info("job() completed");
    }

    public static <T> boolean listOfUsersIsEmpty(Iterable<T> values) {
        if (values instanceof Collection<?>) {
            return ((Collection<T>) values).isEmpty();
        }
        throw new IllegalArgumentException("should be collection");
    }

}