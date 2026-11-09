package net.bitbylogic.utils.context;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ContextBuilderTest {

    private static final ContextKey<String> KEY = new ContextKey<>("builder_test_key", String.class);

    @Test
    void buildReturnsIndependentContexts() {
        ContextBuilder builder = ContextBuilder.create().with(KEY, "one");

        Context first = builder.build();
        Context second = builder.with(KEY, "two").build();

        assertEquals("one", first.get(KEY).orElseThrow());
        assertEquals("two", second.get(KEY).orElseThrow());
    }

    @Test
    void laterBuilderChangesDoNotReachBuiltContexts() {
        ContextBuilder builder = ContextBuilder.create();
        Context built = builder.build();

        builder.with(KEY, "late");

        assertFalse(built.has(KEY));
        assertTrue(builder.build().has(KEY));
    }

}
