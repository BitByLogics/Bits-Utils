package net.bitbylogic.utils.action.parsed;

import lombok.NonNull;
import net.bitbylogic.utils.action.Action;
import net.bitbylogic.utils.action.ActionRegistry;
import net.bitbylogic.utils.action.data.StringActionData;
import net.bitbylogic.utils.action.reference.ActionReference;
import net.bitbylogic.utils.context.Context;
import net.bitbylogic.utils.context.DefaultContextKeys;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ParsedActionTest {

    static class CountingAction implements Action {

        private final boolean executable;
        private final boolean result;
        final AtomicInteger executions = new AtomicInteger();

        CountingAction(boolean executable, boolean result) {
            this.executable = executable;
            this.result = result;
        }

        @Override
        public String getId() {
            return "parsed_test";
        }

        @Override
        public boolean canExecute(@NonNull Context context) {
            return executable;
        }

        @Override
        public boolean execute(@NonNull Context context) {
            executions.incrementAndGet();
            return result;
        }

    }

    @AfterEach
    void clearRegistry() {
        ActionRegistry.clear();
    }

    private ParsedAction parsedFor(Action action) {
        ActionRegistry.register(action);
        return new ParsedAction(new ActionReference(action.getId()), new StringActionData("data"));
    }

    @Test
    void callerContextIsNotMutated() {
        Context context = new Context();

        parsedFor(new CountingAction(true, true)).execute(context);

        assertFalse(context.has(DefaultContextKeys.ACTION_DATA));
    }

    @Test
    void executionResultIsReturned() {
        assertTrue(parsedFor(new CountingAction(true, true)).execute(new Context()));

        ActionRegistry.clear();

        assertFalse(parsedFor(new CountingAction(true, false)).execute(new Context()));
    }

    @Test
    void actionsThatCannotExecuteAreSkipped() {
        CountingAction action = new CountingAction(false, true);

        assertFalse(parsedFor(action).execute(new Context()));
        assertEquals(0, action.executions.get());
    }

    @Test
    void missingActionReturnsFalse() {
        ParsedAction parsed = new ParsedAction(new ActionReference("missing_action"), new StringActionData("data"));

        assertFalse(parsed.execute(new Context()));
    }

}
