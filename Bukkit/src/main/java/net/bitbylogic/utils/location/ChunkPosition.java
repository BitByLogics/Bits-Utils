package net.bitbylogic.utils.location;

import lombok.NonNull;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.World;

import java.util.HashSet;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

public record ChunkPosition(@NonNull String world, int x, int z) {

    public Optional<Chunk> toBukkitChunk() {
        World bukkitWorld = Bukkit.getWorld(world);

        if(bukkitWorld == null) {
            return Optional.empty();
        }

        return Optional.of(bukkitWorld.getChunkAt(x, z));
    }

    public static ChunkPosition of(@NonNull Chunk chunk) {
        return new ChunkPosition(chunk.getWorld().getName(), chunk.getX(), chunk.getZ());
    }

    /**
     * Distance between two chunks, measured in chunks.
     *
     * @throws IllegalArgumentException if worlds differ
     */
    public double distance(@NonNull ChunkPosition chunk) {
        if (!this.world.equals(chunk.world)) {
            throw new IllegalArgumentException("Cannot compare chunks in different worlds");
        }

        double thisCenterX = (this.x << 4) + 8;
        double thisCenterZ = (this.z << 4) + 8;
        double otherCenterX = (chunk.x << 4) + 8;
        double otherCenterZ = (chunk.z << 4) + 8;

        double dx = thisCenterX - otherCenterX;
        double dz = thisCenterZ - otherCenterZ;

        return Math.sqrt(dx * dx + dz * dz) / 16.0;
    }

    /**
     * Gets all chunk positions within the given chunk radius
     *
     * @param radius chunk radius
     * @throws IllegalArgumentException if radius < 0
     */
    public @NonNull Set<ChunkPosition> getNearbyChunks(int radius) {
        if (radius < 0) {
            throw new IllegalArgumentException("Radius must be >= 0");
        }

        Set<ChunkPosition> result = new HashSet<>();

        for (int dx = -radius; dx <= radius; dx++) {
            for (int dz = -radius; dz <= radius; dz++) {
                ChunkPosition chunk = new ChunkPosition(
                        this.world,
                        this.x + dx,
                        this.z + dz
                );

                if (distance(chunk) > radius) {
                    continue;
                }

                result.add(chunk);
            }
        }

        return result;
    }

    public long encode() {
        return LocationUtil.encodeChunk(x, z);
    }

    public static ChunkPosition decodeChunk(@NonNull String worldName, long packed) {
        int x = (int)(packed >> 32);
        int z = (int)(packed & 0xFFFFFFFFL);

        return new ChunkPosition(worldName, x, z);
    }


    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        ChunkPosition that = (ChunkPosition) o;
        return x == that.x && z == that.z && Objects.equals(world, that.world);
    }

    @Override
    public int hashCode() {
        return Objects.hash(world, x, z);
    }

}
