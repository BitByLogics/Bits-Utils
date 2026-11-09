package net.bitbylogic.utils;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

class PairTest {

    @Test
    void nullMembersDoNotThrow() {
        assertEquals(new Pair<String, String>(null, "a"), new Pair<String, String>(null, "a"));
        assertNotEquals(new Pair<String, String>(null, "a"), new Pair<>("b", "a"));
    }

    @Test
    void differentTypesAreNotEqual() {
        assertNotEquals(new Pair<>("a", "b"), "a");
    }

}
