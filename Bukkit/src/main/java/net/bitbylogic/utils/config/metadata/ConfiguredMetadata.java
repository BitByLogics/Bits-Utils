package net.bitbylogic.utils.config.metadata;

import lombok.Getter;
import lombok.NonNull;
import net.bitbylogic.utils.EnumUtil;
import net.bitbylogic.utils.GenericHashMap;
import net.bitbylogic.utils.StringProcessor;
import net.bitbylogic.utils.item.ItemStackUtil;
import net.bitbylogic.utils.location.LocationUtil;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

@Getter
public class ConfiguredMetadata {

    private static final String RANDOM_MIN_KEY = "min-value";
    private static final String RANDOM_MAX_KEY = "max-value";

    private final GenericHashMap<String, Object> metadataMap = new GenericHashMap<>();
    private final GenericHashMap<String, Object> metadataDeepMap = new GenericHashMap<>();

    private @Nullable ConfigurationSection section;

    public ConfiguredMetadata() {
        this.section = null;
    }

    public ConfiguredMetadata(@Nullable ConfigurationSection section) {
        load(section);
    }

    public boolean hasKey(@NonNull String key) {
        return metadataMap.containsKey(key.toLowerCase());
    }

    public boolean hasKeyDeep(@NonNull String key) {
        return metadataDeepMap.containsKey(key.toLowerCase());
    }

    public <T> T getValueAsOrDefault(@NonNull String key, T defaultValue) {
        Object value = metadataMap.getValueAsOrDefault(key.toLowerCase(), defaultValue);
        return convertValue(value, defaultValue);
    }

    public <T> T getDeepValueAsOrDefault(@NonNull String key, T defaultValue) {
        Object value = metadataDeepMap.getValueAsOrDefault(key.toLowerCase(), metadataMap.getValueAsOrDefault(key.toLowerCase(), defaultValue));
        return convertValue(value, defaultValue);
    }

    private <T> T convertValue(Object value, T defaultValue) {
        if (value instanceof Number number) {
            if (defaultValue instanceof Double) {
                return (T) Double.valueOf(number.doubleValue());
            }

            if (defaultValue instanceof Float) {
                return (T) Float.valueOf(number.floatValue());
            }

            if (defaultValue instanceof Integer) {
                return (T) Integer.valueOf(number.intValue());
            }

            if (defaultValue instanceof Long) {
                return (T) Long.valueOf(number.longValue());
            }

            if (defaultValue instanceof Short) {
                return (T) Short.valueOf(number.shortValue());
            }

            if (defaultValue instanceof Byte) {
                return (T) Byte.valueOf(number.byteValue());
            }
        }

        if (!(value instanceof String string) || defaultValue == null) {
            return (T) value;
        }

        return (T) StringProcessor.findAndProcess(defaultValue.getClass(), string);
    }

    public Optional<ItemStack> getItem(@NonNull String key) {
        ConfigurationSection itemSection = metadataMap.getValueAsOrDefault(key.toLowerCase(), null);

        if(itemSection == null) {
            return Optional.empty();
        }

        return Optional.ofNullable(ItemStackUtil.getFromConfig(itemSection));
    }

    public Optional<Location> getLocation(@NonNull String key) {
        if(!metadataMap.containsKey(key.toLowerCase())) {
            return Optional.empty();
        }

        Object value = metadataMap.get(key.toLowerCase());

        if(!(value instanceof String string)) {
            return Optional.empty();
        }

        return Optional.ofNullable(LocationUtil.stringToLocation(string));
    }

    public int getRandomIntInRange(int fallback) {
        if(!metadataMap.containsKey(RANDOM_MIN_KEY) && !metadataMap.containsKey(RANDOM_MAX_KEY)) {
            return fallback;
        }

        int min = metadataMap.getValueAsOrDefault(RANDOM_MIN_KEY, fallback);
        int max = metadataMap.getValueAsOrDefault(RANDOM_MAX_KEY, fallback);

        return ThreadLocalRandom.current().nextInt(min, max + 1);
    }

    public double getRandomDoubleInRange(double fallback) {
        if(!metadataMap.containsKey(RANDOM_MIN_KEY) && !metadataMap.containsKey(RANDOM_MAX_KEY)) {
            return fallback;
        }

        double min = metadataMap.getValueAsOrDefault(RANDOM_MIN_KEY, fallback);
        double max = metadataMap.getValueAsOrDefault(RANDOM_MAX_KEY, fallback);

        if (max <= min) {
            return min;
        }

        return ThreadLocalRandom.current().nextDouble(min, max);
    }

    public <T extends Enum<T>> T getEnum(@NonNull Class<T> enumClass, @NonNull String key, @NonNull T fallback) {
        return EnumUtil.getValue(enumClass, getValueAsOrDefault(key.toLowerCase(), fallback.name()), fallback);
    }

    public <T extends Enum<T>> Set<T> getEnumSet(@NonNull Class<T> enumClass, @NonNull String key, @NonNull Set<T> fallback) {
        Object raw = metadataMap.getValueAsOrDefault(key.toLowerCase(), null);

        if (raw == null) {
            return fallback;
        }

        // Assume that the stored set is valid :pray:
        if(raw instanceof Set<?>) {
            return (Set<T>) raw;
        }

        Set<T> result = new HashSet<>();

        if (raw instanceof List<?> list) {
            for (Object element : list) {
                if (!(element instanceof String str)) {
                    continue;
                }

                T parsed = EnumUtil.getValue(enumClass, str, null);

                if (parsed == null) {
                    continue;
                }

                result.add(parsed);
            }
        } else if (raw instanceof String str) {
            String[] parts = str.split(":");

            for (String part : parts) {
                T parsed = EnumUtil.getValue(enumClass, part.trim(), null);

                if (parsed == null) {
                    continue;
                }

                result.add(parsed);
            }
        }

        return result.isEmpty() ? fallback : result;
    }

    private void loadSection(@NonNull ConfigurationSection section, @Nullable String prefix) {
        section.getKeys(false).forEach(key -> {
            Object value = section.get(key);

            if (value instanceof ConfigurationSection keySection) {
                loadSection(keySection, prefix == null ? key : prefix + "." + key);
                return;
            }

            if (prefix != null) {
                metadataDeepMap.put((prefix + "." + key).toLowerCase(), value);
                return;
            }

            metadataMap.put(key.toLowerCase(), value);
        });
    }

    /**
     * Loads/reloads values from the given configuration section.
     * Clears existing values first.
     *
     * @param section the configuration section to load from
     */
    public void load(@Nullable ConfigurationSection section) {
        this.section = section;

        metadataMap.clear();
        metadataDeepMap.clear();

        if (section == null) {
            return;
        }

        loadSection(section, null);
    }


    @Override
    public boolean equals(Object object) {
        if (object == null || getClass() != object.getClass()) return false;
        ConfiguredMetadata that = (ConfiguredMetadata) object;
        return Objects.equals(metadataMap, that.metadataMap) && Objects.equals(metadataDeepMap, that.metadataDeepMap) && Objects.equals(section, that.section);
    }

    @Override
    public int hashCode() {
        return Objects.hash(metadataMap, metadataDeepMap, section);
    }

}
