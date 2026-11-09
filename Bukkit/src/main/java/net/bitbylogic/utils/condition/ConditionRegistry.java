package net.bitbylogic.utils.condition;

import lombok.NonNull;
import net.bitbylogic.utils.condition.type.impl.HasPermissionCondition;
import net.bitbylogic.utils.registry.IdRegistry;

import java.util.Optional;
import java.util.Set;

public final class ConditionRegistry {

    private static final IdRegistry<Condition> CONDITIONS = new IdRegistry<>();

    static {
        register(new HasPermissionCondition());
    }

    private ConditionRegistry() {

    }

    public static void register(@NonNull Condition condition) {
        CONDITIONS.register(condition.getId(), condition);
    }

    public static void unregister(@NonNull String id) {
        CONDITIONS.unregister(id);
    }

    public static Optional<Condition> get(@NonNull String id) {
        return CONDITIONS.get(id);
    }

    public static Set<String> getIds() {
        return CONDITIONS.ids();
    }

    public static boolean contains(@NonNull String id) {
        return CONDITIONS.contains(id);
    }

    public static void clear() {
        CONDITIONS.clear();
    }

}
