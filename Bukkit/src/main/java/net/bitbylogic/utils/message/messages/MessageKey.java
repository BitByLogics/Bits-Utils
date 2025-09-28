package net.bitbylogic.utils.message.messages;

import lombok.Getter;
import lombok.NonNull;
import net.bitbylogic.utils.StringModifier;
import net.bitbylogic.utils.context.BukkitContextKeys;
import net.bitbylogic.utils.context.Context;
import net.bitbylogic.utils.message.format.Formatter;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

@Getter
public class MessageKey {

    private final @NonNull String path;
    private final @NonNull List<String> values;

    public MessageKey(@NonNull String path, @NonNull String defaultValue) {
        this.path = path;
        this.values = new ArrayList<>(List.of(defaultValue));
    }

    public MessageKey(@NonNull String path, @NonNull List<String> defaultValue) {
        this.path = path;
        this.values = new ArrayList<>(defaultValue);
    }

    public String get(@NonNull Context context) {
        return get();
    }

    public String get(@NonNull Player player, StringModifier... modifiers) {
        return get(modifiers);
    }

    public String get(StringModifier... modifiers) {
        return Formatter.format(values.getFirst(), modifiers);
    }

    public void send(@NonNull Context context, StringModifier... modifiers) {
        Player player = context.get(BukkitContextKeys.PLAYER).orElse(null);

        if(player == null) {
            return;
        }

        send(player, modifiers);
    }

    public void send(@NonNull CommandSender sender, StringModifier... modifiers) {
        values.forEach(message -> sender.sendMessage(Formatter.format(message, modifiers)));
    }

}
