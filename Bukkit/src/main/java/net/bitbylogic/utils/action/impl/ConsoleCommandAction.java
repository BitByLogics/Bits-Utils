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
import org.bukkit.Bukkit;

@NoArgsConstructor
public class ConsoleCommandAction implements Action {

    @Override
    public String getId() {
        return "console_command";
    }

    @Override
    public boolean execute(@NonNull Context context) {
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

        String finalCommand = context.get(BukkitContextKeys.PLAYER)
                .map(player -> PlaceholderAPI.setPlaceholders(player, command.replace("%player%", player.getName())))
                .orElse(command);

        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), finalCommand);
        return true;
    }

    @Override
    public boolean canExecute(@NonNull Context context) {
        return context.has(DefaultContextKeys.ACTION_DATA);
    }

}
