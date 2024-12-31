package net.bitbylogic.utils.config.configurable.wrapper;

import lombok.NonNull;
import org.bukkit.configuration.file.FileConfiguration;

public interface ConfigWrapper<T> {

    void wrap(@NonNull T object, @NonNull String path, @NonNull FileConfiguration config);

    <W> T unwrap(@NonNull W wrappedObject, @NonNull Class<?> requestedClass);

}
