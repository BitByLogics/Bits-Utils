package net.bitbylogic.utils.item;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.withSettings;
import static org.mockito.Mockito.when;

class ItemMatchUtilTest {

    private final PersistentDataContainer container = mock(PersistentDataContainer.class);

    private ItemMeta meta(String name, List<String> lore, Set<ItemFlag> flags, int damage) {
        ItemMeta meta = mock(ItemMeta.class, withSettings().extraInterfaces(Damageable.class));

        when(meta.hasDisplayName()).thenReturn(name != null);
        when(meta.getDisplayName()).thenReturn(name);
        when(meta.hasLore()).thenReturn(lore != null);
        when(meta.getLore()).thenReturn(lore);
        when(meta.getItemFlags()).thenReturn(flags);
        when(meta.getEnchants()).thenReturn(Map.<Enchantment, Integer>of());
        when(meta.hasCustomModelData()).thenReturn(false);
        when(meta.getPersistentDataContainer()).thenReturn(container);
        when(((Damageable) meta).getDamage()).thenReturn(damage);

        return meta;
    }

    private ItemStack item(Material material, ItemMeta meta) {
        ItemStack item = mock(ItemStack.class);

        when(item.getType()).thenReturn(material);
        when(item.getItemMeta()).thenReturn(meta);

        return item;
    }

    private ItemStack plain(Material material) {
        return item(material, meta(null, null, Set.of(), 0));
    }

    @Test
    void nullItemsNeverMatch() {
        assertFalse(ItemMatchUtil.isSimilar(null, plain(Material.DIAMOND), true, true, true));
        assertFalse(ItemMatchUtil.isSimilar(plain(Material.DIAMOND), null, true, true, true));
    }

    @Test
    void differentTypesDoNotMatch() {
        assertFalse(ItemMatchUtil.isSimilar(plain(Material.DIAMOND), plain(Material.EMERALD), false, false, false));
    }

    @Test
    void identicalItemsMatch() {
        assertTrue(ItemMatchUtil.isSimilar(plain(Material.DIAMOND), plain(Material.DIAMOND), true, true, true));
    }

    @Test
    void namesAreOnlyComparedWhenAsked() {
        ItemStack first = item(Material.DIAMOND, meta("One", null, Set.of(), 0));
        ItemStack second = item(Material.DIAMOND, meta("Two", null, Set.of(), 0));

        assertFalse(ItemMatchUtil.isSimilar(first, second, false, true, false));
        assertTrue(ItemMatchUtil.isSimilar(first, second, false, false, false));
    }

    @Test
    void loreIsOnlyComparedWhenAsked() {
        ItemStack first = item(Material.DIAMOND, meta(null, List.of("a"), Set.of(), 0));
        ItemStack second = item(Material.DIAMOND, meta(null, List.of("b"), Set.of(), 0));

        assertFalse(ItemMatchUtil.isSimilar(first, second, false, false, true));
        assertTrue(ItemMatchUtil.isSimilar(first, second, false, false, false));
    }

    @Test
    void flagsAreOnlyComparedWhenAsked() {
        ItemStack first = item(Material.DIAMOND, meta(null, null, Set.of(ItemFlag.HIDE_ENCHANTS), 0));
        ItemStack second = plain(Material.DIAMOND);

        assertFalse(ItemMatchUtil.isSimilar(first, second, true, false, false));
        assertTrue(ItemMatchUtil.isSimilar(first, second, false, false, false));
    }

    @Test
    void differentDamageDoesNotMatch() {
        ItemStack first = item(Material.DIAMOND_SWORD, meta(null, null, Set.of(), 0));
        ItemStack second = item(Material.DIAMOND_SWORD, meta(null, null, Set.of(), 5));

        assertFalse(ItemMatchUtil.isSimilar(first, second, false, false, false));
    }

    @Test
    void loreMatchesWhenBothHaveNoLore() {
        assertTrue(ItemMatchUtil.loreMatches(plain(Material.DIAMOND), plain(Material.DIAMOND)));
    }

    @Test
    void loreDoesNotMatchWhenOnlyOneHasLore() {
        ItemStack withLore = item(Material.DIAMOND, meta(null, List.of("a"), Set.of(), 0));

        assertFalse(ItemMatchUtil.loreMatches(withLore, plain(Material.DIAMOND)));
    }

    @Test
    void spawnerMatchRequiresSpawners() {
        assertFalse(ItemMatchUtil.spawnerMatches(plain(Material.DIAMOND), plain(Material.SPAWNER)));
    }

}
