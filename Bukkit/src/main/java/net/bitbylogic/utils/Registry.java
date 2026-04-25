package net.bitbylogic.utils;

import net.bitbylogic.utils.config.ConfigSerializer;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class Registry<K, V> {

    private final ConcurrentHashMap<K, V> registry = new ConcurrentHashMap<>();

    private final @Nullable ConfigSerializer<V> serializer;

    public Registry() {
        this.serializer = null;
    }

    public Registry(@NotNull ConfigSerializer<V> serializer) {
        this.serializer = serializer;
    }

    public void loadFromConfig(@NotNull YamlConfiguration config, @NotNull String path) {
        ConfigurationSection section = config.getConfigurationSection(path);

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
