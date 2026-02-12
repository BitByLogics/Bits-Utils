package net.bitbylogic.utils.message.structured;

import lombok.NonNull;
import net.bitbylogic.utils.message.MessageUtil;
import net.bitbylogic.utils.smallcaps.SmallCapsConverter;
import net.kyori.adventure.text.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StructuredMessageBuilder {

    private final static String ENTRY_PREFIX = "&8&l⎸";
    private final static String ENDING_ENTRY_PREFIX = "&8⎣";
    private final static String REENTRY_PREFIX = "&8⎡";

    private final static String SECTION_MESSAGE = "%s <c#success_primary>%s</c>";
    private final static String ENTRY_MESSAGE = "%s <c#success_primary>%s</c>&8: <c#success_secondary>%s</c>";

    private static final int MAX_ENTRIES_PER_PAGE = 10;

    private final Map<Integer, List<Component>> pages = new HashMap<>();

    private final Component header;
    private int depth;

    public StructuredMessageBuilder(@NonNull String header) {
        this.header = MessageUtil.deserialize(header);
    }

    public StructuredMessageBuilder sectionUp(@NonNull String key) {
        int newDepth = Math.max(0, depth - 1);
        String prefix = " " + "  ".repeat(newDepth) + (newDepth < depth ? REENTRY_PREFIX : ENTRY_PREFIX);

        depth = newDepth;
        return newEntry(String.format(SECTION_MESSAGE, prefix, SmallCapsConverter.convert(key)));
    }

    public StructuredMessageBuilder entryUp(@NonNull String key, @NonNull String value) {
        int newDepth = Math.max(0, depth - 1);
        String prefix = " " + "  ".repeat(newDepth) + (newDepth < depth ? REENTRY_PREFIX : ENTRY_PREFIX);
        depth = newDepth;

        return newEntry(String.format(ENTRY_MESSAGE, prefix, SmallCapsConverter.convert(key), value));
    }

    public StructuredMessageBuilder rawEntryUp(@NonNull String message) {
        int newDepth = Math.max(0, depth - 1);
        String prefix = " " + "  ".repeat(newDepth) + (newDepth < depth ? REENTRY_PREFIX : ENTRY_PREFIX);
        depth = newDepth;

        return newEntry(prefix + " " + message);
    }

    public StructuredMessageBuilder sectionDown(@NonNull String key) {
        int newDepth = depth + 1;
        String prefix = " " + "  ".repeat(depth) + ENDING_ENTRY_PREFIX;
        depth = newDepth;

        return newEntry(String.format(SECTION_MESSAGE, prefix, SmallCapsConverter.convert(key)));
    }

    public StructuredMessageBuilder entryDown(@NonNull String key, @NonNull String value) {
        int newDepth = depth + 1;
        String prefix = " " + "  ".repeat(depth) + ENDING_ENTRY_PREFIX;
        depth = newDepth;

        return newEntry(String.format(ENTRY_MESSAGE, prefix, SmallCapsConverter.convert(key), value));
    }

    public StructuredMessageBuilder rawEntryDown(@NonNull String message) {
        int newDepth = depth + 1;
        String prefix = " " + "  ".repeat(depth) + ENDING_ENTRY_PREFIX;
        depth = newDepth;

        return newEntry(prefix + " " + message);
    }

    public StructuredMessageBuilder entry(@NonNull String key, @NonNull String value) {
        String prefix = " " + "  ".repeat(Math.max(0, depth)) + ENTRY_PREFIX;
        return newEntry(String.format(ENTRY_MESSAGE, prefix, SmallCapsConverter.convert(key), value));
    }

    public StructuredMessageBuilder finalEntry(@NonNull String key, @NonNull String value) {
        String prefix = " " + "  ".repeat(Math.max(0, depth)) + ENDING_ENTRY_PREFIX;
        return newEntry(String.format(ENTRY_MESSAGE, prefix, SmallCapsConverter.convert(key), value));
    }

    private StructuredMessageBuilder newEntry(@NonNull String message) {
        if(pages.isEmpty()) {
            pages.put(0, new ArrayList<>());
        }

        List<Component> lastPage = pages.get(pages.size() - 1);

        if(lastPage == null) {
            return this;
        }

        List<Component> page = lastPage.size() < MAX_ENTRIES_PER_PAGE ? lastPage : pages.put(pages.size(), new ArrayList<>());

        if(page == null) {
            return this;
        }

        page.add(MessageUtil.deserialize(message));
        return this;
    }

    public StructuredMessage build() {
        return new StructuredMessage(header, pages);
    }

}
