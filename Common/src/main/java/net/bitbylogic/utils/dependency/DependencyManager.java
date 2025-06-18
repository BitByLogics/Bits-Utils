package net.bitbylogic.utils.dependency;

import lombok.Getter;
import lombok.NonNull;
import net.bitbylogic.utils.dependency.annotation.Dependency;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.stream.Collectors;

@Getter
public class DependencyManager {

    private final HashMap<Class<?>, Object> dependencies;
    private final HashMap<Class<?>, List<Object>> missingDependencies;
    private final List<DependencyListener> dependencyListeners;

    public DependencyManager() {
        this.dependencies = new HashMap<>();
        this.missingDependencies = new HashMap<>();
        this.dependencyListeners = new ArrayList<>();

        registerDependency(this.getClass(), this);
    }

    public <T> void registerDependency(Class<? extends T> clazz, T instance) {
        if (dependencies.containsKey(clazz)) {
            throw new IllegalStateException("There is already an instance of " + clazz.getSimpleName() + " registered!");
        }

        dependencies.put(clazz, instance);

        if (!missingDependencies.containsKey(clazz)) {
            return;
        }

        missingDependencies.get(clazz).forEach(obj -> injectDependencies(obj, true));
        missingDependencies.remove(clazz);
    }

    public void injectDependencies(@NonNull Object object, boolean deepInjection) {
        injectFieldDependencies(object, deepInjection);

        if (missingDependencies.values().stream().anyMatch(objects -> objects.contains(object))) {
            return;
        }

        getDependencyMethods(object).forEach(method -> {
            try {
                method.invoke(object);
            } catch (IllegalAccessException | InvocationTargetException e) {
                e.printStackTrace();
            }
        });
    }

    public boolean isDependencyRegistered(@NonNull Class<?> clazz) {
        return dependencies.containsKey(clazz);
    }

    public List<Class<?>> getDependencies(@NonNull Object object, boolean deepCheck) {
        return getDependencyFields(object, deepCheck).stream()
                .map(field -> (Class<?>) field.getType())
                .collect(Collectors.toList());
    }

    private void injectFieldDependencies(@NonNull Object object, boolean deepInjection) {
        getDependencyFields(object, deepInjection).forEach(field -> {
            resolveDependency(field).ifPresentOrElse(
                    dependency -> injectIntoField(object, field, dependency),
                    () -> handleMissingDependency(field, object)
            );
        });
    }

    private Optional<Object> resolveDependency(@NonNull Field field) {
        return Optional.ofNullable(dependencies.get(field.getType()));
    }

    private void handleMissingDependency(@NonNull Field field, @NonNull Object object) {
        List<Object> pendingObjects = missingDependencies.getOrDefault(field.getType(), new ArrayList<>());

        if (pendingObjects.contains(object)) {
            return;
        }

        pendingObjects.add(object);
        missingDependencies.put(field.getType(), pendingObjects);
    }

    private void injectIntoField(@NonNull Object object, @NonNull Field field, @NonNull Object dependency) {
        boolean wasAccessible = field.canAccess(object);

        if (!wasAccessible && !field.trySetAccessible()) {
            return;
        }

        try {
            field.set(object, dependency);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } finally {
            if (!wasAccessible) {
                field.setAccessible(false);
            }
        }
    }

    private Set<Field> getDependencyFields(@NonNull Object object, boolean deepInjection) {
        Class<?> clazz = object.getClass();
        Set<Field> dependencyFields = new HashSet<>();

        do {
            for (Field field : clazz.getDeclaredFields()) {
                if (!field.isAnnotationPresent(Dependency.class)) {
                    continue;
                }

                dependencyFields.add(field);
            }

            clazz = clazz.getSuperclass();
        } while (deepInjection && clazz != null);

        return Set.copyOf(dependencyFields);
    }

    private Set<Method> getDependencyMethods(@NonNull Object object) {
        Class<?> clazz = object.getClass();
        Set<Method> dependencyMethods = new HashSet<>();

        for (Method method : clazz.getDeclaredMethods()) {
            if (!method.isAnnotationPresent(Dependency.class) || method.getParameterCount() > 0) {
                continue;
            }

            dependencyMethods.add(method);
        }

        return Set.copyOf(dependencyMethods);
    }

    public void registerListener(@NonNull DependencyListener listener) {
        dependencyListeners.add(listener);
        dependencies.forEach(listener::onRegistered);
    }

}
