package net.bitbylogic.utils.message;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import net.bitbylogic.utils.message.tag.CenterModifyingTag;
import net.bitbylogic.utils.message.tag.SmallCapsModifyingTag;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import net.kyori.adventure.text.minimessage.tag.standard.StandardTags;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class MessageUtil {

    private static final LegacyComponentSerializer LEGACY_SERIALIZER = LegacyComponentSerializer.builder().hexColors().build();

    private static final TagResolver SMALL_CAPS = TagResolver.resolver(
            "smallcaps",
            (aq, ctx) -> new SmallCapsModifyingTag()
    );

    private static final TagResolver ROMAN = TagResolver.resolver(
            "roman",
            (aq, ctx) -> new SmallCapsModifyingTag()
    );

    private static final TagResolver CENTER = TagResolver.resolver(
            "center",
            (aq, ctx) -> new CenterModifyingTag()
    );

    private static final MiniMessage MINI_MESSAGE = MiniMessage.builder()
            .tags(
                    TagResolver.builder()
                            .resolvers(StandardTags.defaults(), SMALL_CAPS, ROMAN, CENTER)
                            .build()
            ).build();

    @Getter
    private static final List<TagResolver.Single> GLOBAL_PLACEHOLDERS = new ArrayList<>();

    @Getter
    private static final Map<String, Long> MESSAGE_COOLDOWNS = new ConcurrentHashMap<>();

    @Setter
    private static MessageFormat format = MessageFormat.MINI_MESSAGE;

    private static BukkitAudiences AUDIENCES;

    public static void init(@NonNull JavaPlugin plugin) {
        AUDIENCES = BukkitAudiences.create(plugin);
    }

    public static void cleanup() {
        AUDIENCES.close();
    }

    public static Audience asAudience(@NonNull CommandSender sender) {
        return AUDIENCES.sender(sender);
    }

    public static void send(@NonNull CommandSender sender, @NonNull Component component) {
        asAudience(sender).sendMessage(component);
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

    public static String deserializeToSpigot(@NotNull String message, TagResolver.Single... placeholders) {
        Component component;

        if (message.indexOf('§') != -1) {
            component = LEGACY_SERIALIZER.deserialize(message);
        } else {
            component = deserialize(message, placeholders);
        }

        return LEGACY_SERIALIZER.serialize(component);
    }

    public static String legacyColor(@NonNull String message) {
        return ChatColor.translateAlternateColorCodes('&', message);
    }

    public static Component fromLegacy(@NonNull String message) {
        return LEGACY_SERIALIZER.deserialize(message);
    }

    public static String toSpigotFromLegacy(@NonNull String message) {
        return LEGACY_SERIALIZER.serialize(fromLegacy(message));
    }

    public static BaseComponent praiseMD5(@NonNull Component component) {
        return TextComponent.fromLegacy(LEGACY_SERIALIZER.serialize(component));
    }

}