package net.bitbylogic.utils.item;

import com.google.common.collect.Lists;
import lombok.NonNull;
import net.bitbylogic.utils.StringModifier;
import net.bitbylogic.utils.StringUtil;
import net.bitbylogic.utils.message.format.Formatter;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BlockStateMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public class ItemStackUtil {

    private static final ItemStackConfigParser CONFIG_PARSER = new ItemStackConfigParser();

    /**
     * Create an ItemStack object from a configuration
     * section.
     *
     * @param section   The configuration section.
     * @param modifiers Modifiers to replace in the name/lore.
     * @return New ItemStack instance.
     */
    public static ItemStack getFromConfig(@NonNull ConfigurationSection section, StringModifier... modifiers) {
        Optional<ItemStack> optionalItem = CONFIG_PARSER.parseFrom(section);

        if(optionalItem.isEmpty()) {
            return new ItemStack(Material.OAK_LOG);
        }

        ItemStack item = optionalItem.get();
        updateItem(item, modifiers);

        return item;
    }

    public static void saveToConfig(@NonNull ConfigurationSection section, @NonNull ItemStack item) {
        CONFIG_PARSER.parseTo(section, item);
    }

    /**
     * Get an ItemStack's vanilla name.
     *
     * @param item The ItemStack whose vanilla name to retrieve.
     * @return ItemStack's Vanilla Name.
     */
    public static String getVanillaName(ItemStack item) {
        return Formatter.format("&f" + StringUtil.capitalize(item.getType().name().replace("_", " ")));
    }

    /**
     * Update an ItemStack's name & lore with color
     * codes & the provided placeholders.
     *
     * @param item      The ItemStack to update.
     * @param modifiers The placeholders to replace.
     */
    public static void updateItem(ItemStack item, StringModifier... modifiers) {
        if (item == null || !item.hasItemMeta() || item.getItemMeta() == null) {
            return;
        }

        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(Formatter.format(meta.getDisplayName(), modifiers));

        if (meta.hasLore() && meta.getLore() != null) {
            List<String> lore = meta.getLore();
            List<String> updatedLore = Lists.newArrayList();

            lore.forEach(string -> updatedLore.add(Formatter.format(string, modifiers)));
            meta.setLore(updatedLore);
        }

        item.setItemMeta(meta);
    }

    /**
     * Merge ItemStack's lore into a main
     * ItemStack's lore.
     *
     * @param main        The main ItemStack.
     * @param otherStacks The other ItemStacks.
     */
    public static void mergeLore(ItemStack main, ItemStack... otherStacks) {
        if (main.getItemMeta() == null) {
            return;
        }

        ItemMeta meta = main.getItemMeta();
        List<String> mainLore = meta.hasLore() ? meta.getLore() : Lists.newArrayList();

        for (ItemStack otherItem : otherStacks) {
            if (otherItem.getItemMeta() == null || otherItem.getItemMeta().getLore() == null) {
                continue;
            }

            if (!otherItem.getItemMeta().hasLore()) {
                continue;
            }

            mainLore.addAll(otherItem.getItemMeta().getLore());
        }

        meta.setLore(mainLore);
        main.setItemMeta(meta);
    }

    /**
     * Merge all ItemStack lore into
     * a single list.
     *
     * @param items The ItemStacks.
     * @return A compiled lore list.
     */
    public static List<String> getMergedLore(ItemStack... items) {
        List<String> lore = Lists.newArrayList();

        for (ItemStack item : items) {
            if (item.getItemMeta() == null || item.getItemMeta().getLore() == null) {
                continue;
            }

            if (!item.getItemMeta().hasLore()) {
                continue;
            }

            lore.addAll(item.getItemMeta().getLore());
        }

        return lore;
    }

    /**
     * Check whether two ItemStacks are similar.
     *
     * @param item         ItemStack to compare.
     * @param otherItem    The ItemStack to compare it to.
     * @param compareFlags Whether to compare the ItemStack's flags.
     * @param compareName  Whether to compare the ItemStack's names.
     * @param compareLore  Whether to compare the ItemStack's lore.
     * @return Whether the ItemStacks are similar.
     */
    public static boolean isSimilar(ItemStack item, ItemStack otherItem, boolean compareFlags, boolean compareName, boolean compareLore) {
        if (item == null || otherItem == null) {
            return false;
        }

        if (item.getType() != otherItem.getType()) {
            return false;
        }

        ItemMeta mainMeta = item.getItemMeta();
        ItemMeta otherMeta = otherItem.getItemMeta();

        if (compareFlags) {
            if (!flagsMatch(item, otherItem)) {
                return false;
            }
        }

        if (compareName && (mainMeta != null && otherMeta != null)) {
            if (!mainMeta.getDisplayName().equalsIgnoreCase(otherMeta.getDisplayName())) {
                return false;
            }
        }

        if (compareLore && (mainMeta != null && otherMeta != null)) {
            return loreMatches(item, otherItem);
        }

        return true;
    }

    /**
     * Check whether two ItemStack's flags match.
     *
     * @param item      ItemStack to compare.
     * @param otherItem The ItemStack to compare it to.
     * @return Whether the flags match.
     */
    public static boolean flagsMatch(ItemStack item, ItemStack otherItem) {
        if (item.getItemMeta() == null && otherItem.getItemMeta() == null) {
            return true;
        }

        Set<ItemFlag> itemFlags = item.getItemMeta().getItemFlags();
        Set<ItemFlag> otherItemFlags = otherItem.getItemMeta().getItemFlags();

        for (ItemFlag itemFlag : itemFlags) {
            if (!otherItemFlags.contains(itemFlag)) {
                return false;
            }
        }

        for (ItemFlag itemFlag : otherItemFlags) {
            if (!itemFlags.contains(itemFlag)) {
                return false;
            }
        }

        return true;
    }

    /**
     * Check whether two ItemStack's lore matches.
     *
     * @param item      ItemStack to compare.
     * @param otherItem The ItemStack to compare it to.
     * @return Whether the lore matches.
     */
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

        for (String loreItem : itemLore) {
            if (!otherItemLore.contains(loreItem)) {
                return false;
            }
        }

        for (String loreItem : otherItemLore) {
            if (!itemLore.contains(loreItem)) {
                return false;
            }
        }

        return true;
    }

    /**
     * Check whether a spawner item's spawn
     * type matches another spawner's type.
     *
     * @param item      ItemStack to compare.
     * @param otherItem The ItemStack to compare it to.
     * @return Whether the spawn type matches.
     */
    public static boolean spawnerMatches(ItemStack item, ItemStack otherItem) {
        if (item.getType() != Material.SPAWNER || otherItem.getType() != Material.SPAWNER) {
            return false;
        }

        BlockStateMeta meta = (BlockStateMeta) item.getItemMeta();
        BlockStateMeta otherMeta = (BlockStateMeta) otherItem.getItemMeta();

        if (meta == null || otherMeta == null) {
            return false;
        }

        return ((CreatureSpawner) meta.getBlockState()).getSpawnedType() != ((CreatureSpawner) otherMeta.getBlockState()).getSpawnedType();
    }

    public static ItemStack getSpawner(JavaPlugin plugin, EntityType entityType, String name) {
        ItemStack item = new ItemStack(Material.SPAWNER);
        ItemMeta meta = item.getItemMeta();

        if (name != null) {
            meta.setDisplayName(Formatter.format(name));
        }

        meta.getPersistentDataContainer().set(new NamespacedKey(plugin, "bits_spawner"), PersistentDataType.STRING, entityType.name());

        item.setItemMeta(meta);
        return item;
    }

    public static ItemStack setSpawner(JavaPlugin plugin, ItemStack item, EntityType entityType) {
        ItemMeta meta = item.getItemMeta();
        meta.getPersistentDataContainer().set(new NamespacedKey(plugin, "bits_spawner"), PersistentDataType.STRING, entityType.name());

        item.setItemMeta(meta);
        return item;
    }

    public static boolean isSpawner(JavaPlugin plugin, ItemStack item) {
        return item.getItemMeta().getPersistentDataContainer().has(new NamespacedKey(plugin, "bits_spawner"), PersistentDataType.STRING);
    }

    public static EntityType getSpawnerEntity(JavaPlugin plugin, ItemStack item) {
        if (!isSpawner(plugin, item)) {
            return null;
        }

        return EntityType.valueOf(item.getItemMeta().getPersistentDataContainer().get(new NamespacedKey(plugin, "bits_spawner"), PersistentDataType.STRING));
    }

    public static <P, C> void addPersistentData(@NonNull ItemStack item, @NonNull NamespacedKey key, @NonNull PersistentDataType<P, C> dataType, C value) {
        ItemMeta meta = item.getItemMeta();

        if(meta == null) {
            return;
        }

        meta.getPersistentDataContainer().set(key, dataType, value);
        item.setItemMeta(meta);
    }

    public static boolean hasPersistentData(ItemStack itemStack, String key) {
        return itemStack.getItemMeta().getPersistentDataContainer().getKeys().stream().anyMatch(pKey -> pKey.getKey().equalsIgnoreCase(key));
    }

    public static boolean hasPersistentData(@NonNull ItemStack item, @NonNull NamespacedKey key) {
        ItemMeta meta = item.getItemMeta();

        if(meta == null) {
            return false;
        }

        return meta.getPersistentDataContainer().has(key);
    }

    public static <P, C> Optional<C> getPersistentData(@NonNull ItemStack item, @NonNull NamespacedKey key, @NonNull PersistentDataType<P, C> dataType) {
        ItemMeta meta = item.getItemMeta();

        if(meta == null || !hasPersistentData(item, key)) {
            return Optional.empty();
        }

        return Optional.ofNullable(meta.getPersistentDataContainer().get(key, dataType));
    }

    public static <T, Z> boolean persistentDataMatches(ItemStack itemStack, PersistentDataType<T, Z> type, Z value) {
        PersistentDataContainer dataContainer = itemStack.getItemMeta().getPersistentDataContainer();
        return dataContainer.getKeys().stream().filter(pKey -> dataContainer.has(pKey, type)).anyMatch(pKey -> dataContainer.get(pKey, type) == value);
    }

    public static void setSkullOwner(ItemStack stack, String owner) {
        if (stack.getType() != Material.PLAYER_HEAD) {
            return;
        }

        SkullMeta skullMeta = (SkullMeta) stack.getItemMeta();
        skullMeta.setOwner(owner);
        stack.setItemMeta(skullMeta);
    }

    public static boolean isSword(@NonNull ItemStack item) {
        return item.getType().name().endsWith("_SWORD");
    }

    public static boolean isAxe(@NonNull ItemStack item) {
        return item.getType().name().endsWith("_AXE");
    }

    public static boolean isPickaxe(@NonNull ItemStack item) {
        return item.getType().name().endsWith("_PICKAXE");
    }

    public static boolean isHoe(@NonNull ItemStack item) {
        return item.getType().name().endsWith("_HOE");
    }

    public static boolean isShovel(@NonNull ItemStack item) {
        return item.getType().name().endsWith("_SHOVEL");
    }

}
