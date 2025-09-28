package net.bitbylogic.utils.context;

import lombok.NonNull;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public final class ContextBuilder {

    private final Context context = new Context();

    public static ContextBuilder create() {
        return new ContextBuilder();
    }

    public ContextBuilder with(@NonNull UUID playerId) {
        return with(Bukkit.getPlayer(playerId));
    }

    public ContextBuilder with(@Nullable Player player) {
        if(player == null) {
            return this;
        }

        context.put(BukkitContextKeys.PLAYER, player);
        return this;
    }

    public ContextBuilder with(@Nullable Entity entity) {
        if(entity == null) {
            return this;
        }

        context.put(BukkitContextKeys.ENTITY, entity);
        return this;
    }

    public ContextBuilder with(@NonNull CommandSender sender) {
        if (sender instanceof Player player) {
            return with(player);
        }

        return this;
    }

    public <T> ContextBuilder with(@NonNull ContextKey<T> key, @NonNull T value) {
        context.put(key, value);
        return this;
    }

    public ContextBuilder withAttribute(@NonNull String key, @NonNull Object value) {
        context.putAttribute(key, value);
        return this;
    }

    public Context build() {
        return context;
    }

}

