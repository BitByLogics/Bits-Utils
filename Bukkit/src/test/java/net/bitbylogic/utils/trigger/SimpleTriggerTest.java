package net.bitbylogic.utils.trigger;

import lombok.NonNull;
import net.bitbylogic.utils.config.metadata.ConfiguredMetadata;
import net.bitbylogic.utils.context.Context;
import net.bitbylogic.utils.trigger.impl.SimpleTrigger;
import org.bukkit.configuration.MemoryConfiguration;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertSame;

class SimpleTriggerTest {

    static class TestTrigger extends SimpleTrigger {

        TestTrigger(String id) {
            super(id, new ConfiguredMetadata(new MemoryConfiguration()));
        }

        @Override
        public void onDeactivate(@NonNull Context context) {
        }

    }

    @Test
    void cloneKeepsOriginalChildren() {
        TestTrigger parent = new TestTrigger("parent");
        parent.getChildren().add(new TestTrigger("child-a"));
        parent.getChildren().add(new TestTrigger("child-b"));

        Trigger cloned = parent.clone();

        assertEquals(2, parent.getChildren().size());
        assertEquals(2, cloned.getChildren().size());
    }

    @Test
    void cloneHasIndependentChildren() {
        TestTrigger parent = new TestTrigger("parent");
        TestTrigger child = new TestTrigger("child");
        parent.getChildren().add(child);

        Trigger cloned = parent.clone();

        assertNotSame(parent.getChildren(), cloned.getChildren());
        assertNotSame(child, cloned.getChildren().getFirst());
        assertEquals("child", cloned.getChildren().getFirst().getId());

        cloned.getChildren().clear();

        assertEquals(1, parent.getChildren().size());
    }

    @Test
    void cloneKeepsAction() {
        TestTrigger trigger = new TestTrigger("trigger");
        TriggerAction action = context -> {
        };
        trigger.setAction(action);

        Trigger cloned = trigger.clone();

        assertSame(action, cloned.getAction());
    }

}
