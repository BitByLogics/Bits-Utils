package net.bitbylogic.utils.config;

import lombok.NonNull;
import org.bukkit.configuration.ConfigurationSection;

import java.util.Optional;

public interface ConfigParser<O> {

    Optional<O> parseFrom(@NonNull ConfigurationSection section);

    ConfigurationSection parseTo(@NonNull ConfigurationSection section, @NonNull O object);

}
