package net.bitbylogic.utils.item;

import lombok.NonNull;
import net.bitbylogic.utils.EnumUtil;
import net.bitbylogic.utils.NumberUtil;
import net.bitbylogic.utils.color.ColorUtil;
import net.bitbylogic.utils.config.ConfigSerializer;
import net.bitbylogic.utils.message.MessageUtil;
import net.bitbylogic.utils.server.ServerUtil;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.*;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.enchantments.Enchantment;
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
import org.bukkit.inventory.meta.components.consumable.ConsumableComponent;
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

public class ItemStackConfigSerializer implements ConfigSerializer<ItemStack> {

    @Override
    public Optional<ItemStack> deserialize(@NonNull ConfigurationSection section) {
        return Optional.empty();
    }

    @Override
    public Optional<ItemStack> deserialize(@NonNull ConfigurationSection section, TagResolver.Single... placeholders) {
        int amount = section.getInt("Amount", 1);
        ItemStack stack = new ItemStack(Material.valueOf(MessageUtil.deserializeToSpigot(section.getString("Material", "BARRIER"), placeholders)), amount);
        ItemMeta meta = stack.getItemMeta();

        if (meta == null) {
            return Optional.of(stack);
        }

        // Define the items name
        if (section.getString("Name") != null) {
            if (ServerUtil.isPaper()) {
                PaperItemStackUtil.setName(meta, MessageUtil.deserialize(section.getString("Name"), placeholders));
            } else {
                meta.setDisplayName(MessageUtil.deserializeToSpigot(section.getString("Name"), placeholders));
            }
        }

        if (ServerUtil.isPaper()) {
            List<Component> lore = new ArrayList<>();

            section.getStringList("Lore").forEach(string -> lore.add(MessageUtil.deserialize(string, placeholders)));

            PaperItemStackUtil.setLore(meta, lore);
        } else {
            List<String> lore = new ArrayList<>();

            section.getStringList("Lore").forEach(string ->
                    lore.add(MessageUtil.deserializeToSpigot(string, placeholders)));

            meta.setLore(lore);
        }

        meta.setMaxStackSize(section.getInt("Max-Stack-Size", meta.hasMaxStackSize() ? meta.getMaxStackSize() : 64));

        // Add persistent data keys
        if (!section.getStringList("Custom-Data").isEmpty()) {
            for (String data : section.getStringList("Custom-Data")) {
                String[] splitData = data.split(":");
                meta.getPersistentDataContainer().set(new NamespacedKey(splitData[0], splitData[1]), PersistentDataType.STRING, splitData[2]);
            }
        }

        // Make the item glow
        if (section.getBoolean("Glow")) {
            meta.addEnchant(Enchantment.UNBREAKING, 37, true);
            meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        }

        if (section.getBoolean("Hide-Tooltip")) {
            meta.setHideTooltip(true);
        }

        // If leather armor, apply dye color if defined
        if (stack.getType().name().startsWith("LEATHER_") && section.getString("Dye-Color") != null) {
            LeatherArmorMeta leatherArmorMeta = (LeatherArmorMeta) meta;
            java.awt.Color color = ChatColor.of(section.getString("Dye-Color", ColorUtil.colorToChatColor(Bukkit.getServer().getItemFactory().getDefaultLeatherColor()).toString())).getColor();
            leatherArmorMeta.setColor(Color.fromRGB(color.getRed(), color.getGreen(), color.getBlue()));
            meta = leatherArmorMeta;
        }

        // If the item is a potion, apply potion data
        if (stack.getType() == Material.SPLASH_POTION || stack.getType() == Material.POTION) {
            ConfigurationSection potionSection = section.getConfigurationSection("Potion-Data");

            if (potionSection != null) {
                boolean vanilla = potionSection.getBoolean("Vanilla", false);
                PotionMeta potionMeta = (PotionMeta) meta;

                if(potionSection.isSet("Color") && ColorUtil.containsHexColor(potionSection.getString("Color"))) {
                    String hexColor = potionSection.getString("Color");

                    int r = Integer.parseInt(hexColor.substring(0, 2), 16);
                    int g = Integer.parseInt(hexColor.substring(2, 4), 16);
                    int b = Integer.parseInt(hexColor.substring(4, 6), 16);

                    potionMeta.setColor(Color.fromRGB(r, g, b));
                }

                String potionType = potionSection.getString("Type", "POISON");

                if (vanilla) {
                    potionMeta.setBasePotionType(PotionType.valueOf(potionType));
                } else {
                    potionMeta.addCustomEffect(new PotionEffect(PotionEffectType.getByName(potionType), potionSection.getInt("Duration", 20), potionSection.getInt("Amplifier", 1) - 1), true);
                }

                meta = potionMeta;
            }
        }

        if (stack.getType() == Material.TIPPED_ARROW) {
            PotionMeta potionMeta = (PotionMeta) meta;
            potionMeta.setBasePotionType(PotionType.valueOf(section.getString("Arrow-Type", "POISON")));
            meta = potionMeta;
        }

        // If the item is a player head, apply skin
        if (section.getString("Skull-Name") != null && stack.getType() == Material.PLAYER_HEAD) {
            SkullMeta skullMeta = (SkullMeta) meta;
            skullMeta.setOwner(MessageUtil.deserializeToSpigot(section.getString("Skull-Name", "Notch"), placeholders));
            meta = skullMeta;
        }

        if (section.getString("Skull-URL") != null) {
            SkullMeta skullMeta = (SkullMeta) meta;
            PlayerProfile skullProfile = Bukkit.createPlayerProfile("Notch");
            PlayerTextures textures = skullProfile.getTextures();
            textures.clear();
            try {
                textures.setSkin(URI.create(section.getString("Skull-URL")).toURL());
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
            skullProfile.setTextures(textures);
            skullMeta.setOwnerProfile(skullProfile);
            meta = skullMeta;
        }

        if(section.isSet("Glider")) {
            meta.setGlider(section.getBoolean("Glider"));
        }

        // Used for resourcepacks, to display custom models
        if (section.getInt("Model-Data") != 0) {
            meta.setCustomModelData(section.getInt("Model-Data"));
        }

        if(section.getString("Item-Model") != null) {
            String[] itemModelData = section.getString("Item-Model").split(":");
            meta.setItemModel(new NamespacedKey(itemModelData[0], itemModelData[1]));
        }

        if(section.getString("Tooltip-Style") != null) {
            String[] tooltipModel = section.getString("Tooltip-Style").split(":");
            meta.setTooltipStyle(new NamespacedKey(tooltipModel[0], tooltipModel[1]));
        }

        ConfigurationSection equippableSection = section.getConfigurationSection("Equippable-Options");

        if (equippableSection != null) {
            EquippableComponent component = meta.getEquippable();

            if (equippableSection.contains("Slot")) {
                component.setSlot(EquipmentSlot.valueOf(equippableSection.getString("Slot")));
            }

            if (equippableSection.contains("Model")) {
                String[] model = equippableSection.getString("Model").split(":");
                component.setModel(new NamespacedKey(model[0], model[1]));
            }

            if (equippableSection.contains("Camera-Overlay")) {
                String[] overlay = equippableSection.getString("Camera-Overlay").split(":");
                component.setCameraOverlay(new NamespacedKey(overlay[0], overlay[1]));
            }

            if (equippableSection.contains("Equip-Sound")) {
                component.setEquipSound(Sound.valueOf(equippableSection.getString("Equip-Sound")));
            }

            if (equippableSection.contains("Allowed-Entities")) {
                List<String> entityStrings = equippableSection.getStringList("Allowed-Entities");
                List<org.bukkit.entity.EntityType> entities = new ArrayList<>();

                for (String type : entityStrings) {
                    try {
                        entities.add(org.bukkit.entity.EntityType.valueOf(type));
                    } catch (IllegalArgumentException ignored) {
                        Bukkit.getLogger().warning("(ItemStackUtil): Skipped invalid EntityType '" + type + "' for EquippableComponent.");
                    }
                }

                component.setAllowedEntities(entities);
            }

            component.setDispensable(equippableSection.getBoolean("Dispensable", false));
            component.setSwappable(equippableSection.getBoolean("Swappable", false));
            component.setDamageOnHurt(equippableSection.getBoolean("Damage-On-Hurt", false));

            meta.setEquippable(component);
        }

        ConfigurationSection foodSection = section.getConfigurationSection("Food-Options");

        if(foodSection != null) {
            FoodComponent component = meta.getFood();

            component.setCanAlwaysEat(foodSection.getBoolean("Can-Always-Eat", component.canAlwaysEat()));
            component.setNutrition(foodSection.getInt("Nutrition", component.getNutrition()));
            component.setSaturation(foodSection.getInt("Saturation", (int) component.getSaturation()));

            meta.setFood(component);
        }

        ConfigurationSection toolSection = section.getConfigurationSection("Tool-Options");

        if(toolSection != null) {
            ToolComponent component = meta.getTool();

            component.setDamagePerBlock(toolSection.getInt("Damage-Per-Block", component.getDamagePerBlock()));

            toolSection.getStringList("Rules").forEach(s -> {
                String[] ruleData = s.split(":");

                if(ruleData.length < 3) {
                    return;
                }

                component.addRule(EnumUtil.getValue(Material.class, ruleData[0], Material.OAK_LOG), Float.parseFloat(ruleData[1]), Boolean.parseBoolean(ruleData[2]));
            });

            component.setDefaultMiningSpeed(toolSection.getInt("Default-Mining-Speed", (int) component.getDefaultMiningSpeed()));

            meta.setTool(component);
        }

        final ItemMeta finalMeta = meta;

        // Apply enchantments
        section.getStringList("Enchantments").forEach(enchant -> {
            String[] data = enchant.split(":");
            NamespacedKey key = NamespacedKey.minecraft(data[0].trim());
            Enchantment enchantment = Enchantment.getByKey(key);
            int level = 0;

            if (NumberUtil.isNumber(data[1])) {
                level = Integer.parseInt(data[1]);
            }

            if (enchantment == null) {
                Bukkit.getLogger().warning(String.format("[APIByLogic] (ItemStackUtil): Skipped enchantment '%s', invalid enchant.", enchant));
                return;
            }

            finalMeta.addEnchant(enchantment, level, true);
        });

        for (String flag : section.getStringList("Flags")) {
            finalMeta.addItemFlags(ItemFlag.valueOf(flag.toUpperCase()));
        }

        stack.setItemMeta(finalMeta);

        ConfigurationSection consumableSection = section.getConfigurationSection("Consumable-Options");

        if(consumableSection != null) {
            if(ServerUtil.isPaper()) {
                PaperConsumableProvider.provide(stack, consumableSection);
            } else {
                ConsumableComponent consumableComponent = meta.getConsumable();

                consumableComponent.setConsumeSeconds(consumableSection.getInt("Consume-Seconds", 3));
                consumableComponent.setConsumeParticles(consumableSection.getBoolean("Particles", true));

                if(consumableSection.isSet("Sound")) {
                    consumableComponent.setSound(Sound.valueOf(consumableSection.getString("Sound")));
                }

                meta.setConsumable(consumableComponent);
            }
        }

        return Optional.of(stack);
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
            section.set("Dye-Color", ColorUtil.colorToChatColor(leatherArmorMeta.getColor()));
        }

        if (meta instanceof PotionMeta potionMeta
                && (itemStack.getType() == Material.POTION
                || itemStack.getType() == Material.SPLASH_POTION
                || itemStack.getType() == Material.LINGERING_POTION)) {
            if (potionMeta.getBasePotionType() != null) {
                section.set("Potion-Data.Vanilla", true);
                section.set("Potion-Data.Type", potionMeta.getBasePotionType().name());
            } else if (potionMeta.getCustomEffects().size() > 1) {
                PotionEffect effect = potionMeta.getCustomEffects().getFirst();
                section.set("Potion-Data.Type", effect.getType().getKey().getKey());
                section.set("Duration", effect.getDuration());
                section.set("Amplifier", effect.getAmplifier() + 1);
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
