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
