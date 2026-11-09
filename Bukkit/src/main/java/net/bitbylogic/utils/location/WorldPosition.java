package net.bitbylogic.utils.location;

import lombok.NonNull;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;

public record WorldPosition(@NonNull String worldName, double x, double y, double z) implements Serializable {

    public static WorldPosition of(@NonNull Location location) {
        return new WorldPosition(location.getWorld().getName(), location.getX(), location.getY(), location.getZ());
    }

    public static WorldPosition ofBlock(@NonNull Location location) {
        return new WorldPosition(location.getWorld().getName(), location.getBlockX(), location.getBlockY(), location.getBlockZ());
    }

    public static Optional<WorldPosition> parse(@Nullable String string) {
        if (string == null) {
            return Optional.empty();
        }

        String[] split = string.split(":");

        if (split.length != 4) {
            return Optional.empty();
        }

        try {
            return Optional.of(new WorldPosition(split[0], Double.parseDouble(split[1]), Double.parseDouble(split[2]), Double.parseDouble(split[3])));
        } catch (NumberFormatException e) {
            return Optional.empty();
        }
    }

    private static int floor(double value) {
        return (int) Math.floor(value);
    }

    public int getChunkX() {
        return floor(x) >> 4;
    }

    public int getChunkZ() {
        return floor(z) >> 4;
    }

    public WorldPosition toWorldBlockPosition() {
        return new WorldPosition(worldName, floor(x), floor(y), floor(z));
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
            return worldName.equals(location.getWorld().getName()) && floor(x) == location.getBlockX() && floor(y) == location.getBlockY() && floor(z) == location.getBlockZ();
        }

        return worldName.equals(location.getWorld().getName()) && x == location.getX() && y == location.getY() && z == location.getZ();
    }

    public boolean matches(@NonNull WorldPosition position) {
        return matches(position, true);
    }

    public boolean matches(@NonNull WorldPosition position, boolean strict) {
        if(!strict) {
            return worldName.equals(position.worldName()) && floor(x) == floor(position.x()) && floor(y) == floor(position.y()) && floor(z) == floor(position.z());
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
        return LocationUtil.encode(floor(x), floor(y), floor(z));
    }

    public static WorldPosition decode(@NonNull String worldName, long packed) {
        return new WorldPosition(worldName, LocationUtil.unpackX(packed), LocationUtil.unpackY(packed), LocationUtil.unpackZ(packed));
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
