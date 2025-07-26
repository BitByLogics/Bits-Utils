package net.bitbylogic.utils.message;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.HashMap;
import java.util.Map;

@Getter
@AllArgsConstructor
public class BitColor {

    private final static Map<String, String> DEFAULT_COLORS = Map.ofEntries(
            Map.entry("Separator", "#555555"),
            Map.entry("Primary", "#FFB7C5"),
            Map.entry("Secondary", "#EB5EE5"),
            Map.entry("Highlight", "#FFD7EF"),
            Map.entry("Error-Primary", "#FF0030"),
            Map.entry("Error-Secondary", "#FF8C7A"),
            Map.entry("Error-Highlight", "#FF3333"),
            Map.entry("Success-Primary", "#66FF66"),
            Map.entry("Success-Secondary", "#B3FFB3"),
            Map.entry("Success-Highlight", "#00FF66")
    );

    private final static HashMap<String, String> COLORS = new HashMap<>(DEFAULT_COLORS);

    public static void loadColors(FileConfiguration config) {
        COLORS.clear();

        for (String key : config.getConfigurationSection("Colors").getKeys(false)) {
            COLORS.put(key, ChatColor.of(config.getString("Colors." + key)).toString());
        }
    }

    public static String getColor(String name) {
        if(name == null || name.isEmpty()) {
            return null;
        }

        String colorId = COLORS.keySet().stream().filter(color -> color.equalsIgnoreCase(name) ||
                color.replace("-", "_").equalsIgnoreCase(name)).findFirst().orElse(null);
        return colorId == null ? null : COLORS.get(colorId);
    }

}
