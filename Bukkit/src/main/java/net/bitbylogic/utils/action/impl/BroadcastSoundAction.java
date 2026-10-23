package net.bitbylogic.utils.action.impl;

import lombok.NoArgsConstructor;
import lombok.NonNull;
import net.bitbylogic.utils.action.Action;
import net.bitbylogic.utils.action.data.ActionData;
import net.bitbylogic.utils.context.Context;
import net.bitbylogic.utils.context.DefaultContextKeys;
import net.bitbylogic.utils.sound.SoundData;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

@NoArgsConstructor
public class BroadcastSoundAction implements Action {

    @Override
    public String getId() {
        return "broadcast_sound";
    }

    @Override
    public boolean execute(@NonNull Context context) {
        ActionData<?> actionData = context.get(DefaultContextKeys.ACTION_DATA).orElse(null);
        if (actionData == null) return false;

        return SoundData.fromActionData(actionData).map(soundData -> {
            for (Player player : Bukkit.getOnlinePlayers()) {
                soundData.play(player);
            }
            return true;
        }).orElse(false);
    }

    @Override
    public boolean canExecute(@NonNull Context context) {
        return true;
    }

}