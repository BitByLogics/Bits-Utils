package net.bitbylogic.utils.location;

import lombok.NonNull;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.World;

import java.util.Objects;
import java.util.Optional;

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
