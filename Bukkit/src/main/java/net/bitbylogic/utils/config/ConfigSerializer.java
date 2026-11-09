package net.bitbylogic.utils.config;

import lombok.NonNull;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.bukkit.configuration.ConfigurationSection;

import java.util.Optional;

public interface ConfigSerializer<O> {

    Optional<O> deserialize(@NonNull ConfigurationSection section, @NonNull TagResolver.Single... placeholders);

    default Optional<O> deserialize(@NonNull ConfigurationSection section) {
        return deserialize(section, new TagResolver.Single[0]);
    }

    ConfigurationSection serialize(@NonNull ConfigurationSection section, @NonNull O object);

}
