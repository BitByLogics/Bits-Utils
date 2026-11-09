package net.bitbylogic.utils.registry;

import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class IdRegistryTest {

    @Test
    void idsAreCaseInsensitive() {
        IdRegistry<String> registry = new IdRegistry<>();
        registry.register("Alpha", "one");

        assertEquals("one", registry.get("ALPHA").orElseThrow());
        assertTrue(registry.contains("alpha"));
    }

    @Test
    void duplicateRegistrationKeepsTheFirstValue() {
        IdRegistry<String> registry = new IdRegistry<>();

        assertTrue(registry.register("a", "one"));
        assertFalse(registry.register("A", "two"));
        assertEquals("one", registry.get("a").orElseThrow());
    }

    @Test
    void replaceOverwrites() {
        IdRegistry<String> registry = new IdRegistry<>();
        registry.register("a", "one");
        registry.replace("a", "two");

        assertEquals("two", registry.get("a").orElseThrow());
    }

    @Test
    void unregisterReturnsTheRemovedValue() {
        IdRegistry<String> registry = new IdRegistry<>();
        registry.register("a", "one");

        assertEquals("one", registry.unregister("A").orElseThrow());
        assertTrue(registry.get("a").isEmpty());
    }

    @Test
    void idsAreASnapshot() {
        IdRegistry<String> registry = new IdRegistry<>();
        registry.register("a", "one");
        Set<String> ids = registry.ids();
        registry.register("b", "two");

        assertEquals(Set.of("a"), ids);
        assertThrows(UnsupportedOperationException.class, () -> ids.add("c"));
    }

}
