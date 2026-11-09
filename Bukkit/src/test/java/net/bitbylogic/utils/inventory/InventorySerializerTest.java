package net.bitbylogic.utils.inventory;

import org.bukkit.Bukkit;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import java.util.logging.Logger;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.ArgumentMatchers.any;

class InventorySerializerTest {

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
    void serializedEmptyInventoryHasContentsAndArmor() {
        PlayerInventory inventory = mock(PlayerInventory.class);
        when(inventory.getContents()).thenReturn(new ItemStack[2]);
        when(inventory.getArmorContents()).thenReturn(new ItemStack[4]);

        String json = InventorySerializer.serialize(inventory);

        assertTrue(json.contains("contents"));
        assertTrue(json.contains("armor"));
    }

    @Test
    void emptyInventoryRoundTrips() {
        PlayerInventory source = mock(PlayerInventory.class);
        when(source.getContents()).thenReturn(new ItemStack[2]);
        when(source.getArmorContents()).thenReturn(new ItemStack[4]);
        String json = InventorySerializer.serialize(source);

        PlayerInventory target = mock(PlayerInventory.class);

        assertTrue(InventorySerializer.deserialize(target, json));
        verify(target).setContents(any(ItemStack[].class));
        verify(target).setArmorContents(any(ItemStack[].class));
        verify(target).setItemInOffHand(null);
    }

    @Test
    void invalidJsonIsRejected() {
        PlayerInventory target = mock(PlayerInventory.class);

        assertFalse(InventorySerializer.deserialize(target, "{not json"));
        verify(target, never()).setContents(any(ItemStack[].class));
    }

    @Test
    void emptyJsonIsRejected() {
        PlayerInventory target = mock(PlayerInventory.class);

        assertFalse(InventorySerializer.deserialize(target, "null"));
    }

    @Test
    void missingPartsAreRejected() {
        PlayerInventory target = mock(PlayerInventory.class);

        assertFalse(InventorySerializer.deserialize(target, "{\"contents\":[]}"));
        verify(target, never()).setContents(any(ItemStack[].class));
    }

}
