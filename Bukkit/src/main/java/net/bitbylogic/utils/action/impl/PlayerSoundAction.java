package net.bitbylogic.utils.action.impl;

import lombok.NoArgsConstructor;
import lombok.NonNull;
import net.bitbylogic.utils.action.Action;
import net.bitbylogic.utils.action.data.ActionData;
import net.bitbylogic.utils.context.Context;
import net.bitbylogic.utils.context.DefaultContextKeys;
import net.bitbylogic.utils.context.BukkitContextKeys;
import net.bitbylogic.utils.sound.SoundData;
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

        return SoundData.fromActionData(actionData).map(soundData -> {
            soundData.play(player);
            return true;
        }).orElse(false);
    }

    @Override
    public boolean canExecute(@NonNull Context context) {
        return context.has(BukkitContextKeys.PLAYER);
    }

}