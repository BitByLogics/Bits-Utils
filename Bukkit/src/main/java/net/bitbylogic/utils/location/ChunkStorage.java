package net.bitbylogic.utils.location;

import lombok.Getter;
import lombok.NonNull;
import org.bukkit.block.data.BlockData;

public class ChunkStorage {

    private static final int CHUNK_SIZE = 16;

    private final int chunkX, chunkZ;

    @Getter
    private final long packedChunk;

    private BlockData[] blocks = new BlockData[4096];

    public ChunkStorage(int chunkX, int chunkZ) {
        this.chunkX = chunkX;
        this.chunkZ = chunkZ;

        this.packedChunk = LocationUtil.encodeChunk(chunkX, chunkZ);
    }

    private int getIndex(int x, int y, int z) {
        return (y * CHUNK_SIZE * CHUNK_SIZE) + (z * CHUNK_SIZE) + x;
    }

    public BlockData get(long packed) {
        int x = (int)((packed >> 38) & 0x3FFFFFFL);
        int z = (int)((packed >> 12) & 0x3FFFFFFL);
        int y = (int)(packed & 0xFFF);

        if (x >= 0x2000000) x -= 0x4000000;
        if (z >= 0x2000000) z -= 0x4000000;
        if (y >= 0x800) y -= 0x1000;

        return get(x, y, z);
    }

    public BlockData get(int worldX, int worldY, int worldZ) {
        if (blocks == null) {
            return null;
        }

        int localX = Math.floorMod(worldX, CHUNK_SIZE);
        int localY = Math.floorMod(worldY, CHUNK_SIZE);
        int localZ = Math.floorMod(worldZ, CHUNK_SIZE);

        int blockChunkX = Math.floorDiv(worldX, CHUNK_SIZE);
        int blockChunkZ = Math.floorDiv(worldZ, CHUNK_SIZE);

        if (blockChunkX != chunkX || blockChunkZ != chunkZ) {
            throw new IllegalArgumentException(
                    "Block at (" + worldX + ", " + worldY + ", " + worldZ +
                            ") is not in chunk (" + chunkX + ", " + chunkZ + ")"
            );
        }

        return blocks[getIndex(localX, localY, localZ)];
    }

    public void set(long packed, @NonNull BlockData blockData) {
        if (blocks == null) {
            blocks = new BlockData[CHUNK_SIZE * CHUNK_SIZE * CHUNK_SIZE];
        }

        int x = (int)((packed >> 38) & 0x3FFFFFFL);
        int z = (int)((packed >> 12) & 0x3FFFFFFL);
        int y = (int)(packed & 0xFFF);

        if (x >= 0x2000000) x -= 0x4000000;
        if (z >= 0x2000000) z -= 0x4000000;
        if (y >= 0x800) y -= 0x1000;

        set(x, y, z, blockData);
    }

    public void set(int worldX, int worldY, int worldZ, @NonNull BlockData data) {
        if (blocks == null) {
            blocks = new BlockData[CHUNK_SIZE * CHUNK_SIZE * CHUNK_SIZE];
        }

        int localX = Math.floorMod(worldX, CHUNK_SIZE);
        int localY = Math.floorMod(worldY, CHUNK_SIZE);
        int localZ = Math.floorMod(worldZ, CHUNK_SIZE);

        int blockChunkX = Math.floorDiv(worldX, CHUNK_SIZE);
        int blockChunkZ = Math.floorDiv(worldZ, CHUNK_SIZE);

        if (blockChunkX != chunkX || blockChunkZ != chunkZ) {
            throw new IllegalArgumentException(
                    "Block at (" + worldX + ", " + worldY + ", " + worldZ +
                            ") is not in chunk (" + chunkX + ", " + chunkZ + ")"
            );
        }

        blocks[getIndex(localX, localY, localZ)] = data;
    }

    public ChunkPosition asChunkPosition(@NonNull String worldName) {
        return new ChunkPosition(worldName, chunkX, chunkZ);
    }

}