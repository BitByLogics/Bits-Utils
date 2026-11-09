package net.bitbylogic.utils.message.structured;

import net.bitbylogic.utils.message.MessageUtil;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

class StructuredMessageBuilderTest {

    @BeforeAll
    static void initMessages() {
        MessageUtil.init();
    }

    @SuppressWarnings("unchecked")
    private Map<Integer, List<?>> pagesOf(StructuredMessage message) throws Exception {
        Field field = StructuredMessage.class.getDeclaredField("pages");
        field.setAccessible(true);
        return (Map<Integer, List<?>>) field.get(message);
    }

    @Test
    void everyEntryIsKeptAcrossPages() throws Exception {
        StructuredMessageBuilder builder = new StructuredMessageBuilder("header");

        for (int i = 0; i < 25; i++) {
            builder.entry("key" + i, "value");
        }

        Map<Integer, List<?>> pages = pagesOf(builder.build());
        int total = 0;

        for (List<?> page : pages.values()) {
            total += page.size();
        }

        assertEquals(25, total);
        assertEquals(3, pages.size());
        assertEquals(10, pages.get(0).size());
        assertEquals(10, pages.get(1).size());
        assertEquals(5, pages.get(2).size());
    }

}
