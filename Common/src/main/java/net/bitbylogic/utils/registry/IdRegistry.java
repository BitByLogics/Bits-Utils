package net.bitbylogic.utils.registry;

import lombok.NonNull;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class IdRegistry<V> {

    private final Map<String, V> entries = new ConcurrentHashMap<>();

    public boolean register(@NonNull String id, @NonNull V value) {
        return entries.putIfAbsent(id.toLowerCase(), value) == null;
    }

    public void replace(@NonNull String id, @NonNull V value) {
        entries.put(id.toLowerCase(), value);
    }

    public Optional<V> unregister(@NonNull String id) {
        return Optional.ofNullable(entries.remove(id.toLowerCase()));
    }

    public Optional<V> get(@NonNull String id) {
        return Optional.ofNullable(entries.get(id.toLowerCase()));
    }

    public boolean contains(@NonNull String id) {
        return entries.containsKey(id.toLowerCase());
    }

    public Set<String> ids() {
        return Set.copyOf(entries.keySet());
    }

    public Collection<V> values() {
        return List.copyOf(entries.values());
    }

    public Map<String, V> snapshot() {
        return Map.copyOf(entries);
    }

    public void clear() {
        entries.clear();
    }

}
