package net.bitbylogic.utils.message;

import lombok.Getter;
import lombok.Setter;
import net.bitbylogic.utils.message.tag.SmallCapsModifyingTag;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import net.kyori.adventure.text.minimessage.tag.standard.StandardTags;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

// TODO: Add caching, improve this overall
public class MessageUtil {

    private static final int CENTER_PIXELS = 154;

    private static final LegacyComponentSerializer LEGACY_SERIALIZER = LegacyComponentSerializer.builder().hexColors().build();
    private static final PlainTextComponentSerializer PLAIN_SERIALIZER = PlainTextComponentSerializer.builder().build();

    private static final TagResolver SMALL_CAPS = TagResolver.resolver(
            "smallcaps",
            (aq, ctx) -> new SmallCapsModifyingTag()
    );

    private static final TagResolver ROMAN = TagResolver.resolver(
            "roman",
            (aq, ctx) -> new SmallCapsModifyingTag()
    );

    @Getter
    private static final List<TagResolver.Single> GLOBAL_PLACEHOLDERS = new ArrayList<>();

    @Getter
    private static final Map<String, Long> MESSAGE_COOLDOWNS = new ConcurrentHashMap<>();

    @Setter
    private static MessageFormat format = MessageFormat.MINI_MESSAGE;

    private static MiniMessage MINI_MESSAGE;

    public static void init(@NotNull TagResolver... additionalResolvers) {
        List<TagResolver> resolvers = new ArrayList<>(Arrays.asList(additionalResolvers));
        resolvers.add(StandardTags.defaults());
        resolvers.add(SMALL_CAPS);
        resolvers.add(ROMAN);

        MINI_MESSAGE = MiniMessage.builder().tags(TagResolver.builder().resolvers(resolvers).build()).build();
    }

    public static void send(@NotNull CommandSender sender, @NotNull Component component) {
        sender.sendMessage(component);
    }

    public static void sendAll(@NotNull CommandSender sender, @NotNull Component... components) {
        for (Component component : components) {
            sender.sendMessage(component);
        }
    }

    public static void registerGlobalPlaceholder(TagResolver.Single... placeholders) {
        GLOBAL_PLACEHOLDERS.addAll(Arrays.asList(placeholders));
    }

    public static String serialize(@NotNull Component component) {
        return format == MessageFormat.MINI_MESSAGE ? MINI_MESSAGE.serialize(component) : LEGACY_SERIALIZER.serialize(component);
    }

    public static String serializeColored(@NotNull String coloredString) {
        Component component = LEGACY_SERIALIZER.deserialize(coloredString);
        return serialize(component);
    }

    public static Component deserialize(@NotNull String message, TagResolver.Single... placeholders) {
        List<TagResolver.Single> allPlaceholders = new ArrayList<>(GLOBAL_PLACEHOLDERS);
        allPlaceholders.addAll(List.of(placeholders));

        if (message.contains("<center>")) {
            message = message.replaceFirst("<center>", "");
            message = center(message, calculatePadding(PLAIN_SERIALIZER.serialize(MINI_MESSAGE.deserialize(message, allPlaceholders.toArray(new TagResolver.Single[0])))));
        }

        if (format == MessageFormat.MINI_MESSAGE) {
            return MINI_MESSAGE.deserialize(message, allPlaceholders.toArray(new TagResolver.Single[0]));
        }

        Component component = LEGACY_SERIALIZER.deserialize(message);

        if (!allPlaceholders.isEmpty()) {
            component = MINI_MESSAGE.deserialize(
                    MiniMessage.miniMessage().serialize(component),
                    allPlaceholders.toArray(new TagResolver.Single[0])
            );
        }

        return component;
    }

    public static String legacyColor(@NotNull String message) {
        return ChatColor.translateAlternateColorCodes('&', message);
    }

    public static Component fromLegacy(@NotNull String message) {
        return LEGACY_SERIALIZER.deserialize(message);
    }

    public static String toSpigotFromLegacy(@NotNull String message) {
        return LEGACY_SERIALIZER.serialize(fromLegacy(message));
    }

    private static String center(@NotNull String string, int padding) {
        return " ".repeat(Math.max(0, padding)) + string;
    }

    private static int calculatePadding(@NotNull String content) {
        int messagePxSize = 0;

        for (char c : content.toCharArray()) {
            messagePxSize += DefaultFontInfo.getDefaultFontInfo(c).getLength() + 1;
        }

        int halvedMessageSize = messagePxSize / 2;
        int toCompensate = CENTER_PIXELS - halvedMessageSize;

        int spacePx = DefaultFontInfo.getDefaultFontInfo(' ').getLength();

        return toCompensate / (spacePx + 1);
    }

}