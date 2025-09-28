package net.bitbylogic.utils.trigger;

import lombok.NonNull;
import net.bitbylogic.utils.context.Context;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public interface Trigger extends Cloneable {

    @NonNull String getId();

    boolean isActive();

    void setActive(boolean active);

    boolean isRepeatable();

    void setRepeatable(boolean repeatable);

    boolean isDeactivateChildren();

    @NonNull List<Trigger> getChildren();

    void setAction(@NonNull TriggerAction action);

    @Nullable TriggerAction getAction();

    void trigger(@NonNull Context context);

    void onDeactivate(@NonNull Context context);

    @NonNull Trigger clone();

}
