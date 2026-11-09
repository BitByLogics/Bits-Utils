package net.bitbylogic.utils.context.registry;

import net.bitbylogic.utils.context.Context;
import net.bitbylogic.utils.context.ContextKey;
import net.bitbylogic.utils.context.DefaultContextKeys;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ContextRegistryTest {

    @AfterEach
    void unregisterKey() {
        ContextRegistry.unregister("registry_test_key");
    }

    @Test
    void creatingAKeyDoesNotRegisterIt() {
        new ContextKey<>("registry_test_key", String.class);

        assertTrue(ContextRegistry.get("registry_test_key").isEmpty());
    }

    @Test
    void getAndRetrieveReturnsTheStoredValue() {
        ContextKey<String> key = new ContextKey<>("registry_test_key", String.class);
        ContextRegistry.register(key);
        Context context = new Context();
        context.put(key, "value");

        assertEquals("value", ContextRegistry.getAndRetrieve(context, "registry_test_key").orElseThrow());
    }

    @Test
    void getAndRetrieveIsEmptyWhenValueMissing() {
        ContextRegistry.register(new ContextKey<>("registry_test_key", String.class));

        assertTrue(ContextRegistry.getAndRetrieve(new Context(), "registry_test_key").isEmpty());
    }

    @Test
    void duplicateRegistrationKeepsTheFirstKeyAndReportsIt() {
        ContextKey<String> first = new ContextKey<>("registry_test_key", String.class);
        ContextKey<String> second = new ContextKey<>("registry_test_key", String.class);

        assertTrue(ContextRegistry.register(first));
        assertFalse(ContextRegistry.register(second));
        assertSame(first, ContextRegistry.get("registry_test_key").orElseThrow());
    }

    @Test
    void defaultKeysAreAlwaysRegistered() {
        assertSame(DefaultContextKeys.LOCALE, ContextRegistry.get("locale").orElseThrow());
        assertSame(DefaultContextKeys.ACTION_DATA, ContextRegistry.get("action_data").orElseThrow());
    }

}
