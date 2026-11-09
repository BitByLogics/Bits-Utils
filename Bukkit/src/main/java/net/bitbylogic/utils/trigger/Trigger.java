package net.bitbylogic.utils.trigger;

import lombok.NonNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public interface Trigger {

    @NonNull String getId();

    boolean isRepeatable();

    boolean isDeactivateChildren();

    @NonNull List<Trigger> getChildren();

    void setAction(@NonNull TriggerAction action);

    @Nullable TriggerAction getAction();

    @NonNull TriggerInstance createInstance();

}
