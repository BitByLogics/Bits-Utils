package net.bitbylogic.utils.dependency;

import net.bitbylogic.utils.dependency.annotation.Dependency;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DependencyManagerTest {

    static class Consumer {

        @Dependency
        private String value;

    }

    @Test
    void listenerIsNotifiedWhenDependencyRegisteredLater() {
        DependencyManager manager = new DependencyManager();
        List<Class<?>> registered = new ArrayList<>();

        manager.registerListener((dependencyClass, dependency) -> registered.add(dependencyClass));
        manager.registerDependency(String.class, "hello");

        assertTrue(registered.contains(String.class));
    }

    @Test
    void listenerIsNotifiedForExistingDependencies() {
        DependencyManager manager = new DependencyManager();
        List<Class<?>> registered = new ArrayList<>();

        manager.registerDependency(String.class, "hello");
        manager.registerListener((dependencyClass, dependency) -> registered.add(dependencyClass));

        assertTrue(registered.contains(String.class));
    }

    @Test
    void pendingObjectIsInjectedOnceDependencyArrives() {
        DependencyManager manager = new DependencyManager();
        Consumer consumer = new Consumer();

        manager.injectDependencies(consumer, false);
        manager.registerDependency(String.class, "hello");

        assertSame("hello", consumer.value);
    }

}
