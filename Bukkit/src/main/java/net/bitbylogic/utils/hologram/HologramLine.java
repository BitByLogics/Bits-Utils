package net.bitbylogic.utils.hologram;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import net.bitbylogic.utils.message.format.Formatter;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Transformation;
import org.joml.Quaternionf;
import org.joml.Vector3f;

@Getter
public class HologramLine {

    private final EntityType displayType;

    // General Display Settings
    private final Transformation transformation = new Transformation(new Vector3f(), new Quaternionf(), new Vector3f(), new Quaternionf());

    private Display.Billboard billboard;
    private Display.Brightness brightness;
    private boolean glowing;
    private Color glowColor;

    private float yaw;
    private float pitch;

    // Text Display Settings
    private String text;
    private Color backgroundColor;
    private byte opacity = -1;
    private boolean shadow;
    private boolean seeThrough;
    private TextDisplay.TextAlignment alignment;

    // Item Display Settings
    private ItemStack itemStack;
    private ItemDisplay.ItemDisplayTransform displayTransform;

    // Block Display Settings
    private BlockData blockData;

    private Display display;

    private HologramLine(@NonNull EntityType entityType, @NonNull String text) {
        this.displayType = entityType;
        this.text = text;
    }

    private HologramLine(@NonNull EntityType entityType, @NonNull ItemStack itemStack) {
        this.displayType = entityType;
        this.itemStack = itemStack;
    }

    private HologramLine(@NonNull EntityType entityType, @NonNull BlockData blockData) {
        this.displayType = entityType;
        this.blockData = blockData;
    }

    public static HologramLine of(@NonNull String text) {
        return new HologramLine(EntityType.TEXT_DISPLAY, text);
    }

    public static HologramLine of(@NonNull ItemStack itemStack) {
        return new HologramLine(EntityType.ITEM_DISPLAY, itemStack);
    }

    public static HologramLine of(@NonNull BlockData blockData) {
        return new HologramLine(EntityType.BLOCK_DISPLAY, blockData);
    }

    public HologramLine billboard(@NonNull Display.Billboard billboard) {
        this.billboard = billboard;
        return this;
    }

    public HologramLine brightness(@NonNull Display.Brightness brightness) {
        this.brightness = brightness;
        return this;
    }

    public HologramLine glowing(boolean glowing) {
        this.glowing = glowing;
        return this;
    }

    public HologramLine glowColor(@NonNull Color color) {
        this.glowColor = color;
        return this;
    }

    public HologramLine scale(float scale) {
        this.transformation.getScale().set(scale);
        return this;
    }

    public HologramLine translation(@NonNull Vector3f translation) {
        this.transformation.getTranslation().set(translation);
        return this;
    }

    public HologramLine rotation(float yaw, float pitch) {
        this.yaw = yaw;
        this.pitch = pitch;
        return this;
    }

    public HologramLine backgroundColor(@NonNull Color color) {
        if(displayType != EntityType.TEXT_DISPLAY) {
            throw new UnsupportedOperationException("Cannot set background color for non-text display entity");
        }

        backgroundColor = color;
        return this;
    }

    public HologramLine textOpacity(byte opacity) {
        if(displayType != EntityType.TEXT_DISPLAY) {
            throw new UnsupportedOperationException("Cannot set background color for non-text display entity");
        }

        this.opacity = opacity;
        return this;
    }

    public HologramLine textShadow(boolean shadow) {
        if(displayType != EntityType.TEXT_DISPLAY) {
            throw new UnsupportedOperationException("Cannot set background color for non-text display entity");
        }

        this.shadow = shadow;
        return this;
    }

    public HologramLine seeThrough(boolean seeThrough) {
        if(displayType != EntityType.TEXT_DISPLAY) {
            throw new UnsupportedOperationException("Cannot set background color for non-text display entity");
        }

        this.seeThrough = seeThrough;
        return this;
    }

    public HologramLine textAlignment(@NonNull TextDisplay.TextAlignment alignment) {
        if(displayType != EntityType.TEXT_DISPLAY) {
            throw new UnsupportedOperationException("Cannot set background color for non-text display entity");
        }

        this.alignment = alignment;
        return this;
    }

    public HologramLine displayTransform(@NonNull ItemDisplay.ItemDisplayTransform transform) {
        if(displayType != EntityType.ITEM_DISPLAY) {
            throw new UnsupportedOperationException("Cannot set background color for non-text display entity");
        }

        this.displayTransform = transform;
        return this;
    }

    public Display build(@NonNull Location location, boolean global) {
        if(location.getWorld() == null) {
            throw new IllegalArgumentException("Invalid location, world cannot be null");
        }

        switch (displayType) {
            case TEXT_DISPLAY -> {
                TextDisplay textDisplay = location.getWorld().spawn(location, TextDisplay.class);

                textDisplay.setText(Formatter.format(text));
                textDisplay.setBackgroundColor(backgroundColor == null ? textDisplay.getBackgroundColor() : backgroundColor);
                textDisplay.setTextOpacity(opacity == -1 ? textDisplay.getTextOpacity() : opacity);
                textDisplay.setShadowed(shadow);
                textDisplay.setSeeThrough(seeThrough);
                textDisplay.setAlignment(alignment == null ? textDisplay.getAlignment() : alignment);

                display = textDisplay;
            }
            case ITEM_DISPLAY -> {
                ItemDisplay itemDisplay = location.getWorld().spawn(location, ItemDisplay.class);

                itemDisplay.setItemStack(itemStack);
                itemDisplay.setItemDisplayTransform(displayTransform == null ? itemDisplay.getItemDisplayTransform() : displayTransform);

                display = itemDisplay;
            }
            case BLOCK_DISPLAY -> {
                BlockDisplay blockDisplay = location.getWorld().spawn(location, BlockDisplay.class);

                blockDisplay.setBlock(blockData);

                display = blockDisplay;
            }
            default -> {
                TextDisplay textDisplay = location.getWorld().spawn(location, TextDisplay.class);

                textDisplay.setText(Formatter.format("&c&lINVALID LINE"));
                textDisplay.setShadowed(true);

                display = textDisplay;
            }
        }

        display.setVisibleByDefault(global);
        display.setPersistent(false);
        display.setTeleportDuration(1);
        display.setBillboard(billboard == null ? display.getBillboard() : billboard);
        display.setBrightness(brightness == null ? display.getBrightness() : brightness);
        display.setGlowing(glowing);
        display.setGlowColorOverride(glowColor == null ? display.getGlowColorOverride() : glowColor);
        display.setTransformation(transformation);
        display.setRotation(yaw, pitch);

        return display;
    }

}
