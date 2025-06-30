package com.example.telegramclientposter.util;

import it.tdlight.jni.TdApi;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class AdValidator {

    private AdValidator() {}

    private static final Pattern AD_KEYWORDS_PATTERN = Pattern.compile(
            "\\b(скидка|акция|купить|sale|бесплатно|переходи|успей|промокод|бонус|выгода|регистрируйся|получи|" +
                    "подпишись|подпишитесь|подписывайся|рекомендуем|советуем|обязательно подпишись|наш партн[её]р|" +
                    "партн[её]рский канал|спонсор|реклама|рекламный пост|канале)",
            Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE
    );

    private static final Pattern LINK_PATTERN = Pattern.compile(
            "\\b(t\\.me|telegram\\.me)/([a-zA-Z0-9_]+)(?!/(?:addstickers|iv)?\\b)", // Links to Telegram (t.me/channel, telegram.me/user)
            Pattern.CASE_INSENSITIVE
    );

    public static boolean checkWithRegex(TdApi.Message message) {

        String messageText = extractTextMessage(message);

        if (messageText == null || messageText.isEmpty()) {
            return false;
        }

        Matcher keywordMatcher = AD_KEYWORDS_PATTERN.matcher(messageText);
        Matcher linkMatcher = LINK_PATTERN.matcher(messageText);

        return keywordMatcher.find() || linkMatcher.find();
    }

    private static String extractTextMessage(TdApi.Message message) {
        if (message == null || message.content == null) {
            return null;
        }

        if (message.content instanceof TdApi.MessagePhoto messagePhoto) {
            // Возвращаем подпись фото, если она есть
            return (messagePhoto.caption != null && messagePhoto.caption.text != null) ? messagePhoto.caption.text.trim() : null;
        } else if (message.content instanceof TdApi.MessageText messageText) {
            // Возвращаем текст сообщения
            return (messageText.text != null && messageText.text.text != null) ? messageText.text.text.trim() : null;
        }

        return null;
    }
}
