package net.bitbylogic.utils.trigger;

import lombok.NonNull;
import net.bitbylogic.utils.trigger.impl.event.PlayerChatTrigger;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;

public final class TriggerRegistry {

    private static final Map<String, TriggerProvider> PROVIDERS = new ConcurrentHashMap<>();
    private static final Map<String, List<Consumer<TriggerProvider>>> PENDING_TRIGGERS = new ConcurrentHashMap<>();

    static {
        register("player_chat", PlayerChatTrigger::new);
    }

    private TriggerRegistry() {

    }

    public static void register(@NonNull String id, @NonNull TriggerProvider provider) {
        String loweredId = id.toLowerCase();

        if (PROVIDERS.putIfAbsent(loweredId, provider) != null) {
            throw new IllegalArgumentException("Trigger with ID '" + id + "' already registered");
        }

        List<Consumer<TriggerProvider>> consumers = PENDING_TRIGGERS.remove(loweredId);

        if (consumers == null) {
            return;
        }

        consumers.forEach(consumer -> consumer.accept(provider));
    }

    public static void unregister(@NonNull String id) {
        PROVIDERS.remove(id.toLowerCase());
    }

    public static Optional<TriggerProvider> get(@NonNull String id) {
        return Optional.ofNullable(PROVIDERS.get(id.toLowerCase()));
    }

    public static Set<String> getIds() {
        return Set.copyOf(PROVIDERS.keySet());
    }

    public static boolean contains(@NonNull String id) {
        return PROVIDERS.containsKey(id.toLowerCase());
    }

    public static void clear() {
        PROVIDERS.clear();
    }

    public static void whenAvailable(@NonNull String id, @NonNull Consumer<TriggerProvider> consumer) {
        String loweredId = id.toLowerCase();
        TriggerProvider existingProvider = PROVIDERS.get(loweredId);

        if (existingProvider != null) {
            consumer.accept(existingProvider);
            return;
        }

        PENDING_TRIGGERS.computeIfAbsent(loweredId, ignored -> new CopyOnWriteArrayList<>()).add(consumer);
    }

}
