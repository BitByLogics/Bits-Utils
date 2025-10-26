package net.bitbylogic.utils.condition.parsed;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import net.bitbylogic.utils.condition.reference.ConditionReference;
import net.bitbylogic.utils.config.metadata.ConfiguredMetadata;
import net.bitbylogic.utils.context.BukkitContextKeys;
import net.bitbylogic.utils.context.Context;

@Getter
@RequiredArgsConstructor
public class ParsedCondition {

    private final ConditionReference conditionReference;
    private final ConfiguredMetadata metadata;

    public boolean matches(@NonNull Context context) {
        context.put(BukkitContextKeys.CONFIGURED_METADATA, metadata);
        return conditionReference.get().map(condition -> condition.matches(context)).orElse(false);
    }

}
