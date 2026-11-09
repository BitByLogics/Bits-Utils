package net.bitbylogic.utils.trigger;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TriggerRegistryTest {

    private static final TriggerProvider PROVIDER = metadata -> null;

    @AfterEach
    void unregisterTestIds() {
        TriggerRegistry.unregister("test_trigger");
    }

    @Test
    void lookupIsCaseInsensitive() {
        TriggerRegistry.register("Test_Trigger", PROVIDER);

        assertTrue(TriggerRegistry.contains("TEST_TRIGGER"));
        assertTrue(TriggerRegistry.get("test_trigger").isPresent());
    }

    @Test
    void duplicateRegistrationThrows() {
        TriggerRegistry.register("test_trigger", PROVIDER);

        assertThrows(IllegalArgumentException.class, () -> TriggerRegistry.register("TEST_TRIGGER", PROVIDER));
    }

    @Test
    void pendingCallbackFiresRegardlessOfCase() {
        AtomicInteger calls = new AtomicInteger();

        TriggerRegistry.whenAvailable("Test_Trigger", provider -> calls.incrementAndGet());
        TriggerRegistry.register("test_trigger", PROVIDER);

        assertEquals(1, calls.get());
    }

    @Test
    void callbackRunsImmediatelyWhenAlreadyRegistered() {
        AtomicInteger calls = new AtomicInteger();

        TriggerRegistry.register("test_trigger", PROVIDER);
        TriggerRegistry.whenAvailable("TEST_TRIGGER", provider -> calls.incrementAndGet());

        assertEquals(1, calls.get());
    }

    @Test
    void idsAreACopy() {
        TriggerRegistry.register("test_trigger", PROVIDER);

        assertThrows(UnsupportedOperationException.class, () -> TriggerRegistry.getIds().add("other"));
    }

}
