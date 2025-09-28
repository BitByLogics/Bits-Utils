package net.bitbylogic.utils.context.registry;

import lombok.NonNull;
import net.bitbylogic.utils.context.Context;
import net.bitbylogic.utils.context.ContextKey;

import java.lang.reflect.Type;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class ContextRegistry {

    private static final Map<String, ContextKey<?>> KEY_REGISTRY = new ConcurrentHashMap<>();

    public static <T> void register(@NonNull ContextKey<T> key) {
        if(KEY_REGISTRY.containsKey(key.getId().toLowerCase())) {
            return;
        }

        KEY_REGISTRY.putIfAbsent(key.getId().toLowerCase(), key);
    }

    public static void unregister(@NonNull String id) {
        KEY_REGISTRY.remove(id.toLowerCase());
    }

    @SuppressWarnings("unchecked")
    public static <T> Optional<ContextKey<T>> get(@NonNull String id) {
        try {
            return Optional.ofNullable((ContextKey<T>) KEY_REGISTRY.get(id.toLowerCase()));
        } catch (ClassCastException e) {
            return Optional.empty();
        }
    }

    @SuppressWarnings("unchecked")
    public static <T> Optional<ContextKey<T>> get(@NonNull String id, @NonNull Class<T> type) {
        ContextKey<?> key = KEY_REGISTRY.get(id.toLowerCase());

        if (key == null || !type.isAssignableFrom(key.getRawType())) {
            return Optional.empty();
        }

        return Optional.of((ContextKey<T>) key);
    }

    public static Optional<?> getAndRetrieve(@NonNull Context context, @NonNull String id) {
        return get(id).map(context::get);
    }

    public static <T> Optional<T> getAndRetrieve(@NonNull Context context, @NonNull String id, @NonNull Class<T> type) {
        return get(id, type).flatMap(context::get);
    }

    @SuppressWarnings("unchecked")
    public static <T> List<ContextKey<T>> allOfType(@NonNull Class<T> rawType) {
        List<ContextKey<T>> result = new ArrayList<>();
        for (ContextKey<?> key : KEY_REGISTRY.values()) {
            if (rawType.isAssignableFrom(key.getRawType())) {
                result.add((ContextKey<T>) key);
            }
        }
        return Collections.unmodifiableList(result);
    }

    @SuppressWarnings("unchecked")
    public static <T> List<ContextKey<T>> allOfGenericType(@NonNull Type genericType) {
        List<ContextKey<T>> result = new ArrayList<>();
        for (ContextKey<?> key : KEY_REGISTRY.values()) {
            if (key.getGenericType().equals(genericType)) {
                result.add((ContextKey<T>) key);
            }
        }
        return Collections.unmodifiableList(result);
    }

    /**
     * Retrieves all registered context keys as an unmodifiable collection.
     *
     * @return a collection of all registered {@code ContextKey} instances.
     */
    public static Collection<ContextKey<?>> all() {
        return Collections.unmodifiableCollection(KEY_REGISTRY.values());
    }

}
