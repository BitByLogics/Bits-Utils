package net.bitbylogic.utils.trigger.impl;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;
import net.bitbylogic.utils.config.metadata.ConfiguredMetadata;
import net.bitbylogic.utils.context.Context;
import org.bukkit.Bukkit;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.Nullable;

public abstract class EventTrigger<T extends Event> extends SimpleTrigger implements Listener {

    @Getter(AccessLevel.PROTECTED)
    private @Nullable JavaPlugin plugin;

    public EventTrigger(@NonNull String id, @NonNull ConfiguredMetadata metadata) {
        super(id, metadata);
    }

    public void setActive(boolean active, @NonNull JavaPlugin plugin) {
        setActive(active);

        if(this.plugin == null) {
            this.plugin = plugin;
        }

        if(!active) {
            return;
        }

        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    @Override
    public void setActive(boolean active) {
        super.setActive(active);

        if(active) {
            return;
        }

        HandlerList.unregisterAll(this);
    }

    @Override
    public void onDeactivate(@NonNull Context context) {
        HandlerList.unregisterAll(this);
    }

    public abstract void onEvent(T event);

}