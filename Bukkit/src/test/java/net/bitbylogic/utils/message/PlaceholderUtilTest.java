package net.bitbylogic.utils.message;

import org.bukkit.entity.Player;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Proxy;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

class PlaceholderUtilTest {

    private Player playerNamed(String name) {
        return (Player) Proxy.newProxyInstance(
                Player.class.getClassLoader(),
                new Class<?>[]{Player.class},
                (proxy, method, args) -> {
                    if (method.getName().equals("getName")) {
                        return name;
                    }

                    return null;
                }
        );
    }

    @Test
    void playerPlaceholderIsReplacedWithoutPlaceholderApi() {
        assertEquals("Hello Steve!", PlaceholderUtil.apply(playerNamed("Steve"), "Hello %player%!"));
    }

    @Test
    void textWithoutPlaceholdersIsUnchanged() {
        assertEquals("plain text", PlaceholderUtil.apply(playerNamed("Steve"), "plain text"));
    }

    @Test
    void placeholderApiIsUnavailableWithoutAServer() {
        assertFalse(PlaceholderUtil.isPlaceholderApiAvailable());
    }

}
