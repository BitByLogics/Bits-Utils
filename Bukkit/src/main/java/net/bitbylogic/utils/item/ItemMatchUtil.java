package net.bitbylogic.utils.item;

import org.bukkit.Material;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BlockStateMeta;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Set;

public final class ItemMatchUtil {

    private ItemMatchUtil() {

    }

    public static boolean isSimilar(ItemStack item, ItemStack otherItem, boolean compareFlags, boolean compareName, boolean compareLore) {
        if (item == null || otherItem == null) {
            return false;
        }

        if (item.getType() != otherItem.getType()) {
            return false;
        }

        ItemMeta meta = item.getItemMeta();
        ItemMeta otherMeta = otherItem.getItemMeta();

        if (compareFlags && !flagsMatch(item, otherItem)) {
            return false;
        }

        if (compareName) {
            String name = null;
            String otherName = null;

            if (meta != null && meta.hasDisplayName()) {
                name = meta.getDisplayName();
            }

            if (otherMeta != null && otherMeta.hasDisplayName()) {
                otherName = otherMeta.getDisplayName();
            }

            if (!Objects.equals(name, otherName)) {
                return false;
            }
        }

        if (compareLore && !loreMatches(item, otherItem)) {
            return false;
        }

        return dataMatches(meta, otherMeta);
    }

    public static boolean flagsMatch(ItemStack item, ItemStack otherItem) {
        ItemMeta meta = item.getItemMeta();
        ItemMeta otherMeta = otherItem.getItemMeta();

        Set<ItemFlag> itemFlags = meta == null ? Collections.emptySet() : meta.getItemFlags();
        Set<ItemFlag> otherItemFlags = otherMeta == null ? Collections.emptySet() : otherMeta.getItemFlags();

        return itemFlags.equals(otherItemFlags);
    }

    public static boolean loreMatches(ItemStack item, ItemStack otherItem) {
        if (item.getItemMeta() == null && otherItem.getItemMeta() == null) {
            return true;
        }

        if (item.getItemMeta() == null || otherItem.getItemMeta() == null) {
            return false;
        }

        if (item.getItemMeta().hasLore() != otherItem.getItemMeta().hasLore()) {
            return false;
        }

        List<String> itemLore = item.getItemMeta().getLore();
        List<String> otherItemLore = otherItem.getItemMeta().getLore();

        if (itemLore == null && otherItemLore == null) {
            return true;
        }

        if (itemLore == null || otherItemLore == null) {
            return false;
        }

        return itemLore.equals(otherItemLore);
    }

    public static boolean spawnerMatches(ItemStack item, ItemStack otherItem) {
        if (item.getType() != Material.SPAWNER || otherItem.getType() != Material.SPAWNER) {
            return false;
        }

        BlockStateMeta meta = (BlockStateMeta) item.getItemMeta();
        BlockStateMeta otherMeta = (BlockStateMeta) otherItem.getItemMeta();

        if (meta == null || otherMeta == null) {
            return false;
        }

        return ((CreatureSpawner) meta.getBlockState()).getSpawnedType() == ((CreatureSpawner) otherMeta.getBlockState()).getSpawnedType();
    }

    private static boolean dataMatches(ItemMeta meta, ItemMeta otherMeta) {
        if (meta == null || otherMeta == null) {
            return meta == otherMeta;
        }

        if (!meta.getEnchants().equals(otherMeta.getEnchants())) {
            return false;
        }

        if (meta.hasCustomModelData() != otherMeta.hasCustomModelData()) {
            return false;
        }

        if (meta.hasCustomModelData() && meta.getCustomModelData() != otherMeta.getCustomModelData()) {
            return false;
        }

        if (!meta.getPersistentDataContainer().equals(otherMeta.getPersistentDataContainer())) {
            return false;
        }

        if (meta instanceof Damageable damageable && otherMeta instanceof Damageable otherDamageable) {
            return damageable.getDamage() == otherDamageable.getDamage();
        }

        return true;
    }

}
