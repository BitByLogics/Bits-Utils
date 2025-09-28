package net.bitbylogic.utils.action;

import lombok.NonNull;
import net.bitbylogic.utils.context.Context;

public interface Action {

    String getId();

    boolean execute(@NonNull Context context);

    boolean canExecute(@NonNull Context context);

}
