package net.bitbylogic.utils.action;

import lombok.NonNull;
import net.bitbylogic.utils.action.data.StringActionData;
import net.bitbylogic.utils.action.parsed.ParsedAction;
import net.bitbylogic.utils.action.reference.ActionReference;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ActionParser {

    public static List<ParsedAction> parseActions(@NonNull List<String> dataList) {
        List<ParsedAction> actions = new ArrayList<>();
        dataList.forEach(actionData -> parseAction(actionData).ifPresent(actions::add));
        return actions;
    }

    public static Optional<ParsedAction> parseAction(@NonNull String actionData) {
        String[] data = actionData.split(":", 2);
        String id = data[0];

        return Optional.of(new ParsedAction(new ActionReference(id), new StringActionData(actionData)));
    }

}
