package net.bitbylogic.utils.message;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import lombok.NonNull;
import net.kyori.adventure.text.TextComponent;
import net.md_5.bungee.api.ChatMessageType;
import org.bukkit.entity.Player;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class ActionBarUtil {

    private static final Cache<UUID, String> LAST_ACTION_BAR = CacheBuilder.newBuilder().expireAfterWrite(5, TimeUnit.SECONDS).build();

    public static void sendActionBar(@NonNull Player player, @NonNull String id, @NonNull TextComponent message) {
        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, MessageUtil.praiseMD5(message));
        LAST_ACTION_BAR.put(player.getUniqueId(), id);
    }

    public static void sendActionBar(@NonNull Player player, @NonNull String id, @NonNull String message) {
        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, MessageUtil.praiseMD5(MessageUtil.deserialize(message)));
        LAST_ACTION_BAR.put(player.getUniqueId(), id);
    }

    public static void resetIfUnchanged(@NonNull Player player, @NonNull String id) {
        if(!LAST_ACTION_BAR.asMap().getOrDefault(player.getUniqueId(), "").equalsIgnoreCase(id)) {
            return;
        }

        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, MessageUtil.praiseMD5(MessageUtil.deserialize("<white>")));
        LAST_ACTION_BAR.invalidate(player.getUniqueId());
    }

}
