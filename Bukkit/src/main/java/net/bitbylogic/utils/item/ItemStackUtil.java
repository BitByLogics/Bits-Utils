package net.bitbylogic.utils.item;

import com.google.common.collect.Lists;
import net.bitbylogic.utils.message.MessageUtil;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Tag;
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
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Function;

public class ItemStackUtil {

    private static final ItemStackConfigSerializer CONFIG_PARSER = new ItemStackConfigSerializer();
    
    public static ItemStack getFromConfig(@NotNull ConfigurationSection section, TagResolver.Single... modifiers) {
        Optional<ItemStack> optionalItem = CONFIG_PARSER.deserialize(section, modifiers);

        if (optionalItem.isEmpty()) {
            return new ItemStack(Material.OAK_LOG);
        }

        ItemStack item = optionalItem.get();
        updateItem(item, modifiers);

        return item;
    }

    public static void saveToConfig(@NotNull ConfigurationSection section, @NotNull ItemStack item) {
        CONFIG_PARSER.serialize(section, item);
    }

    public static Component getTranslatedName(@NotNull Material material) {
        return Component.translatable(material.translationKey());
    }

    public static void updateItem(@NotNull ItemStack item, TagResolver.Single... modifiers) {
        if (!item.hasItemMeta() || item.getItemMeta() == null) {
            return;
        }

        ItemMeta meta = item.getItemMeta();

        setName(meta, component -> MessageUtil.deserialize(MessageUtil.serialize(component), modifiers));

        if (meta.hasLore() && meta.getLore() != null) {
            updateLore(meta, components -> {
                List<Component> updated = new ArrayList<>(components.size());
                components.forEach(component -> updated.add(MessageUtil.deserialize(MessageUtil.serialize(component), modifiers)));
                return updated;
            });
        }

        item.setItemMeta(meta);
    }

    public static void updateItem(@NotNull ItemStack item, @NotNull Map<String, Component> placeholders) {
        if (!item.hasItemMeta() || item.getItemMeta() == null) {
            return;
        }

        ItemMeta meta = item.getItemMeta();

        updateName(meta, placeholders);

        if (meta.hasLore() && meta.getLore() != null) {
            List<Component> lore = meta.lore();

            for (Map.Entry<String, Component> entry : placeholders.entrySet()) {
                lore.replaceAll(component -> component.replaceText(b -> b.matchLiteral(entry.getKey()).replacement(entry.getValue())).compact().style(style -> style.decoration(TextDecoration.ITALIC, false)));
            }

            meta.lore(lore);
        }

        item.setItemMeta(meta);
    }

    public static void setName(@NotNull ItemMeta meta, @NotNull Function<Component, Component> componentUpdater) {
        if (!meta.hasDisplayName()) {
            return;
        }

        meta.displayName(componentUpdater.apply(meta.displayName()).compact().style(style -> style.decoration(TextDecoration.ITALIC, false)));
    }

    public static void updateName(@NotNull ItemMeta meta, @NotNull Map<String, Component> placeholders) {
        Component displayName = meta.displayName();

        if (displayName == null) {
            return;
        }

        for (Map.Entry<String, Component> entry : placeholders.entrySet()) {
            displayName = displayName.replaceText(b -> b
                    .matchLiteral(entry.getKey())
                    .replacement(entry.getValue())
            );
        }

        meta.displayName(displayName.compact().style(style -> style.decoration(TextDecoration.ITALIC, false)));
    }

    public static void setLore(@NotNull ItemMeta meta, @NotNull List<Component> lore) {
        lore.replaceAll(component -> component.compact().style(style -> style.decoration(TextDecoration.ITALIC, false)));

        meta.lore(lore);
    }

    public static void updateLore(@NotNull ItemMeta meta, @NotNull Map<String, Component> placeholders) {
        List<Component> lore = meta.lore();

        if (lore == null) {
            return;
        }

        for (Map.Entry<String, Component> entry : placeholders.entrySet()) {
            lore.replaceAll(component -> component.replaceText(b -> b.matchLiteral(entry.getKey()).replacement(entry.getValue())).compact().style(style -> style.decoration(TextDecoration.ITALIC, false)));
        }

        meta.lore(lore);
    }

    public static void updateLore(@NotNull ItemMeta meta, @NotNull Function<List<Component>, List<Component>> componentUpdater) {
        if (!meta.hasLore()) {
            return;
        }

        List<Component> lore = componentUpdater.apply(meta.lore());
        lore.replaceAll(component -> component.compact().style(style -> style.decoration(TextDecoration.ITALIC, false)));

        meta.lore(lore);
    }

    public static void mergeLore(ItemStack main, ItemStack... otherStacks) {
        if (main.getItemMeta() == null) {
            return;
        }

        ItemMeta meta = main.getItemMeta();
        List<Component> mainLore = meta.hasLore() ? meta.lore() : Lists.newArrayList();

        for (ItemStack otherItem : otherStacks) {
            if (otherItem.getItemMeta() == null || otherItem.getItemMeta().lore() == null) {
                continue;
            }

            mainLore.addAll(otherItem.getItemMeta().lore());
        }

        meta.lore(mainLore);
        main.setItemMeta(meta);
    }

    public static List<Component> getMergedLore(ItemStack... items) {
        List<Component> lore = Lists.newArrayList();

        for (ItemStack item : items) {
            if (item.getItemMeta() == null || item.getItemMeta().lore() == null) {
                continue;
            }

            if (!item.getItemMeta().hasLore()) {
                continue;
            }

            lore.addAll(item.getItemMeta().lore());
        }

        return lore;
    }

