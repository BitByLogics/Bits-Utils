package net.bitbylogic.utils.context;

import lombok.Getter;
import lombok.NonNull;
import net.bitbylogic.utils.context.registry.ContextRegistry;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Type;
import java.util.Objects;
import java.util.function.Function;

@Getter
public final class ContextKey<T> {

    private final @NonNull String id;
    private final @NonNull Class<T> rawType;
    private final @NonNull Type genericType;

    private final @Nullable Function<Context, T> provider;

    public ContextKey(@NonNull String id,
                      @NonNull Class<T> rawType,
                      @NonNull Type genericType,
                      @Nullable Function<Context, T> provider) {
        this.id = id;
        this.rawType = rawType;
        this.genericType = genericType;
        this.provider = provider;

        ContextRegistry.register(this);
    }

    public ContextKey(@NonNull String id, @NonNull Class<T> rawType, @NonNull Type genericType) {
        this(id, rawType, genericType, null);
    }

    public ContextKey(@NonNull String id, @NonNull Class<T> rawType) {
        this(id, rawType, rawType, null); // default: genericType = rawType
    }

    public T provide(@NonNull Context context) {
        if (provider == null) throw new IllegalStateException("No provider for key: " + id);
        return provider.apply(context);
    }

    @Override
    public boolean equals(Object object) {
        if (object == null || getClass() != object.getClass()) return false;
        ContextKey<?> that = (ContextKey<?>) object;
        return Objects.equals(id, that.id)
                && Objects.equals(rawType, that.rawType)
                && Objects.equals(genericType, that.genericType)
                && Objects.equals(provider, that.provider);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, rawType, genericType, provider);
    }

}
