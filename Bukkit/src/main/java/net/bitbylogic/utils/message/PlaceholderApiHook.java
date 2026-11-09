package net.bitbylogic.utils.message;

import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.entity.Player;

final class PlaceholderApiHook {

    private PlaceholderApiHook() {

    }

    static String apply(Player player, String text) {
        return PlaceholderAPI.setPlaceholders(player, text);
    }

}
