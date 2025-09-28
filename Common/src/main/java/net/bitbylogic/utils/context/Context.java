package net.bitbylogic.utils.context;

import lombok.NonNull;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public final class Context {

    private final Map<ContextKey<?>, Object> values = new HashMap<>();
    private final Map<String, Object> attributes = new HashMap<>();

    public <T> void put(@NonNull ContextKey<T> key, @NonNull T value) {
        values.put(key, value);
    }

    @SuppressWarnings("unchecked")
    public <T> Optional<T> get(@NonNull ContextKey<T> key) {
        if (values.containsKey(key)) {
            try {
                return Optional.ofNullable((T) values.get(key));
            } catch (ClassCastException e) {
                return Optional.empty();
            }
        }

        if (key.getProvider() != null) {
            T value = key.provide(this);

            if (value != null) {
                values.put(key, value);
            }

            return Optional.ofNullable(value);
        }

        return Optional.empty();
    }

    @SuppressWarnings("unchecked")
    public <T> T getOrDefault(@NonNull ContextKey<T> key, @NonNull T defaultValue) {
        if (values.containsKey(key)) {
            Object value = values.get(key);
            return value == null ? defaultValue : (T) value;
        }

        if (key.getProvider() != null) {
            T value = key.provide(this);

            if (value != null) {
                values.put(key, value);
            }

            return value == null ? defaultValue : value;
        }

        return defaultValue;
    }

    public <T> boolean has(@NonNull ContextKey<T> key) {
        return values.containsKey(key);
    }

    public void putAttribute(@NonNull String key, @NonNull Object value) {
        attributes.put(key.toLowerCase(), value);
    }

    @SuppressWarnings("unchecked")
    public <T> T getAttribute(@NonNull String key) {
        if (attributes.containsKey(key.toLowerCase())) {
            return (T) attributes.get(key.toLowerCase());
        }

        return null;
    }

    @SuppressWarnings("unchecked")
    public <T> T getAttributeOrDefault(@NonNull String key, @NonNull T defaultValue) {
        if (attributes.containsKey(key.toLowerCase())) {
            return (T) attributes.get(key.toLowerCase());
        }

        return defaultValue;
    }

    public boolean hasAttribute(@NonNull String key) {
        return attributes.containsKey(key.toLowerCase());
    }

}

