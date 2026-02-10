package net.bitbylogic.utils.condition.type.impl;

import lombok.NonNull;
import net.bitbylogic.utils.condition.type.SimpleCondition;
import net.bitbylogic.utils.config.metadata.ConfiguredMetadata;
import net.bitbylogic.utils.context.BukkitContextKeys;
import net.bitbylogic.utils.context.Context;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class HasPermissionCondition extends SimpleCondition {

    private static final String FALLBACK_PERMISSION = "bukkit.command.reload";

    @Override
    public @NonNull String getId() {
        return "player_has_permission";
    }

    @Override
    public boolean matches(@NonNull Context context) {
        Player player = context.get(BukkitContextKeys.PLAYER).orElse(null);
        ConfiguredMetadata metadata = context.get(BukkitContextKeys.CONFIGURED_METADATA).orElse(null);

        if(player == null || metadata == null || !metadata.hasKey("Permission")) {
            return false;
        }

        return player.hasPermission(metadata.getValueAsOrDefault("Permission", FALLBACK_PERMISSION));
    }

    @Override
    public @NonNull @NotNull Component getErrorMessage(@NonNull Context context) {
        return Component.empty();
    }

}
