package net.bitbylogic.utils.message;

import lombok.NonNull;
import net.bitbylogic.utils.message.format.Formatter;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.chat.ComponentSerializer;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ActionBarUtil {

    private static final Map<UUID, String> LAST_ACTION_BAR = new HashMap<>();

    public static void sendActionBar(@NonNull Player player, @NonNull String id, @NonNull String message) {
        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacy(Formatter.format(message)));
        LAST_ACTION_BAR.put(player.getUniqueId(), id);
    }

    public static void resetIfUnchanged(@NonNull Player player, @NonNull String id) {
        if(!LAST_ACTION_BAR.getOrDefault(player.getUniqueId(), "").equalsIgnoreCase(id)) {
            return;
        }

        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacy(Formatter.format("&a")));
        LAST_ACTION_BAR.remove(player.getUniqueId());
    }

}
