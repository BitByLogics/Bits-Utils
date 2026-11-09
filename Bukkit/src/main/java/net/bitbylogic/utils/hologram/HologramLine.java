package net.bitbylogic.utils.hologram;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import net.bitbylogic.utils.hologram.line.HologramTextLine;
import net.bitbylogic.utils.hologram.type.HologramType;
import org.bukkit.Color;
import org.bukkit.entity.Display;
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

    @Getter(AccessLevel.NONE)
    private final Transformation transformation = new Transformation(
            new Vector3f(),
            new Quaternionf(),
            new Vector3f(1, 1, 1),
            new Quaternionf()
    );

    @Getter(AccessLevel.NONE)
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

    public HologramLine(@NonNull T data) {
        this.data = data;
    }

    public HologramLine(@NonNull Supplier<T> dataSupplier) {
        this.dataSupplier = dataSupplier;
    }

    public Optional<T> getData() {
        if (dataSupplier == null) {
            return Optional.ofNullable(data);
        }

        return Optional.ofNullable(dataSupplier.get());
    }

    public Transformation getTransformation() {
        return new Transformation(
                new Vector3f(transformation.getTranslation()),
                new Quaternionf(transformation.getLeftRotation()),
                new Vector3f(transformation.getScale()),
                new Quaternionf(transformation.getRightRotation())
        );
    }

    public Map<String, Object> getMetadata() {
        return Map.copyOf(metadata);
    }

    protected abstract SELF self();

    public abstract HologramType getType();

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
        this.dataSupplier = null;
        return self();
    }

    public SELF data(@NonNull Supplier<T> dataSupplier) {
        this.dataSupplier = dataSupplier;
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

    public static HologramTextLine of(@NonNull String text) {
        return new HologramTextLine(text);
    }

}
