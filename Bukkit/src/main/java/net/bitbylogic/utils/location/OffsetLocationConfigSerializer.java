package net.bitbylogic.utils.location;

import lombok.NonNull;
import net.bitbylogic.utils.config.ConfigSerializer;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.bukkit.configuration.ConfigurationSection;

import java.util.Optional;

public class OffsetLocationConfigSerializer implements ConfigSerializer<OffsetLocation> {

    @Override
    public Optional<OffsetLocation> deserialize(@NonNull ConfigurationSection section, @NonNull TagResolver.Single... placeholders) {
        double xOffset = section.getDouble("X-Offset");
        double yOffset = section.getDouble("Y-Offset");
        double zOffset = section.getDouble("Z-Offset");

        return Optional.of(new OffsetLocation(xOffset, yOffset, zOffset));
    }

    @Override
    public ConfigurationSection serialize(@NonNull ConfigurationSection section, @NonNull OffsetLocation location) {
        section.set("X-Offset", location.getXOffset());
        section.set("Y-Offset", location.getYOffset());
        section.set("Z-Offset", location.getZOffset());
        return section;
    }

}
