package net.bitbylogic.utils.message;

import lombok.NonNull;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public final class PlaceholderUtil {

    private static final String PLUGIN_NAME = "PlaceholderAPI";

    private PlaceholderUtil() {

    }

    public static String apply(@NonNull Player player, @NonNull String text) {
        String replaced = text.replace("%player%", player.getName());

        if (!isPlaceholderApiAvailable()) {
            return replaced;
        }

        return PlaceholderApiHook.apply(player, replaced);
    }

    public static boolean isPlaceholderApiAvailable() {
        if (Bukkit.getServer() == null) {
            return false;
        }

        return Bukkit.getPluginManager().isPluginEnabled(PLUGIN_NAME);
    }

}
