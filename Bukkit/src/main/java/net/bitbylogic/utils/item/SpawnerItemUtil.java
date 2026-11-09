package net.bitbylogic.utils.item;

import net.bitbylogic.utils.message.MessageUtil;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public final class SpawnerItemUtil {

    private static final NamespacedKey SPAWNER_KEY = new NamespacedKey("bitsutils", "bits_spawner");

    private SpawnerItemUtil() {

    }

    public static ItemStack getSpawner(@NotNull EntityType entityType, @Nullable String name) {
        ItemStack item = new ItemStack(Material.SPAWNER);
        ItemMeta meta = item.getItemMeta();

        if (name != null) {
            meta.displayName(MessageUtil.deserialize(name));
        }

        meta.getPersistentDataContainer().set(SPAWNER_KEY, PersistentDataType.STRING, entityType.name());

        item.setItemMeta(meta);
        return item;
    }

    public static ItemStack setSpawner(@NotNull ItemStack item, @NotNull EntityType entityType) {
        ItemMeta meta = item.getItemMeta();

        if (meta == null) {
            return item;
        }

        meta.getPersistentDataContainer().set(SPAWNER_KEY, PersistentDataType.STRING, entityType.name());

        item.setItemMeta(meta);
        return item;
    }

    public static boolean isSpawner(@NotNull ItemStack item) {
        ItemMeta meta = item.getItemMeta();

        if (meta == null) {
            return false;
        }

        return meta.getPersistentDataContainer().has(SPAWNER_KEY, PersistentDataType.STRING);
    }

    public static Optional<EntityType> getSpawnerEntity(@NotNull ItemStack item) {
        ItemMeta meta = item.getItemMeta();

        if (meta == null) {
            return Optional.empty();
        }

        String entityName = meta.getPersistentDataContainer().get(SPAWNER_KEY, PersistentDataType.STRING);

        if (entityName == null) {
            return Optional.empty();
        }

        try {
            return Optional.of(EntityType.valueOf(entityName));
        } catch (IllegalArgumentException e) {
            return Optional.empty();
        }
    }

}
