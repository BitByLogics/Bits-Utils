package net.bitbylogic.utils.message.format;

import lombok.Getter;
import lombok.NonNull;
import net.bitbylogic.rps.client.RedisClient;
import net.bitbylogic.rps.listener.ListenerComponent;
import net.bitbylogic.utils.Placeholder;
import net.bitbylogic.utils.StringModifier;
import net.bitbylogic.utils.TimeConverter;
import net.bitbylogic.utils.message.BitColor;
import net.bitbylogic.utils.message.DefaultFontInfo;
import net.bitbylogic.utils.message.PlayerStringModifier;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Text;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.awt.*;
import java.io.File;
import java.util.*;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Formatter {

    @Getter
    private static final List<StringModifier> GLOBAL_MODIFIERS = new ArrayList<>();

    @Getter
    private static final Map<String, Long> MESSAGE_COOLDOWNS = new ConcurrentHashMap<>();

    @Getter
    private static FormatConfig config;

    private static final Map<String, String> FORMAT_CACHE = new ConcurrentHashMap<>(256);
    private static final int MAX_CACHE_SIZE = 1000;

    private static final Map<String, ChatColor> HEX_COLOR_CACHE = new ConcurrentHashMap<>();
    private static final Map<String, String> CONSOLE_COLOR_MAP = createConsoleColorMap();

    public static void registerConfig(@NonNull File configFile) {
        config = new FormatConfig(configFile);
    }

    public static void registerGlobalModifier(StringModifier... modifiers) {
        GLOBAL_MODIFIERS.addAll(Arrays.asList(modifiers));

        FORMAT_CACHE.clear();
    }

    public static String color(String message) {
        if (message == null || message.isEmpty()) {
            return message;
        }

        String coloredMessage = ChatColor.translateAlternateColorCodes('&', message);

        Pattern hexPattern = config.getHexPattern();
        Matcher matcher = hexPattern.matcher(coloredMessage);

        if (!matcher.find()) {
            return coloredMessage;
        }

        matcher.reset();
        StringBuilder sb = new StringBuilder(coloredMessage.length() + 32);

        while (matcher.find()) {
            String hexColor = matcher.group();
            ChatColor chatColor = HEX_COLOR_CACHE.computeIfAbsent(hexColor, ChatColor::of);
            matcher.appendReplacement(sb, Matcher.quoteReplacement(chatColor.toString()));
        }

        matcher.appendTail(sb);

        return sb.toString();
    }

    public static String reverseColors(@NonNull String message) {
        Matcher matcher = config.getSpigotHexPattern().matcher(message);

        if (!matcher.find()) {
            return message.replace("§", "&");
        }

        matcher.reset();
        StringBuilder sb = new StringBuilder(message.length());

        while (matcher.find()) {
            String match = matcher.group();
            StringBuilder hexColor = new StringBuilder("#");

            for (int i = 2; i < match.length(); i += 2) {
                hexColor.append(match.charAt(i + 1));
            }

            matcher.appendReplacement(sb, Matcher.quoteReplacement(hexColor.toString()));
        }

        matcher.appendTail(sb);

        return sb.toString().replace("§", "&");
    }

    public static ChatColor colorToChatColor(@NonNull org.bukkit.Color color) {
        String hex = String.format("#%02x%02x%02x", color.getRed(), color.getGreen(), color.getBlue());
        return HEX_COLOR_CACHE.computeIfAbsent(hex, ChatColor::of);
    }

    public static String format(String message, StringModifier... modifiers) {
        return format(message, null, modifiers);
    }

    /**
     * Format a string with color & placeholders.
     *
     * @param message   The message.
     * @param modifiers The placeholders being applied.
     * @return The formatted string.
     */
    public static String format(String message, @Nullable Player player, StringModifier... modifiers) {
        if (message == null || message.isEmpty()) {
            return message;
        }

        boolean canCache = player == null && modifiers.length == 0 && GLOBAL_MODIFIERS.isEmpty();
        String cacheKey = canCache ? message : null;

        if (canCache) {
            String cached = FORMAT_CACHE.get(cacheKey);

            if (cached != null) {
                return cached;
            }
        }

        String formattedMessage = message;

        if (modifiers.length > 0) {
            formattedMessage = applyModifiers(formattedMessage, player, modifiers);
        }

        if (!GLOBAL_MODIFIERS.isEmpty()) {
            formattedMessage = applyModifiers(formattedMessage, player,
                    GLOBAL_MODIFIERS.toArray(new StringModifier[0]));
        }

        if (containsFormatCodes(formattedMessage)) {
            formattedMessage = processFormatCodes(formattedMessage);
        }

        formattedMessage = color(formattedMessage);

        if (canCache && FORMAT_CACHE.size() < MAX_CACHE_SIZE) {
            FORMAT_CACHE.put(cacheKey, formattedMessage);
        }

        return formattedMessage;
    }

    private static boolean containsFormatCodes(String message) {
        return message.indexOf('<') >= 0 || message.indexOf('[') >= 0;
    }

    private static String applyModifiers(String message, @Nullable Player player, StringModifier[] modifiers) {
        String result = message;

        for (StringModifier modifier : modifiers) {
            if (modifier instanceof PlayerStringModifier playerModifier) {
                result = playerModifier.modify(result, player);
            } else {
                result = modifier.modify(result);
            }
        }
        return result;
    }

    private static String processFormatCodes(String message) {
        List<FormatData> formatData = new ArrayList<>(8);
        Deque<FormatData> queue = new ArrayDeque<>(8);
        queue.add(new FormatData(null, null, null, message));

        while (!queue.isEmpty()) {
            FormatData data = queue.poll();
            List<FormatData> newData = findFormatMatches(data.getContents());

            if (newData.isEmpty()) {
                continue;
            }

            if (data.getEntireMatch() != null) {
                for (FormatData nd : newData) {
                    nd.setParentData(data);
                }
            }

            formatData.addAll(newData);
            queue.addAll(newData);
        }

        if (formatData.isEmpty()) {
            return message;
        }

        formatData.sort(Comparator.comparingInt(d -> d.getCode().getPriority()));

        String result = message;
        for (FormatData data : formatData) {
            result = data.format(result);
        }

        return result;
    }

    private static List<FormatData> findFormatMatches(@NonNull String string) {
        Pattern formatPattern = config.getFormatPattern();
        Matcher matcher = formatPattern.matcher(string);

        if (!matcher.find()) {
            return Collections.emptyList();
        }

        matcher.reset();
        List<FormatData> formatData = new ArrayList<>(4);

        while (matcher.find()) {
            String codeIdentifier = matcher.group(1) != null ? matcher.group(1) : matcher.group(3);
            String codeData = matcher.group(4);
            String text = matcher.group(2) != null ? matcher.group(2) : matcher.group(5);

            FormatCode code = FormatCode.match(codeIdentifier);

            if (code != null) {
                formatData.add(new FormatData(matcher.group(), code, codeData, text));
            }
        }

        return formatData;
    }

    public static String autoFormat(String message, Object... replacements) {
        Matcher placeholderMatcher = config.getPlaceholderPattern().matcher(message);
        StringBuilder sb = new StringBuilder(message.length() + 32);
        int currentIndex = 0;

        while (placeholderMatcher.find() && currentIndex < replacements.length) {
            placeholderMatcher.appendReplacement(sb,
                    Matcher.quoteReplacement(replacements[currentIndex++].toString()));
        }

        placeholderMatcher.appendTail(sb);

        return format(sb.toString());
    }

    public static BaseComponent richFormat(String message, Object... replacements) {
        return TextComponent.fromLegacy(replace(message, replacements));
    }

    public static String replace(String message, Object... replacements) {
        String formattedMessage = message;

        for (Object replacement : replacements) {
            if (replacement instanceof StringModifier modifier) {
                formattedMessage = modifier.modify(formattedMessage);
            } else if (formattedMessage.contains("%s")) {
                formattedMessage = formattedMessage.replaceFirst("%s",
                        Matcher.quoteReplacement(replacement.toString()));
            }
        }

        return format(formattedMessage);
    }

    public static String command(String command, String description) {
        return replace(config.getConfigValue("Command"),
                new Placeholder("%command%", command),
                new Placeholder("%description%", description));
    }

    public static BaseComponent richCommand(String command, String description) {
        BaseComponent component = richFormat(config.getConfigValue("Rich-Format.Text"),
                new Placeholder("%command%", command));
        component.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                new Text(richFormat(config.getConfigValue("Rich-Format.Hover"),
                        new Placeholder("%description%", description)))));
        return component;
    }

    public static String main(String prefix, String message, Object... replacements) {
        return replace(config.getConfigValue("Main"),
                new Placeholder("%prefix%", prefix),
                new Placeholder("%message%", replace(message,
                        applyHighlightColor(BitColor.getColor("primary"),
                                BitColor.getColor("highlight"), replacements))));
    }

    public static String error(String prefix, String message, Object... replacements) {
        return replace(config.getConfigValue("Error"),
                new Placeholder("%prefix%", prefix),
                new Placeholder("%message%", replace(message,
                        applyHighlightColor(BitColor.getColor("error-secondary"),
                                BitColor.getColor("error-highlight"), replacements))));
    }

    public static String success(String prefix, String message, Object... replacements) {
        return replace(config.getConfigValue("Success"),
                new Placeholder("%prefix%", prefix),
                new Placeholder("%message%", replace(message,
                        applyHighlightColor(BitColor.getColor("success-secondary"),
                                BitColor.getColor("success-highlight"), replacements))));
    }

    public static String listHeader(String prefix, String info, Object... replacements) {
        return replace(config.getConfigValue("List.Header"),
                new Placeholder("%prefix%", prefix),
                new Placeholder("%info%", replace(info,
                        applyHighlightColor(BitColor.getColor("secondary"),
                                BitColor.getColor("highlight"), replacements))));
    }

    public static String listItem(String prefix, String message, Object... replacements) {
        return replace(config.getConfigValue("List.Item"),
                new Placeholder("%prefix%", prefix),
                new Placeholder("%message%", replace(message,
                        applyHighlightColor(BitColor.getColor("success-primary"),
                                BitColor.getColor("success-secondary"), replacements))));
    }

    public static String dottedMessage(String prefix, String message, Object... replacements) {
        return replace(config.getConfigValue("Dotted-Message"),
                new Placeholder("%prefix%", prefix),
                new Placeholder("%message%", replace(message,
                        applyHighlightColor(BitColor.getColor("success-primary"),
                                BitColor.getColor("success-secondary"), replacements))));
    }

    public static void sendMessage(Player player, String prefix, String message, Placeholder... placeholders) {
        String formattedMessage = BitColor.getColor("primary") + "&l" + prefix +
                " &8• " + BitColor.getColor("secondary") + message;

        for (Placeholder placeholder : placeholders) {
            formattedMessage = placeholder.modify(formattedMessage);
        }

        player.sendMessage(format(formattedMessage));
    }

    public static void sendRedisMessage(@NonNull RedisClient redisClient, UUID player, String message) {
        redisClient.sendListenerMessage(
                new ListenerComponent(null, "abl-message")
                        .addData("uuid", player)
                        .addData("message", message));
    }

    public static void sendRawMessage(Player player, String message, Placeholder... placeholders) {
        String formattedMessage = message;

        for (Placeholder placeholder : placeholders) {
            formattedMessage = placeholder.modify(formattedMessage);
        }

        player.sendMessage(formattedMessage);
    }

    public static void sendCooldownMessage(Player player, String message, String id, String cooldown) {
        if (player == null) {
            return;
        }

        Long cooldownEnd = MESSAGE_COOLDOWNS.get(id);

        if (cooldownEnd != null && cooldownEnd > System.currentTimeMillis()) {
            return;
        }

        long cooldownTime = TimeConverter.convert(cooldown);
        MESSAGE_COOLDOWNS.put(id, System.currentTimeMillis() + cooldownTime);

        player.sendMessage(format(message));
    }

    public static Object[] applyHighlightColor(String primaryColor, String highlightColor, Object[] objects) {
        Object[] result = new Object[objects.length];

        for (int i = 0; i < objects.length; i++) {
            Object o = objects[i];

            if (o instanceof Placeholder placeholder) {
                Map<String, String> map = placeholder.getPlaceholderMap();
                map.replaceAll((k, v) -> highlightColor + v + primaryColor);
                result[i] = placeholder;
            } else {
                result[i] = highlightColor + o.toString() + primaryColor;
            }
        }

        return result;
    }

    public static String[] getPagedList(String header, List<String> data, int page) {
        if (data.isEmpty()) {
            return new String[]{Formatter.error(header, "No data available")};
        }

        int itemsPerPage = 10;
        int pages = (int) Math.ceil((double) data.size() / itemsPerPage);

        if (page <= 0 || page > pages) {
            return new String[]{Formatter.error(header, config.getConfigValue("Paged.Invalid-Page"))};
        }

        List<String> text = new ArrayList<>(itemsPerPage + 2);

        int startingItem = (page - 1) * itemsPerPage;
        int lastItem = Math.min(startingItem + itemsPerPage, data.size());

        text.add(main(header, ""));

        for (int i = startingItem; i < lastItem; i++) {
            text.add(format(config.getConfigValue("Paged.Item"),
                    new Placeholder("%text%", data.get(i))));
        }

        text.add(format(config.getConfigValue("Paged.Footer"),
                new Placeholder("%current-page%", page),
                new Placeholder("%pages%", pages)));

        return text.toArray(new String[0]);
    }

    public static String applyGradientToText(String text, String[] colors) {
        int steps = text.length();
        String[] gradientColors = generateGradientColors(colors, steps);
        StringBuilder gradientText = new StringBuilder(text.length() * 10);

        for (int i = 0; i < text.length(); i++) {
            gradientText.append(gradientColors[i]).append(text.charAt(i));
        }

        return gradientText.toString();
    }

    private static String[] generateGradientColors(String[] colors, int steps) {
        List<String> stylePrefixes = new ArrayList<>();
        List<Color> colorList = new ArrayList<>();

        for (String color : colors) {
            if (color.startsWith("&")) {
                stylePrefixes.add(color);
            } else {
                colorList.add(Color.decode(color));
            }
        }

        int numColors = colorList.size();
        String[] gradientColors = new String[steps];

        for (int i = 0; i < steps; i++) {
            float ratio = steps > 1 ? (float) i / (steps - 1) : 0;
            int segment = Math.min(numColors - 2, (int) (ratio * (numColors - 1)));
            float segmentRatio = (ratio * (numColors - 1)) - segment;

            Color startColor = colorList.get(segment);
            Color endColor = colorList.get(segment + 1);

            int r = interpolate(startColor.getRed(), endColor.getRed(), segmentRatio);
            int g = interpolate(startColor.getGreen(), endColor.getGreen(), segmentRatio);
            int b = interpolate(startColor.getBlue(), endColor.getBlue(), segmentRatio);

            StringBuilder formattedColor = new StringBuilder(String.format("#%02X%02X%02X", r, g, b));
            stylePrefixes.forEach(formattedColor::append);

            gradientColors[i] = formattedColor.toString();
        }

        return gradientColors;
    }

    private static int interpolate(int start, int end, float ratio) {
        return (int) (start + ratio * (end - start));
    }

    public static String centerMessage(String message) {
        String sanitizedString = color(message);

        int messagePxSize = 0;
        boolean previousCode = false;
        boolean isBold = false;

        for (char c : sanitizedString.toCharArray()) {
            if (c == '§') {
                previousCode = true;
            } else if (previousCode) {
                previousCode = false;
                isBold = (c == 'l');
            } else {
                DefaultFontInfo fontInfo = DefaultFontInfo.getDefaultFontInfo(c);
                messagePxSize += isBold ? fontInfo.getBoldLength() : fontInfo.getLength();
                messagePxSize++;
            }
        }

        int centerPixels = config.getConfigValue("Center-Pixels");
        int toCompensate = centerPixels - messagePxSize / 2;
        int spaceLength = DefaultFontInfo.SPACE.getLength() + 1;

        int spaces = Math.max(0, toCompensate / spaceLength);
        return " ".repeat(spaces) + "§r" + message;
    }

    public static void sendCenteredMessages(Player player, String[] lines) {
        for (String line : lines) {
            player.sendMessage(centerMessage(line));
        }
    }

    public static void sendCenteredMessage(Player player, String message) {
        player.sendMessage(centerMessage(message));
    }

    public static org.bukkit.Color hexToRGB(String hex) {
        hex = hex.replace("#", "");

        int r = Integer.parseInt(hex.substring(0, 2), 16);
        int g = Integer.parseInt(hex.substring(2, 4), 16);
        int b = Integer.parseInt(hex.substring(4, 6), 16);

        return org.bukkit.Color.fromRGB(r, g, b);
    }

    private static Map<String, String> createConsoleColorMap() {
        return Map.ofEntries(
                Map.entry("§0", "\u001B[30m"),
                Map.entry("§1", "\u001B[34m"),
                Map.entry("§2", "\u001B[32m"),
                Map.entry("§3", "\u001B[36m"),
                Map.entry("§4", "\u001B[31m"),
                Map.entry("§5", "\u001B[35m"),
                Map.entry("§6", "\u001B[33m"),
                Map.entry("§7", "\u001B[37m"),
                Map.entry("§8", "\u001B[90m"),
                Map.entry("§9", "\u001B[94m"),
                Map.entry("§a", "\u001B[92m"),
                Map.entry("§b", "\u001B[96m"),
                Map.entry("§c", "\u001B[91m"),
                Map.entry("§d", "\u001B[95m"),
                Map.entry("§e", "\u001B[93m"),
                Map.entry("§f", "\u001B[97m"),
                Map.entry("§r", "\u001B[0m"));
    }

    public static String translateForConsole(String message) {
        String colored = ChatColor.translateAlternateColorCodes('&', message);

        for (Map.Entry<String, String> entry : CONSOLE_COLOR_MAP.entrySet()) {
            colored = colored.replace(entry.getKey(), entry.getValue());
        }

        return colored + "\u001B[0m";
    }

    public static void clearCaches() {
        FORMAT_CACHE.clear();
        HEX_COLOR_CACHE.clear();
    }

}