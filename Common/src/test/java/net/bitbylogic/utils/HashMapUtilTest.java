package net.bitbylogic.utils;

import org.junit.jupiter.api.Test;

import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.assertEquals;

class HashMapUtilTest {

    @Test
    void singleCharacterSeparatorsKeepWorking() {
        HashMap<String, String> map = new HashMap<>();
        map.put("a", "1");

        assertEquals("a:1", HashMapUtil.mapToString(map));
    }

    @Test
    void multiCharacterEntrySeparatorIsFullyTrimmed() {
        HashMap<String, String> map = new HashMap<>();
        map.put("a", "1");

        assertEquals("a:1", HashMapUtil.mapToString(map, ";;", ":", null));
    }

}
