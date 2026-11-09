package net.bitbylogic.utils.item;

import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.Consumable;
import lombok.NonNull;
import net.bitbylogic.utils.EnumUtil;
import net.bitbylogic.utils.NumberUtil;
import net.bitbylogic.utils.color.ColorUtil;
import net.bitbylogic.utils.config.ConfigSerializer;
import net.bitbylogic.utils.message.MessageUtil;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Sound;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.inventory.meta.components.EquippableComponent;
import org.bukkit.inventory.meta.components.FoodComponent;
import org.bukkit.inventory.meta.components.ToolComponent;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionType;
import org.bukkit.profile.PlayerProfile;
import org.bukkit.profile.PlayerTextures;

import java.net.MalformedURLException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;

public class ItemStackConfigSerializer implements ConfigSerializer<ItemStack> {

    @Override
    public Optional<ItemStack> deserialize(@NonNull ConfigurationSection section, @NonNull TagResolver.Single... placeholders) {
        String materialName = section.getString("Material", "BARRIER");
        Material material = Material.matchMaterial(materialName);

        if (material == null || !material.isItem()) {
            warn("Invalid material '" + materialName + "'.");
            return Optional.empty();
        }

        ItemStack stack = new ItemStack(material, section.getInt("Amount", 1));
        ItemMeta meta = stack.getItemMeta();

        if (meta == null) {
            return Optional.of(stack);
        }

        applyDisplay(section, meta, placeholders);
        applyBasics(section, stack, meta);
        applyDye(section, stack, meta);
        applyPotion(section, stack, meta);
        applySkull(section, meta);
        applyModels(section, meta);
        applyEquippable(section, meta);
        applyFood(section, meta);
        applyTool(section, meta);
        applyEnchantments(section, meta);
        applyFlags(section, meta);

        stack.setItemMeta(meta);

        applyConsumable(section, stack);

        return Optional.of(stack);
    }

    private void applyDisplay(ConfigurationSection section, ItemMeta meta, TagResolver.Single[] placeholders) {
        String name = section.getString("Name");

        if (name != null) {
            meta.displayName(withoutItalic(MessageUtil.deserialize(name, placeholders)));
        }

        List<Component> lore = new ArrayList<>();

        for (String line : section.getStringList("Lore")) {
            lore.add(withoutItalic(MessageUtil.deserialize(line, placeholders)));
        }

        meta.lore(lore);
    }

    private void applyBasics(ConfigurationSection section, ItemStack stack, ItemMeta meta) {
        meta.setMaxStackSize(section.getInt("Max-Stack-Size", stack.getType().getMaxStackSize()));

        for (String data : section.getStringList("Custom-Data")) {
            String[] splitData = data.split(":", 3);

            if (splitData.length != 3) {
                warn("Skipped invalid custom data '" + data + "'.");
                continue;
            }

            NamespacedKey key = parseKey(splitData[0] + ":" + splitData[1]);

            if (key == null) {
                warn("Skipped invalid custom data key '" + data + "'.");
                continue;
            }

            meta.getPersistentDataContainer().set(key, PersistentDataType.STRING, splitData[2]);
        }

        if (section.getBoolean("Glow")) {
            meta.addEnchant(Enchantment.UNBREAKING, 37, true);
            meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        }

        if (section.getBoolean("Hide-Tooltip")) {
            meta.setHideTooltip(true);
        }

        if (section.getBoolean("Unbreakable")) {
            meta.setUnbreakable(true);
        }

        if (section.isSet("Glider")) {
            meta.setGlider(section.getBoolean("Glider"));
        }
    }

    private void applyDye(ConfigurationSection section, ItemStack stack, ItemMeta meta) {
        String dye = section.getString("Dye-Color");

        if (dye == null || !(meta instanceof LeatherArmorMeta leatherArmorMeta)) {
            return;
        }

        try {
            java.awt.Color color = ChatColor.of(dye).getColor();
            leatherArmorMeta.setColor(Color.fromRGB(color.getRed(), color.getGreen(), color.getBlue()));
        } catch (IllegalArgumentException e) {
            warn("Invalid dye color '" + dye + "'.");
        }
    }

