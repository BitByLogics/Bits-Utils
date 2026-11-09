package net.bitbylogic.utils.config.metadata;

import org.bukkit.configuration.MemoryConfiguration;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ConfiguredMetadataNumberTest {

    private ConfiguredMetadata metadata(String key, Object value) {
        MemoryConfiguration section = new MemoryConfiguration();
        section.set(key, value);
        return new ConfiguredMetadata(section);
    }

    @Test
    void doubleValueIsCoercedToFloatDefault() {
        float volume = metadata("volume", 0.5).getValueAsOrDefault("Volume", 1.0f);

        assertEquals(0.5f, volume);
    }

    @Test
    void integerValueIsCoercedToLongDefault() {
        long value = metadata("amount", 5).getValueAsOrDefault("Amount", 1L);

        assertEquals(5L, value);
    }

    @Test
    void doubleValueIsCoercedToIntegerDefault() {
        int value = metadata("amount", 5.0).getValueAsOrDefault("Amount", 1);

        assertEquals(5, value);
    }

    @Test
    void deepLookupAlsoCoerces() {
        float pitch = metadata("pitch", 2.0).getDeepValueAsOrDefault("Pitch", 1.0f);

        assertEquals(2.0f, pitch);
    }

    @Test
    void missingValueReturnsDefault() {
        float volume = metadata("other", 0.5).getValueAsOrDefault("Volume", 1.0f);

        assertEquals(1.0f, volume);
    }

}
