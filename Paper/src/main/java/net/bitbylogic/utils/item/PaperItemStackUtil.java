package net.bitbylogic.utils.item;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.function.Function;

public class PaperItemStackUtil {

    public static void setName(@NotNull ItemMeta meta, @NotNull Component component) {
        meta.displayName(component.style(style -> style.decoration(TextDecoration.ITALIC, false)));
    }

    public static void updateName(@NotNull ItemMeta meta, @NotNull Function<Component, Component> componentUpdater) {
        if (!meta.hasDisplayName()) {
            return;
        }

        meta.displayName(componentUpdater.apply(meta.displayName()).style(style -> style.decoration(TextDecoration.ITALIC, false)));
    }

    public static void updateLore(@NotNull ItemMeta meta, @NotNull Function<List<Component>, List<Component>> componentUpdater) {
        if (!meta.hasLore()) {
            return;
        }

        List<Component> lore = componentUpdater.apply(meta.lore());
        lore.replaceAll(component -> component.style(style -> style.decoration(TextDecoration.ITALIC, false)));

        meta.lore(lore);
    }

    public static void setLore(@NotNull ItemMeta meta, @NotNull List<Component> lore) {
        lore.replaceAll(component -> component.style(style -> style.decoration(TextDecoration.ITALIC, false)));

        meta.lore(lore);
    }

}
