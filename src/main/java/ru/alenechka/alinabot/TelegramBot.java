package ru.alenechka.alinabot;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.TelegramBotsApi;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.exceptions.TelegramApiException;
import org.telegram.telegrambots.generics.BotSession;
import ru.alenechka.alinabot.handler.MessageHandlerExecutor;
import ru.alenechka.alinabot.word2vec.Word2VecEngine;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

@Component
public class TelegramBot extends TelegramLongPollingBot {

    private static final Logger LOGGER = LoggerFactory.getLogger(TelegramBot.class);

    private final MessageHandlerExecutor messageHandlerExecutor;

    private final String botName;

    private final String botToken;

    private BotSession session;

    public TelegramBot(final MessageHandlerExecutor messageHandlerExecutor,
                       @Value("${credential.telegram.name}") final String botName,
                       @Value("${credential.telegram.token}") final String botToken) {
        this.messageHandlerExecutor = messageHandlerExecutor;
        this.botName = botName;
        this.botToken = botToken;
    }

    @Override
    public void onUpdateReceived(Update update) {
        messageHandlerExecutor.execute(update, this);
    }

    @Override
    public String getBotUsername() {
        return botName;
    }

    @Override
    public String getBotToken() {
        return botToken;
    }

    static {
        ApiContextInitializer.init();
    }

    @PostConstruct
    public void init() {
        if (StringUtils.isNotEmpty(botName) && StringUtils.isNotEmpty(botToken)) {
            TelegramBotsApi botsApi = new TelegramBotsApi();
            try {
                session = botsApi.registerBot(this);

                // Init word2vec model
                new Word2VecEngine("/app/src/main/resources/word2vec/training_data_strange_dialog.txt",
                        Word2VecEngine.ModelMode.INIT);

                LOGGER.info("Alina bot initialized");
            } catch (Exception e) {
                LOGGER.error("Can't initialize Alina bot", e);
            }
        } else {
            LOGGER.error("Can't initialize Alina bot. Please, provide botName and botToken!");
        }
    }

    @PreDestroy
    public void destroy() {
        if (session != null) {
            session.stop();
        }
    }
}