    private void applyPotion(ConfigurationSection section, ItemStack stack, ItemMeta meta) {
        if (!(meta instanceof PotionMeta potionMeta)) {
            return;
        }

        Material type = stack.getType();

        if (type == Material.TIPPED_ARROW) {
            PotionType arrowType = parsePotionType(section.getString("Arrow-Type", "POISON"));

            if (arrowType != null) {
                potionMeta.setBasePotionType(arrowType);
            }

            return;
        }

        if (type != Material.SPLASH_POTION && type != Material.POTION) {
            return;
        }

        ConfigurationSection potionSection = section.getConfigurationSection("Potion-Data");

        if (potionSection == null) {
            return;
        }

        if (potionSection.isSet("Color")) {
            ColorUtil.parseHex(potionSection.getString("Color")).ifPresent(potionMeta::setColor);
        }

        String potionType = potionSection.getString("Type", "POISON");

        if (potionSection.getBoolean("Vanilla", false)) {
            PotionType base = parsePotionType(potionType);

            if (base != null) {
                potionMeta.setBasePotionType(base);
            }

            return;
        }

        PotionEffectType effectType = PotionEffectType.getByName(potionType);

        if (effectType == null) {
            warn("Invalid potion effect '" + potionType + "'.");
            return;
        }

        potionMeta.addCustomEffect(new PotionEffect(effectType, potionSection.getInt("Duration", 20), potionSection.getInt("Amplifier", 1) - 1), true);
    }

    private void applySkull(ConfigurationSection section, ItemMeta meta) {
        if (!(meta instanceof SkullMeta skullMeta)) {
            return;
        }

        String skullName = section.getString("Skull-Name");

        if (skullName != null) {
            skullMeta.setOwner(skullName);
        }

        String skullUrl = section.getString("Skull-URL");

        if (skullUrl == null) {
            return;
        }

        PlayerProfile skullProfile = Bukkit.createPlayerProfile("Notch");
        PlayerTextures textures = skullProfile.getTextures();
        textures.clear();

        try {
            textures.setSkin(URI.create(skullUrl).toURL());
        } catch (MalformedURLException | IllegalArgumentException e) {
            Bukkit.getLogger().log(Level.WARNING, "Invalid skull URL: " + skullUrl, e);
            return;
        }

        skullProfile.setTextures(textures);
        skullMeta.setOwnerProfile(skullProfile);
    }

    private void applyModels(ConfigurationSection section, ItemMeta meta) {
        if (section.getInt("Model-Data") != 0) {
            meta.setCustomModelData(section.getInt("Model-Data"));
        }

        NamespacedKey itemModel = parseKey(section.getString("Item-Model"));

        if (itemModel != null) {
            meta.setItemModel(itemModel);
        }

        NamespacedKey tooltipStyle = parseKey(section.getString("Tooltip-Style"));

        if (tooltipStyle != null) {
            meta.setTooltipStyle(tooltipStyle);
        }
    }

    private void applyEquippable(ConfigurationSection section, ItemMeta meta) {
        ConfigurationSection equippableSection = section.getConfigurationSection("Equippable-Options");

        if (equippableSection == null) {
            return;
        }

        EquippableComponent component = meta.getEquippable();

        if (equippableSection.contains("Slot")) {
            EquipmentSlot slot = EnumUtil.getValue(EquipmentSlot.class, equippableSection.getString("Slot"), null);

            if (slot != null) {
                component.setSlot(slot);
            }
        }

        NamespacedKey model = parseKey(equippableSection.getString("Model"));

        if (model != null) {
            component.setModel(model);
        }

        NamespacedKey overlay = parseKey(equippableSection.getString("Camera-Overlay"));

        if (overlay != null) {
            component.setCameraOverlay(overlay);
        }

        if (equippableSection.contains("Equip-Sound")) {
            Sound sound = parseSound(equippableSection.getString("Equip-Sound"));

            if (sound != null) {
                component.setEquipSound(sound);
            }
        }

        if (equippableSection.contains("Allowed-Entities")) {
            List<EntityType> entities = new ArrayList<>();

            for (String type : equippableSection.getStringList("Allowed-Entities")) {
                EntityType entityType = EnumUtil.getValue(EntityType.class, type, null);

                if (entityType == null) {
                    warn("Skipped invalid EntityType '" + type + "' for EquippableComponent.");
                    continue;
                }

                entities.add(entityType);
            }

            component.setAllowedEntities(entities);
        }

        component.setDispensable(equippableSection.getBoolean("Dispensable", false));
        component.setSwappable(equippableSection.getBoolean("Swappable", false));
        component.setDamageOnHurt(equippableSection.getBoolean("Damage-On-Hurt", false));

        meta.setEquippable(component);
    }

