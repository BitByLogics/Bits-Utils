package net.bitbylogic.utils.action.reference;

import lombok.NonNull;
import net.bitbylogic.utils.reference.ObjectReference;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Explicit registry for ActionReference objects.
 * References must be explicitly tracked via {@link #track(ActionReference)}.
 */
public final class ActionReferenceRegistry {

    private static final Map<String, ActionReference> REFERENCES = new HashMap<>();

    private ActionReferenceRegistry() {
        // utility class
    }

    /**
     * Tracks an ActionReference by its ID.
     * If a reference with the same ID already exists, it will be replaced.
     *
     * @param reference the reference to track
     */
    static void track(@NonNull ActionReference reference) {
        REFERENCES.put(reference.getId(), reference);
    }

    /**
     * Gets a previously tracked reference.
     *
     * @param id the item ID
     * @return the tracked ActionReference, or null if not tracked
     */
    public static ActionReference getAction(@NonNull String id) {
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
     * Clears all tracked actions.
     */
    public static void clear() {
        REFERENCES.clear();
    }

    public static void invalidateAll() {
        REFERENCES.values().forEach(ObjectReference::invalidateCache);
    }

    /**
     * @return an unmodifiable view of all tracked action references.
     */
    public static Map<String, ActionReference> all() {
        return Collections.unmodifiableMap(REFERENCES);
    }

}