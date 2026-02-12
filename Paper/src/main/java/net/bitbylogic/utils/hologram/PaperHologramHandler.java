package net.bitbylogic.utils.hologram;

import net.kyori.adventure.text.Component;
import org.bukkit.entity.TextDisplay;
import org.jetbrains.annotations.NotNull;

public class PaperHologramHandler {

    public static void setText(@NotNull Component component, @NotNull TextDisplay display) {
        display.text(component);
    }

}
