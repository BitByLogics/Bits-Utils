package net.bitbylogic.utils.trigger;

import net.bitbylogic.utils.config.metadata.ConfiguredMetadata;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class TriggerParser {

    public static List<Trigger> loadTriggers(@Nullable ConfigurationSection triggersSection) {
        return loadTriggers(triggersSection, null);
    }

    public static List<Trigger> loadTriggers(@Nullable ConfigurationSection triggersSection, @Nullable TriggerAction action) {
        if(triggersSection == null) {
            return new ArrayList<>();
        }

        List<Trigger> triggers = new ArrayList<>();

        triggersSection.getKeys(false).forEach(key -> {
            ConfigurationSection triggerSection = triggersSection.getConfigurationSection(key);

            if(triggerSection == null) {
                return;
            }

            parseTrigger(triggerSection, action).ifPresent(triggers::add);
        });

        for (Trigger trigger : triggers) {
            for (Trigger other : triggers) {
                if(trigger == other) {
                    continue;
                }

                trigger.getChildren().add(other);
            }
        }

        return triggers;
    }

    public static Optional<Trigger> parseTrigger(@Nullable ConfigurationSection triggerSection) {
        return parseTrigger(triggerSection, null);
    }

    public static Optional<Trigger> parseTrigger(@Nullable ConfigurationSection triggerSection, @Nullable TriggerAction action) {
        if(triggerSection == null) {
            return Optional.empty();
        }

        String id = triggerSection.getString("Trigger-ID");

        if(id == null) {
            return Optional.empty();
        }

        ConfigurationSection metadataSection = triggerSection.getConfigurationSection("Metadata");
        ConfiguredMetadata metadata = new ConfiguredMetadata(metadataSection);

        Optional<Trigger> resolved = TriggerRegistry.get(id).map(provider -> provider.provide(metadata));

        if (resolved.isPresent()) {
            return resolved;
        }

        return Optional.of(new DeferredTrigger(id, metadata));
    }

}
