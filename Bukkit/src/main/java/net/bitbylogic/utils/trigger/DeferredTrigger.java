package net.bitbylogic.utils.trigger;

import lombok.NonNull;
import net.bitbylogic.utils.config.metadata.ConfiguredMetadata;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;

public class DeferredTrigger implements Trigger {

    private static final Logger LOGGER = Logger.getLogger(DeferredTrigger.class.getName());

    private final Object lock = new Object();

    private final String targetId;
    private final ConfiguredMetadata metadata;

    private volatile @Nullable Trigger delegate;
    private volatile @Nullable TriggerAction action;

    public DeferredTrigger(@NonNull String targetId, @NonNull ConfiguredMetadata metadata) {
        this.targetId = targetId;
        this.metadata = metadata;
    }

    public Optional<Trigger> resolve() {
        Trigger resolved = delegate;

        if (resolved != null) {
            return Optional.of(resolved);
        }

        synchronized (lock) {
            if (delegate != null) {
                return Optional.of(delegate);
            }

            Optional<TriggerProvider> provider = TriggerRegistry.get(targetId);

            if (provider.isEmpty()) {
                return Optional.empty();
            }

            Trigger provided = provider.get().provide(metadata);

            if (provided == null) {
                return Optional.empty();
            }

            if (action != null) {
                provided.setAction(action);
            }

            delegate = provided;
            return Optional.of(provided);
        }
    }

    @Override
    public @NonNull String getId() {
        return targetId;
    }

    @Override
    public boolean isRepeatable() {
        return resolve().map(Trigger::isRepeatable).orElse(false);
    }

    @Override
    public boolean isDeactivateChildren() {
        return resolve().map(Trigger::isDeactivateChildren).orElse(false);
    }

    @Override
    public @NonNull List<Trigger> getChildren() {
        return resolve().map(Trigger::getChildren).orElse(List.of());
    }

    @Override
    public void setAction(@NonNull TriggerAction action) {
        synchronized (lock) {
            this.action = action;

            if (delegate != null) {
                delegate.setAction(action);
            }
        }
    }

    @Override
    public @Nullable TriggerAction getAction() {
        return action;
    }

    @Override
    public @NonNull TriggerInstance createInstance() {
        Optional<Trigger> resolved = resolve();

        if (resolved.isPresent()) {
            return resolved.get().createInstance();
        }

        LOGGER.warning("Could not resolve trigger '" + targetId + "', the instance will never fire.");
        return new InertTriggerInstance(this);
    }

    private static final class InertTriggerInstance extends TriggerInstance {

        private InertTriggerInstance(Trigger trigger) {
            super(trigger);
        }

        @Override
        public void activate() {

        }

    }

}
