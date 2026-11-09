package net.bitbylogic.utils.roman;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class RomanConverterTest {

    @Test
    void convertsCommonNumbers() {
        assertEquals("I", RomanConverter.convert(1));
        assertEquals("IV", RomanConverter.convert(4));
        assertEquals("XIV", RomanConverter.convert(14));
        assertEquals("MCMXCIV", RomanConverter.convert(1994));
    }

    @Test
    void nonPositiveNumbersAreReturnedAsDigits() {
        assertEquals("0", RomanConverter.convert(0));
        assertEquals("-3", RomanConverter.convert(-3));
    }

    @Test
    void smallCapsApplyToSingleSymbols() {
        assertEquals("ᴠ", RomanConverter.convert(5, true));
        assertEquals("ɪᴠ", RomanConverter.convert(4, true));
    }

}
