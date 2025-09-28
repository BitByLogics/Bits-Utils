package net.bitbylogic.utils.reference;

import java.util.Optional;

public interface ReferenceResolver<K, O> {

    Optional<O> resolve(K id);

}
