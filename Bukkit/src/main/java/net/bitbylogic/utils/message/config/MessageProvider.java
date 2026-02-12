package net.bitbylogic.utils.message.config;

import lombok.Getter;
import net.bitbylogic.utils.message.MessageUtil;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.MemorySection;
import org.bukkit.configuration.file.FileConfiguration;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Getter
public class MessageProvider {

    private final HashMap<String, Object> messages = new HashMap<>();

    public MessageProvider(ConfigurationSection config) {
        config.getKeys(true).stream().filter(key -> !(config.get(key) instanceof MemorySection)).forEach(key -> messages.put(key, config.get(key)));
    }

    public void loadMessages(FileConfiguration config, @Nullable String messagesPath) {
        messages.clear();

        ConfigurationSection memorySection = config;

        if (messagesPath != null) {
            ConfigurationSection messageSection = config.getConfigurationSection(messagesPath);

            if (messageSection != null) {
                memorySection = messageSection;
            }
        }
    }

    public void reload(ConfigurationSection section) {
        messages.clear();
        section.getKeys(true).stream().filter(key -> !(section.get(key) instanceof MemorySection)).forEach(key -> messages.put(key, section.get(key)));
    }

    public Component getMessage(String key, TagResolver.Single... externalPlaceholders) {
        String rawMessage = getRawMessage(key);

        if (rawMessage == null) {
            return null;
        }

        return MessageUtil.deserialize(rawMessage, externalPlaceholders);
    }

    public List<Component> getMessageList(String key, TagResolver.Single... externalPlaceholders) {
        List<String> rawList = getRawMessageList(key);

        if (rawList == null) {
            return null;
        }

        List<Component> formattedMessages = new ArrayList<>();
        rawList.forEach(string -> formattedMessages.add(MessageUtil.deserialize(string, externalPlaceholders)));

        return formattedMessages;
    }

    public List<String> getRawMessageList(String key) {
        if (!messages.containsKey(key)) {
            return null;
        }

        if (!(messages.get(key) instanceof List)) {
            return null;
        }

        return (List<String>) messages.get(key);
    }

    public String getRawMessage(String key) {
        if (!messages.containsKey(key)) {
            return null;
        }

        if (!(messages.get(key) instanceof String)) {
            return null;
        }

        return (String) messages.get(key);
    }

}
