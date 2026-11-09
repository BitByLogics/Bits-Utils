package net.bitbylogic.utils.item;

import org.bukkit.Bukkit;
import org.bukkit.inventory.ItemStack;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import java.util.logging.Logger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mockStatic;

class ItemSerializerTest {

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
    void emptyArrayEncodesToAnEmptyString() {
        assertEquals("", ItemSerializer.itemStackArrayToBase64(new ItemStack[]{}).orElseThrow());
    }

    @Test
    void emptyDataDecodesToAnEmptyArray() {
        assertEquals(0, ItemSerializer.itemStackArrayFromBase64("").orElseThrow().length);
        assertEquals(0, ItemSerializer.itemStackArrayFromBase64(null).orElseThrow().length);
    }

    @Test
    void emptyDataDecodesToNoItem() {
        assertTrue(ItemSerializer.itemStackFromBase64(null).isEmpty());
        assertTrue(ItemSerializer.itemStackFromBase64("").isEmpty());
    }

    @Test
    void garbageDataDecodesToNothing() {
        assertTrue(ItemSerializer.itemStackFromBase64("not base64 at all").isEmpty());
        assertTrue(ItemSerializer.itemStackArrayFromBase64("not base64 at all").isEmpty());
    }

}
