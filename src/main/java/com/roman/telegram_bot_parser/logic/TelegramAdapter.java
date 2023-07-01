package com.roman.telegram_bot_parser.logic;

import com.roman.telegram_bot_parser.config.BotConfig;
import com.roman.telegram_bot_parser.dao.Ad;
import com.roman.telegram_bot_parser.dao.AdsRepository;
import com.roman.telegram_bot_parser.dao.TelegramUser;
import com.roman.telegram_bot_parser.dao.TelegramUserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.DefaultBotOptions;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.commands.SetMyCommands;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.commands.BotCommand;
import org.telegram.telegrambots.meta.api.objects.commands.scope.BotCommandScopeDefault;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;

@Component
public class TelegramAdapter extends TelegramLongPollingBot {

    private static final Logger LOGGER = LoggerFactory.getLogger(TelegramAdapter.class);

    private static final String INFORMATION_ABOUT_APP = "Разработчик приложения: https://t.me/roman_f_dev Все права защищены ©";

    private final BotConfig config;

    private final AdsRepository adsRepository;

    private final TelegramUserRepository telegramUserRepository;

    private final ParserAdapter parserAdapter;

    private final AtomicBoolean isItAnswer = new AtomicBoolean(false);

    @Autowired
    public TelegramAdapter(BotConfig config,
                           AdsRepository adsRepository,
                           TelegramUserRepository telegramUserRepository,
                           ParserAdapter parserAdapter) {
        this.config = config;
        this.adsRepository = adsRepository;
        this.telegramUserRepository = telegramUserRepository;
        this.parserAdapter = parserAdapter;
        createMainMenu();
    }

    public TelegramAdapter(DefaultBotOptions options,
                           BotConfig config,
                           AdsRepository adsRepository,
                           TelegramUserRepository telegramUserRepository,
                           ParserAdapter parserAdapter) {
        super(options);
        this.config = config;
        this.adsRepository = adsRepository;
        this.telegramUserRepository = telegramUserRepository;
        this.parserAdapter = parserAdapter;
        createMainMenu();
    }

    private void createMainMenu() {
        List<BotCommand> listOfCommands = new ArrayList<>();
        listOfCommands.add(new BotCommand("/info", "get application information"));
        listOfCommands.add(new BotCommand("/start", "enable update subscription"));
        listOfCommands.add(new BotCommand("/edit", "change search link"));
        listOfCommands.add(new BotCommand("/excluded", "not implemented yet"));
        listOfCommands.add(new BotCommand("/stop", "disable update subscription"));

        try {
            this.execute(new SetMyCommands(listOfCommands, new BotCommandScopeDefault(), null));
        } catch (TelegramApiException e) {
            LOGGER.error("Error setting bot's command list: ", e);
        }
    }

    @Override
    public String getBotUsername() {
        return config.getBothName();
    }

    @Override
    public String getBotToken() {
        return config.getToken();
    }

    @Override
    public void onUpdateReceived(Update update) {

        if(update.hasMessage() && update.getMessage().hasText()) {
            String messageText = update.getMessage().getText();

            long chatId = update.getMessage().getChatId();

            if(isItAnswer.get()) {
                isItAnswer.set(false);
                validateAndSetURL(messageText, chatId);
                return;
            }

            switch (messageText) {
                case "/info" :
                    sendMessage(chatId, INFORMATION_ABOUT_APP);
                    break;
                case "/start" :
                    registerUserAsSubscriber(update.getMessage());
                    break;
                case "/edit" :
                    sendMessage(chatId, "Следующим сообщением введите ссылку для поиска: ");
                    isItAnswer.set(true);
                    break;
                case "/stop" :
                    unregisterUserAsSubscriber(update.getMessage());
                    break;
                default: sendMessage(chatId, "Команда не распознана.");
            }

        }

    }

    private void validateAndSetURL(String messageText, long chatId) {
        if(messageText.startsWith("https://www.avito.ru") && messageText.endsWith("104")) {
            parserAdapter.setUrlForParsing(messageText);
            sendMessage(chatId, "Отслеживаемый поисковой запрос успешно изменен.");

        } else {
            sendMessage(chatId, "Ошибка! Объявления должны быть отфильтрованы по дате и ссылка должна начинаться на: 'http/avito'");
            sendMessage(chatId, "попробуйте снова вызвать команду /edit а затем ввести правильную ссылку");
        }
    }


