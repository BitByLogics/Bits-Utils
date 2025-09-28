package net.bitbylogic.utils.file;

import lombok.NonNull;
import net.bitbylogic.utils.config.ConfigSerializer;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class DirectoryProcessor {

    public static <T> List<T> processDirectory(@NonNull File folder, ConfigSerializer<T> parser) {
        return processDirectory(folder, null, parser);
    }

    public static <T> List<T> processDirectory(@NonNull File folder, @Nullable String baseSection, ConfigSerializer<T> parser) {
        if (!folder.isDirectory()) {
            return new ArrayList<>();
        }

        return processDirectory(folder, baseSection, new ArrayList<>(), parser);
    }

    private static <T> List<T> processDirectory(@NonNull File directory, @Nullable String baseSection, @NonNull List<T> data, ConfigSerializer<T> parser) {
        if (!directory.isDirectory()) {
            return data;
        }

        File[] directoryFiles = directory.listFiles();

        if (directoryFiles == null) {
            return data;
        }

        for (File file : directoryFiles) {
            if (file.isDirectory()) {
                processDirectory(file, baseSection, data, parser);
                continue;
            }

            processFile(file, baseSection, data, parser);
        }

        return data;
    }

    private static <T> void processFile(@NonNull File file, @Nullable String baseSection, @NonNull List<T> data, @NonNull ConfigSerializer<T> parser) {
        if (!file.getName().endsWith(".yml")) {
            return;
        }

        YamlConfiguration config = YamlConfiguration.loadConfiguration(file);

        if(baseSection != null) {
            ConfigurationSection section = config.getConfigurationSection(baseSection);

            if(section == null) {
                return;
            }

            section.getKeys(false).forEach(key -> {
                ConfigurationSection keySection = section.getConfigurationSection(key);

                if(keySection == null) {
                    return;
                }

                parser.serializeFrom(keySection).ifPresent(data::add);
            });
            return;
        }

        parser.serializeFrom(config).ifPresent(data::add);
    }

}
