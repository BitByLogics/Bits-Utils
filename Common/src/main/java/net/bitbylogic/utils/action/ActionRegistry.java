package net.bitbylogic.utils.action;

import lombok.NonNull;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public final class ActionRegistry {

    private static final Map<String, Action> ACTIONS = new HashMap<>();

    // Prevent instantiation
    private ActionRegistry() {

    }

    public static void register(@NonNull Action action) {
        if(ACTIONS.containsKey(action.getId().toLowerCase())) {
            return;
        }

        ACTIONS.putIfAbsent(action.getId().toLowerCase(), action);
    }

    public static void register(@NonNull Action @NonNull... actions) {
        for (Action action : actions) {
            if(ACTIONS.containsKey(action.getId().toLowerCase())) {
                return;
            }

            ACTIONS.putIfAbsent(action.getId().toLowerCase(), action);
        }
    }

    public static void unregister(@NonNull String id) {
        ACTIONS.remove(id.toLowerCase());
    }

    public static Optional<Action> get(@NonNull String id) {
        return Optional.ofNullable(ACTIONS.get(id.toLowerCase()));
    }

    public static Set<String> getIds() {
        return ACTIONS.keySet();
    }

    public static boolean contains(@NonNull String id) {
        return ACTIONS.containsKey(id.toLowerCase());
    }

    public static void clear() {
        ACTIONS.clear();
    }

}
