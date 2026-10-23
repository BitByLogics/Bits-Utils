package net.bitbylogic.utils.sound;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import net.bitbylogic.utils.NumberUtil;
import net.bitbylogic.utils.action.ConfiguredMetadataActionData;
import net.bitbylogic.utils.action.data.ActionData;
import net.bitbylogic.utils.action.data.StringActionData;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import java.util.Optional;

@Getter
@RequiredArgsConstructor
public class SoundData {

    private static final float DEFAULT_VOLUME = 1.0f;
    private static final float DEFAULT_PITCH = 1.0f;

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

    /**
     * Parses SoundData from either a {@link ConfiguredMetadataActionData} (Sound/Volume/Pitch
     * metadata keys) or a {@link StringActionData} (colon-separated "Sound:Volume:Pitch" string).
     *
     * @param actionData The action data to parse.
     * @return The parsed SoundData, or empty if the data is unsupported or the sound is invalid.
     */
    public static Optional<SoundData> fromActionData(@NonNull ActionData<?> actionData) {
        if (actionData instanceof ConfiguredMetadataActionData metaData) {
            String soundName = metaData.getData().getValueAsOrDefault("Sound", "");
            float volume = metaData.getData().getValueAsOrDefault("Volume", DEFAULT_VOLUME);
            float pitch = metaData.getData().getValueAsOrDefault("Pitch", DEFAULT_PITCH);

            try {
                return Optional.of(new SoundData(Sound.valueOf(soundName.toUpperCase()), volume, pitch));
            } catch (IllegalArgumentException e) {
                return Optional.empty();
            }
        }

        if (actionData instanceof StringActionData stringData) {
            String[] parts = stringData.getData().split(":");
            Sound sound;

            try {
                sound = Sound.valueOf(parts[0].toUpperCase());
            } catch (IllegalArgumentException e) {
                return Optional.empty();
            }

            float volume = parts.length >= 2 ? NumberUtil.parseFloat(parts[1], DEFAULT_VOLUME) : DEFAULT_VOLUME;
            float pitch = parts.length >= 3 ? NumberUtil.parseFloat(parts[2], DEFAULT_PITCH) : DEFAULT_PITCH;

            return Optional.of(new SoundData(sound, volume, pitch));
        }

        return Optional.empty();
    }

    @Override
    public String toString() {
        return String.format("%s:%s:%s", sound.name(), volume, pitch);
    }

}
