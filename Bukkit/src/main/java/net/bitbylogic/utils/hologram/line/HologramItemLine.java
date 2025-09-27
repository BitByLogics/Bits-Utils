package net.bitbylogic.utils.hologram.line;

import lombok.Getter;
import lombok.NonNull;
import net.bitbylogic.utils.hologram.HologramLine;
import net.bitbylogic.utils.hologram.type.HologramType;
import org.bukkit.entity.ItemDisplay;
import org.bukkit.inventory.ItemStack;

import java.util.function.Supplier;

@Getter
public class HologramItemLine extends HologramLine<HologramItemLine, ItemStack> {

    private ItemDisplay.ItemDisplayTransform displayTransform;

    public HologramItemLine(@NonNull ItemStack data) {
        super(data);
    }

    public HologramItemLine(@NonNull Supplier<ItemStack> dataSupplier) {
        super(dataSupplier);
    }

    @Override
    protected HologramItemLine self() {
        return this;
    }

    public HologramItemLine displayTransform(@NonNull ItemDisplay.ItemDisplayTransform displayTransform) {
        this.displayTransform = displayTransform;
        return this;
    }

    @Override
    public HologramType getType() {
        return HologramType.ITEM;
    }

}
