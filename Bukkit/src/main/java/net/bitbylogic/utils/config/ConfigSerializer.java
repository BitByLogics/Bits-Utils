package net.bitbylogic.utils.config;

import lombok.NonNull;
import org.bukkit.configuration.ConfigurationSection;

import java.util.Optional;

public interface ConfigSerializer<O> {

    Optional<O> deserialize(@NonNull ConfigurationSection section);

    ConfigurationSection serialize(@NonNull ConfigurationSection section, @NonNull O object);

}
