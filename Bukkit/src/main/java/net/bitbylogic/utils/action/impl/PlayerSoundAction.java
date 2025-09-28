package net.bitbylogic.utils.action.impl;

import lombok.NoArgsConstructor;
import lombok.NonNull;
import net.bitbylogic.utils.action.Action;
import net.bitbylogic.utils.action.ConfiguredMetadataActionData;
import net.bitbylogic.utils.action.data.ActionData;
import net.bitbylogic.utils.action.data.StringActionData;
import net.bitbylogic.utils.context.Context;
import net.bitbylogic.utils.context.DefaultContextKeys;
import net.bitbylogic.utils.context.BukkitContextKeys;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

@NoArgsConstructor
public class PlayerSoundAction implements Action {

    @Override
    public String getId() {
        return "player_sound";
    }

    @Override
    public boolean execute(@NonNull Context context) {
        Player player = context.get(BukkitContextKeys.PLAYER).orElse(null);
        if (player == null) return false;

        ActionData<?> actionData = context.get(DefaultContextKeys.ACTION_DATA).orElse(null);
        if (actionData == null) return false;

        Sound sound;
        float volume = 1.0f;
        float pitch = 1.0f;

        if (actionData instanceof ConfiguredMetadataActionData metaData) {
            String soundName = metaData.getData().getValueAsOrDefault("Sound", "");
            volume = metaData.getData().getValueAsOrDefault("Volume", 1.0f);
            pitch = metaData.getData().getValueAsOrDefault("Pitch", 1.0f);
            try {
                sound = Sound.valueOf(soundName.toUpperCase());
            } catch (IllegalArgumentException e) {
                return false;
            }
        } else if (actionData instanceof StringActionData stringData) {
            String[] parts = stringData.getData().split(":");
            try {
                sound = Sound.valueOf(parts[0].toUpperCase());
            } catch (IllegalArgumentException e) {
                return false;
            }
            if (parts.length >= 2) volume = parseFloat(parts[1]);
            if (parts.length >= 3) pitch = parseFloat(parts[2]);
        } else {
            return false;
        }

        player.playSound(player.getLocation(), sound, volume, pitch);
        return true;
    }

    private float parseFloat(String str) {
        try {
            return Float.parseFloat(str);
        } catch (NumberFormatException e) {
            return (float) 1.0;
        }
    }

    @Override
    public boolean canExecute(@NonNull Context context) {
        return context.has(BukkitContextKeys.PLAYER);
    }

}