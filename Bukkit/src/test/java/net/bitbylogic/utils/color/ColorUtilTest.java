package net.bitbylogic.utils.color;

import org.bukkit.Color;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ColorUtilTest {

    @Test
    void formattingCodesAreConverted() {
        assertEquals("\u001B[1mbold\u001B[0m", ColorUtil.colorForConsole("&lbold"));
    }

    @Test
    void legacyHexCodesBecomeTrueColor() {
        String result = ColorUtil.colorForConsole("§x§f§f§0§0§8§0text");

        assertEquals("\u001B[38;2;255;0;128mtext\u001B[0m", result);
    }

    @Test
    void validHexIsParsed() {
        assertEquals(Color.fromRGB(255, 0, 128), ColorUtil.parseHex("#ff0080").orElseThrow());
    }

    @Test
    void invalidHexIsRejected() {
        assertTrue(ColorUtil.parseHex("#ff").isEmpty());
        assertTrue(ColorUtil.parseHex("zzzzzz").isEmpty());
        assertTrue(ColorUtil.parseHex(null).isEmpty());
    }

}
