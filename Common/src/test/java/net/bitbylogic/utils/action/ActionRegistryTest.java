package net.bitbylogic.utils.action;

import lombok.NonNull;
import net.bitbylogic.utils.context.Context;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ActionRegistryTest {

    static class TestAction implements Action {

        private final String id;

        TestAction(String id) {
            this.id = id;
        }

        @Override
        public String getId() {
            return id;
        }

        @Override
        public boolean execute(@NonNull Context context) {
            return true;
        }

    }

    @AfterEach
    void clearRegistry() {
        ActionRegistry.clear();
    }

    @Test
    void duplicateRegistrationKeepsFirstAction() {
        TestAction first = new TestAction("Greet");
        TestAction second = new TestAction("greet");

        ActionRegistry.register(first);
        ActionRegistry.register(second);

        assertSame(first, ActionRegistry.get("GREET").orElseThrow());
    }

    @Test
    void varargsRegistrationKeepsFirstAction() {
        TestAction first = new TestAction("greet");
        TestAction second = new TestAction("greet");

        ActionRegistry.register(first, second);

        assertSame(first, ActionRegistry.get("greet").orElseThrow());
    }

    @Test
    void idsAreACopy() {
        ActionRegistry.register(new TestAction("greet"));

        Set<String> ids = ActionRegistry.getIds();

        assertEquals(Set.of("greet"), ids);
        assertThrows(UnsupportedOperationException.class, () -> ids.add("other"));
    }

}
