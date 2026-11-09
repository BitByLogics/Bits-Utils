package net.bitbylogic.utils.action.reference;

import lombok.NonNull;
import net.bitbylogic.utils.reference.ReferenceRegistry;

import java.util.Map;

public final class ActionReferenceRegistry {

    private static final ReferenceRegistry<ActionReference> REFERENCES = new ReferenceRegistry<>();

    private ActionReferenceRegistry() {

    }

    static void track(@NonNull ActionReference reference) {
        REFERENCES.track(reference);
    }

    public static ActionReference getAction(@NonNull String id) {
        return REFERENCES.get(id).orElse(null);
    }

    public static void untrack(@NonNull String id) {
        REFERENCES.untrack(id);
    }

    public static void clear() {
        REFERENCES.clear();
    }

    public static void invalidateAll() {
        REFERENCES.invalidateAll();
    }

    public static Map<String, ActionReference> all() {
        return REFERENCES.all();
    }

}
