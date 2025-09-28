package net.bitbylogic.utils.context;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

public class BukkitContextKeys {

    public static final ContextKey<Player> PLAYER = new ContextKey<>("player", Player.class);
    public static final ContextKey<Entity> ENTITY = new ContextKey<>("entity", Entity.class);

}
