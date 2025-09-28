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
        if (number == letterMap.floorKey(number)) {
            return letterMap.get(number);
        }

        int key = letterMap.floorKey(number);
        String romanNumbers = letterMap.get(key) + convert(number - key);
        return smallCaps ? SmallCapsConverter.convert(romanNumbers) : romanNumbers;
    }

}
