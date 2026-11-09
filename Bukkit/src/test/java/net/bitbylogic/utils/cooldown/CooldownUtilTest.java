package net.bitbylogic.utils.cooldown;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CooldownUtilTest {

    private final UUID identifier = UUID.randomUUID();

    @AfterEach
    void endCooldowns() {
        CooldownUtil.endCooldown("test", identifier);
    }

    @Test
    void attemptRunRunsOnceWhileCooldownIsActive() {
        AtomicInteger runs = new AtomicInteger();

        CooldownUtil.attemptRun("test", identifier, 10, TimeUnit.SECONDS, runs::incrementAndGet);
        CooldownUtil.attemptRun("test", identifier, 10, TimeUnit.SECONDS, runs::incrementAndGet);

        assertEquals(1, runs.get());
        assertTrue(CooldownUtil.hasCooldown("TEST", identifier));
    }

    @Test
    void attemptRunRunsAgainAfterCooldownExpires() throws InterruptedException {
        AtomicInteger runs = new AtomicInteger();

        CooldownUtil.attemptRun("test", identifier, 20, TimeUnit.MILLISECONDS, runs::incrementAndGet);
        Thread.sleep(50);
        CooldownUtil.attemptRun("test", identifier, 10, TimeUnit.SECONDS, runs::incrementAndGet);

        assertEquals(2, runs.get());
        assertEquals(1, CooldownUtil.getCooldowns(identifier).size());
    }

    @Test
    void endCooldownRemovesIt() {
        CooldownUtil.attemptRun("test", identifier, 10, TimeUnit.SECONDS, () -> {
        });

        CooldownUtil.endCooldown("test", identifier);

        assertFalse(CooldownUtil.hasCooldown("test", identifier));
    }

    @Test
    void readingDoesNotCreateEntries() {
        assertTrue(CooldownUtil.getCooldowns(identifier).isEmpty());
        assertEquals(-1.0, CooldownUtil.getRemainingTime("test", identifier));
    }

    @Test
    void cooldownListIsASnapshot() {
        CooldownUtil.attemptRun("test", identifier, 10, TimeUnit.SECONDS, () -> {
        });

        assertThrows(UnsupportedOperationException.class, () -> CooldownUtil.getCooldowns(identifier).clear());
        assertTrue(CooldownUtil.hasCooldown("test", identifier));
    }

}
