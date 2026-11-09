package net.bitbylogic.utils.action.reference;

import lombok.NonNull;
import net.bitbylogic.utils.action.Action;
import net.bitbylogic.utils.action.ActionRegistry;
import net.bitbylogic.utils.reference.ObjectReference;

public class ActionReference extends ObjectReference<String, Action> {

    public ActionReference(@NonNull String id) {
        super(id, unused -> ActionRegistry.get(id));

        ActionReferenceRegistry.track(this);
    }

}
