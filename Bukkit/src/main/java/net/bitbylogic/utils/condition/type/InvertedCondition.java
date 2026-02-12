package net.bitbylogic.utils.condition.type;

import lombok.NonNull;
import net.bitbylogic.utils.condition.Condition;
import net.bitbylogic.utils.context.Context;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;

public class InvertedCondition implements Condition {

    private final Condition original;

    public InvertedCondition(Condition original) {
        this.original = original;
    }

    @Override
    public @NotNull String getId() {
        return "inverted_" + original.getId();
    }

    @Override
    public boolean matches(@NonNull Context context) {
        return !original.matches(context);
    }

    @Override
    public @NotNull Component getErrorMessage(@NonNull Context context) {
        return Component.text("INVERTED: ").append(original.getErrorMessage(context));
    }

}
