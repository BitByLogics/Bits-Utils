package net.bitbylogic.utils.action.impl;

import lombok.NoArgsConstructor;
import lombok.NonNull;
import net.bitbylogic.utils.action.Action;
import net.bitbylogic.utils.action.ConfiguredMetadataActionData;
import net.bitbylogic.utils.action.data.ActionData;
import net.bitbylogic.utils.action.data.StringActionData;
import net.bitbylogic.utils.context.Context;
import net.bitbylogic.utils.context.DefaultContextKeys;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

@NoArgsConstructor
public class BroadcastSoundAction implements Action {

    private static final float DEFAULT_VOLUME = 1.0f;
    private static final float DEFAULT_PITCH = 1.0f;

    @Override
    public String getId() {
        return "broadcast_sound";
    }

    @Override
    public boolean execute(@NonNull Context context) {
        ActionData<?> actionData = context.get(DefaultContextKeys.ACTION_DATA).orElse(null);
        if (actionData == null) return false;

        String soundName = "";
        float volume = DEFAULT_VOLUME;
        float pitch = DEFAULT_PITCH;

        if (actionData instanceof ConfiguredMetadataActionData metaData) {
            soundName = metaData.getData().getValueAsOrDefault("Sound", "");
            volume = metaData.getData().getValueAsOrDefault("Volume", DEFAULT_VOLUME);
            pitch = metaData.getData().getValueAsOrDefault("Pitch", DEFAULT_PITCH);
        } else if (actionData instanceof StringActionData stringData) {
            // Format: SOUNDNAME:VOLUME:PITCH
            String[] parts = stringData.getData().split(":");
            if (parts.length >= 1) soundName = parts[0].toUpperCase();
            if (parts.length >= 2) volume = parseFloat(parts[1]);
            if (parts.length >= 3) pitch = parseFloat(parts[2]);
        } else {
            return false;
        }

        Sound sound;
        try {
            sound = Sound.valueOf(soundName);
        } catch (IllegalArgumentException e) {
            return false;
        }

        for (Player player : Bukkit.getOnlinePlayers()) {
            player.playSound(player.getLocation(), sound, volume, pitch);
        }

        return true;
    }

    private float parseFloat(String str) {
        try {
            return Float.parseFloat(str);
        } catch (NumberFormatException e) {
            return DEFAULT_VOLUME;
        }
    }

    @Override
    public boolean canExecute(@NonNull Context context) {
        return true;
    }

}