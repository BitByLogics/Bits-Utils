package net.bitbylogic.utils;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class StringUtilTest {

    @Test
    void readingTimeIsReturnedInSeconds() {
        String text = "one two three four five six seven eight nine ten";

        assertEquals(3, StringUtil.calculateReadingTime(text, 200));
    }

    @Test
    void invalidSpeedOrBlankTextReturnsZero() {
        assertEquals(0, StringUtil.calculateReadingTime("hello", 0));
        assertEquals(0, StringUtil.calculateReadingTime("  ", 200));
    }

    @Test
    void joinStartsAtIndex() {
        assertEquals("b c", StringUtil.join(1, new String[]{"a", "b", "c"}, " "));
    }

    @Test
    void joinPastTheEndReturnsEmpty() {
        assertEquals("", StringUtil.join(3, new String[]{"a", "b", "c"}, " "));
        assertEquals("a b", StringUtil.join(-2, new String[]{"a", "b"}, " "));
    }

}
