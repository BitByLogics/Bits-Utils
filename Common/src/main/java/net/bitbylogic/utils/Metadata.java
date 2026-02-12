package net.bitbylogic.utils;

import lombok.NonNull;

import java.util.HashMap;
import java.util.Map;

public class Metadata {

    private final Map<String, Object> metadata = new HashMap<>();

    public Metadata() {}

    private Metadata(@NonNull Map<String, Object> metadata) {
        this.metadata.putAll(metadata);
    }

    public static Builder builder() {
        return new Builder();
    }

    @SuppressWarnings("unchecked")
    public <T> T get(@NonNull String key) {
        return (T) metadata.getOrDefault(key, null);
    }

    @SuppressWarnings("unchecked")
    public <T> T get(@NonNull String key, T defaultValue) {
        try {
            return (T) metadata.getOrDefault(key, defaultValue);
        } catch (ClassCastException exception) {
            return defaultValue;
        }
    }

    public void set(@NonNull String key, Object value) {
        metadata.put(key, value);
    }

    public void setIfAbsent(@NonNull String key, Object value) {
        metadata.putIfAbsent(key, value);
    }

    public void remove(@NonNull String key) {
        metadata.remove(key);
    }

    public boolean has(@NonNull String key) {
        return metadata.containsKey(key);
    }

    public Map<String, Object> getAll() {
        return Map.copyOf(metadata);
    }

    public static class Builder {

        private final Map<String, Object> metadata = new HashMap<>();

        public Builder withDefault(@NonNull String key, Object value) {
            metadata.put(key, value);
            return this;
        }

        public Metadata build() {
            return new Metadata(metadata);
        }

    }

}
