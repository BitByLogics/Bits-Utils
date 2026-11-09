package net.bitbylogic.utils.config.configurable;

import net.bitbylogic.utils.config.configurable.data.ConfigKeyData;
import org.bukkit.configuration.file.YamlConfiguration;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ConfigurableTest {

    @TempDir
    Path directory;

    @Test
    void keyDataEqualsByPathAndWrapper() {
        assertEquals(ConfigKeyData.of("Path", "w"), ConfigKeyData.of("Path", "w"));
        assertEquals(ConfigKeyData.of("Path").hashCode(), ConfigKeyData.of("Path").hashCode());
    }

    @Test
    void defaultIsPersistedUnderThePrefix() {
        File file = directory.resolve("config.yml").toFile();
        Configurable configurable = new Configurable(file, "Prefix.");

        int value = configurable.getConfigValueOrDefault("Amount", 5);

        assertEquals(5, value);
        assertEquals(5, YamlConfiguration.loadConfiguration(file).getInt("Prefix.Amount"));
        assertTrue(configurable.hasConfigValue("Amount"));
    }

    @Test
    void existingPrefixedValueIsNotOverwritten() throws Exception {
        File file = directory.resolve("config.yml").toFile();
        YamlConfiguration existing = new YamlConfiguration();
        existing.set("Prefix.Amount", 9);
        existing.save(file);

        Configurable configurable = new Configurable(file, "Prefix.");

        int value = configurable.getConfigValueOrDefault("Amount", 5);

        assertEquals(9, value);
        assertEquals(9, YamlConfiguration.loadConfiguration(file).getInt("Prefix.Amount"));
    }

    @Test
    void repeatedLookupsKeepASingleEntry() {
        File file = directory.resolve("config.yml").toFile();
        Configurable configurable = new Configurable(file, "Prefix.");

        configurable.getConfigValueOrDefault("Amount", 5);
        int second = configurable.getConfigValueOrDefault("Amount", 7);

        assertEquals(5, second);
    }

}
