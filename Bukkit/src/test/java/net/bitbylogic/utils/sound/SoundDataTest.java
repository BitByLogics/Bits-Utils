package net.bitbylogic.utils.sound;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

class SoundDataTest {

    @Test
    void wrongPartCountIsRejected() {
        assertTrue(SoundData.parse("ENTITY_PLAYER_LEVELUP").isEmpty());
        assertTrue(SoundData.parse("a:1").isEmpty());
        assertTrue(SoundData.parse("a:1:1:1").isEmpty());
    }

}