    /**
     *
     * Добавить пользователя в таблицу подписчиков если его еще там нет.
     *
     * @param msg
     */

    private void registerUserAsSubscriber(Message msg) {

        if(!telegramUserRepository.findById(msg.getChatId()).isPresent()) {

            long chatId = msg.getChatId();
            Chat chat = msg.getChat();

            TelegramUser telegramUser = new TelegramUser();

            telegramUser.setChatId(chatId);
            telegramUser.setFirstName(chat.getFirstName());
            telegramUser.setLastName(chat.getLastName());
            telegramUser.setUserName(chat.getUserName());
            telegramUser.setRegisteredId(new Timestamp(System.currentTimeMillis()));

            telegramUserRepository.save(telegramUser);
            announceServiceAvailability(chatId, msg.getChat().getFirstName(), true);
        } else {
            sendMessage(msg.getChatId(), "Вы уже подписаны на уведомления!");
        }

    }


    /**
     *
     * Удалить пользователя из таблицы подписчиков если он там есть.
     *
     * @param msg
     */

    private void unregisterUserAsSubscriber(Message msg) {
            long chatId = msg.getChatId();
            Optional<TelegramUser> userOptional = telegramUserRepository.findById(chatId);
            if(userOptional.isPresent()) {
                TelegramUser telegramUser = userOptional.get();
                telegramUserRepository.delete(telegramUser);
                announceServiceAvailability(chatId, msg.getChat().getFirstName(), false);
            } else {
                sendMessage(chatId, "Уведомления уже были отключены!");
            }
    }



    private void announceServiceAvailability(long chatId, String firstName, boolean available) {
        String answer;
        if (available) {
            answer = "Привет, " + firstName + " уведомления включены!";
        } else {
            answer = firstName + ", уведомления отключены!";
        }
        sendMessage(chatId, answer);
    }

    private void sendMessage(long chatId, String textToSend) {
        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));
        message.setText(textToSend);

        try {
            execute(message);
        } catch (TelegramApiException e) {
            LOGGER.error("sendMessage()" ,e);
        }

    }


    /**
     *
     * Проверить наличие объявлений и отправить их всем пользователям из базы.
     *
     */

    public void sendAllUsers() {

        Iterable<TelegramUser> subscribersList = telegramUserRepository.findAll();

        Iterable<Ad> adsList = adsRepository.findAll();

        List<Ad> ads = (List<Ad>) convertToList(adsList);
        
        if(!ads.isEmpty()) {

            for (TelegramUser u : subscribersList) {

                for(Ad a : ads) {
                    SendMessage message = getSendMessage(a, u);
                    try {
                        execute(message);
                    } catch (TelegramApiException e) {
                        LOGGER.error("sendAllUsers().exception" ,e);
                    }
                }

            }

        }

        adsRepository.deleteAll();
        LOGGER.info("clear DB after sending");

    }


    /**
     *
     * Создать сообщение и заполнить его данными из объекта объявления.
     *
     */

    private SendMessage getSendMessage(Ad ad, TelegramUser u) {

        StringBuilder sb = new StringBuilder();

        sb.append(System.lineSeparator());
        sb.append("------------------------");
        sb.append(System.lineSeparator());
        sb.append("Объявление: ");
        sb.append(System.lineSeparator());
        sb.append(System.lineSeparator());
        sb.append("время: ");
        sb.append(ad.getDate());
        sb.append(System.lineSeparator());
        sb.append("цена: ");
        sb.append(ad.getPrice());
        sb.append(System.lineSeparator());
        sb.append("ссылка: ");
        sb.append(ad.getLink());
        sb.append(System.lineSeparator());
        sb.append("------------------------");
        sb.append(System.lineSeparator());

        SendMessage message = new SendMessage();

        message.setChatId(String.valueOf(u.getChatId()));
        message.setText(sb.toString());

        return message;
    }


    public static <T> Collection<T> convertToList(Iterable<T> values) {
        if (values instanceof Collection<?>) {
            return (Collection<T>) values;
        }
        throw new IllegalArgumentException("should be collection");
    }

    public TelegramUserRepository getTelegramUserRepository() {
        return telegramUserRepository;
    }
}
