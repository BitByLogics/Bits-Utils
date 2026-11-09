package net.bitbylogic.utils.condition;

import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertThrows;

class ConditionRegistryTest {

    @Test
    void idsAreASnapshot() {
        Set<String> ids = ConditionRegistry.getIds();

        assertThrows(UnsupportedOperationException.class, () -> ids.add("other"));
    }

}
