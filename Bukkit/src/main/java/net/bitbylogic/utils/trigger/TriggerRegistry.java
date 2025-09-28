package net.bitbylogic.utils.trigger;

import lombok.NonNull;
import net.bitbylogic.utils.trigger.impl.event.PlayerChatTrigger;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;

public final class TriggerRegistry {

    private static final Map<String, TriggerProvider> PROVIDERS = new HashMap<>();
    private static final Map<String, List<Consumer<TriggerProvider>>> PENDING_TRIGGERS = new ConcurrentHashMap<>();

    static {
        register("player_chat", PlayerChatTrigger::new);
    }

    // Prevent instantiation
    private TriggerRegistry() {

    }

    public static void register(@NonNull String id, @NonNull TriggerProvider provider) {
        if(PROVIDERS.containsKey(id.toLowerCase())) {
            throw new IllegalArgumentException("Trigger with ID '" + id + "' already registered");
        }

        PROVIDERS.putIfAbsent(id.toLowerCase(), provider);

        List<Consumer<TriggerProvider>> consumers = PENDING_TRIGGERS.remove(id);

        if (consumers == null) {
            return;
        }

        consumers.forEach(c -> c.accept(provider));
    }

    public static void unregister(@NonNull String id) {
        PROVIDERS.remove(id.toLowerCase());
    }

    public static Optional<TriggerProvider> get(@NonNull String id) {
        return Optional.ofNullable(PROVIDERS.get(id.toLowerCase()));
    }

    public static Set<String> getIds() {
        return PROVIDERS.keySet();
    }

    public static boolean contains(@NonNull String id) {
        return PROVIDERS.containsKey(id.toLowerCase());
    }

    public static void clear() {
        PROVIDERS.clear();
    }

    public static void whenAvailable(String id, Consumer<TriggerProvider> consumer) {
        TriggerProvider existingProvider = PROVIDERS.get(id);

        if(existingProvider != null) {
            consumer.accept(existingProvider);
            return;
        }

        PENDING_TRIGGERS.computeIfAbsent(id, k -> new CopyOnWriteArrayList<>()).add(consumer);
    }

}
