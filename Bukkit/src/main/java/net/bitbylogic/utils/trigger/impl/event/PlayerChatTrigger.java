package net.bitbylogic.utils.trigger.impl.event;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import lombok.NonNull;
import net.bitbylogic.utils.config.metadata.ConfiguredMetadata;
import net.bitbylogic.utils.context.ContextBuilder;
import net.bitbylogic.utils.trigger.impl.EventTrigger;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class PlayerChatTrigger extends EventTrigger<AsyncPlayerChatEvent> {

    private final Cache<UUID, Integer> messageCountCache = CacheBuilder.newBuilder()
            .expireAfterWrite(5, TimeUnit.MINUTES).build();

    private final String mustContain;
    private final int messageCount;

    public PlayerChatTrigger(@NonNull ConfiguredMetadata metadata) {
        super("player_chat", metadata);

        this.mustContain = metadata.getDeepValueAsOrDefault("Must-Contain", null);
        this.messageCount = metadata.getValueAsOrDefault("Message-Count", 0);
    }

    @EventHandler(priority = EventPriority.LOWEST)
    @Override
    public void onEvent(AsyncPlayerChatEvent event) {
        if(mustContain != null && !event.getMessage().contains(mustContain)) {
            return;
        }

        if(messageCount <= 0) {
            Bukkit.getScheduler().runTask(getPlugin(), () ->
                    trigger(ContextBuilder.create().with(event.getPlayer()).build())
            );
            return;
        }

        UUID playerId = event.getPlayer().getUniqueId();

        Integer messagesCached = messageCountCache.getIfPresent(playerId);
        int messages = messagesCached != null ? messagesCached : 0;

        messageCountCache.put(playerId, ++messages);

        if (messages >= messageCount) {
            Bukkit.getScheduler().runTask(getPlugin(), () ->
                    trigger(ContextBuilder.create().with(event.getPlayer()).build())
            );

            messageCountCache.invalidate(playerId);
        }
    }

}
