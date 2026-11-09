package net.bitbylogic.utils.trigger.impl;

import lombok.NonNull;
import net.bitbylogic.utils.context.Context;
import net.bitbylogic.utils.trigger.Trigger;
import net.bitbylogic.utils.trigger.TriggerInstance;
import org.bukkit.Bukkit;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

public abstract class EventTriggerInstance<T extends Event> extends TriggerInstance implements Listener {

    public EventTriggerInstance(@NonNull Trigger trigger) {
        super(trigger);
    }

    @Override
    protected void onActivate() {
        HandlerList.unregisterAll(this);

        JavaPlugin plugin = getPlugin();

        if (plugin == null) {
            Bukkit.getLogger().warning("Event trigger " + getId() + " was activated without a plugin and will not listen for events.");
            return;
        }

        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    @Override
    protected void onDeactivate(@NonNull Context context) {
        HandlerList.unregisterAll(this);
    }

    public abstract void onEvent(T event);

}
