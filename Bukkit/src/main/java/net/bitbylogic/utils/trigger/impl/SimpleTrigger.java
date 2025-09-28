package net.bitbylogic.utils.trigger.impl;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import net.bitbylogic.utils.config.metadata.ConfiguredMetadata;
import net.bitbylogic.utils.context.Context;
import net.bitbylogic.utils.trigger.Trigger;
import net.bitbylogic.utils.trigger.TriggerAction;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Getter
public abstract class SimpleTrigger implements Trigger {

    private final List<Trigger> children = new ArrayList<>();

    private final @NonNull String id;

    private boolean repeatable;

    @Setter(AccessLevel.PROTECTED)
    private boolean deactivateChildren;

    private boolean active;

    private TriggerAction action;

    protected SimpleTrigger(@NonNull String id, @NonNull ConfiguredMetadata metadata) {
        this.id = id;

        repeatable = metadata.getDeepValueAsOrDefault("Repeatable", false);
        deactivateChildren = metadata.getDeepValueAsOrDefault("Deactivate-Children", false);
    }

    @Override
    public void trigger(@NonNull Context context) {
        if(!active) {
            return;
        }

        if(action != null) {
            action.onTrigger(context);
        }

        if(repeatable) {
            return;
        }

        active = false;
        onDeactivate(context);

        if(!deactivateChildren) {
            return;
        }

        children.forEach(trigger -> {
            trigger.setActive(false);
            trigger.onDeactivate(context);
        });
    }

    @Override
    public boolean isActive() {
        return active;
    }

    @Override
    public void setActive(boolean active) {
        this.active = active;
    }

    @Override
    public boolean isRepeatable() {
        return repeatable;
    }

    @Override
    public void setRepeatable(boolean repeatable) {
        this.repeatable = repeatable;
    }

    @Override
    public boolean isDeactivateChildren() {
        return deactivateChildren;
    }

    @Override
    public @NotNull List<Trigger> getChildren() {
        return children;
    }

    @Override
    public void setAction(@NonNull TriggerAction action) {
        this.action = action;
    }

    @Override
    public boolean equals(Object object) {
        if (object == null || getClass() != object.getClass()) return false;
        SimpleTrigger that = (SimpleTrigger) object;
        return repeatable == that.repeatable && deactivateChildren == that.deactivateChildren &&
                active == that.active && Objects.equals(children, that.children) &&
                Objects.equals(id, that.id) && Objects.equals(action, that.action);
    }

    @Override
    public int hashCode() {
        return Objects.hash(children, id, repeatable, deactivateChildren, active, action);
    }

    @Override
    public @NotNull Trigger clone() {
        try {
            Trigger cloned = (Trigger) super.clone();

            if(action != null) {
                cloned.setAction(action);
            }

            cloned.getChildren().clear();

            for (Trigger child : this.children) {
                cloned.getChildren().add(child.clone());
            }

            return cloned;
        } catch (CloneNotSupportedException e) {
            throw new AssertionError("Trigger should be cloneable", e);
        }
    }

}
