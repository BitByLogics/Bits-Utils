package net.bitbylogic.utils.hologram.line;

import lombok.Getter;
import lombok.NonNull;
import net.bitbylogic.utils.hologram.HologramLine;
import net.bitbylogic.utils.hologram.type.HologramType;
import org.bukkit.Color;
import org.bukkit.entity.TextDisplay;

import java.util.function.Supplier;

@Getter
public class HologramTextLine extends HologramLine<HologramTextLine, String> {

    private Color backgroundColor;
    private byte opacity = -1;
    private boolean shadow;
    private boolean seeThrough;
    private TextDisplay.TextAlignment alignment;

    public HologramTextLine(@NonNull String data) {
        super(data);
    }

    public HologramTextLine(@NonNull Supplier<String> dataSupplier) {
        super(dataSupplier);
    }

    @Override
    protected HologramTextLine self() {
        return this;
    }

    public HologramTextLine backgroundColor(@NonNull Color color) {
        this.backgroundColor = color;
        return this;
    }

    public HologramTextLine opacity(byte opacity) {
        this.opacity = opacity;
        return this;
    }

    public HologramTextLine shadow(boolean shadow) {
        this.shadow = shadow;
        return this;
    }

    public HologramTextLine seeThrough(boolean seeThrough) {
        this.seeThrough = seeThrough;
        return this;
    }

    public HologramTextLine alignment(@NonNull TextDisplay.TextAlignment alignment) {
        this.alignment = alignment;
        return this;
    }

    @Override
    public HologramType getType() {
        return HologramType.TEXT;
    }

}