    public static boolean isSimilar(ItemStack item, ItemStack otherItem, boolean compareFlags, boolean compareName, boolean compareLore) {
        if (item == null || otherItem == null) return false;
        if (item.getType() != otherItem.getType()) return false;

        ItemMeta meta = item.getItemMeta();
        ItemMeta otherMeta = otherItem.getItemMeta();

        // Compare flags
        if (compareFlags) {
            Set<ItemFlag> flags = (meta == null ? Collections.emptySet() : meta.getItemFlags());
            Set<ItemFlag> otherFlags = (otherMeta == null ? Collections.emptySet() : otherMeta.getItemFlags());

            if (!flags.equals(otherFlags)) {
                return false;
            }
        }

        // Compare display name
        if (compareName) {
            String name = (meta != null && meta.hasDisplayName()) ? meta.getDisplayName() : null;
            String otherName = (otherMeta != null && otherMeta.hasDisplayName()) ? otherMeta.getDisplayName() : null;

            if (!Objects.equals(name, otherName)) {
                return false;
            }
        }

        // Compare lore
        if (compareLore) {
            List<String> lore = (meta != null && meta.hasLore()) ? meta.getLore() : null;
            List<String> otherLore = (otherMeta != null && otherMeta.hasLore()) ? otherMeta.getLore() : null;
            return Objects.equals(lore, otherLore);
        }

        return true;
    }

    public static boolean flagsMatch(ItemStack item, ItemStack otherItem) {
        if (item.getItemMeta() == null && otherItem.getItemMeta() == null) {
            return true;
        }

        Set<ItemFlag> itemFlags = item.getItemMeta().getItemFlags();
        Set<ItemFlag> otherItemFlags = otherItem.getItemMeta().getItemFlags();

        return itemFlags.equals(otherItemFlags);
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

        return itemLore.equals(otherItemLore);
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

    public static ItemStack getSpawner(@NotNull EntityType entityType, @Nullable String name) {
        ItemStack item = new ItemStack(Material.SPAWNER);
        ItemMeta meta = item.getItemMeta();

        if (name != null) {
            meta.displayName(MessageUtil.deserialize(name));
        }

        meta.getPersistentDataContainer().set(new NamespacedKey("bitsutils", "bits_spawner"), PersistentDataType.STRING, entityType.name());

        item.setItemMeta(meta);
        return item;
    }

    public static ItemStack setSpawner(@NotNull ItemStack item, @NotNull EntityType entityType) {
        ItemMeta meta = item.getItemMeta();
        meta.getPersistentDataContainer().set(new NamespacedKey("bitsutils", "bits_spawner"), PersistentDataType.STRING, entityType.name());

        item.setItemMeta(meta);
        return item;
    }

    public static boolean isSpawner(@NotNull ItemStack item) {
        return item.getItemMeta().getPersistentDataContainer().has(new NamespacedKey("bitsutils", "bits_spawner"), PersistentDataType.STRING);
    }

    public static EntityType getSpawnerEntity(@NotNull ItemStack item) {
        if (!isSpawner(item)) {
            return null;
        }

        return EntityType.valueOf(item.getItemMeta().getPersistentDataContainer().get(new NamespacedKey("bitsutils", "bits_spawner"), PersistentDataType.STRING));
    }

    public static <P, C> void addPersistentData(@NotNull ItemStack item, @NotNull NamespacedKey key, @NotNull PersistentDataType<P, C> dataType, C value) {
        ItemMeta meta = item.getItemMeta();

        if (meta == null) {
            return;
        }

        meta.getPersistentDataContainer().set(key, dataType, value);
        item.setItemMeta(meta);
    }

    public static boolean hasPersistentData(ItemStack itemStack, String key) {
        return itemStack.getItemMeta().getPersistentDataContainer().getKeys().stream().anyMatch(pKey -> pKey.getKey().equalsIgnoreCase(key));
    }

    public static boolean hasPersistentData(@NotNull ItemStack item, @NotNull NamespacedKey key) {
        ItemMeta meta = item.getItemMeta();

        if (meta == null) {
            return false;
        }

        return meta.getPersistentDataContainer().has(key);
    }

    public static <P, C> Optional<C> getPersistentData(@NotNull ItemStack item, @NotNull NamespacedKey key, @NotNull PersistentDataType<P, C> dataType) {
        ItemMeta meta = item.getItemMeta();

        if (meta == null || !hasPersistentData(item, key)) {
            return Optional.empty();
        }

        return Optional.ofNullable(meta.getPersistentDataContainer().get(key, dataType));
    }

    public static <T, Z> boolean persistentDataMatches(ItemStack itemStack, PersistentDataType<T, Z> type, Z value) {
        PersistentDataContainer dataContainer = itemStack.getItemMeta().getPersistentDataContainer();
        return dataContainer.getKeys().stream().filter(pKey -> dataContainer.has(pKey, type)).anyMatch(pKey -> dataContainer.get(pKey, type) == value);
    }

    @Deprecated
    public static void setSkullOwner(@NotNull ItemStack stack, @NotNull String owner) {
        if (stack.getType() != Material.PLAYER_HEAD) {
            return;
        }

        SkullMeta skullMeta = (SkullMeta) stack.getItemMeta();
        skullMeta.setOwner(owner);
        stack.setItemMeta(skullMeta);
    }

    public static boolean isTagged(@NotNull ItemStack itemStack, @NotNull Tag<Material> tag) {
        return tag.isTagged(itemStack.getType());
    }

}
