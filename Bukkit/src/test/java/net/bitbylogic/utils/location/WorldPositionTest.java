package net.bitbylogic.utils.location;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class WorldPositionTest {

    @Test
    void negativeFractionalCoordinatesFloorToTheirBlock() {
        WorldPosition position = new WorldPosition("world", -0.5, -0.5, -0.5);
        WorldPosition block = position.toWorldBlockPosition();

        assertEquals(-1.0, block.x());
        assertEquals(-1.0, block.y());
        assertEquals(-1.0, block.z());
    }

    @Test
    void negativeFractionalCoordinatesUseTheCorrectChunk() {
        WorldPosition position = new WorldPosition("world", -0.5, 64, -16.5);

        assertEquals(-1, position.getChunkX());
        assertEquals(-2, position.getChunkZ());
    }

    @Test
    void positiveCoordinatesAreUnchanged() {
        WorldPosition position = new WorldPosition("world", 17.9, 64, 0.1);

        assertEquals(1, position.getChunkX());
        assertEquals(0, position.getChunkZ());
    }

    @Test
    void nonStrictMatchComparesBlocks() {
        WorldPosition first = new WorldPosition("world", -0.2, 10, -0.8);
        WorldPosition second = new WorldPosition("world", -0.9, 10.5, -0.1);

        assertTrue(first.matches(second, false));
    }

    @Test
    void encodeRoundTripsNegativeBlocks() {
        WorldPosition position = new WorldPosition("world", -0.5, 70, -3.2);
        WorldPosition decoded = WorldPosition.decode("world", position.encode());

        assertEquals(-1.0, decoded.x());
        assertEquals(70.0, decoded.y());
        assertEquals(-4.0, decoded.z());
    }

}
