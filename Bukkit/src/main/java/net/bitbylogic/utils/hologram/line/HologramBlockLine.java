package net.bitbylogic.utils.hologram.line;

import lombok.NonNull;
import net.bitbylogic.utils.hologram.HologramLine;
import net.bitbylogic.utils.hologram.type.HologramType;
import org.bukkit.block.data.BlockData;

import java.util.function.Supplier;

public class HologramBlockLine extends HologramLine<HologramBlockLine, BlockData> {

    public HologramBlockLine(@NonNull BlockData data) {
        super(data);
    }

    public HologramBlockLine(@NonNull Supplier<BlockData> dataSupplier) {
        super(dataSupplier);
    }

    @Override
    protected HologramBlockLine self() {
        return this;
    }

    @Override
    public HologramType getType() {
        return HologramType.BLOCK;
    }

}
