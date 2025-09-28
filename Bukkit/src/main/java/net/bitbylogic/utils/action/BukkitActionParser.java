package net.bitbylogic.utils.action;

import net.bitbylogic.utils.action.impl.*;
import net.bitbylogic.utils.action.parsed.ParsedAction;
import net.bitbylogic.utils.action.reference.ActionReference;
import net.bitbylogic.utils.config.metadata.ConfiguredMetadata;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class BukkitActionParser extends ActionParser {

    static {
        ActionRegistry.register(
                new BroadcastMessageAction(), new BroadcastSoundAction(),
                new ConsoleCommandAction(), new PlayerCommandAction(),
                new PlayerMessageAction(), new PlayerSoundAction()
        );
    }

    public static List<ParsedAction> parseActions(@Nullable ConfigurationSection actionsSection) {
        List<ParsedAction> actions = new ArrayList<>();

        if(actionsSection == null) {
            return actions;
        }

        actionsSection.getKeys(false).forEach(key -> {
            if(actionsSection.isString(key)) {
                String data = actionsSection.getString(key);

                if(data == null) {
                    return;
                }

                ActionParser.parseAction(data).ifPresent(actions::add);
                return;
            }

            if(actionsSection.isList(key)) {
                List<String> list = actionsSection.getStringList(key);
                actions.addAll(ActionParser.parseActions(list));
                return;
            }

            ConfigurationSection actionSection = actionsSection.getConfigurationSection(key);

            if(actionSection == null) {
                return;
            }

            parseAction(actionSection).ifPresent(actions::add);
        });

        return actions;
    }

    public static Optional<ParsedAction> parseAction(@Nullable ConfigurationSection conditionSection) {
        if(conditionSection == null) {
            return Optional.empty();
        }

        String id = conditionSection.getString("Condition-ID");

        if(id == null) {
            return Optional.empty();
        }

        ConfigurationSection metadataSection = conditionSection.getConfigurationSection("Metadata");

        ConfiguredMetadata metadata = new ConfiguredMetadata(metadataSection);

        return Optional.of(new ParsedAction(new ActionReference(id), new ConfiguredMetadataActionData(metadata)));
    }

}
