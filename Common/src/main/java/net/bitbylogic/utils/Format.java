package net.bitbylogic.utils;

public class Format {

    /**
     * Format a string with placeholders.
     *
     * @param string       The string being formatted.
     * @param placeholders The placeholders being applied.
     * @return The formatted string.
     */
    public static String format(String string, Placeholder... placeholders) {
        String formattedMessage = string;

        for (Placeholder placeholder : placeholders) {
            formattedMessage = placeholder.modify(formattedMessage);
        }

        return formattedMessage;
    }

}
