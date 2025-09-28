package net.bitbylogic.utils.condition.reference;

import lombok.NonNull;
import net.bitbylogic.utils.reference.ObjectReference;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Explicit registry for ConditionReference objects.
 * References must be explicitly tracked via {@link #track(ConditionReference)}.
 */
public final class ConditionReferenceRegistry {

    private static final Map<String, ConditionReference> REFERENCES = new HashMap<>();

    private ConditionReferenceRegistry() {
        // utility class
    }

    /**
     * Tracks a ConditionReference by its ID.
     * If a reference with the same ID already exists, it will be replaced.
     *
     * @param reference the reference to track
     */
    static void track(@NonNull ConditionReference reference) {
        REFERENCES.put(reference.getId(), reference);
    }

    /**
     * Gets a previously tracked reference.
     *
     * @param id the item ID
     * @return the tracked ConditionReference, or null if not tracked
     */
    public static ConditionReference getCondition(@NonNull String id) {
        return REFERENCES.get(id);
    }

    /**
     * Removes a reference from the registry.
     *
     * @param id the item ID
     */
    public static void untrack(@NonNull String id) {
        REFERENCES.remove(id);
    }

    /**
     * Clears all tracked conditions.
     */
    public static void clear() {
        REFERENCES.clear();
    }

    public static void invalidateAll() {
        REFERENCES.values().forEach(ObjectReference::invalidateCache);
    }

    /**
     * @return an unmodifiable view of all tracked condition references.
     */
    public static Map<String, ConditionReference> all() {
        return Collections.unmodifiableMap(REFERENCES);
    }

}