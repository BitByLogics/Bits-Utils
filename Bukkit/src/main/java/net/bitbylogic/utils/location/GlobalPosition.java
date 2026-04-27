package net.bitbylogic.utils.location;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.util.NumberConversions;
import org.jetbrains.annotations.NotNull;

public record GlobalPosition(double x, double y, double z) {

    public GlobalPosition toBlock() {
        return new GlobalPosition(NumberConversions.floor(x), NumberConversions.floor(y), NumberConversions.floor(z));
    }

    public WorldPosition toWorldPosition(@NotNull World world) {
        return new WorldPosition(world.getName(), x, y, z);
    }

    public Location toLocation(@NotNull World world) {
        return new Location(world, x, y, z);
    }

    public String toString() {
        return String.format("%s:%s:%s", x, y, z);
    }

    public static GlobalPosition fromString(@NotNull String string) {
        String[] data = string.split(":");
        return new GlobalPosition(Double.parseDouble(data[0]), Double.parseDouble(data[1]), Double.parseDouble(data[2]));
    }

}
