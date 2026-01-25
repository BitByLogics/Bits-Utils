package net.bitbylogic.utils.action.impl;

import lombok.NonNull;
import me.clip.placeholderapi.PlaceholderAPI;
import net.bitbylogic.utils.action.Action;
import net.bitbylogic.utils.action.ConfiguredMetadataActionData;
import net.bitbylogic.utils.action.data.StringActionData;
import net.bitbylogic.utils.context.BukkitContextKeys;
import net.bitbylogic.utils.context.Context;
import net.bitbylogic.utils.context.DefaultContextKeys;
import net.bitbylogic.utils.message.MessageUtil;
import org.bukkit.Bukkit;

public class BroadcastMessageAction implements Action {

    @Override
    public String getId() {
        return "broadcast_message";
    }

    @Override
    public boolean execute(@NonNull Context context) {
        context.get(DefaultContextKeys.ACTION_DATA).ifPresent(actionData -> {
            String message = switch (actionData) {
                case StringActionData stringData -> stringData.getData();
                case ConfiguredMetadataActionData metaData -> metaData.getData().getValueAsOrDefault("Message", "");
                default -> "";
            };

            context.get(BukkitContextKeys.PLAYER).ifPresent(player -> {
                String personalized = PlaceholderAPI.setPlaceholders(player, message.replace("%player%", player.getName()));
                Bukkit.broadcastMessage(MessageUtil.deserializeToSpigot(personalized));
            });
        });

        return context.get(DefaultContextKeys.ACTION_DATA).isPresent();
    }

    @Override
    public boolean canExecute(@NonNull Context context) {
        return true;
    }
}