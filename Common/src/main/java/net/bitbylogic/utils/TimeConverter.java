package net.bitbylogic.utils;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TimeConverter {

    private static final Pattern TIME_PATTERN = Pattern.compile("(?i)(\\d+mh|\\d+[smhdwyf])");
    private static final Pattern NUMBER_PATTERN = Pattern.compile("(\\d+)");
    private static final Pattern LETTER_PATTERN = Pattern.compile("[A-Za-z]");

    /**
     * Check if a string is a valid time string.
     *
     * @param string The string being checked.
     * @return Whether the specified string is valid.
     */
    public static boolean isTimeString(String string) {
        for (String split : string.trim().split("\\s+")) {
            if (!TIME_PATTERN.matcher(split).matches()) {
                return false;
            }
        }
        return true;
    }

    public static String getTimeString(String string) {
        Matcher matcher = TIME_PATTERN.matcher(string);
        StringBuilder result = new StringBuilder();

        while (matcher.find()) {
            result.append(matcher.group()).append(" ");
        }

        return result.toString().trim();
    }

    /**
     * Convert a long to a human-readable time.
     *
     * @param time The time being converted.
     * @return A readable time string.
     */
    public static String convertToReadableTime(long time) {
        return convertToReadableTime(time, false);
    }

    /**
     * Convert a long to a human-readable time.
     *
     * @param time         The time being converted.
     * @param longPrefixes Whether to use long time prefixes.
     * @return A readable time string.
     */
    public static String convertToReadableTime(long time, boolean longPrefixes, String... excludedTimes) {
        if (time <= 0) {
            return "Forever";
        }

        long milliseconds = time - ((time) / 1000 * 1000);
        long seconds = TimeUnit.MILLISECONDS.toSeconds(time);
        seconds = seconds - ((seconds / 60) * 60);
        long minutes = TimeUnit.MILLISECONDS.toMinutes(time);
        minutes = minutes - ((minutes / 60) * 60);
        long hours = TimeUnit.MILLISECONDS.toHours(time);
        hours = hours - ((hours / 24) * 24);
        long days = TimeUnit.MILLISECONDS.toDays(time);
        long months = days / 30;
        days = days - ((days / 30) * 30);
        long weeks = days / 7;
        days = days - ((days / 7) * 7);
        long years = months / 12;
        months = months - ((months / 12) * 12);

        StringBuilder message = new StringBuilder();

        if (years > 0 && Arrays.stream(excludedTimes).noneMatch(s -> s.equalsIgnoreCase("year"))) {
            message.append(years).append(TimeAbbreviation.YEAR.getPrefix(longPrefixes) + (longPrefixes ? (years > 1 ? "s" : "") : "")).append(" ");
        }

        if (months > 0 && Arrays.stream(excludedTimes).noneMatch(s -> s.equalsIgnoreCase("month"))) {
            message.append(months).append(TimeAbbreviation.MONTH.getPrefix(longPrefixes) + (longPrefixes ? (months > 1 ? "s" : "") : "")).append(" ");
        }

        if (weeks > 0 && Arrays.stream(excludedTimes).noneMatch(s -> s.equalsIgnoreCase("weeks"))) {
            message.append(weeks).append(TimeAbbreviation.WEEK.getPrefix(longPrefixes) + (longPrefixes ? (weeks > 1 ? "s" : "") : "")).append(" ");
        }

        if (days > 0 && Arrays.stream(excludedTimes).noneMatch(s -> s.equalsIgnoreCase("days"))) {
            message.append(days).append(TimeAbbreviation.DAY.getPrefix(longPrefixes) + (longPrefixes ? (days > 1 ? "s" : "") : "")).append(" ");
        }

        if (hours > 0 && Arrays.stream(excludedTimes).noneMatch(s -> s.equalsIgnoreCase("hours"))) {
            message.append(hours).append(TimeAbbreviation.HOUR.getPrefix(longPrefixes) + (longPrefixes ? (hours > 1 ? "s" : "") : "")).append(" ");
        }

        if (minutes > 0 && Arrays.stream(excludedTimes).noneMatch(s -> s.equalsIgnoreCase("minutes"))) {
            message.append(minutes).append(TimeAbbreviation.MINUTE.getPrefix(longPrefixes) + (longPrefixes ? (minutes > 1 ? "s" : "") : "")).append(" ");
        }

        if (seconds > 0 && Arrays.stream(excludedTimes).noneMatch(s -> s.equalsIgnoreCase("seconds"))) {
            message.append(seconds);

            if(Arrays.stream(excludedTimes).noneMatch(s -> s.equalsIgnoreCase("milliseconds"))) {
                message.append(".").append((int) (milliseconds / 100));
            }

            message.append(TimeAbbreviation.SECOND.getPrefix(longPrefixes)).append(longPrefixes ? (seconds > 1 ? "s" : "") : "");
        }

        if(seconds <= 0 && milliseconds > 0 && milliseconds < 1000 && Arrays.stream(excludedTimes).noneMatch(s -> s.equalsIgnoreCase("milliseconds"))) {
            message.append("0").append(".").append(((int) (milliseconds / 100))).append(TimeAbbreviation.SECOND.getPrefix(longPrefixes)).append(longPrefixes ? (milliseconds > 1 ? "s" : "") : "");
        }

        return message.toString().trim();
    }

    /**
     * Converts a string into a time value.
     *
     * @param combindedArgs The string being converted.
     * @return Converted time value.
     */
    public static long convert(String combindedArgs) {
        String[] args = combindedArgs.split(" ");
        long time = 0;

        for (String arg : args) {
            if (arg.matches("((\\d+)[A-Za-z])")) {
                Matcher numberMatcher = NUMBER_PATTERN.matcher(arg);
                numberMatcher.find();
                Matcher letterMatcher = LETTER_PATTERN.matcher(arg);
                letterMatcher.find();
                long tempTime = Long.valueOf(numberMatcher.group(0));
                TimeAbbreviation abbreviation = getAbbreviation(letterMatcher.group(0));
                time += abbreviation.getTime(tempTime);
            }
        }

        return time;
    }

    /**
     * Get TimeAbbreviation from string.
     *
     * @param data The string being converted.
     * @return Matched TimeAbbreviation instance.
     */
    private static TimeAbbreviation getAbbreviation(String data) {
        for (TimeAbbreviation abbreviation : TimeAbbreviation.values()) {
            for (String prefix : abbreviation.getPrefix()) {
                if (data.equalsIgnoreCase(prefix)) {
                    return abbreviation;
                }
            }
        }

        return null;
    }

    public enum TimeAbbreviation {

        SECOND(1, "s", " Second"),
        MINUTE(60, "m", " Minute"),
        HOUR(3600, "h", " Hour"),
        DAY(86400, "d", " Day"),
        WEEK(604800, "w", " Week"),
        MONTH(2592000, "mh", " Month"),
        YEAR(31556952, "y", " Year"),
        INFINITE(-1, "f", "Forever");

        String[] prefix;
        long multiplier;

        TimeAbbreviation(long multiplier, String... prefix) {
            this.prefix = prefix;
            this.multiplier = multiplier;
        }

        public String[] getPrefix() {
            return prefix;
        }

        public long getTime(long time) {
            if (multiplier == -1) {
                return -1;
            }

            return (time * multiplier) * 1000;
        }

        public String getPrefix(boolean longPrefix) {
            return longPrefix ? prefix[1] : prefix[0];
        }

    }

}