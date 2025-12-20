package net.bitbylogic.utils.location;

import lombok.NonNull;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.Optional;

public record WorldPosition(@NonNull String worldName, double x, double y, double z) {

    public static WorldPosition of(@NonNull Location location) {
        return new WorldPosition(location.getWorld().getName(), location.getX(), location.getY(), location.getZ());
    }

    public static WorldPosition ofBlock(@NonNull Location location) {
        return new WorldPosition(location.getWorld().getName(), location.getBlockX(), location.getBlockY(), location.getBlockZ());
    }

    public static WorldPosition of(@NonNull String string) {
        String[] split = string.split(":");

        if(split.length != 4) {
            throw new IllegalArgumentException("Invalid WorldPosition string");
        }

        return new WorldPosition(split[0], Double.parseDouble(split[1]), Double.parseDouble(split[2]), Double.parseDouble(split[3]));
    }

    public int getChunkX() {
        return (int) x >> 4;
    }

    public int getChunkZ() {
        return (int) z >> 4;
    }

    public WorldPosition toWorldBlockPosition() {
        return new WorldPosition(worldName, (int) x, (int) y, (int) z);
    }

    public ChunkPosition toChunkPosition() {
        return new ChunkPosition(worldName, getChunkX(), getChunkZ());
    }

    public Vector toVector() {
        return new Vector(x, y, z);
    }

    public boolean matches(@NonNull Location location) {
        return matches(location, true);
    }

    public boolean matches(@NonNull Location location, boolean strict) {
        if(location.getWorld() == null) {
            return false;
        }

        if(!strict) {
            return worldName.equals(location.getWorld().getName()) && ((int) x) == location.getBlockX() && ((int) y) == location.getBlockY() && ((int) z) == location.getBlockZ();
        }

        return worldName.equals(location.getWorld().getName()) && x == location.getX() && y == location.getY() && z == location.getZ();
    }

    public boolean matches(@NonNull WorldPosition position) {
        return matches(position, true);
    }

    public boolean matches(@NonNull WorldPosition position, boolean strict) {
        if(!strict) {
            return worldName.equals(position.worldName()) && ((int) x) == ((int) position.x()) && ((int) y) == ((int) position.y) && ((int) z) == ((int) position.z);
        }

        return worldName.equals(position.worldName()) && x == position.x() && y == position.y() && z == position.z();
    }

    public Optional<Location> toBukkitLocation() {
        World bukkitWorld = Bukkit.getWorld(worldName);

        if (bukkitWorld == null) {
            return Optional.empty();
        }

        return Optional.of(new Location(bukkitWorld, x, y, z));
    }

    public long encode() {
        return LocationUtil.encode((int) x, (int) y, (int) z);
    }

    public static WorldPosition decode(@NonNull String world, long packed) {
        Location location = LocationUtil.decode(world, packed);

        if (location == null) {
            return null;
        }

        return ofBlock(location);
    }

    @Override
    public @NotNull String toString() {
        return String.format("%s:%s:%s:%s", worldName, x, y, z);
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        WorldPosition that = (WorldPosition) o;
        return Double.compare(x, that.x) == 0 && Double.compare(y, that.y) == 0 && Double.compare(z, that.z) == 0 && Objects.equals(worldName, that.worldName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(worldName, x, y, z);
    }

}
