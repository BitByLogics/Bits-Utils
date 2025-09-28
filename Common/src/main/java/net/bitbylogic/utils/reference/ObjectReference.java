package net.bitbylogic.utils.reference;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

/**
 * A thread-safe reference to an object that can be resolved on demand and cached.
 * The resolver is responsible for providing the actual instance from the ID.
 */
@RequiredArgsConstructor
public class ObjectReference<K, O> {

    @Getter
    private final @NonNull K id;

    private final @NonNull ReferenceResolver<K, O> resolver;

    private volatile @Nullable O cachedObject;

    /**
     * Attempts to resolve and return the referenced object.
     * If a cached instance exists but differs from the resolved one,
     * the cache is updated.
     */
    public Optional<O> get() {
        O resolved = resolver.resolve(id).orElse(null);
        if (resolved == null) {
            return Optional.empty();
        }

        O localCache = cachedObject;

        // Refresh if cache is empty OR stale
        if (localCache == null || localCache != resolved) {
            synchronized (this) {
                if (cachedObject == null || cachedObject != resolved) {
                    cachedObject = resolved;
                }
            }
        }

        return Optional.of(resolved);
    }

    /**
     * Returns true if a cached object exists.
     * Does NOT attempt to resolve.
     */
    public boolean isCached() {
        return cachedObject != null;
    }

    /**
     * Attempts to resolve without updating the cache.
     */
    public boolean forceValidate() {
        return resolver.resolve(id).isPresent();
    }

    /**
     * Clears the cached object so it must be resolved again.
     */
    public synchronized void invalidateCache() {
        cachedObject = null;
    }

    /**
     * Forces resolution and updates the cache immediately.
     *
     * @return an Optional containing the resolved object if successful,
     *         otherwise Optional.empty()
     */
    public Optional<O> refresh() {
        Optional<O> resolved = resolver.resolve(id);
        resolved.ifPresent(o -> {
            synchronized (this) {
                cachedObject = o;
            }
        });
        return resolved;
    }
}