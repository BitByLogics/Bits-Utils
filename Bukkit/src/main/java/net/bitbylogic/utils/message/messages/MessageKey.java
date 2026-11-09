package net.bitbylogic.utils.message.messages;

import lombok.Getter;
import net.bitbylogic.utils.context.BukkitContextKeys;
import net.bitbylogic.utils.context.Context;
import net.bitbylogic.utils.context.DefaultContextKeys;
import net.bitbylogic.utils.message.MessageUtil;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.*;

@Getter
public class MessageKey {

    private final @NotNull String path;
    private final @NotNull Map<Locale, List<String>> values;

    public MessageKey(@NotNull String path, @NotNull String defaultValue) {
        this.path = path;
        this.values = new HashMap<>();

        values.put(Locale.ENGLISH, new ArrayList<>(List.of(defaultValue)));
    }

    public MessageKey(@NotNull String path, @NotNull List<String> defaultValue) {
        this.path = path;
        this.values = new HashMap<>();

        values.put(Locale.ENGLISH, new ArrayList<>(defaultValue));
    }

    public List<String> getValues(@NotNull Locale locale) {
        return values.computeIfAbsent(locale, l -> new ArrayList<>());
    }

    public Component get(@NotNull Context context, TagResolver.Single... placeholders) {
        Locale locale = context.getOrDefault(DefaultContextKeys.LOCALE, Locale.ENGLISH);

        return get(locale, placeholders);
    }

    public Component get(Audience audience) {
        if (audience instanceof Player player) {
            return get(localeOf(player));
        }

        return get(Locale.ENGLISH);
    }

    public Component get(@NotNull Player player, TagResolver.Single... placeholders) {
        return get(localeOf(player), placeholders);
    }

    public Component get(@NotNull Locale locale, TagResolver.Single... placeholders) {
        return MessageUtil.deserialize(valuesFor(locale).getFirst(), placeholders);
    }

    public Component get(TagResolver.Single... placeholders) {
        return MessageUtil.deserialize(values.get(Locale.ENGLISH).getFirst(), placeholders);
    }

    public List<Component> getAll(TagResolver.Single... placeholders) {
        List<Component> components = new ArrayList<>();

        for (String string : values.get(Locale.ENGLISH)) {
            components.add(MessageUtil.deserialize(string, placeholders));
        }

        return components;
    }

    public String getPlain() {
        return values.get(Locale.ENGLISH).getFirst();
    }

    public List<String> getPlainValues() {
        return values.get(Locale.ENGLISH);
    }

    public void send(@NotNull Context context, TagResolver.Single... placeholders) {
        Player player = context.get(BukkitContextKeys.PLAYER).orElse(null);

        if (player == null) {
            return;
        }

        Locale locale = context.getOrDefault(DefaultContextKeys.LOCALE, Locale.ENGLISH);

        send(player, locale, placeholders);
    }

    public void send(@NotNull Audience audience, TagResolver.Single... placeholders) {
        send(audience, Locale.ENGLISH, placeholders);
    }

    public void send(@NotNull Audience audience, @NotNull Locale locale, TagResolver.Single... placeholders) {
        valuesFor(locale).forEach(message -> audience.sendMessage(MessageUtil.deserialize(message, placeholders)));
    }

    private List<String> valuesFor(Locale locale) {
        List<String> localized = values.get(locale);

        if (localized == null || localized.isEmpty()) {
            return values.get(Locale.ENGLISH);
        }

        return localized;
    }

    private Locale localeOf(Player player) {
        String localeString = player.getLocale();

        if (localeString.isEmpty()) {
            return Locale.ENGLISH;
        }

        return Locale.of(localeString.split("_", 2)[0]);
    }

}
