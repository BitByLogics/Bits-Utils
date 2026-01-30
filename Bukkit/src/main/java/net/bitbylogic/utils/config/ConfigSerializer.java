package net.bitbylogic.utils.config;

import lombok.NonNull;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.bukkit.configuration.ConfigurationSection;

import java.util.Optional;

public interface ConfigSerializer<O> {

    Optional<O> deserialize(@NonNull ConfigurationSection section);

    // Default to keep backwards compatibility
    default Optional<O> deserialize(@NonNull ConfigurationSection section, @NonNull TagResolver.Single... placeholders) {
        return deserialize(section);
    }

    ConfigurationSection serialize(@NonNull ConfigurationSection section, @NonNull O object);

}
