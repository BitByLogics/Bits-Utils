package net.bitbylogic.utils.action.impl;

import lombok.NoArgsConstructor;
import lombok.NonNull;
import net.bitbylogic.utils.action.Action;
import net.bitbylogic.utils.action.ConfiguredMetadataActionData;
import net.bitbylogic.utils.action.data.ActionData;
import net.bitbylogic.utils.action.data.StringActionData;
import net.bitbylogic.utils.context.BukkitContextKeys;
import net.bitbylogic.utils.context.Context;
import net.bitbylogic.utils.context.DefaultContextKeys;
import net.bitbylogic.utils.message.PlaceholderUtil;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

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
                .map(player -> PlaceholderUtil.apply(player, command))
                .orElse(command);

        if (Bukkit.isGlobalTickThread()) {
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), finalCommand);
            return true;
        }

        Bukkit.getGlobalRegionScheduler().run(JavaPlugin.getProvidingPlugin(ConsoleCommandAction.class), task ->
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), finalCommand));
        return true;
    }

    @Override
    public boolean canExecute(@NonNull Context context) {
        return context.has(DefaultContextKeys.ACTION_DATA);
    }

}
