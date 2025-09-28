package net.bitbylogic.utils.trigger;

import lombok.NonNull;
import net.bitbylogic.utils.config.metadata.ConfiguredMetadata;
import net.bitbylogic.utils.context.Context;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;

/**
 * A placeholder trigger that resolves its actual implementation lazily
 * via the TriggerRegistry. Supports callbacks when the delegate becomes available.
 */
public class DeferredTrigger implements Trigger {

    private final String targetId;
    private final ConfiguredMetadata metadata;

    private volatile @Nullable Trigger delegate;

    private final List<Consumer<Trigger>> whenAvailableCallbacks = new CopyOnWriteArrayList<>();

    public DeferredTrigger(@NonNull String targetId, @NonNull ConfiguredMetadata metadata) {
        this.targetId = targetId;
        this.metadata = metadata;
    }

    private synchronized Trigger resolve() {
        if (delegate == null) {
            Optional<Trigger> provided = TriggerRegistry.get(targetId)
                    .map(provider -> provider.provide(metadata));

            if (provided.isEmpty()) {
                throw new IllegalStateException("Could not resolve trigger: " + targetId);
            }

            delegate = provided.get();

            for (Consumer<Trigger> callback : whenAvailableCallbacks) {
                callback.accept(delegate);
            }

            whenAvailableCallbacks.clear();
        }
        return delegate;
    }

    /**
     * Registers a callback to run when the delegate trigger becomes available.
     * If already resolved, the callback runs immediately.
     */
    public void whenAvailable(@NonNull Consumer<Trigger> callback) {
        if(delegate != null) {
            callback.accept(delegate);
            return;
        }

        whenAvailableCallbacks.add(callback);
    }

    @Override
    public @NonNull String getId() {
        return targetId;
    }

    @Override
    public boolean isActive() {
        return resolve().isActive();
    }

    @Override
    public void setActive(boolean active) {
        resolve().setActive(active);
    }

    @Override
    public boolean isRepeatable() {
        return resolve().isRepeatable();
    }

    @Override
    public void setRepeatable(boolean repeatable) {
        resolve().setRepeatable(repeatable);
    }

    @Override
    public boolean isDeactivateChildren() {
        return resolve().isDeactivateChildren();
    }

    @Override
    public @NonNull List<Trigger> getChildren() {
        return resolve().getChildren();
    }

    @Override
    public void setAction(@NonNull TriggerAction action) {
        resolve().setAction(action);
    }

    @Override
    public @Nullable TriggerAction getAction() {
        return resolve().getAction();
    }

    @Override
    public void trigger(@NonNull Context context) {
        resolve().trigger(context);
    }

    @Override
    public void onDeactivate(@NonNull Context context) {
        resolve().onDeactivate(context);
    }

    @Override
    public @NonNull Trigger clone() {
        DeferredTrigger cloned = new DeferredTrigger(targetId, metadata);

        if (delegate != null) {
            cloned.delegate = delegate.clone();
        }

        return cloned;
    }

}