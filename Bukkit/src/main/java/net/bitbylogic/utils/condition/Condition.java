package net.bitbylogic.utils.condition;

import lombok.NonNull;
import net.bitbylogic.utils.condition.type.InvertedCondition;
import net.bitbylogic.utils.context.Context;

public interface Condition {

    @NonNull String getId();

    boolean matches(@NonNull Context context);

    @NonNull String getErrorMessage(@NonNull Context context);

    default Condition inverted() {
        return new InvertedCondition(this);
    }

}
