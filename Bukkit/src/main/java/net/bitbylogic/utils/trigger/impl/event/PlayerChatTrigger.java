package net.bitbylogic.utils.trigger.impl.event;

import lombok.NonNull;
import net.bitbylogic.utils.config.metadata.ConfiguredMetadata;
import net.bitbylogic.utils.trigger.TriggerInstance;
import net.bitbylogic.utils.trigger.impl.SimpleTrigger;
import org.jetbrains.annotations.Nullable;

public class PlayerChatTrigger extends SimpleTrigger {

    private final @Nullable String mustContain;
    private final int messageCount;

    public PlayerChatTrigger(@NonNull ConfiguredMetadata metadata) {
        super("player_chat", metadata);

        this.mustContain = metadata.getDeepValueAsOrDefault("Must-Contain", null);
        this.messageCount = metadata.getValueAsOrDefault("Message-Count", 0);
    }

    public @Nullable String getMustContain() {
        return mustContain;
    }

    public int getMessageCount() {
        return messageCount;
    }

    @Override
    public @NonNull TriggerInstance createInstance() {
        return new PlayerChatTriggerInstance(this);
    }

}
