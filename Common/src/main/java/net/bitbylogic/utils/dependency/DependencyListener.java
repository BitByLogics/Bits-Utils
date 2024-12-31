package net.bitbylogic.utils.dependency;

public interface DependencyListener {

    void onRegistered(Class<?> dependencyClass, Object dependency);

}
