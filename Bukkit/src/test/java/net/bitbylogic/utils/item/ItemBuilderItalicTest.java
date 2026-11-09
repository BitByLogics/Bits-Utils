package net.bitbylogic.utils.item;

import net.bitbylogic.utils.message.MessageUtil;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ItemBuilderItalicTest {

    @BeforeAll
    static void initMessages() {
        MessageUtil.init();
    }

    private Component apply(Component component) throws Exception {
        Method method = ItemBuilder.class.getDeclaredMethod("withoutDefaultItalic", Component.class);
        method.setAccessible(true);
        return (Component) method.invoke(null, component);
    }

    @Test
    void plainTextIsNotItalic() throws Exception {
        Component result = apply(MessageUtil.deserialize("<gray>Hello"));

        assertEquals(TextDecoration.State.FALSE, result.decoration(TextDecoration.ITALIC));
    }

    @Test
    void explicitItalicIsKept() throws Exception {
        Component result = apply(MessageUtil.deserialize("<italic>Hello"));

        assertEquals(TextDecoration.State.TRUE, result.decoration(TextDecoration.ITALIC));
    }

}
