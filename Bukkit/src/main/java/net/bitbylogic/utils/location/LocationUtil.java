package net.bitbylogic.utils.location;

import lombok.NonNull;
import org.bukkit.*;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class LocationUtil {

    public static long encode(int x, int y, int z) {
        long lx = ((long)x & 0x3FFFFFFL);
        long lz = ((long)z & 0x3FFFFFFL);
        long ly = ((long)y & 0xFFF);

        return (lx << 38) | (lz << 12) | ly;
    }

    public static Location decode(@NonNull String worldName, long packed) {
        World world = Bukkit.getWorld(worldName);

        if (world == null) {
            return null;
        }

        int x = (int)((packed >> 38) & 0x3FFFFFFL);
        int z = (int)((packed >> 12) & 0x3FFFFFFL);
        int y = (int)(packed & 0xFFF);

        if (x >= 0x2000000) x -= 0x4000000;
        if (z >= 0x2000000) z -= 0x4000000;
        if (y >= 0x800) y -= 0x1000;

        return new Location(world, x, y, z);
    }

    public static long encodeChunk(@NonNull int chunkX, int chunkZ) {
        long lx = ((long)chunkX & 0xFFFFFFFFL);
        long lz = ((long)chunkZ & 0xFFFFFFFFL);

        return (lx << 32) | lz;
    }

    public static Chunk decodeChunk(@NonNull String worldName, long packed) {
        World world = Bukkit.getWorld(worldName);

        if (world == null) {
            return null;
        }

        int x = (int)(packed >> 32);
        int z = (int)(packed & 0xFFFFFFFFL);

        return world.getChunkAt(x, z);
    }

    /**
     * Convert a location to string.
     *
     * @param location The location being converted.
     * @return The converted string.
     */
    public static String locationToString(Location location) {
        return locationToString(location, ":");
    }

    public static String locationToStringWithYawPitch(Location location) {
        return locationToStringWithYawPitch(location, ":");
    }

    public static String locationToString(Location location, String separator) {
        return location.getWorld().getName() + separator +
                location.getBlockX() + separator +
                location.getBlockY() + separator +
                location.getBlockZ();
    }

    public static String locationToStringWithYawPitch(Location location, String separator) {
        return location.getWorld().getName() + separator +
                location.getBlockX() + separator +
                location.getBlockY() + separator +
                location.getBlockZ() + separator +
                location.getYaw() + separator +
                location.getPitch();
    }

    /**
     * Convert a string to a location.
     *
     * @param string The string to convert.
     * @return The converted location.
     */
    public static Location stringToLocation(String string) {
        return stringToLocation(string, ":");
    }

    public static Location stringToLocation(String string, String separator) {
        String[] splitArgs = string.split(separator);

        if (splitArgs.length == 4) {
            return new Location(Bukkit.getWorld(splitArgs[0]), Double.parseDouble(splitArgs[1]), Double.parseDouble(splitArgs[2]), Double.parseDouble(splitArgs[3]));
        } else {
            return new Location(Bukkit.getWorld(splitArgs[0]), Double.parseDouble(splitArgs[1]), Double.parseDouble(splitArgs[2]), Double.parseDouble(splitArgs[3]), Float.parseFloat(splitArgs[4]), Float.parseFloat(splitArgs[5]));
        }
    }

    public static boolean isLocationString(String string) {
        return isLocationString(string, ":");
    }

    public static boolean isLocationString(String string, String separator) {
        return string.split(separator).length >= 4;
    }

    public static boolean matches(Location location, Location other) {
        if (location.getWorld() == null || other.getWorld() == null) {
            return false;
        }

        if (!location.getWorld().getName().equalsIgnoreCase(other.getWorld().getName())) {
            return false;
        }

        return location.getX() == other.getX() && location.getY() == other.getY() && location.getZ() == other.getZ();
    }

    public static HashMap<Location, PersistentDataContainer> getAllPersistentData(Chunk chunk) {
        HashMap<Location, PersistentDataContainer> data = new HashMap<>();
        PersistentDataContainer dataContainer = chunk.getPersistentDataContainer();

        if (dataContainer.isEmpty()) {
            return data;
        }

        dataContainer.getKeys().forEach(key -> {
            if (!isLocationString(key.getKey(), "._.")) {
                return;
            }

            if (dataContainer.get(key, PersistentDataType.TAG_CONTAINER) == null) {
                return;
            }

            data.put(stringToLocation(key.getKey(), "._."), dataContainer.get(key, PersistentDataType.TAG_CONTAINER));
        });

        return data;
    }

    public static boolean hasPersistentData(JavaPlugin plugin, Location location) {
        return location.getChunk().getPersistentDataContainer().has(new NamespacedKey(plugin, locationToString(location, "._.")), PersistentDataType.TAG_CONTAINER);
    }

    public static PersistentDataContainer getPersistentData(JavaPlugin plugin, Location location, boolean create) {
        NamespacedKey locationKey = new NamespacedKey(plugin, locationToString(location, "._."));
        Chunk chunk = location.getChunk();

        if (!chunk.getPersistentDataContainer().has(locationKey, PersistentDataType.TAG_CONTAINER)) {
            if (!create) {
                return null;
            }

            chunk.getPersistentDataContainer().set(locationKey, PersistentDataType.TAG_CONTAINER,
                    chunk.getPersistentDataContainer().getAdapterContext().newPersistentDataContainer());
        }

        return chunk.getPersistentDataContainer().get(locationKey, PersistentDataType.TAG_CONTAINER);
    }

    public static void deletePersistentData(JavaPlugin plugin, Location location) {
        NamespacedKey locationKey = new NamespacedKey(plugin, locationToString(location, "._."));
        Chunk chunk = location.getChunk();

        if (!chunk.getPersistentDataContainer().has(locationKey, PersistentDataType.TAG_CONTAINER)) {
            return;
        }

        chunk.getPersistentDataContainer().remove(locationKey);
    }

    public static void savePersistentData(JavaPlugin plugin, Location location, PersistentDataContainer container) {
        location.getChunk().getPersistentDataContainer().set(new NamespacedKey(plugin, locationToString(location, "._.")), PersistentDataType.TAG_CONTAINER, container);
    }

    public static int getHighestBlockY(World world, int x, int z) {
        int currentY = world.getMaxHeight();
        int minY = world.getMinHeight();

        world.getChunkAt(x, z);

        for (int y = currentY; y > minY; y--) {
            if (world.getBlockAt(x, y, z).getType().isAir()) {
                continue;
            }

            return y;
        }

        return 0;
    }

    public static Set<Location> locationsBetweenTwoPoints(Location cornerA, Location cornerB) {
        Set<Location> locations = new HashSet<>();

        int topBlockX = Math.max(cornerA.getBlockX(), cornerB.getBlockX());
        int topBlockY = Math.max(cornerA.getBlockY(), cornerB.getBlockY());
        int topBlockZ = Math.max(cornerA.getBlockZ(), cornerB.getBlockZ());

        int bottomBlockX = Math.min(cornerA.getBlockX(), cornerB.getBlockX());
        int bottomBlockY = Math.min(cornerA.getBlockY(), cornerB.getBlockY());
        int bottomBlockZ = Math.min(cornerA.getBlockZ(), cornerB.getBlockZ());

        for (int x = bottomBlockX; x <= topBlockX; x++) {
            for (int z = bottomBlockZ; z <= topBlockZ; z++) {
                for (int y = bottomBlockY; y <= topBlockY; y++) {
                    locations.add(new Location(cornerA.getWorld(), x, y, z));
                }
            }
        }

        return locations;
    }

    public static boolean isInside(Location cornerA, Location cornerB, Location location) {
        double minX = Math.min(cornerA.getX(), cornerB.getX());
        double maxX = Math.max(cornerA.getX(), cornerB.getX());
        double minY = Math.min(cornerA.getY(), cornerB.getY());
        double maxY = Math.max(cornerA.getY(), cornerB.getY());
        double minZ = Math.min(cornerA.getZ(), cornerB.getZ());
        double maxZ = Math.max(cornerA.getZ(), cornerB.getZ());

        return location.getX() >= minX && location.getX() <= maxX &&
                location.getY() >= minY && location.getY() <= maxY &&
                location.getZ() >= minZ && location.getZ() <= maxZ;
    }

    public static Location toBlockLocation(@NonNull Location location) {
        Location blockLocation = location.clone();
        blockLocation.setX(location.getBlockX());
        blockLocation.setY(location.getBlockY());
        blockLocation.setZ(location.getBlockZ());
        return location;
    }

    public static double distance(@NonNull Location location, @NonNull Location other) {
        double dx = location.getX() - other.getX();
        double dy = location.getY() - other.getY();
        double dz = location.getZ() - other.getZ();

        return Math.sqrt(dx * dx + dy * dy + dz * dz);
    }

    public static Location getCenterBetween(@NonNull Location location, @NonNull Location other) {
        double centerX = (location.getX() + other.getX()) / 2.0;
        double centerY = (location.getY() + other.getY()) / 2.0;
        double centerZ = (location.getZ() + other.getZ()) / 2.0;

        return new Location(location.getWorld(), centerX, centerY, centerZ);
    }

}
