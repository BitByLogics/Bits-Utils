package net.bitbylogic.utils.condition;

import net.bitbylogic.utils.condition.parsed.ParsedCondition;
import net.bitbylogic.utils.condition.reference.ConditionReference;
import net.bitbylogic.utils.config.metadata.ConfiguredMetadata;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public final class ConditionParser {

    public static List<ParsedCondition> loadConditions(@Nullable ConfigurationSection conditionsSection) {
        List<ParsedCondition> conditions = new ArrayList<>();

        if(conditionsSection == null) {
            return conditions;
        }

        conditionsSection.getKeys(false).forEach(key -> {
            ConfigurationSection conditionSection = conditionsSection.getConfigurationSection(key);

            if(conditionSection == null) {
                return;
            }

            parseCondition(conditionSection).ifPresent(conditions::add);
        });

        return conditions;
    }

    public static Optional<ParsedCondition> parseCondition(@Nullable ConfigurationSection conditionSection) {
        if(conditionSection == null) {
            return Optional.empty();
        }

        String id = conditionSection.getString("Condition-ID");

        if(id == null) {
            return Optional.empty();
        }

        ConfigurationSection metadataSection = conditionSection.getConfigurationSection("Metadata");

        ConfiguredMetadata metadata = new ConfiguredMetadata(metadataSection);

        return Optional.of(new ParsedCondition(new ConditionReference(id), metadata));
    }

}
