package net.bitbylogic.utils.item;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.MemoryConfiguration;
import org.bukkit.inventory.ItemStack;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import java.util.logging.Logger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;

class ItemStackConfigSerializerTest {

    private final ItemStackConfigSerializer serializer = new ItemStackConfigSerializer();

    private MockedStatic<Bukkit> bukkit;

    @BeforeEach
    void mockBukkit() {
        bukkit = mockStatic(Bukkit.class);
        bukkit.when(Bukkit::getLogger).thenReturn(Logger.getLogger("test"));
    }

    @AfterEach
    void closeBukkit() {
        bukkit.close();
    }

    @Test
    void unknownMaterialIsRejected() {
        MemoryConfiguration section = new MemoryConfiguration();
        section.set("Material", "NOT_A_MATERIAL");

        assertTrue(serializer.deserialize(section).isEmpty());
    }

    @Test
    void serializeWritesMaterialAndAmountWithoutMeta() {
        ItemStack item = mock(ItemStack.class);
        when(item.getType()).thenReturn(Material.DIAMOND);
        when(item.getAmount()).thenReturn(3);
        when(item.getItemMeta()).thenReturn(null);

        MemoryConfiguration section = new MemoryConfiguration();
        serializer.serialize(section, item);

        assertEquals("DIAMOND", section.getString("Material"));
        assertEquals(3, section.getInt("Amount"));
    }

}
