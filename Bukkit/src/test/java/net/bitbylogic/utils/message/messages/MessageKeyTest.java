package net.bitbylogic.utils.message.messages;

import net.bitbylogic.utils.message.MessageUtil;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static org.junit.jupiter.api.Assertions.assertEquals;

class MessageKeyTest {

    @BeforeAll
    static void initMessages() {
        MessageUtil.init();
    }

    private String plain(Component component) {
        return PlainTextComponentSerializer.plainText().serialize(component);
    }

    @Test
    void emptyLocaleListFallsBackToEnglish() {
        MessageKey key = new MessageKey("test.path", "hello");
        key.getValues(Locale.GERMAN);

        assertEquals("hello", plain(key.get(Locale.GERMAN)));
    }

    @Test
    void definedLocaleIsUsed() {
        MessageKey key = new MessageKey("test.path", "hello");
        key.getValues(Locale.GERMAN).add("hallo");

        assertEquals("hallo", plain(key.get(Locale.GERMAN)));
    }

    @Test
    void sendFallsBackToEnglishForEmptyLocale() {
        MessageKey key = new MessageKey("test.path", List.of("one", "two"));
        key.getValues(Locale.GERMAN);
        List<String> received = new ArrayList<>();

        Audience audience = new Audience() {
            @Override
            public void sendMessage(@NotNull Component message) {
                received.add(plain(message));
            }
        };

        key.send(audience, Locale.GERMAN);

        assertEquals(List.of("one", "two"), received);
    }

}
