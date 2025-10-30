package net.bitbylogic.utils.abstraction;

import lombok.NonNull;
import org.jetbrains.annotations.Nullable;

public interface MetadataHolder<K, V> {

    /**
     * Adds a metadata entry with the specified key and value.
     *
     * @param key the metadata key to add; must not be null
     * @param value the metadata value to associate with the key; must not be null
     */
    void addMetadata(@NonNull K key, @NonNull V value);

    /**
     * Removes the metadata associated with the specified key.
     *
     * @param key the key for which the associated metadata will be removed; must not be null
     */
    void removeMetadata(@NonNull K key);

    /**
     * Checks whether metadata associated with the specified key exists.
     *
     * @param key the key for which the presence of associated metadata is to be checked
     * @return true if metadata exists for the given key, false otherwise
     */
    boolean hasMetadata(@NonNull K key);

    /**
     * Retrieves the metadata value associated with the specified key.
     *
     * @param key the key for which the metadata value is to be retrieved; must not be null
     * @return the metadata value associated with the specified key, or null if no value is found
     */
    V getMetadata(@NonNull K key);

    /**
     * Retrieves the metadata value associated with the given key. If no value
     * is associated with the key, the fallback value is returned instead.
     *
     * @param key the key associated with the metadata value must not be null
     * @param fallback the fallback value to return if no metadata is found for the given key, may be null
     * @return the metadata value associated with the key if present, otherwise the fallback value
     */
    V getMetadata(@NonNull K key, @Nullable V fallback);

}
