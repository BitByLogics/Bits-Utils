package net.bitbylogic.utils.color;

import lombok.NonNull;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Color;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;

public class ColorUtil {

    private static final Pattern HEX_COLOR = Pattern.compile(
            "#[0-9a-fA-F]{6}"
    );

    private static final Pattern LEGACY_HEX = Pattern.compile("§x(§[0-9a-fA-F]){6}");

    private static final Map<String, ChatColor> HEX_COLOR_CACHE = new ConcurrentHashMap<>();

    private static final Map<String, String> CONSOLE_COLOR_MAP = Map.ofEntries(
            Map.entry("§0", "\u001B[30m"),
            Map.entry("§1", "\u001B[34m"),
            Map.entry("§2", "\u001B[32m"),
            Map.entry("§3", "\u001B[36m"),
            Map.entry("§4", "\u001B[31m"),
            Map.entry("§5", "\u001B[35m"),
            Map.entry("§6", "\u001B[33m"),
            Map.entry("§7", "\u001B[37m"),
            Map.entry("§8", "\u001B[90m"),
            Map.entry("§9", "\u001B[94m"),
            Map.entry("§a", "\u001B[92m"),
            Map.entry("§b", "\u001B[96m"),
            Map.entry("§c", "\u001B[91m"),
            Map.entry("§d", "\u001B[95m"),
            Map.entry("§e", "\u001B[93m"),
            Map.entry("§f", "\u001B[97m"),
            Map.entry("§l", "\u001B[1m"),
            Map.entry("§o", "\u001B[3m"),
            Map.entry("§n", "\u001B[4m"),
            Map.entry("§m", "\u001B[9m"),
            Map.entry("§r", "\u001B[0m")
    );

    public static Optional<Color> parseHex(String hex) {
        if (hex == null) {
            return Optional.empty();
        }

        String digits = hex.replace("#", "");

        if (!digits.matches("[0-9a-fA-F]{6}")) {
            return Optional.empty();
        }

        int r = Integer.parseInt(digits.substring(0, 2), 16);
        int g = Integer.parseInt(digits.substring(2, 4), 16);
        int b = Integer.parseInt(digits.substring(4, 6), 16);

        return Optional.of(Color.fromRGB(r, g, b));
    }

    public static ChatColor colorToChatColor(@NonNull Color color) {
        String hex = String.format("#%02x%02x%02x", color.getRed(), color.getGreen(), color.getBlue());
        return HEX_COLOR_CACHE.computeIfAbsent(hex, ChatColor::of);
    }

    public static String colorForConsole(String message) {
        String colored = ChatColor.translateAlternateColorCodes('&', message);

        colored = LEGACY_HEX.matcher(colored).replaceAll(match -> {
            String digits = match.group().replace("§", "").substring(1);
            int red = Integer.parseInt(digits.substring(0, 2), 16);
            int green = Integer.parseInt(digits.substring(2, 4), 16);
            int blue = Integer.parseInt(digits.substring(4, 6), 16);
            return "[38;2;" + red + ";" + green + ";" + blue + "m";
        });

        for (Map.Entry<String, String> entry : CONSOLE_COLOR_MAP.entrySet()) {
            colored = colored.replace(entry.getKey(), entry.getValue());
        }

        return colored + "\u001B[0m";
    }

    public static boolean containsHexColor(@NonNull String text) {
        return HEX_COLOR.matcher(text).find();
    }

}
