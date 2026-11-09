package net.bitbylogic.utils.reflection;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

class ReflectionUtilTest {

    static class Target {

        final String name;
        final int amount;

        Target(String name, int amount) {
            this.name = name;
            this.amount = amount;
        }

    }

    @Test
    void constructorIsFoundBySuppliedTypesInAnyOrder() throws Exception {
        Target target = ReflectionUtil.findAndCallConstructor(Target.class, 5, "name");

        assertNotNull(target);
        assertEquals("name", target.name);
        assertEquals(5, target.amount);
    }

    @Test
    void subclassArgumentsAreAccepted() throws Exception {
        Target target = ReflectionUtil.findAndCallConstructor(Target.class, "name", 3);

        assertNotNull(target);
        assertEquals(3, target.amount);
    }

    @Test
    void mismatchedTypesReturnNull() throws Exception {
        assertNull(ReflectionUtil.findAndCallConstructor(Target.class, 1.5, "name"));
        assertNull(ReflectionUtil.findAndCallConstructor(Target.class, "name"));
    }

}
