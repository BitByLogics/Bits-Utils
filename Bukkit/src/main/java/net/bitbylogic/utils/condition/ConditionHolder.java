package net.bitbylogic.utils.condition;

import lombok.NonNull;
import net.bitbylogic.utils.context.Context;

import java.util.List;

public interface ConditionHolder {

    boolean meetsConditions(@NonNull Context context);

    List<Condition> getUnmetConditions(@NonNull Context context);

}
