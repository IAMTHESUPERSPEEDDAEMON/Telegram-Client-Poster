package com.example.telegramclientposter.util;

import java.util.regex.Pattern;

public final class TextCleanerForEmbeddings {

    private TextCleanerForEmbeddings() {}

    private static final Pattern URL_PATTERN = Pattern.compile(
            "\\b(?:(?:https?|ftp)://|www\\.)\\S+\\b",
            Pattern.CASE_INSENSITIVE
    );

    private static final Pattern EMOJI_PATTERN = Pattern.compile(
            "[" +
                    "\\uD83C\\uDF00-\\uD83D\\uDDFF" +
                    "\\uD83D\\uDE00-\\uD83D\\uDE4F" +
                    "\\uD83D\\uDE80-\\uD83D\\uDEFF" +
                    "\\uD83E\\uDD00-\\uD83E\\uDDFF" +
                    "\\u2600-\\u26FF" +
                    "\\u2700-\\u27BF" +
                    "\\uFE0F" +
                    "\\u200D" +
                    "\\u200C" +
                    "]+",
            Pattern.UNICODE_CHARACTER_CLASS
    );

    public static String cleanTextForEmbedding(String text) {
        if (text == null || text.isEmpty()) {
            return text;
        }

        String cleanedText = URL_PATTERN.matcher(text).replaceAll(" ");
        cleanedText = EMOJI_PATTERN.matcher(cleanedText).replaceAll(" ");

        cleanedText = cleanedText.replaceAll("\\s+", " ").trim();

        return cleanedText;
    }

}
