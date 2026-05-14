package net.bitbylogic.utils.message;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import lombok.NonNull;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import org.bukkit.entity.Player;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class ActionBarUtil {

    private static final Cache<UUID, String> LAST_ACTION_BAR = CacheBuilder.newBuilder().expireAfterWrite(5, TimeUnit.SECONDS).build();

    public static void sendActionBar(@NonNull Player player, @NonNull String id, @NonNull TextComponent message) {
        player.sendActionBar(message);
        LAST_ACTION_BAR.put(player.getUniqueId(), id);
    }

    public static void sendActionBar(@NonNull Player player, @NonNull String id, @NonNull String message) {
        player.sendActionBar(MessageUtil.deserialize(message));

        LAST_ACTION_BAR.put(player.getUniqueId(), id);
    }

    public static void resetIfUnchanged(@NonNull Player player, @NonNull String id) {
        if(!LAST_ACTION_BAR.asMap().getOrDefault(player.getUniqueId(), "").equalsIgnoreCase(id)) {
            return;
        }

        player.sendActionBar(Component.text(""));
        LAST_ACTION_BAR.invalidate(player.getUniqueId());
    }

}
