package net.bitbylogic.utils.sound;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

@Getter
@RequiredArgsConstructor
public class SoundData {

    private final Sound sound;
    private final float volume;
    private final float pitch;

    public void play(@NonNull Player player) {
        player.playSound(player, sound, volume, pitch);
    }

    public void play(@NonNull Location location) {
        location.getWorld().playSound(location, sound, volume, pitch);
    }

    public static SoundData fromString(@NonNull String string) {
        String[] data = string.split(":");

        if (data.length != 3) {
            throw new IllegalArgumentException("Invalid SoundData string");
        }

        return new SoundData(Sound.valueOf(data[0].toUpperCase()), Float.parseFloat(data[1]), Float.parseFloat(data[2]));
    }

    @Override
    public String toString() {
        return String.format("%s:%s:%s", sound.name(), volume, pitch);
    }

}
