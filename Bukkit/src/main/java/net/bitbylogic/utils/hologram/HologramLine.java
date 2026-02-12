package net.bitbylogic.utils.hologram;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import net.bitbylogic.utils.hologram.line.HologramBlockLine;
import net.bitbylogic.utils.hologram.line.HologramItemLine;
import net.bitbylogic.utils.hologram.line.HologramTextLine;
import net.bitbylogic.utils.hologram.type.HologramType;
import net.bitbylogic.utils.message.MessageUtil;
import net.bitbylogic.utils.server.ServerUtil;
import net.kyori.adventure.text.minimessage.internal.parser.ParsingExceptionImpl;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.BlockDisplay;
import org.bukkit.entity.Display;
import org.bukkit.entity.ItemDisplay;
import org.bukkit.entity.TextDisplay;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Transformation;
import org.jetbrains.annotations.Nullable;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Supplier;

@Getter
public abstract class HologramLine<SELF extends HologramLine<SELF, T>, T> {

    private final Transformation transformation = new Transformation(
            new Vector3f(),
            new Quaternionf(),
            new Vector3f(1, 1, 1),
            new Quaternionf()
    );

    private final Map<String, Object> metadata = new HashMap<>();

    private @NonNull String id = UUID.randomUUID().toString();

    private Display.Billboard billboard;
    private Display.Brightness brightness;

    private boolean glowing;
    private Color glowColor;

    private float yaw;
    private float pitch;

    private long updateInterval = 50;

    private T data;
    private Supplier<T> dataSupplier;

    @Setter
    private boolean applyLineSpacing = true;

    private transient @Nullable Display display;

    @Setter
    protected transient @Nullable Hologram hologram;

    @Setter
    protected transient @Nullable Vector3f lineOffset;

    @Setter
    protected transient long lastUpdateTime;

    public HologramLine(@NonNull T data) {
        this.data = data;
    }

    public HologramLine(@NonNull Supplier<T> dataSupplier) {
        this.dataSupplier = dataSupplier;
    }

    public Optional<T> getData() {
        return dataSupplier == null ? Optional.ofNullable(data) : Optional.ofNullable(dataSupplier.get());
    }

    protected abstract SELF self();

    public abstract HologramType getType();

    protected void updateData() {
        if(display == null || getData().isEmpty()) {
            return;
        }

        T data = getData().get();

        switch (getType()) {
            case TEXT -> {
                TextDisplay textDisplay = (TextDisplay) display;

                if (ServerUtil.isPaper()) {
                    try {
                        PaperHologramHandler.setText(MessageUtil.deserialize((String) data), textDisplay);
                    } catch (ParsingExceptionImpl exception) {
                        Bukkit.getLogger().warning("Failed to deserialize hologram text: " + data);
                        exception.printStackTrace();
                    }
                } else {
                    textDisplay.setText(MessageUtil.deserializeToSpigot((String) data));
                }
            }
            case ITEM -> {
                ItemDisplay itemDisplay = (ItemDisplay) display;

                itemDisplay.setItemStack((ItemStack) data);
            }
            case BLOCK -> {
                BlockDisplay blockDisplay = (BlockDisplay) display;

                blockDisplay.setBlock((BlockData) data);
            }
        }
    }

    public SELF id(@NonNull String id) {
        this.id = id;
        return self();
    }

    public SELF billboard(@NonNull Display.Billboard billboard) {
        this.billboard = billboard;
        return self();
    }

    public SELF brightness(@NonNull Display.Brightness brightness) {
        this.brightness = brightness;
        return self();
    }

    public SELF glowing(boolean glowing) {
        this.glowing = glowing;
        return self();
    }

    public SELF glowColor(@NonNull Color color) {
        this.glowColor = color;
        return self();
    }

    public SELF yaw(float yaw) {
        this.yaw = yaw;
        return self();
    }

    public SELF pitch(float pitch) {
        this.pitch = pitch;
        return self();
    }

    public SELF updateInterval(long updateInterval) {
        this.updateInterval = updateInterval;
        return self();
    }

