package net.bitbylogic.utils.hologram;

import lombok.Getter;
import lombok.NonNull;
import org.bukkit.Location;
import org.bukkit.entity.Display;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Transformation;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class Hologram {

    private float lineSpacing = 0.17f;

    @Getter
    private final List<HologramLine> lines = new ArrayList<>();

    private boolean global = true;

    public Hologram(@NonNull HologramLine hologramLine) {
        this.lines.add(hologramLine);
    }

    public Hologram addLine(@NonNull HologramLine line) {
        this.lines.add(line);
        return this;
    }

    public Hologram global(boolean global) {
        this.global = global;
        return this;
    }

    public Hologram lineSpacing(float lineSpacing) {
        this.lineSpacing = lineSpacing;
        return this;
    }

    public void spawn(@NonNull Location location) {
        spawn(location, null, new ArrayList<>());
    }

    public void spawn(@NonNull Location location, @Nullable JavaPlugin plugin, @NonNull List<Player> viewers) {
        Display display = null;

        for(int i = 0; i < lines.size(); i++) {
            HologramLine line = lines.get(i);

            if (display != null) {
                Display lineDisplay = line.build(location, global);
                Transformation transformation = lineDisplay.getTransformation();
                transformation.getTranslation().set(0, transformation.getTranslation().y + (lineSpacing * i), 0);

                lineDisplay.setTransformation(transformation);

                if (!global && plugin != null) {
                    viewers.forEach(player -> player.showEntity(plugin, lineDisplay));
                }

                display = lineDisplay;
                continue;
            }

            display = line.build(location, global);

            if (!global && plugin != null) {
                Display finalDisplay = display;
                viewers.forEach(player -> player.showEntity(plugin, finalDisplay));
            }
        }
    }

    public void teleport(@NonNull Location location) {
        for(int i = 0; i < lines.size(); i++) {
            HologramLine line = lines.get(i);

            if(line.getDisplay() == null) {
                continue;
            }

            line.getDisplay().teleport(location);
        }
    }

    public boolean cleanup() {
        if (lines.isEmpty()) {
            return false;
        }

        lines.stream()
                .filter(hologramLine -> hologramLine.getDisplay() != null)
                .forEach(hologramLine -> hologramLine.getDisplay().remove());
        return true;
    }

    public Optional<Display> getTop() {
        return lines.isEmpty() ? Optional.empty() : Optional.of(lines.getLast().getDisplay());
    }

    public Optional<Display> getBase() {
        return lines.isEmpty() ? Optional.empty() : Optional.of(lines.getFirst().getDisplay());
    }

}
