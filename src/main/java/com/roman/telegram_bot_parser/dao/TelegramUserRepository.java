package com.roman.telegram_bot_parser.dao;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface TelegramUserRepository extends MongoRepository<TelegramUser, Long> {

}
