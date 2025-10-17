package net.bitbylogic.utils.hologram;

import lombok.Getter;
import lombok.NonNull;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Display;
import org.bukkit.entity.Player;
import org.bukkit.entity.TextDisplay;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Transformation;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class Hologram {

    private static final NamespacedKey HOLOGRAM_KEY = new NamespacedKey("bitsutils", "hologram");

    @Getter
    private final List<HologramLine<?, ?>> lines = new ArrayList<>();

    private float lineSpacing = 0.17f;

    private boolean persistent;
    private boolean global = true;

    private Location location;

    private int taskId = -1;

    public Hologram(@NonNull HologramLine<?, ?> hologramLine) {
        this.lines.add(hologramLine);
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
        if(taskId != -1) {
            return this;
        }

        taskId = Bukkit.getScheduler().runTaskTimer(plugin, () -> {
            if (lines.isEmpty()) return;

            lines.forEach(hologramLine -> {
                if(hologramLine.getLastUpdateTime() != 0 &&
                        System.currentTimeMillis() - hologramLine.getLastUpdateTime() < hologramLine.getUpdateInterval()) {
                    return;
                }

                hologramLine.setLastUpdateTime(System.currentTimeMillis());
                hologramLine.updateData();

                updateSpacing();
            });
        }, 0, 1).getTaskId();
        return this;
    }

    public void spawn(@NonNull Location location) {
        spawn(location, null, new ArrayList<>());
    }

    public void spawn(@NonNull Location location, @Nullable JavaPlugin plugin, @NonNull List<Player> viewers) {
        this.location = location;

        Display display = null;

        List<HologramLine<?, ?>> sortedLines = new ArrayList<>(lines);
        sortedLines = sortedLines.reversed();

        for(int i = 0; i < sortedLines.size(); i++) {
            HologramLine<?, ?> line = sortedLines.get(i);

            if (display != null) {
                Display lineDisplay = line.build(location, persistent, global);
                lineDisplay.getPersistentDataContainer().set(HOLOGRAM_KEY, PersistentDataType.BOOLEAN, true);

                Transformation transformation = lineDisplay.getTransformation();

                if(line.isApplyLineSpacing()) {
                    transformation.getTranslation().add(0, transformation.getTranslation().y + (lineSpacing * i), 0);
                }

                line.setLineOffset(new Vector3f(transformation.getTranslation()));
                lineDisplay.setTransformation(transformation);

                if (!global && plugin != null) {
                    viewers.forEach(player -> player.showEntity(plugin, lineDisplay));
                }

                display = lineDisplay;
                continue;
            }

            display = line.build(location, persistent, global);
            display.getPersistentDataContainer().set(HOLOGRAM_KEY, PersistentDataType.BOOLEAN, true);

            if (!global && plugin != null) {
                Display finalDisplay = display;
                viewers.forEach(player -> player.showEntity(plugin, finalDisplay));
            }
        }
    }

    public void teleport(@NonNull Location location) {
        for (HologramLine<?, ?> line : lines) {
            if (line.getDisplay() == null) {
                continue;
            }

            line.getDisplay().teleport(location);
        }
    }

    public void updateSpacing() {
        List<HologramLine<?, ?>> sortedLines = new ArrayList<>(lines);
        sortedLines = sortedLines.reversed();

        float currentY = 0f;

        for (HologramLine<?, ?> line : sortedLines) {
            if (line.getDisplay() == null && location != null) {
                line.build(location, persistent, global);
            }

            if (line.getDisplay() == null) {
                continue;
            }

            Display display = line.getDisplay();
            Transformation transformation = display.getTransformation();

            int lineCount = 1;

            if (display instanceof TextDisplay textDisplay) {
                String text = Objects.requireNonNullElse(textDisplay.getText(), "");
                lineCount = text.split("\n").length;
            }

            transformation.getTranslation().set(0, currentY, 0);
            display.setTransformation(transformation);

            line.setLineOffset(new Vector3f(transformation.getTranslation()));

            currentY += lineSpacing * lineCount;
        }
    }

    public boolean cleanup() {
        if(taskId != -1) {
            Bukkit.getScheduler().cancelTask(taskId);
        }

        if (lines.isEmpty()) {
            return false;
        }

        lines.stream()
                .filter(hologramLine -> hologramLine.getDisplay() != null)
                .forEach(hologramLine -> hologramLine.getDisplay().remove());
        return true;
    }

    public Optional<HologramLine<?, ?>> getLine(@NonNull String id) {
        return lines.stream().filter(line -> line.getId().equalsIgnoreCase(id)).findFirst();
    }

    public Optional<Display> getTop() {
        return lines.isEmpty() ? Optional.empty() : Optional.of(lines.getLast().getDisplay());
    }

    public Optional<Display> getBase() {
        return lines.isEmpty() ? Optional.empty() : Optional.of(lines.getFirst().getDisplay());
    }

    public static boolean isHologram(@NonNull Display display) {
        return display.getPersistentDataContainer().has(HOLOGRAM_KEY, PersistentDataType.BOOLEAN);
    }

}
