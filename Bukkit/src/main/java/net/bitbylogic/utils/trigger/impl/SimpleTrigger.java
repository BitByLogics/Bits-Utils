package net.bitbylogic.utils.trigger.impl;

import lombok.NonNull;
import net.bitbylogic.utils.config.metadata.ConfiguredMetadata;
import net.bitbylogic.utils.trigger.Trigger;
import net.bitbylogic.utils.trigger.TriggerAction;
import net.bitbylogic.utils.trigger.TriggerInstance;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public abstract class SimpleTrigger implements Trigger {

    private final List<Trigger> children = new CopyOnWriteArrayList<>();

    private final @NonNull String id;

    private volatile boolean repeatable;
    private volatile boolean deactivateChildren;
    private volatile @Nullable TriggerAction action;

    protected SimpleTrigger(@NonNull String id, @NonNull ConfiguredMetadata metadata) {
        this.id = id;

        repeatable = metadata.getDeepValueAsOrDefault("Repeatable", false);
        deactivateChildren = metadata.getDeepValueAsOrDefault("Deactivate-Children", false);
    }

    @Override
    public @NonNull String getId() {
        return id;
    }

    @Override
    public boolean isRepeatable() {
        return repeatable;
    }

    public void setRepeatable(boolean repeatable) {
        this.repeatable = repeatable;
    }

    @Override
    public boolean isDeactivateChildren() {
        return deactivateChildren;
    }

    protected void setDeactivateChildren(boolean deactivateChildren) {
        this.deactivateChildren = deactivateChildren;
    }

    @Override
    public @NonNull List<Trigger> getChildren() {
        return List.copyOf(children);
    }

    public void addChild(@NonNull Trigger child) {
        children.add(child);
    }

    @Override
    public void setAction(@NonNull TriggerAction action) {
        this.action = action;
    }

    @Override
    public @Nullable TriggerAction getAction() {
        return action;
    }

    @Override
    public @NonNull TriggerInstance createInstance() {
        return new TriggerInstance(this);
    }

}
