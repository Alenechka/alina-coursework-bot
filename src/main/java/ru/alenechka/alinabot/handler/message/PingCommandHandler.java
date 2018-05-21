package ru.alenechka.alinabot.handler.message;

import org.apache.commons.lang3.StringUtils;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Message;
import org.telegram.telegrambots.bots.AbsSender;
import org.telegram.telegrambots.exceptions.TelegramApiException;

public class PingCommandHandler implements MessageHandler {
    @Override
    public boolean isNeedToHandle(Message message) {
        final String mes = message.getText().trim();
        return StringUtils.isNotEmpty(mes) && ("/ping".equalsIgnoreCase(mes) || "пинг".equalsIgnoreCase(mes));
    }

    @Override
    public void execute(Message message, AbsSender sender) throws TelegramApiException {
        sender.execute(new SendMessage().setText("Я жив, всё нормально.")
                .setChatId(message.getChatId()));
    }
}
