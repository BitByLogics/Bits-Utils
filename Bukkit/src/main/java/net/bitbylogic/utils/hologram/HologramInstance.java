package net.bitbylogic.utils.hologram;

import io.papermc.paper.threadedregions.scheduler.ScheduledTask;
import lombok.Getter;
import lombok.NonNull;
import net.bitbylogic.utils.hologram.line.HologramBlockLine;
import net.bitbylogic.utils.hologram.line.HologramItemLine;
import net.bitbylogic.utils.hologram.line.HologramTextLine;
import net.bitbylogic.utils.message.MessageUtil;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.BlockDisplay;
import org.bukkit.entity.Display;
import org.bukkit.entity.ItemDisplay;
import org.bukkit.entity.Player;
import org.bukkit.entity.TextDisplay;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Transformation;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.locks.ReentrantLock;

public class HologramInstance {

    private final ReentrantLock lock = new ReentrantLock();

    @Getter
    private final Hologram hologram;

    private final List<LineState> states = new ArrayList<>();

    private final @Nullable JavaPlugin plugin;

    private Location location;
    private @Nullable ScheduledTask updateTask;

    HologramInstance(@NonNull Hologram hologram, @NonNull Location location, @Nullable JavaPlugin plugin, @NonNull List<Player> viewers) {
        this.hologram = hologram;
        this.location = location.clone();
        this.plugin = plugin;

        if (location.getWorld() == null) {
            Bukkit.getLogger().warning("Cannot spawn hologram, the location has no world.");
            return;
        }

        List<HologramLine<?, ?>> lines = hologram.getLines();

        for (int i = lines.size() - 1; i >= 0; i--) {
            LineState state = new LineState(lines.get(i));
            state.display = build(state.line, this.location);
            state.display.getPersistentDataContainer().set(Hologram.HOLOGRAM_KEY, PersistentDataType.BOOLEAN, true);

            if (!hologram.isGlobal() && plugin != null) {
                Display display = state.display;
                viewers.forEach(player -> player.showEntity(plugin, display));
            }

            states.add(state);
        }

        updateSpacing();
        startUpdateTask();
    }

    public Location getLocation() {
        lock.lock();

        try {
            return location.clone();
        } finally {
            lock.unlock();
        }
    }

    public Optional<Display> getDisplay(@NonNull String lineId) {
        lock.lock();

        try {
            for (LineState state : states) {
                if (state.line.getId().equalsIgnoreCase(lineId)) {
                    return Optional.ofNullable(state.display);
                }
            }

            return Optional.empty();
        } finally {
            lock.unlock();
        }
    }

    public Optional<Display> getTop() {
        lock.lock();

        try {
            if (states.isEmpty()) {
                return Optional.empty();
            }

            return Optional.ofNullable(states.getFirst().display);
        } finally {
            lock.unlock();
        }
    }

    public Optional<Display> getBase() {
        lock.lock();

        try {
            if (states.isEmpty()) {
                return Optional.empty();
            }

            return Optional.ofNullable(states.getLast().display);
        } finally {
            lock.unlock();
        }
    }

    public void teleport(@NonNull Location location) {
        lock.lock();

        try {
            for (LineState state : states) {
                if (state.display == null) {
                    continue;
                }

                state.display.teleportAsync(location);
            }

            this.location = location.clone();
            stopUpdateTask();
            startUpdateTask();
        } finally {
            lock.unlock();
        }
    }

    public void refresh() {
        lock.lock();

        try {
            long now = System.currentTimeMillis();

            for (LineState state : states) {
                state.lastUpdate = now;
                updateData(state);
            }

            updateSpacing();
        } finally {
            lock.unlock();
        }
    }

    public void updateSpacing() {
        lock.lock();

        try {
            float currentY = 0f;

            for (LineState state : states) {
                if (state.display == null || !state.line.isApplyLineSpacing()) {
                    continue;
                }

                Display display = state.display;
                Transformation transformation = display.getTransformation();
                Transformation base = state.line.getTransformation();

                int lineCount = 1;

                if (display instanceof TextDisplay textDisplay) {
                    String text = Objects.requireNonNullElse(textDisplay.getText(), "");
                    lineCount = text.split("\n").length;
                }

                transformation.getTranslation().set(
                        base.getTranslation().x(),
                        base.getTranslation().y() + currentY,
                        base.getTranslation().z()
                );
                display.setTransformation(transformation);

                currentY += hologram.getLineSpacing() * lineCount;
            }
        } finally {
            lock.unlock();
        }
    }

