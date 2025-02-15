package net.bitbylogic.utils;

import java.util.ArrayList;
import java.util.List;

public class RichTextUtil {


    /**
     * Retrieve data from a string array.
     * <p>
     * Example:
     * <p>
     * Input: Testing! 123 | Wow
     * Output: ["Testing! 123", "Wow"]
     *
     * @param args       The string array.
     * @param startIndex Index to start from.
     * @return A String array of extracted data.
     */
    public static String[] getRichText(String[] args, int startIndex) {
        String combinedString = StringUtil.join(startIndex, args, " ").trim();
        List<String> newString = new ArrayList<>();

        for (String s : combinedString.split("\\|")) {
            newString.add(s.trim());
        }

        return newString.isEmpty() ? new String[] {combinedString} : newString.toArray(new String[]{});
    }

    /**
     * Retrieve data from a string array.
     * <p>
     * Example:
     * <p>
     * Input: Testing! 123 | Wow
     * Output: ["Testing! 123", "Wow"]
     *
     * @param args       The string.
     * @param startIndex Index to start from.
     * @return A String array of extracted data.
     */
    public static String[] getRichText(String args, int startIndex) {
        List<String> newString = new ArrayList<>();

        for (String s : args.trim().split("\\|")) {
            newString.add(s.trim());
        }

        if (newString.isEmpty()) {
            newString.add(args.trim());
        }

        return newString.toArray(new String[]{});
    }

    /**
     * Retrieve data from a string array.
     * <p>
     * Example:
     * <p>
     * Input: Testing! 123 | Wow
     * Output: ["Testing! 123", "Wow"]
     *
     * @param args       The string.
     * @param startIndex Index to start from.
     * @param separator The separator.
     * @return A String array of extracted data.
     */
    public static String[] getRichText(String args, int startIndex, String separator) {
        List<String> newString = new ArrayList<>();

        for (String s : args.trim().split(separator)) {
            newString.add(s.trim());
        }

        if (newString.isEmpty()) {
            newString.add(args.trim());
        }

        return newString.toArray(new String[]{});
    }

}
