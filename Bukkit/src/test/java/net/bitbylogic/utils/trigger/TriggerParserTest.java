package net.bitbylogic.utils.trigger;

import lombok.NonNull;
import net.bitbylogic.utils.config.metadata.ConfiguredMetadata;
import net.bitbylogic.utils.context.Context;
import net.bitbylogic.utils.trigger.impl.SimpleTrigger;
import org.bukkit.configuration.MemoryConfiguration;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;

class TriggerParserTest {

    static class TestTrigger extends SimpleTrigger {

        TestTrigger(String id, ConfiguredMetadata metadata) {
            super(id, metadata);
        }

        @Override
        public void onDeactivate(@NonNull Context context) {
        }

    }

    private final TriggerAction action = context -> {
    };

    @AfterEach
    void unregisterTriggers() {
        TriggerRegistry.unregister("parser_registered");
        TriggerRegistry.unregister("parser_deferred");
    }

    private MemoryConfiguration sectionFor(String triggerId) {
        MemoryConfiguration root = new MemoryConfiguration();
        root.set("one.Trigger-ID", triggerId);
        return root;
    }

    @Test
    void registeredTriggerReceivesTheAction() {
        TriggerRegistry.register("parser_registered", metadata -> new TestTrigger("parser_registered", metadata));

        List<Trigger> triggers = TriggerParser.loadTriggers(sectionFor("parser_registered"), action);

        assertEquals(1, triggers.size());
        assertSame(action, triggers.getFirst().getAction());
    }

    @Test
    void triggerWithoutActionKeepsNoAction() {
        TriggerRegistry.register("parser_registered", metadata -> new TestTrigger("parser_registered", metadata));

        List<Trigger> triggers = TriggerParser.loadTriggers(sectionFor("parser_registered"));

        assertNull(triggers.getFirst().getAction());
    }

    @Test
    void deferredTriggerReceivesTheActionOnceResolved() {
        List<Trigger> triggers = TriggerParser.loadTriggers(sectionFor("parser_deferred"), action);

        assertInstanceOf(DeferredTrigger.class, triggers.getFirst());

        TriggerRegistry.register("parser_deferred", metadata -> new TestTrigger("parser_deferred", metadata));

        assertSame(action, triggers.getFirst().getAction());
    }

}
