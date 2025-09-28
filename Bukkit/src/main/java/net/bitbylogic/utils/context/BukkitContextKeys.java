package net.bitbylogic.utils.context;

import net.bitbylogic.utils.config.metadata.ConfiguredMetadata;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

public class BukkitContextKeys {

    public static final ContextKey<ConfiguredMetadata> CONFIGURED_METADATA = new ContextKey<>("configured_metadata", ConfiguredMetadata.class);

    public static final ContextKey<Player> PLAYER = new ContextKey<>("player", Player.class);
    public static final ContextKey<Entity> ENTITY = new ContextKey<>("entity", Entity.class);

}
