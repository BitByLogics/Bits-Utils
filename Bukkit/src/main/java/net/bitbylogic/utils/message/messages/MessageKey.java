package net.bitbylogic.utils.message.messages;

import lombok.Getter;
import lombok.NonNull;
import net.bitbylogic.utils.StringModifier;
import net.bitbylogic.utils.context.BukkitContextKeys;
import net.bitbylogic.utils.context.Context;
import net.bitbylogic.utils.context.DefaultContextKeys;
import net.bitbylogic.utils.message.format.Formatter;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.*;

@Getter
public class MessageKey {

    private final @NonNull String path;
    private final @NonNull Map<Locale, List<String>> values;

    public MessageKey(@NonNull String path, @NonNull String defaultValue) {
        this.path = path;
        this.values = new HashMap<>();

        values.put(Locale.ENGLISH, new ArrayList<>(List.of(defaultValue)));
    }

    public MessageKey(@NonNull String path, @NonNull List<String> defaultValue) {
        this.path = path;
        this.values = new HashMap<>();

        values.put(Locale.ENGLISH, new ArrayList<>(defaultValue));
    }

    public List<String> getValues(@NonNull Locale locale) {
        return values.computeIfAbsent(locale, loc -> values.get(Locale.ENGLISH));
    }

    public String get(@NonNull Context context) {
        Locale locale = context.getOrDefault(DefaultContextKeys.LOCALE, Locale.ENGLISH);

        return get(locale);
    }

    public String get(@NonNull Player player) {
        String localeString = player.getLocale();

        if (localeString.isEmpty()) {
            return get(Locale.ENGLISH);
        }

        String[] parts = localeString.split("_", 2);

        Locale locale = parts.length == 2
                ? Locale.of(parts[0], parts[1].toUpperCase())
                : Locale.of(parts[0]);

        return get(locale);
    }

    public String get(@NonNull Locale locale, StringModifier... modifiers) {
        return Formatter.format(values.get(locale).getFirst(), modifiers);
    }

    public void send(@NonNull Context context, StringModifier... modifiers) {
        Player player = context.get(BukkitContextKeys.PLAYER).orElse(null);

        if (player == null) {
            return;
        }

        Locale locale = context.getOrDefault(DefaultContextKeys.LOCALE, Locale.ENGLISH);

        send(player, locale, modifiers);
    }

    public void send(@NonNull CommandSender sender, StringModifier... modifiers) {
        send(sender, Locale.ENGLISH, modifiers);
    }

    public void send(@NonNull CommandSender sender, @NonNull Locale locale, StringModifier... modifiers) {
        values.getOrDefault(locale, values.get(Locale.ENGLISH)).forEach(message -> sender.sendMessage(Formatter.format(message, modifiers)));
    }

}
