package net.bitbylogic.utils.config.metadata;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.MemoryConfiguration;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ConfigurableMetadataTest {

    @Test
    void stringSetIsStoredAsFlatList() {
        ConfigurationSection section = new MemoryConfiguration();
        ConfigurableMetadata metadata = new ConfigurableMetadata(null, section, Map.of());

        metadata.setValue("names", Set.of("alpha"), false);

        assertEquals(List.of("alpha"), section.getList("names"));
    }

    @Test
    void enumSetIsStoredAsNames() {
        ConfigurationSection section = new MemoryConfiguration();
        ConfigurableMetadata metadata = new ConfigurableMetadata(null, section, Map.of());

        metadata.setValue("units", Set.of(TimeUnit.SECONDS), false);

        assertEquals(List.of("SECONDS"), section.getList("units"));
    }

    @Test
    void randomDoubleStaysWithinBounds() {
        ConfigurationSection section = new MemoryConfiguration();
        section.set("min-value", 1.0);
        section.set("max-value", 2.0);
        ConfiguredMetadata metadata = new ConfiguredMetadata(section);

        for (int i = 0; i < 200; i++) {
            double value = metadata.getRandomDoubleInRange(0.0);

            assertTrue(value >= 1.0 && value < 2.0);
        }
    }

    @Test
    void randomDoubleWithEqualBoundsReturnsMin() {
        ConfigurationSection section = new MemoryConfiguration();
        section.set("min-value", 3.0);
        section.set("max-value", 3.0);
        ConfiguredMetadata metadata = new ConfiguredMetadata(section);

        assertEquals(3.0, metadata.getRandomDoubleInRange(0.0));
    }

}
