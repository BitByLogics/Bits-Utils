package net.bitbylogic.utils.action.impl;

import lombok.NoArgsConstructor;
import lombok.NonNull;
import me.clip.placeholderapi.PlaceholderAPI;
import net.bitbylogic.utils.action.Action;
import net.bitbylogic.utils.action.ConfiguredMetadataActionData;
import net.bitbylogic.utils.action.data.ActionData;
import net.bitbylogic.utils.action.data.StringActionData;
import net.bitbylogic.utils.context.BukkitContextKeys;
import net.bitbylogic.utils.context.Context;
import net.bitbylogic.utils.context.DefaultContextKeys;
import org.bukkit.entity.Player;

@NoArgsConstructor
public class PlayerCommandAction implements Action {

    @Override
    public String getId() {
        return "player_command";
    }

    @Override
    public boolean execute(@NonNull Context context) {
        Player player = context.get(BukkitContextKeys.PLAYER).orElse(null);
        if (player == null) return false;

        ActionData<?> actionData = context.get(DefaultContextKeys.ACTION_DATA).orElse(null);
        if (actionData == null) return false;

        String command;

        if (actionData instanceof ConfiguredMetadataActionData metaData) {
            command = metaData.getData().getValueAsOrDefault("Command", "");
        } else if (actionData instanceof StringActionData stringData) {
            command = stringData.getData();
        } else {
            return false;
        }

        String finalCommand = PlaceholderAPI.setPlaceholders(player, command.replace("%player%", player.getName()));

        player.performCommand(finalCommand);
        return true;
    }

    @Override
    public boolean canExecute(@NonNull Context context) {
        return context.has(BukkitContextKeys.PLAYER) && context.has(DefaultContextKeys.ACTION_DATA);
    }
}