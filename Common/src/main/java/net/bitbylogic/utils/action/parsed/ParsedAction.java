package net.bitbylogic.utils.action.parsed;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import net.bitbylogic.utils.action.Action;
import net.bitbylogic.utils.action.data.ActionData;
import net.bitbylogic.utils.action.reference.ActionReference;
import net.bitbylogic.utils.context.Context;
import net.bitbylogic.utils.context.DefaultContextKeys;

import java.util.Optional;

@RequiredArgsConstructor
public class ParsedAction {

    private final ActionReference actionReference;
    private final ActionData<?> data;

    public boolean execute(@NonNull Context context) {
        Optional<Action> action = actionReference.get();

        if (action.isEmpty()) {
            return false;
        }

        Context actionContext = context.copy();
        actionContext.put(DefaultContextKeys.ACTION_DATA, data);

        if (!action.get().canExecute(actionContext)) {
            return false;
        }

        return action.get().execute(actionContext);
    }

}
