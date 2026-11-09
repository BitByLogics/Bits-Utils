package net.bitbylogic.utils.trigger.impl.event;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import lombok.NonNull;
import net.bitbylogic.utils.context.ContextBuilder;
import net.bitbylogic.utils.trigger.impl.EventTriggerInstance;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class PlayerChatTriggerInstance extends EventTriggerInstance<AsyncPlayerChatEvent> {

    private final Cache<UUID, Integer> messageCountCache = CacheBuilder.newBuilder()
            .expireAfterWrite(5, TimeUnit.MINUTES).build();

    private final PlayerChatTrigger definition;

    public PlayerChatTriggerInstance(@NonNull PlayerChatTrigger definition) {
        super(definition);
        this.definition = definition;
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    @Override
    public void onEvent(AsyncPlayerChatEvent event) {
        String mustContain = definition.getMustContain();

        if (mustContain != null && !event.getMessage().contains(mustContain)) {
            return;
        }

        if (getPlugin() == null) {
            return;
        }

        int messageCount = definition.getMessageCount();

        if (messageCount <= 0) {
            runTrigger(event);
            return;
        }

        UUID playerId = event.getPlayer().getUniqueId();

        Integer messagesCached = messageCountCache.getIfPresent(playerId);
        int messages = messagesCached != null ? messagesCached : 0;

        messageCountCache.put(playerId, ++messages);

        if (messages >= messageCount) {
            runTrigger(event);

            messageCountCache.invalidate(playerId);
        }
    }

    private void runTrigger(AsyncPlayerChatEvent event) {
        JavaPlugin plugin = getPlugin();

        if (plugin == null) {
            return;
        }

        event.getPlayer().getScheduler().run(plugin, task ->
                trigger(ContextBuilder.create().with(event.getPlayer()).build()), null);
    }

}
