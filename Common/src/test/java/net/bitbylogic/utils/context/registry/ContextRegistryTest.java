package net.bitbylogic.utils.context.registry;

import net.bitbylogic.utils.context.Context;
import net.bitbylogic.utils.context.ContextKey;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ContextRegistryTest {

    @AfterEach
    void unregisterKey() {
        ContextRegistry.unregister("registry_test_key");
    }

    @Test
    void getAndRetrieveReturnsTheStoredValue() {
        ContextKey<String> key = new ContextKey<>("registry_test_key", String.class);
        Context context = new Context();
        context.put(key, "value");

        assertEquals("value", ContextRegistry.getAndRetrieve(context, "registry_test_key").orElseThrow());
    }

    @Test
    void getAndRetrieveIsEmptyWhenValueMissing() {
        new ContextKey<>("registry_test_key", String.class);

        assertTrue(ContextRegistry.getAndRetrieve(new Context(), "registry_test_key").isEmpty());
    }

}
