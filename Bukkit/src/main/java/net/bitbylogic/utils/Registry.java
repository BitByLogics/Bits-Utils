package net.bitbylogic.utils;

import lombok.Getter;
import net.bitbylogic.utils.config.ConfigSerializer;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

@Getter
public class Registry<K, V> {

    private final ConcurrentHashMap<K, V> registry = new ConcurrentHashMap<>();

    private final @Nullable ConfigSerializer<V> serializer;

    private final @Nullable File configFile;
    private final @Nullable YamlConfiguration config;
    private final @Nullable String configPath;

    public Registry() {
        this.serializer = null;
        this.configFile = null;
        this.config = null;
        this.configPath = null;
    }

    public Registry(@NotNull File configFile, @NotNull String configPath, @NotNull ConfigSerializer<V> serializer) {
        this.serializer = serializer;

        this.configFile = configFile;
        this.config = YamlConfiguration.loadConfiguration(configFile);
        this.configPath = configPath;
    }

    public void reloadConfig() {
        if (configFile == null || config == null) {
            return;
        }

        try {
            this.config.load(configFile);
        } catch (IOException | InvalidConfigurationException e) {
            Logger.getGlobal().log(Level.WARNING, "Failed to reload config");
        }
    }

    public void loadFromConfig() {
        if (config == null || configPath == null) {
            return;
        }

        ConfigurationSection section = config.getConfigurationSection(configPath);

        if(serializer == null || section == null) {
            return;
        }

        for (String key : section.getKeys(false)) {
            ConfigurationSection keySection = section.getConfigurationSection(key);

            if (keySection == null) {
                continue;
            }

            serializer.deserialize(keySection).ifPresent(v -> registry.putIfAbsent((K) key, v));
        }
    }

    public Optional<V> get(@NotNull K key) {
        return Optional.ofNullable(registry.get(key));
    }

    public void register(@NotNull K key, @NotNull V value) {
        registry.put(key, value);
    }

    public void unregister(@NotNull K key) {
        registry.remove(key);
    }

    public void clear() {
        registry.clear();
    }

    public List<K> keys() {
        return List.copyOf(registry.keySet());
    }

    public List<V> values() {
        return List.copyOf(registry.values());
    }

}
