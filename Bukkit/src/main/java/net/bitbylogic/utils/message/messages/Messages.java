package net.bitbylogic.utils.message.messages;

import lombok.NonNull;
import net.bitbylogic.utils.message.progressbar.ProgressBarMessages;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.*;

public abstract class Messages {

    private static final Map<String, MessageKey> REGISTRY = new LinkedHashMap<>();
    private static final List<MessageGroup> GROUP_REGISTRY = new ArrayList<>();

    private static final Locale[] SUPPORTED_LOCALES = { Locale.ENGLISH, Locale.GERMAN };

    static {
        registerGroup(new ProgressBarMessages());
    }

    public static MessageKey register(String path, String defaultValue) {
        MessageKey key = new MessageKey(path, defaultValue);
        REGISTRY.put(path, key);
        return key;
    }

    public static MessageKey register(String path, List<String> defaultValue) {
        MessageKey key = new MessageKey(path, defaultValue);
        REGISTRY.put(path, key);
        return key;
    }

    public static void registerGroup(@NonNull MessageGroup... groups) {
        GROUP_REGISTRY.addAll(Arrays.asList(groups));
    }

    public static Collection<MessageKey> all() {
        return REGISTRY.values();
    }

    public static MessageKey getByPath(String path) {
        return REGISTRY.get(path);
    }

    public static void initialize(@NonNull File messagesFolder) {
        for (MessageRegistry registrar : GROUP_REGISTRY) {
            registrar.register();
        }

        reload(messagesFolder);
    }

    public static void reload(@NonNull File messagesFolder) {
        for (MessageKey key : REGISTRY.values()) {
            for (Locale locale : SUPPORTED_LOCALES) {
                File localeConfig = new File(messagesFolder, locale.toLanguageTag() + ".yml");

                if (!localeConfig.exists()) {
                    try {
                        localeConfig.createNewFile();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }

                FileConfiguration config = YamlConfiguration.loadConfiguration(localeConfig);

                if(!config.isSet(key.getPath())) {
                    List<String> valuesToSave = key.getValues(locale);

                    if(valuesToSave.isEmpty()) {
                        continue;
                    }

                    if (valuesToSave.size() == 1) {
                        config.set(key.getPath(), valuesToSave.getFirst());

                        try {
                            config.save(localeConfig);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        continue;
                    }

                    config.set(key.getPath(), valuesToSave);

                    try {
                        config.save(localeConfig);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    continue;
                }

                key.getValues().put(locale, new ArrayList<>());

                if (config.isList(key.getPath())) {
                    List<String> value = new ArrayList<>(config.getStringList(key.getPath()));

                    key.getValues(locale).addAll(value);
                    continue;
                }

                String value = config.getString(key.getPath());

                if (value == null) {
                    continue;
                }

                key.getValues(locale).add(value);
            }
        }
    }

}
