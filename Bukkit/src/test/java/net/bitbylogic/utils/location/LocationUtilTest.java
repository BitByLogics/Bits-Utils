package net.bitbylogic.utils.location;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class LocationUtilTest {

    @Test
    void separatorIsTreatedLiterally() {
        assertTrue(LocationUtil.isLocationString("my_world._.1._.2._.3", "._."));
        assertFalse(LocationUtil.isLocationString("my_world._.1._.2", "._."));
    }

    @Test
    void sixPartStringsAreLocationStrings() {
        assertTrue(LocationUtil.isLocationString("world:1:2:3:0.0:0.0"));
    }

    @Test
    void fivePartStringsAreNotLocationStrings() {
        assertFalse(LocationUtil.isLocationString("world:1:2:3:0.0"));
    }

    @Test
    void malformedLocationStringReturnsNull() {
        assertNull(LocationUtil.stringToLocation("world:1:2"));
        assertNull(LocationUtil.stringToLocation("world:1:2:3:0.0"));
    }

    @Test
    void dotSeparatorDoesNotActAsRegex() {
        assertFalse(LocationUtil.isLocationString("abcd", "."));
    }

}
