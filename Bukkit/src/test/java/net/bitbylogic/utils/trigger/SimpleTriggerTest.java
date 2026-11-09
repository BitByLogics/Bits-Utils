package net.bitbylogic.utils.trigger;

import net.bitbylogic.utils.config.metadata.ConfiguredMetadata;
import net.bitbylogic.utils.context.ContextBuilder;
import net.bitbylogic.utils.trigger.impl.SimpleTrigger;
import org.bukkit.configuration.MemoryConfiguration;
import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SimpleTriggerTest {

    static class TestTrigger extends SimpleTrigger {

        TestTrigger(String id) {
            super(id, new ConfiguredMetadata(new MemoryConfiguration()));
        }

        TestTrigger(String id, MemoryConfiguration configuration) {
            super(id, new ConfiguredMetadata(configuration));
        }

    }

    @Test
    void definitionChildrenAreASnapshot() {
        TestTrigger parent = new TestTrigger("parent");
        parent.addChild(new TestTrigger("child-a"));

        assertThrows(UnsupportedOperationException.class, () -> parent.getChildren().add(new TestTrigger("other")));
        assertEquals(1, parent.getChildren().size());
    }

    @Test
    void instancesGetTheirOwnChildInstances() {
        TestTrigger parent = new TestTrigger("parent");
        parent.addChild(new TestTrigger("child"));

        TriggerInstance first = parent.createInstance();
        TriggerInstance second = parent.createInstance();

        assertEquals(1, first.getChildren().size());
        assertNotSame(first.getChildren().getFirst(), second.getChildren().getFirst());
        assertEquals("child", first.getChildren().getFirst().getId());
    }

    @Test
    void instancesDoNotShareActiveState() {
        TestTrigger trigger = new TestTrigger("trigger");

        TriggerInstance first = trigger.createInstance();
        TriggerInstance second = trigger.createInstance();
        first.activate();

        assertTrue(first.isActive());
        assertFalse(second.isActive());
    }

    @Test
    void inactiveInstanceDoesNotRunTheAction() {
        TestTrigger trigger = new TestTrigger("trigger");
        AtomicInteger calls = new AtomicInteger();
        trigger.setAction(context -> calls.incrementAndGet());

        trigger.createInstance().trigger(ContextBuilder.create().build());

        assertEquals(0, calls.get());
    }

    @Test
    void nonRepeatableInstanceFiresOnce() {
        TestTrigger trigger = new TestTrigger("trigger");
        AtomicInteger calls = new AtomicInteger();
        trigger.setAction(context -> calls.incrementAndGet());

        TriggerInstance instance = trigger.createInstance();
        instance.activate();
        instance.trigger(ContextBuilder.create().build());
        instance.trigger(ContextBuilder.create().build());

        assertEquals(1, calls.get());
        assertFalse(instance.isActive());
    }

    @Test
    void repeatableInstanceKeepsFiring() {
        MemoryConfiguration configuration = new MemoryConfiguration();
        configuration.set("Repeatable", true);
        TestTrigger trigger = new TestTrigger("trigger", configuration);
        AtomicInteger calls = new AtomicInteger();
        trigger.setAction(context -> calls.incrementAndGet());

        TriggerInstance instance = trigger.createInstance();
        instance.activate();
        instance.trigger(ContextBuilder.create().build());
        instance.trigger(ContextBuilder.create().build());

        assertEquals(2, calls.get());
        assertTrue(instance.isActive());
    }

    @Test
    void firingDeactivatesChildInstancesWhenConfigured() {
        MemoryConfiguration configuration = new MemoryConfiguration();
        configuration.set("Deactivate-Children", true);
        TestTrigger parent = new TestTrigger("parent", configuration);
        parent.addChild(new TestTrigger("child"));

        TriggerInstance instance = parent.createInstance();
        TriggerInstance child = instance.getChildren().getFirst();
        instance.activate();
        child.activate();
        instance.trigger(ContextBuilder.create().build());

        assertFalse(child.isActive());
    }

    @Test
    void instanceUsesTheDefinitionAction() {
        TestTrigger trigger = new TestTrigger("trigger");
        TriggerAction action = context -> {
        };
        trigger.setAction(action);

        assertSame(action, trigger.createInstance().getTrigger().getAction());
    }

}
