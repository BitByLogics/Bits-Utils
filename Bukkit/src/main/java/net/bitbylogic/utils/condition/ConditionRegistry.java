package net.bitbylogic.utils.condition;

import lombok.NonNull;
import net.bitbylogic.utils.condition.type.impl.HasPermissionCondition;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public final class ConditionRegistry {

    private static final Map<String, Condition> CONDITIONS = new HashMap<>();

    static {
        register(new HasPermissionCondition());
    }

    // Prevent instantiation
    private ConditionRegistry() {

    }

    public static void register(@NonNull Condition condition) {
        CONDITIONS.putIfAbsent(condition.getId().toLowerCase(), condition);
    }

    public static void unregister(@NonNull String id) {
        CONDITIONS.remove(id.toLowerCase());
    }

    public static Optional<Condition> get(@NonNull String id) {
        return Optional.ofNullable(CONDITIONS.get(id.toLowerCase()));
    }

    public static Set<String> getIds() {
        return CONDITIONS.keySet();
    }

    public static boolean contains(@NonNull String id) {
        return CONDITIONS.containsKey(id.toLowerCase());
    }

    public static void clear() {
        CONDITIONS.clear();
    }

}
