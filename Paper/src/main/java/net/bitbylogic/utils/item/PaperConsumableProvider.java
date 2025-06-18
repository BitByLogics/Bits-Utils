package net.bitbylogic.utils.item;

import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.Consumable;
import org.bukkit.Sound;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;

public class PaperConsumableProvider {

    public static void provide(ItemStack item, ConfigurationSection section) {
        Consumable consumable = Consumable.consumable()
                .hasConsumeParticles(section.getBoolean("Particles", true))
                .consumeSeconds(section.getInt("Consume-Seconds", 3))
                .sound(Sound.valueOf(section.getString("Sound", "ENTITY_GENERIC_EAT")).getKey())
                .build();

        item.setData(DataComponentTypes.CONSUMABLE, consumable);
    }

}
