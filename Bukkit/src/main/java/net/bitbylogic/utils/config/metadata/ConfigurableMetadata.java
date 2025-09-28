package net.bitbylogic.utils.config.metadata;

import lombok.NonNull;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class ConfigurableMetadata extends ConfiguredMetadata {

    private final Map<String, Object> pendingDefaults = new LinkedHashMap<>();

    private @Nullable File configFile;

    public ConfigurableMetadata() {
        this.configFile = null;
    }

    /**
     * Constructor that applies defaults if missing in the section.
     *
     * @param configFile optional config file
     * @param section    config section
     * @param defaults   map of default values
     */
    public ConfigurableMetadata(@Nullable File configFile, @Nullable ConfigurationSection section,
                                Map<String, Object> defaults) {
        super(section);

        this.configFile = configFile;

        if (section == null) {
            return;
        }

        boolean changed = false;

        for (Map.Entry<String, Object> entry : defaults.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();

            if (section.contains(key)) {
                continue;
            }

            setValue(key, value, false);

            changed = true;
        }

        if (!changed || configFile == null) {
            return;
        }

        saveToFile();
    }

    /**
     * Adds a default value if the key is missing in the section.
     *
     * @param key   key to set
     * @param value default value
     * @return this (for chaining)
     */

    public ConfigurableMetadata withDefault(String key, Object value) {
        pendingDefaults.put(key, value);

        if (getSection() != null) {
            if(getSection().isSet(key)) {
                pendingDefaults.remove(key);
                return this;
            }

            setValue(key, value, true);
            pendingDefaults.remove(key);
        }

        return this;
    }

    private Object convertForConfig(Object value) {
        if (value instanceof Set<?> set) {
            if(set.isEmpty()) {
                return new ArrayList<>();
            }

            if(!(set.iterator().next() instanceof Enum<?>)) {
                return List.of(set.stream().map(Object::toString).toList());
            }

            return set.stream()
                    .map(e -> ((Enum<?>) e).name())
                    .toList();
        }

        return value;
    }

    public <T> T getValueAsOrDefault(@NonNull String key, T defaultValue, boolean save) {
        String loweredKey = key.toLowerCase();
        T value = getMetadataMap().getValueAsOrDefault(loweredKey, null);

        if (value != null) {
            return value;
        }

        setValue(key, defaultValue, save);
        return defaultValue;
    }

    public <T> T getDeepValueAsOrDefault(@NonNull String key, T defaultValue, boolean save) {
        String loweredKey = key.toLowerCase();
        T value = getMetadataDeepMap().getValueAsOrDefault(loweredKey, null);

        if (value != null) {
            return value;
        }

        setValue(key, defaultValue, save);
        return defaultValue;
    }

    public <T> void setValue(String key, @Nullable T value, boolean save) {
        if (getSection() == null) {
            throw new IllegalStateException("Cannot set value without a configuration section");
        }

        String loweredKey = key.toLowerCase();

        getMetadataMap().put(loweredKey, value);
        getMetadataDeepMap().put(loweredKey, value);

        getSection().set(key, convertForConfig(value));

        if (!save || configFile == null) {
            return;
        }

        saveToFile();
    }

    public void removeValue(String key, boolean save) {
        if (getSection() == null) {
            throw new IllegalStateException("Cannot remove value without a configuration section");
        }

        String loweredKey = key.toLowerCase();

        getMetadataMap().remove(loweredKey);
        getMetadataDeepMap().remove(loweredKey);

        getSection().set(key, null);

        if (!save || configFile == null) {
            return;
        }

        saveToFile();
    }

    public void load(@NonNull File configFile) {
        if(!configFile.getName().endsWith(".yml")) {
            throw new IllegalArgumentException("File must be a YAML file");
        }

        this.configFile = configFile;
        configFile.getParentFile().mkdirs();

        if(!configFile.exists()) {
            try {
                configFile.createNewFile();
            } catch (IOException e) {
                Bukkit.getLogger().severe("Failed to create configuration file " + configFile);
            }
        }

        super.load(YamlConfiguration.loadConfiguration(configFile));

        for (Map.Entry<String, Object> entry : pendingDefaults.entrySet()) {
            if(getSection() != null && getSection().isSet(entry.getKey())) {
                continue;
            }

            setValue(entry.getKey(), entry.getValue(), true);
        }

        pendingDefaults.clear();
    }

    /**
     * Load/reload metadata from a section and associate with a file.
     *
     * @param configFile optional backing file
     * @param section    configuration section
     */
    public void load(@NonNull File configFile, @NonNull ConfigurationSection section) {
        if(!configFile.getName().endsWith(".yml")) {
            throw new IllegalArgumentException("File must be a YAML file");
        }

        this.configFile = configFile;
        super.load(section);

        for (Map.Entry<String, Object> entry : pendingDefaults.entrySet()) {
            if(section.isSet(entry.getKey())) {
                continue;
            }

            setValue(entry.getKey(), entry.getValue(), true);
        }

        pendingDefaults.clear();
    }

    /**
     * Reload directly from a YAML file's root.
     */
    public void reloadFromFile() {
        if (configFile == null || getSection() == null) {
            return;
        }

        YamlConfiguration config = YamlConfiguration.loadConfiguration(configFile);

        String path = getSection().getCurrentPath();
        ConfigurationSection sectionToLoad = (path == null || path.isEmpty()) ? config : config.getConfigurationSection(path);

        if (sectionToLoad == null) {
            return;
        }

        load(configFile, sectionToLoad);
    }

    public void saveToFile() {
        if (configFile == null) {
            return;
        }

        try {
            YamlConfiguration config = YamlConfiguration.loadConfiguration(configFile);

            if (getSection() == null) {
                return;
            }

            for (String key : getSection().getKeys(true)) {
                config.set(key, getSection().get(key));
            }

            config.save(configFile);
        } catch (IOException e) {
            throw new RuntimeException("Failed to save configuration to " + configFile, e);
        }
    }
}