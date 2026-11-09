package net.bitbylogic.utils.reference;

import org.junit.jupiter.api.Test;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ObjectReferenceTest {

    @Test
    void getResolvesOnceAndCaches() {
        AtomicInteger resolves = new AtomicInteger();
        ObjectReference<String, String> reference = new ObjectReference<>("id", id -> {
            resolves.incrementAndGet();
            return Optional.of("value");
        });

        reference.get();
        reference.get();

        assertEquals(1, resolves.get());
        assertTrue(reference.isCached());
    }

    @Test
    void invalidateForcesANewResolve() {
        AtomicInteger resolves = new AtomicInteger();
        ObjectReference<String, String> reference = new ObjectReference<>("id", id -> {
            resolves.incrementAndGet();
            return Optional.of("value");
        });

        reference.get();
        reference.invalidateCache();
        reference.get();

        assertEquals(2, resolves.get());
    }

    @Test
    void missingTargetIsNotCached() {
        ObjectReference<String, String> reference = new ObjectReference<>("id", id -> Optional.empty());

        assertTrue(reference.get().isEmpty());
        assertFalse(reference.isCached());
    }

}
