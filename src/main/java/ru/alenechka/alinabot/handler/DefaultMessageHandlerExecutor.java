package ru.alenechka.alinabot.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.bots.AbsSender;
import org.telegram.telegrambots.exceptions.TelegramApiException;
import ru.alenechka.alinabot.handler.message.MessageHandler;

import java.util.List;

@Component
public class DefaultMessageHandlerExecutor implements MessageHandlerExecutor {

    private static final Logger LOGGER = LoggerFactory.getLogger(DefaultMessageHandlerExecutor.class);

    // private final List<CallbackHandler> callbackHandlers;

    private final List<MessageHandler> messageHandlers;

    public DefaultMessageHandlerExecutor(final List<MessageHandler> messageHandlers) {
        this.messageHandlers = messageHandlers;
    }

    @Override
    public void execute(final Update update, final AbsSender sender) {
//        try {
//            if (update.hasCallbackQuery()) {
//                for (CallbackHandler callbackHandler : callbackHandlers) {
//                    if (callbackHandler.isNeedToHandle(update.getCallbackQuery())) {
//                        callbackHandler.execute(update.getCallbackQuery(), sender);
//                        return;
//                    }
//                }
//            } else
        try {
             if (update.hasMessage()) {
                 for (MessageHandler messageHandler : messageHandlers) {
                     if (messageHandler.isNeedToHandle(update.getMessage())) {
                         messageHandler.execute(update.getMessage(), sender);
                         return;
                     }
                 }
             }
        } catch (TelegramApiException e) {
            LOGGER.error("Can't handle update", e);
        }
    }
}
