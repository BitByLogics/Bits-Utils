package net.bitbylogic.utils.location;

import org.junit.jupiter.api.Test;

import java.util.OptionalDouble;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PositionParsingTest {

    @Test
    void offsetLocationParsesThreeNumbers() {
        OffsetLocation offset = OffsetLocation.parse("1:2.5:-3").orElseThrow();

        assertEquals(new OffsetLocation(1, 2.5, -3), offset);
    }

    @Test
    void offsetLocationRejectsMalformedInput() {
        assertTrue(OffsetLocation.parse("1:2").isEmpty());
        assertTrue(OffsetLocation.parse("a:b:c").isEmpty());
    }

    @Test
    void globalPositionParsesThreeNumbers() {
        assertEquals(new GlobalPosition(1, 2, 3), GlobalPosition.parse("1:2:3").orElseThrow());
    }

    @Test
    void globalPositionRejectsMalformedInput() {
        assertTrue(GlobalPosition.parse("1:2").isEmpty());
        assertTrue(GlobalPosition.parse("1:2:3:4").isEmpty());
        assertTrue(GlobalPosition.parse("1:x:3").isEmpty());
    }

    @Test
    void worldPositionRoundTrips() {
        WorldPosition position = new WorldPosition("world", 1.5, 2, 3);

        assertEquals(position, WorldPosition.parse(position.toString()).orElseThrow());
    }

    @Test
    void worldPositionRejectsMalformedInput() {
        assertTrue(WorldPosition.parse(null).isEmpty());
        assertTrue(WorldPosition.parse("world:1:2").isEmpty());
        assertTrue(WorldPosition.parse("world:a:b:c").isEmpty());
    }

    @Test
    void packedCoordinatesUnpackNegativeValues() {
        long packed = LocationUtil.encode(-5, -60, -1000);

        assertEquals(-5, LocationUtil.unpackX(packed));
        assertEquals(-60, LocationUtil.unpackY(packed));
        assertEquals(-1000, LocationUtil.unpackZ(packed));
    }

    @Test
    void packedChunksRoundTrip() {
        long packed = LocationUtil.encodeChunk(-3, 7);
        ChunkPosition chunk = ChunkPosition.decodeChunk("world", packed);

        assertEquals(new ChunkPosition("world", -3, 7), chunk);
    }

    @Test
    void chunkDistanceIsEmptyAcrossWorlds() {
        ChunkPosition first = new ChunkPosition("one", 0, 0);
        ChunkPosition second = new ChunkPosition("two", 0, 0);

        assertEquals(OptionalDouble.empty(), first.distance(second));
    }

    @Test
    void chunkDistanceIsMeasuredInChunks() {
        ChunkPosition first = new ChunkPosition("one", 0, 0);
        ChunkPosition second = new ChunkPosition("one", 3, 4);

        assertEquals(5.0, first.distance(second).orElseThrow());
    }

}
