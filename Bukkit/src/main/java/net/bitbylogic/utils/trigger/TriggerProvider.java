package net.bitbylogic.utils.trigger;

import lombok.NonNull;
import net.bitbylogic.utils.config.metadata.ConfiguredMetadata;

public interface TriggerProvider {

    Trigger provide(@NonNull ConfiguredMetadata metadata);

}
