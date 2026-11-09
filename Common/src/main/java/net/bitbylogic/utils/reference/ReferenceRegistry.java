package net.bitbylogic.utils.reference;

import lombok.NonNull;
import net.bitbylogic.utils.registry.IdRegistry;

import java.util.Map;
import java.util.Optional;

public class ReferenceRegistry<R extends ObjectReference<String, ?>> {

    private final IdRegistry<R> references = new IdRegistry<>();

    public void track(@NonNull R reference) {
        references.replace(reference.getId(), reference);
    }

    public Optional<R> get(@NonNull String id) {
        return references.get(id);
    }

    public void untrack(@NonNull String id) {
        references.unregister(id);
    }

    public void clear() {
        references.clear();
    }

    public void invalidateAll() {
        references.values().forEach(ObjectReference::invalidateCache);
    }

    public Map<String, R> all() {
        return references.snapshot();
    }

}
