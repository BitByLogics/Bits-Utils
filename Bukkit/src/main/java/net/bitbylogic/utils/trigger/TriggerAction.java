package net.bitbylogic.utils.trigger;

import lombok.NonNull;
import net.bitbylogic.utils.context.Context;

public interface TriggerAction {

    void onTrigger(@NonNull Context context);

}
