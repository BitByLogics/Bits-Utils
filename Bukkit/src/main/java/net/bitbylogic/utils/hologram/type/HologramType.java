package net.bitbylogic.utils.hologram.type;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.entity.EntityType;

@Getter
@RequiredArgsConstructor
public enum HologramType {

    TEXT(EntityType.TEXT_DISPLAY),
    ITEM(EntityType.ITEM_DISPLAY),
    BLOCK(EntityType.BLOCK_DISPLAY);

    final EntityType entityType;

}