    public SELF data(@NonNull T data) {
        this.data = data;

        if(display != null) {
            updateData();
        }

        return self();
    }

    public SELF data(@NonNull Supplier<T> dataSupplier) {
        this.dataSupplier = dataSupplier;

        if(display != null) {
            updateData();
        }

        return self();
    }

    public SELF scale(float scale) {
        this.transformation.getScale().set(scale);
        return self();
    }

    public SELF translation(@NonNull Vector3f translation) {
        this.transformation.getTranslation().set(translation);
        return self();
    }

    public SELF rotation(float yaw, float pitch) {
        this.yaw = yaw;
        this.pitch = pitch;
        return self();
    }

    public SELF applyLineSpacing(boolean applyLineSpacing) {
        this.applyLineSpacing = applyLineSpacing;
        return self();
    }

    public SELF metadata(@NonNull String key, @NonNull Object value) {
        metadata.put(key, value);
        return self();
    }

    public @Nullable Object getMetadata(@NonNull String key) {
        return metadata.get(key);
    }

    protected Display build(@NonNull Location location, boolean persistent, boolean global) {
        if(location.getWorld() == null) {
            throw new IllegalArgumentException("Invalid location, world cannot be null");
        }

        switch (getType()) {
            case TEXT -> {
                TextDisplay textDisplay = location.getWorld().spawn(location, TextDisplay.class);

                HologramTextLine textLine = (HologramTextLine) this;

                if (ServerUtil.isPaper()) {
                    try {
                        PaperHologramHandler.setText(MessageUtil.deserialize(textLine.getData().orElse("")), textDisplay);
                    } catch (ParsingExceptionImpl exception) {
                        Bukkit.getLogger().warning("Failed to deserialize hologram text: " + textLine.getData().orElse(""));
                        exception.printStackTrace();
                    }
                } else {
                    textDisplay.setText(MessageUtil.deserializeToSpigot(textLine.getData().orElse("")));
                }

                textDisplay.setBackgroundColor(textLine.getBackgroundColor() == null
                        ? textDisplay.getBackgroundColor() : textLine.getBackgroundColor());
                textDisplay.setTextOpacity(textLine.getOpacity() == -1
                        ? textDisplay.getTextOpacity() : textLine.getOpacity());
                textDisplay.setShadowed(textLine.isShadow());
                textDisplay.setSeeThrough(textLine.isSeeThrough());
                textDisplay.setAlignment(textLine.getAlignment() == null
                        ? textDisplay.getAlignment() : textLine.getAlignment());

                display = textDisplay;
            }
            case ITEM -> {
                ItemDisplay itemDisplay = location.getWorld().spawn(location, ItemDisplay.class);

                HologramItemLine itemLine = (HologramItemLine) this;

                itemDisplay.setItemStack(itemLine.getData().orElse(new ItemStack(Material.BARRIER)));
                itemDisplay.setItemDisplayTransform(itemLine.getDisplayTransform() == null
                        ? itemDisplay.getItemDisplayTransform() : itemLine.getDisplayTransform());

                display = itemDisplay;
            }
            case BLOCK -> {
                BlockDisplay blockDisplay = location.getWorld().spawn(location, BlockDisplay.class);

                HologramBlockLine blockLine = (HologramBlockLine) this;

                blockDisplay.setBlock(blockLine.getData().orElse(Material.OAK_LOG.createBlockData()));

                display = blockDisplay;
            }
        }

        display.setVisibleByDefault(global);
        display.setPersistent(persistent);
        display.setTeleportDuration(1);
        display.setBillboard(billboard == null ? display.getBillboard() : billboard);
        display.setBrightness(brightness == null ? display.getBrightness() : brightness);
        display.setGlowing(glowing);
        display.setGlowColorOverride(glowColor == null ? display.getGlowColorOverride() : glowColor);
        display.setTransformation(transformation);
        display.setRotation(yaw, pitch);

        return display;
    }

    public static HologramTextLine of(@NonNull String text) {
        return new HologramTextLine(text);
    }

}
