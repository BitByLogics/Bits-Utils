package net.bitbylogic.utils.action;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;

@Getter
public class ActionManager implements Listener {

    private static final UUID GLOBAL_UUID = UUID.randomUUID();

    private final JavaPlugin plugin;

    private final HashMap<UUID, List<Action>> actionMap;
    private final HashMap<UUID, List<ItemAction>> itemActionMap;

    public ActionManager(JavaPlugin plugin) {
        this.plugin = plugin;
        actionMap = Maps.newHashMap();
        itemActionMap = Maps.newHashMap();
    }

    public void trackItemAction(@Nullable Player player, ItemAction action) {
        UUID id = player == null ? GLOBAL_UUID : player.getUniqueId();

        List<ItemAction> actions = itemActionMap.getOrDefault(id, Lists.newArrayList());
        actions.add(action);
        itemActionMap.put(id, actions);
        plugin.getServer().getPluginManager().registerEvents(action, plugin);

        if (action.getExpireTime() != -1) {
            Bukkit.getScheduler().runTaskLater(plugin, () -> {
                List<ItemAction> actions1 = itemActionMap.getOrDefault(id, Lists.newArrayList());
                actions1.remove(action);
                HandlerList.unregisterAll(action);
                itemActionMap.put(id, actions1);

                if (!action.isCompleted()) {
                    action.onExpire(player);
                }
            }, action.getExpireTime());
        }
    }

    public void trackAction(@Nullable Player player, Action action) {
        UUID id = player == null ? GLOBAL_UUID : player.getUniqueId();

        List<Action> actions = actionMap.getOrDefault(id, Lists.newArrayList());
        actions.add(action);
        actionMap.put(id, actions);
        action.register(plugin);

        if (action.getExpireTime() != -1) {
            Bukkit.getScheduler().runTaskLater(plugin, () -> {
                List<Action> actions1 = actionMap.getOrDefault(id, Lists.newArrayList());
                actions1.remove(action);
                HandlerList.unregisterAll(action);
                actionMap.put(id, actions1);

                if (!action.isCompleted()) {
                    action.onExpire(player);
                }
            }, action.getExpireTime());
        }
    }

}
