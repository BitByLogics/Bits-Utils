package net.bitbylogic.utils.hologram;

import lombok.NonNull;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Display;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CopyOnWriteArrayList;

public class Hologram {

    static final NamespacedKey HOLOGRAM_KEY = new NamespacedKey("bitsutils", "hologram");

    private final List<HologramLine<?, ?>> lines = new CopyOnWriteArrayList<>();

    private volatile float lineSpacing = 0.17f;

    private volatile boolean persistent;
    private volatile boolean global = true;

    private volatile @Nullable JavaPlugin plugin;

    public Hologram(@NonNull HologramLine<?, ?> hologramLine) {
        this.lines.add(hologramLine);
    }

    public List<HologramLine<?, ?>> getLines() {
        return List.copyOf(lines);
    }

    public float getLineSpacing() {
        return lineSpacing;
    }

    public boolean isPersistent() {
        return persistent;
    }

    public boolean isGlobal() {
        return global;
    }

    public Hologram addLine(@NonNull HologramLine<?, ?> line) {
        this.lines.add(line);
        return this;
    }

    public Hologram global(boolean global) {
        this.global = global;
        return this;
    }

    public Hologram persistent(boolean persistent) {
        this.persistent = persistent;
        return this;
    }

    public Hologram lineSpacing(float lineSpacing) {
        this.lineSpacing = lineSpacing;
        return this;
    }

    public Hologram schedule(@NonNull JavaPlugin plugin) {
        this.plugin = plugin;
        return this;
    }

    public HologramInstance spawn(@NonNull Location location) {
        return new HologramInstance(this, location, plugin, List.of());
    }

    public HologramInstance spawn(@NonNull Location location, @NonNull JavaPlugin plugin, @NonNull List<Player> viewers) {
        return new HologramInstance(this, location, plugin, List.copyOf(viewers));
    }

    public Optional<HologramLine<?, ?>> getLine(@NonNull String id) {
        return lines.stream().filter(line -> line.getId().equalsIgnoreCase(id)).findFirst();
    }

    public static boolean isHologram(@NonNull Display display) {
        return display.getPersistentDataContainer().has(HOLOGRAM_KEY, PersistentDataType.BOOLEAN);
    }

}
