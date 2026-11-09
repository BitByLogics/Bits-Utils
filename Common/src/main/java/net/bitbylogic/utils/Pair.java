package net.bitbylogic.utils;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Objects;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Pair<K, V> {

    private K key;
    private V value;

    @Override
    public boolean equals(final Object o) {
        if (!(o instanceof Pair<?, ?> pair)) {
            return false;
        }

        return Objects.equals(pair.getKey(), key) && Objects.equals(pair.getValue(), value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(key, value);
    }

}
