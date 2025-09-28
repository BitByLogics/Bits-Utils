package net.bitbylogic.utils.action.impl;

import lombok.NoArgsConstructor;
import lombok.NonNull;
import me.clip.placeholderapi.PlaceholderAPI;
import net.bitbylogic.utils.action.Action;
import net.bitbylogic.utils.action.ConfiguredMetadataActionData;
import net.bitbylogic.utils.action.data.ActionData;
import net.bitbylogic.utils.action.data.StringActionData;
import net.bitbylogic.utils.context.Context;
import net.bitbylogic.utils.context.DefaultContextKeys;
import net.bitbylogic.utils.context.BukkitContextKeys;
import net.bitbylogic.utils.message.format.Formatter;
import org.bukkit.entity.Player;

@NoArgsConstructor
public class PlayerMessageAction implements Action {

    @Override
    public String getId() {
        return "player_message";
    }

    @Override
    public boolean execute(@NonNull Context context) {
        Player player = context.get(BukkitContextKeys.PLAYER).orElse(null);
        if (player == null) return false;

        ActionData<?> actionData = context.get(DefaultContextKeys.ACTION_DATA).orElse(null);
        if (actionData == null) return false;

        String message;

        if (actionData instanceof ConfiguredMetadataActionData metaData) {
            message = metaData.getData().getValueAsOrDefault("Message", "");
        } else if (actionData instanceof StringActionData stringData) {
            message = stringData.getData();
        } else {
            return false;
        }

        String finalMessage = Formatter.format(PlaceholderAPI.setPlaceholders(player, message.replace("%player%", player.getName())));
        player.sendMessage(finalMessage);
        return true;
    }

    @Override
    public boolean canExecute(@NonNull Context context) {
        return context.has(BukkitContextKeys.PLAYER);
    }

}
