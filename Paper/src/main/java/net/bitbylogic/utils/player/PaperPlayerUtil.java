package net.bitbylogic.utils.player;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.title.Title;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class PaperPlayerUtil {

    public static void sendActionBar(@NotNull Player player, @NotNull Component message) {
        player.sendActionBar(message);
    }

    public static void sendTitle(@NotNull Player player, @NotNull Title title) {
        player.showTitle(title);
    }

}
