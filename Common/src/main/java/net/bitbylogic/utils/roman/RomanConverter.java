package net.bitbylogic.utils.roman;

import net.bitbylogic.utils.smallcaps.SmallCapsConverter;

import java.util.TreeMap;

public class RomanConverter {

    private final static TreeMap<Integer, String> letterMap = new TreeMap<>();

    static {

        letterMap.put(1000, "M");
        letterMap.put(900, "CM");
        letterMap.put(500, "D");
        letterMap.put(400, "CD");
        letterMap.put(100, "C");
        letterMap.put(90, "XC");
        letterMap.put(50, "L");
        letterMap.put(40, "XL");
        letterMap.put(10, "X");
        letterMap.put(9, "IX");
        letterMap.put(5, "V");
        letterMap.put(4, "IV");
        letterMap.put(1, "I");

    }

    public static String convert(int number) {
        return convert(number, false);
    }

    public static String convert(int number, boolean smallCaps) {
        if (number <= 0) {
            return String.valueOf(number);
        }

        StringBuilder builder = new StringBuilder();
        int remaining = number;

        while (remaining > 0) {
            int key = letterMap.floorKey(remaining);
            builder.append(letterMap.get(key));
            remaining -= key;
        }

        String romanNumbers = builder.toString();

        if (!smallCaps) {
            return romanNumbers;
        }

        return SmallCapsConverter.convert(romanNumbers);
    }

}
