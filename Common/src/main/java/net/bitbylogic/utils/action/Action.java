package net.bitbylogic.utils.action;

import lombok.NonNull;
import net.bitbylogic.utils.context.Context;

public interface Action {

    String getId();

    boolean execute(@NonNull Context context);

    default boolean canExecute(@NonNull Context context) {
        return true;
    }

}
