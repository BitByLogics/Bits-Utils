package net.bitbylogic.utils.config.configurable.wrapper.impl;

import lombok.NonNull;
import net.bitbylogic.utils.config.configurable.wrapper.ConfigWrapper;
import net.bitbylogic.utils.item.ItemStackUtil;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class DefaultConfigWrapper implements ConfigWrapper<Object> {

    @Override
    public void wrap(@NonNull Object object, @NonNull String path, @NonNull FileConfiguration config) {
        if (object instanceof ItemStack) {
            ConfigurationSection section = config.getConfigurationSection(path) == null ? config.createSection(path) : config.getConfigurationSection(path);

            if(section == null) {
                return;
            }

            ItemStackUtil.saveToConfig(section, (ItemStack) object);
            return;
        }

        if (object instanceof HashMap<?,?>) {
            ((HashMap<?, ?>) object).forEach((key, value) -> {
                if(!(key instanceof String) || !(value instanceof ItemStack)) {
                    return;
                }

                ConfigurationSection section = config.isConfigurationSection(path + key) ? config.getConfigurationSection(path + key)
                        : config.createSection(path + key);

                if(section == null) {
                    return;
                }

                ItemStackUtil.saveToConfig(section, (ItemStack) value);
            });

            return;
        }

        config.set(path, object);
    }

    @Override
    public <W> Object unwrap(@NonNull W wrappedObject, @NonNull Class<?> requestedClass) {
        if (requestedClass.isAssignableFrom(ItemStack.class) && wrappedObject instanceof ConfigurationSection) {
            return ItemStackUtil.getFromConfig((ConfigurationSection) wrappedObject);
        }

        if(requestedClass.isAssignableFrom(Map.class) && wrappedObject instanceof ConfigurationSection) {
            ConfigurationSection wrappedSection = (ConfigurationSection) wrappedObject;
            Map<String, ItemStack> map = new LinkedHashMap<>();

            for (String key : wrappedSection.getKeys(false)) {
                ConfigurationSection itemSection = wrappedSection.getConfigurationSection(key);

                if (itemSection == null) {
                    continue;
                }

                map.put(key, ItemStackUtil.getFromConfig(itemSection));
            }

            return map;
        }

        return wrappedObject;
    }
}