    public void cleanup() {
        lock.lock();

        try {
            stopUpdateTask();

            for (LineState state : states) {
                if (state.display == null) {
                    continue;
                }

                state.display.remove();
                state.display = null;
            }
        } finally {
            lock.unlock();
        }
    }

    private void startUpdateTask() {
        if (plugin == null || updateTask != null || states.isEmpty()) {
            return;
        }

        updateTask = Bukkit.getRegionScheduler().runAtFixedRate(plugin, location, task -> tick(), 1, 1);
    }

    private void stopUpdateTask() {
        if (updateTask == null) {
            return;
        }

        updateTask.cancel();
        updateTask = null;
    }

    private void tick() {
        lock.lock();

        try {
            boolean updated = false;
            long now = System.currentTimeMillis();

            for (LineState state : states) {
                if (state.lastUpdate != 0 && now - state.lastUpdate < state.line.getUpdateInterval()) {
                    continue;
                }

                state.lastUpdate = now;
                updateData(state);
                updated = true;
            }

            if (!updated) {
                return;
            }

            updateSpacing();
        } finally {
            lock.unlock();
        }
    }

    private void updateData(LineState state) {
        if (state.display == null) {
            return;
        }

        HologramLine<?, ?> line = state.line;
        Display display = state.display;

        if (line instanceof HologramTextLine textLine && display instanceof TextDisplay textDisplay) {
            textLine.getData().ifPresent(text -> textDisplay.text(MessageUtil.deserialize(text)));
            return;
        }

        if (line instanceof HologramItemLine itemLine && display instanceof ItemDisplay itemDisplay) {
            itemLine.getData().ifPresent(itemDisplay::setItemStack);
            return;
        }

        if (line instanceof HologramBlockLine blockLine && display instanceof BlockDisplay blockDisplay) {
            blockLine.getData().ifPresent(blockDisplay::setBlock);
        }
    }

    private Display build(HologramLine<?, ?> line, Location location) {
        Display display = spawnDisplay(line, location);

        display.setVisibleByDefault(hologram.isGlobal());
        display.setPersistent(hologram.isPersistent());
        display.setTeleportDuration(1);

        if (line.getBillboard() != null) {
            display.setBillboard(line.getBillboard());
        }

        if (line.getBrightness() != null) {
            display.setBrightness(line.getBrightness());
        }

        display.setGlowing(line.isGlowing());

        if (line.getGlowColor() != null) {
            display.setGlowColorOverride(line.getGlowColor());
        }

        display.setTransformation(line.getTransformation());
        display.setRotation(line.getYaw(), line.getPitch());

        return display;
    }

    private Display spawnDisplay(HologramLine<?, ?> line, Location location) {
        if (line instanceof HologramTextLine textLine) {
            TextDisplay textDisplay = location.getWorld().spawn(location, TextDisplay.class);

            textDisplay.text(MessageUtil.deserialize(textLine.getData().orElse("")));

            if (textLine.getBackgroundColor() != null) {
                textDisplay.setBackgroundColor(textLine.getBackgroundColor());
            }

            if (textLine.getOpacity() != -1) {
                textDisplay.setTextOpacity(textLine.getOpacity());
            }

            textDisplay.setShadowed(textLine.isShadow());
            textDisplay.setSeeThrough(textLine.isSeeThrough());

            if (textLine.getAlignment() != null) {
                textDisplay.setAlignment(textLine.getAlignment());
            }

            return textDisplay;
        }

        if (line instanceof HologramItemLine itemLine) {
            ItemDisplay itemDisplay = location.getWorld().spawn(location, ItemDisplay.class);

            itemDisplay.setItemStack(itemLine.getData().orElse(new ItemStack(Material.BARRIER)));

            if (itemLine.getDisplayTransform() != null) {
                itemDisplay.setItemDisplayTransform(itemLine.getDisplayTransform());
            }

            return itemDisplay;
        }

        HologramBlockLine blockLine = (HologramBlockLine) line;
        BlockDisplay blockDisplay = location.getWorld().spawn(location, BlockDisplay.class);

        BlockData blockData = blockLine.getData().orElse(Material.OAK_LOG.createBlockData());
        blockDisplay.setBlock(blockData);

        return blockDisplay;
    }

    private static final class LineState {

        private final HologramLine<?, ?> line;

        private @Nullable Display display;
        private long lastUpdate;

        private LineState(HologramLine<?, ?> line) {
            this.line = line;
        }

    }

}
