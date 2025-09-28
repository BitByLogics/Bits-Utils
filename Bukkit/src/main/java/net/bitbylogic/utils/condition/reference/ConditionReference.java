package net.bitbylogic.utils.condition.reference;

import lombok.NonNull;
import net.bitbylogic.utils.condition.Condition;
import net.bitbylogic.utils.condition.ConditionRegistry;
import net.bitbylogic.utils.reference.ObjectReference;

public class ConditionReference extends ObjectReference<String, Condition> {

    public ConditionReference(@NonNull String id) {
        super(id, unused -> ConditionRegistry.get(id));

        ConditionReferenceRegistry.track(this);
    }

}