    private void applyFood(ConfigurationSection section, ItemMeta meta) {
        ConfigurationSection foodSection = section.getConfigurationSection("Food-Options");

        if (foodSection == null) {
            return;
        }

        FoodComponent component = meta.getFood();

        component.setCanAlwaysEat(foodSection.getBoolean("Can-Always-Eat", component.canAlwaysEat()));
        component.setNutrition(foodSection.getInt("Nutrition", component.getNutrition()));
        component.setSaturation(foodSection.getInt("Saturation", (int) component.getSaturation()));

        meta.setFood(component);
    }

    private void applyTool(ConfigurationSection section, ItemMeta meta) {
        ConfigurationSection toolSection = section.getConfigurationSection("Tool-Options");

        if (toolSection == null) {
            return;
        }

        ToolComponent component = meta.getTool();

        component.setDamagePerBlock(toolSection.getInt("Damage-Per-Block", component.getDamagePerBlock()));

        for (String rule : toolSection.getStringList("Rules")) {
            String[] ruleData = rule.split(":");

            if (ruleData.length < 3 || !NumberUtil.isNumber(ruleData[1])) {
                warn("Skipped invalid tool rule '" + rule + "'.");
                continue;
            }

            component.addRule(EnumUtil.getValue(Material.class, ruleData[0], Material.OAK_LOG), Float.parseFloat(ruleData[1]), Boolean.parseBoolean(ruleData[2]));
        }

        component.setDefaultMiningSpeed(toolSection.getInt("Default-Mining-Speed", (int) component.getDefaultMiningSpeed()));

        meta.setTool(component);
    }

    private void applyEnchantments(ConfigurationSection section, ItemMeta meta) {
        for (String enchant : section.getStringList("Enchantments")) {
            String[] data = enchant.split(":");

            if (data.length < 2) {
                warn("Skipped enchantment '" + enchant + "', expected name:level.");
                continue;
            }

            NamespacedKey key = parseKey("minecraft:" + data[0].trim().toLowerCase());
            Enchantment enchantment = null;

            if (key != null) {
                enchantment = Enchantment.getByKey(key);
            }

            if (enchantment == null) {
                warn("Skipped enchantment '" + enchant + "', invalid enchant.");
                continue;
            }

            int level = 0;

            if (NumberUtil.isNumber(data[1])) {
                level = Integer.parseInt(data[1]);
            }

            meta.addEnchant(enchantment, level, true);
        }
    }

    private void applyFlags(ConfigurationSection section, ItemMeta meta) {
        for (String flag : section.getStringList("Flags")) {
            ItemFlag itemFlag = EnumUtil.getValue(ItemFlag.class, flag.toUpperCase(), null);

            if (itemFlag == null) {
                warn("Skipped invalid item flag '" + flag + "'.");
                continue;
            }

            meta.addItemFlags(itemFlag);
        }
    }

    private void applyConsumable(ConfigurationSection section, ItemStack stack) {
        ConfigurationSection consumableSection = section.getConfigurationSection("Consumable-Options");

        if (consumableSection == null) {
            return;
        }

        Sound sound = parseSound(consumableSection.getString("Sound", "ENTITY_GENERIC_EAT"));

        if (sound == null) {
            sound = Sound.ENTITY_GENERIC_EAT;
        }

        Consumable consumable = Consumable.consumable()
                .hasConsumeParticles(consumableSection.getBoolean("Particles", true))
                .consumeSeconds(consumableSection.getInt("Consume-Seconds", 3))
                .sound(sound.getKey())
                .build();

        stack.setData(DataComponentTypes.CONSUMABLE, consumable);
    }

    private Component withoutItalic(Component component) {
        return component.compact().style(style -> style.decoration(TextDecoration.ITALIC, false));
    }

    @SuppressWarnings("deprecation")
    private Sound parseSound(String name) {
        if (name == null) {
            return null;
        }

        try {
            return Sound.valueOf(name.toUpperCase());
        } catch (IllegalArgumentException e) {
            warn("Invalid sound '" + name + "'.");
            return null;
        }
    }

    private PotionType parsePotionType(String name) {
        PotionType type = EnumUtil.getValue(PotionType.class, name, null);

        if (type == null) {
            warn("Invalid potion type '" + name + "'.");
        }

        return type;
    }

