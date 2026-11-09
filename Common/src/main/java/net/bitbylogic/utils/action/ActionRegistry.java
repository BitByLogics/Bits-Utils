package net.bitbylogic.utils.action;

import lombok.NonNull;
import net.bitbylogic.utils.registry.IdRegistry;

import java.util.Optional;
import java.util.Set;

public final class ActionRegistry {

    private static final IdRegistry<Action> ACTIONS = new IdRegistry<>();

    private ActionRegistry() {

    }

    public static void register(@NonNull Action action) {
        ACTIONS.register(action.getId(), action);
    }

    public static void register(@NonNull Action @NonNull... actions) {
        for (Action action : actions) {
            ACTIONS.register(action.getId(), action);
        }
    }

    public static void unregister(@NonNull String id) {
        ACTIONS.unregister(id);
    }

    public static Optional<Action> get(@NonNull String id) {
        return ACTIONS.get(id);
    }

    public static Set<String> getIds() {
        return ACTIONS.ids();
    }

    public static boolean contains(@NonNull String id) {
        return ACTIONS.contains(id);
    }

    public static void clear() {
        ACTIONS.clear();
    }

}
