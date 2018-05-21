package ru.alenechka.alinabot.handler.message;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Message;
import org.telegram.telegrambots.bots.AbsSender;
import org.telegram.telegrambots.exceptions.TelegramApiException;
import ru.alenechka.alinabot.word2vec.Word2VecEngine;

@Component
public class Word2VecTextMessageHandler implements MessageHandler {
    @Override
    public boolean isNeedToHandle(Message message) {
        final String mes = message.getText().trim();
        return StringUtils.isNotEmpty(mes) && !("/ping".equalsIgnoreCase(mes) || "пинг".equalsIgnoreCase(mes));
    }

    @Override
    public void execute(Message message, AbsSender sender) throws TelegramApiException {
//        try {
//            Word2VecEngine word2VecEngine = new Word2VecEngine("~/src/main/resources/word2vec/model.txt",
//                    Word2VecEngine.ModelMode.LOAD);
//
//            String messageTheme = word2VecEngine.getNearestNWords()
//
//        } catch (Exception e) {
//            throw new RuntimeException(e);
//        }

        sender.execute(new SendMessage().setText(System.getProperty("user.dir"))
                .setChatId(message.getChatId()));
    }
}