    private NamespacedKey parseKey(String value) {
        if (value == null) {
            return null;
        }

        String[] data = value.split(":");

        if (data.length != 2) {
            return null;
        }

        return NamespacedKey.fromString(data[0].toLowerCase() + ":" + data[1].toLowerCase());
    }

    private void warn(String message) {
        Bukkit.getLogger().warning("(ItemStackConfigSerializer): " + message);
    }

    @Override
    public ConfigurationSection serialize(@NonNull ConfigurationSection section, @NonNull ItemStack itemStack) {
        section.set("Material", itemStack.getType().name());
        section.set("Amount", itemStack.getAmount());

        ItemMeta meta = itemStack.getItemMeta();

        if (meta == null) {
            return section;
        }

        if (meta.hasDisplayName()) {
            section.set("Name", MessageUtil.serializeColored(meta.getDisplayName()));
        }

        if (meta.hasLore() && meta.getLore() != null) {
            List<String> plainLore = new ArrayList<>();
            meta.getLore().forEach(loreLine -> plainLore.add(MessageUtil.serializeColored(loreLine)));
            section.set("Lore", plainLore);
        }

        List<String> flags = new ArrayList<>();
        meta.getItemFlags().forEach(itemFlag -> flags.add(itemFlag.name()));

        if (!flags.isEmpty()) {
            section.set("Flags", flags);
        }

        List<String> customData = new ArrayList<>();
        PersistentDataContainer dataContainer = meta.getPersistentDataContainer();

        for (NamespacedKey key : dataContainer.getKeys()) {
            if (!dataContainer.has(key, PersistentDataType.STRING)) {
                continue;
            }

            customData.add(key.getNamespace() + ":" + key.getKey() + ":" + dataContainer.get(key, PersistentDataType.STRING));
        }

        if (!customData.isEmpty()) {
            section.set("Custom-Data", customData);
        }

        if (meta.hasEnchant(Enchantment.UNBREAKING)
                && meta.getEnchantLevel(Enchantment.UNBREAKING) == 37
                && meta.hasItemFlag(ItemFlag.HIDE_ENCHANTS)) {
            section.set("Glow", true);
        }

        if (meta.isHideTooltip()) {
            section.set("Hide-Tooltip", true);
        }

        if (meta instanceof LeatherArmorMeta leatherArmorMeta
                && !leatherArmorMeta.getColor().equals(Bukkit.getServer().getItemFactory().getDefaultLeatherColor())) {
            section.set("Dye-Color", ColorUtil.colorToChatColor(leatherArmorMeta.getColor()).getName());
        }

        if (meta instanceof PotionMeta potionMeta
                && (itemStack.getType() == Material.POTION
                || itemStack.getType() == Material.SPLASH_POTION
                || itemStack.getType() == Material.LINGERING_POTION)) {
            if (potionMeta.getBasePotionType() != null) {
                section.set("Potion-Data.Vanilla", true);
                section.set("Potion-Data.Type", potionMeta.getBasePotionType().name());
            } else if (!potionMeta.getCustomEffects().isEmpty()) {
                PotionEffect effect = potionMeta.getCustomEffects().getFirst();
                section.set("Potion-Data.Type", effect.getType().getKey().getKey());
                section.set("Potion-Data.Duration", effect.getDuration());
                section.set("Potion-Data.Amplifier", effect.getAmplifier() + 1);
            }
        }

        if (meta instanceof PotionMeta potionMeta && potionMeta.getBasePotionType() != null
                && itemStack.getType() == Material.TIPPED_ARROW) {
            section.set("Arrow-Type", potionMeta.getBasePotionType().name());
        }

        if (meta instanceof SkullMeta skullMeta && skullMeta.getOwnerProfile() != null
                && skullMeta.getOwnerProfile().getTextures().getSkin() != null) {
            section.set("Skull-URL", skullMeta.getOwnerProfile().getTextures().getSkin().toString());
        }

        if (meta.hasCustomModelData()) {
            section.set("Model-Data", meta.getCustomModelData());
        }

        List<String> enchants = new ArrayList<>();

        meta.getEnchants().forEach((enchantment, integer) -> {
            enchants.add(enchantment.getKey().getKey() + ":" + integer);
        });

        if (!enchants.isEmpty()) {
            section.set("Enchantments", enchants);
        }

        return section;
    }

}
