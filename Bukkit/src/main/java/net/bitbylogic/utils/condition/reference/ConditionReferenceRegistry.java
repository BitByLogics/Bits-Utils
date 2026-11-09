package net.bitbylogic.utils.condition.reference;

import lombok.NonNull;
import net.bitbylogic.utils.reference.ReferenceRegistry;

import java.util.Map;

public final class ConditionReferenceRegistry {

    private static final ReferenceRegistry<ConditionReference> REFERENCES = new ReferenceRegistry<>();

    private ConditionReferenceRegistry() {

    }

    static void track(@NonNull ConditionReference reference) {
        REFERENCES.track(reference);
    }

    public static ConditionReference getCondition(@NonNull String id) {
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

    public static Map<String, ConditionReference> all() {
        return REFERENCES.all();
    }

}
