package net.bitbylogic.utils.trigger;

import lombok.Getter;
import lombok.NonNull;
import net.bitbylogic.utils.context.Context;
import net.bitbylogic.utils.context.ContextBuilder;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class TriggerInstance {

    @Getter
    private final Trigger trigger;

    private final List<TriggerInstance> children;
    private final AtomicBoolean active = new AtomicBoolean();

    private volatile @Nullable JavaPlugin plugin;

    public TriggerInstance(@NonNull Trigger trigger) {
        this.trigger = trigger;

        List<TriggerInstance> created = new ArrayList<>();

        for (Trigger child : trigger.getChildren()) {
            created.add(child.createInstance());
        }

        this.children = List.copyOf(created);
    }

    public String getId() {
        return trigger.getId();
    }

    public boolean isActive() {
        return active.get();
    }

    public List<TriggerInstance> getChildren() {
        return children;
    }

    protected @Nullable JavaPlugin getPlugin() {
        return plugin;
    }

    public void activate(@NonNull JavaPlugin plugin) {
        this.plugin = plugin;
        activate();
    }

    public void activate() {
        if (!active.compareAndSet(false, true)) {
            return;
        }

        onActivate();
    }

    public void deactivate() {
        deactivate(ContextBuilder.create().build());
    }

    public void deactivate(@NonNull Context context) {
        if (!active.compareAndSet(true, false)) {
            return;
        }

        onDeactivate(context);
    }

    public void trigger(@NonNull Context context) {
        if (trigger.isRepeatable()) {
            if (!active.get()) {
                return;
            }

            runAction(context);
            return;
        }

        if (!active.compareAndSet(true, false)) {
            return;
        }

        runAction(context);
        onDeactivate(context);

        if (!trigger.isDeactivateChildren()) {
            return;
        }

        for (TriggerInstance child : children) {
            child.deactivate(context);
        }
    }

    protected void onActivate() {

    }

    protected void onDeactivate(@NonNull Context context) {

    }

    private void runAction(Context context) {
        TriggerAction action = trigger.getAction();

        if (action == null) {
            return;
        }

        action.onTrigger(context);
    }

}
