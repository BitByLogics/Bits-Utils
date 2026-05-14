package net.bitbylogic.utils.item;

import com.destroystokyo.paper.profile.PlayerProfile;
import lombok.NonNull;
import net.bitbylogic.utils.message.MessageUtil;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.profile.PlayerTextures;
import org.jetbrains.annotations.NotNull;

import java.net.MalformedURLException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;

public class ItemBuilder {

    private final @NotNull ItemStack item;

    private ItemBuilder(@NotNull ItemStack item) {
        this.item = item;
    }

    public static ItemBuilder of(@NotNull Material material) {
        return new ItemBuilder(new ItemStack(material));
    }

    public static ItemBuilder from(@NotNull ItemStack item) { return new ItemBuilder(item); }

    public ItemBuilder name(@NotNull String name) {
        ItemMeta stackMeta = item.getItemMeta();
        stackMeta.displayName(MessageUtil.deserialize(name));
        item.setItemMeta(stackMeta);
        return this;
    }

    public ItemBuilder amount(int amount) {
        item.setAmount(amount);
        return this;
    }

    public ItemBuilder lore(@NotNull Component... lore) {
        ItemMeta meta = item.getItemMeta();

        List<Component> newLore = new ArrayList<>(Arrays.asList(lore));

        meta.lore(newLore);
        this.item.setItemMeta(meta);
        return this;
    }

    public ItemBuilder lore(@NotNull String... lore) {
        ItemMeta meta = item.getItemMeta();
        List<Component> newLore = new ArrayList<>();

        for (String loreLine : lore) {
            newLore.add(MessageUtil.deserialize(loreLine));
        }

        meta.lore(newLore);
        this.item.setItemMeta(meta);
        return this;
    }

    public ItemBuilder lore(@NotNull List<String> lore) {
        for (String part : lore) {
            lore(part);
        }

        return this;
    }

    @Deprecated
    public ItemBuilder durability(short durability) {
        item.setDurability(durability);
        return this;
    }

    @Deprecated
    public ItemBuilder modelData(int modelData) {
        ItemMeta meta = item.getItemMeta();
        meta.setCustomModelData(modelData);
        item.setItemMeta(meta);
        return this;
    }

    public ItemBuilder itemModel(@NotNull NamespacedKey modelKey) {
        ItemMeta meta = item.getItemMeta();
        meta.setItemModel(modelKey);
        item.setItemMeta(meta);
        return this;
    }

    public ItemBuilder removeAttributes() {
        ItemMeta meta = item.getItemMeta();
        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_STORED_ENCHANTS);
        item.setItemMeta(meta);
        return this;
    }

    public ItemBuilder unbreakable() {
        ItemMeta meta = item.getItemMeta();
        meta.setUnbreakable(true);
        item.setItemMeta(meta);
        return this;
    }

    public ItemBuilder hideToolTip() {
        ItemMeta meta = item.getItemMeta();
        meta.setHideTooltip(true);
        item.setItemMeta(meta);
        return this;
    }

    public ItemBuilder flags(@NotNull ItemFlag... flags) {
        ItemMeta meta = item.getItemMeta();
        meta.addItemFlags(flags);
        item.setItemMeta(meta);
        return this;
    }

    @Deprecated
    public ItemBuilder skullName(@NonNull String skullName) {
        ItemMeta meta = item.getItemMeta();

        if(!(meta instanceof SkullMeta skullMeta)) {
            return this;
        }

        skullMeta.setOwner(skullName);
        item.setItemMeta(skullMeta);
        return this;
    }

    public ItemBuilder skullURL(@NonNull String skullURL) {
        ItemMeta meta = item.getItemMeta();

        if(!(meta instanceof SkullMeta skullMeta)) {
            return this;
        }

        PlayerProfile skullProfile = Bukkit.createProfile(UUID.randomUUID());
        PlayerTextures textures = skullProfile.getTextures();
        textures.clear();

        try {
            textures.setSkin(URI.create(skullURL).toURL());
        } catch (MalformedURLException e) {
            Bukkit.getLogger().log(Level.WARNING, "Invalid skull URL: " + skullURL);
        }

        skullProfile.setTextures(textures);
        skullMeta.setPlayerProfile(skullProfile);
        item.setItemMeta(skullMeta);
        return this;
    }

    public <T, Z> ItemBuilder addPersistentData(@NotNull NamespacedKey key, @NotNull PersistentDataType<T, Z> type, @NotNull Z value) {
        ItemMeta meta = item.getItemMeta();

        if(meta == null) {
            return this;
        }

        meta.getPersistentDataContainer().set(key, type, value);
        item.setItemMeta(meta);
        return this;
    }

    public ItemBuilder spawner(@NotNull EntityType entityType) {
        ItemMeta meta = item.getItemMeta();
        meta.getPersistentDataContainer().set(new NamespacedKey("bitsutils", "bits_spawner"), PersistentDataType.STRING, entityType.name());
        item.setItemMeta(meta);
        return this;
    }

    public ItemStack build() {
        return item;
    }

}