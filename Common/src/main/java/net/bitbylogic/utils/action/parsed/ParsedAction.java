package net.bitbylogic.utils.action.parsed;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import net.bitbylogic.utils.action.data.ActionData;
import net.bitbylogic.utils.action.reference.ActionReference;
import net.bitbylogic.utils.context.Context;
import net.bitbylogic.utils.context.DefaultContextKeys;

@RequiredArgsConstructor
public class ParsedAction {

    private final ActionReference actionReference;
    private final ActionData<?> data;

    public void execute(@NonNull Context context) {
        context.put(DefaultContextKeys.ACTION_DATA, data);

        actionReference.get().ifPresent(action -> action.execute(context));
    }

}
