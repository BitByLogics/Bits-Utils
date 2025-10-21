package net.bitbylogic.utils;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.enchantments.Enchantment;

@Getter
@RequiredArgsConstructor
public class EnchantmentData {

    private final Enchantment enchantment;
    private final int level;
    private final boolean ignoreLevelRestriction;

}
