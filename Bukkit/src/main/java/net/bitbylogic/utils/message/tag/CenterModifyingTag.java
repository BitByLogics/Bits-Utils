package net.bitbylogic.utils.message.tag;

import net.bitbylogic.utils.message.DefaultFontInfo;
import net.bitbylogic.utils.message.MessageUtil;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.minimessage.tag.Modifying;
import org.jetbrains.annotations.NotNull;

public class CenterModifyingTag implements Modifying {

    private static final int CENTER_PIXELS = 154;

    @Override
    public Component apply(@NotNull Component current, int depth) {
        if (current instanceof TextComponent) {

            String sanitizedString = MessageUtil.serialize(current);

            int messagePxSize = 0;
            boolean previousCode = false;
            boolean isBold = false;

            for (char c : sanitizedString.toCharArray()) {
                if (c == '§') {
                    previousCode = true;
                } else if (previousCode) {
                    previousCode = false;
                    isBold = (c == 'l' || c == 'L');
                } else {
                    DefaultFontInfo fontInfo = DefaultFontInfo.getDefaultFontInfo(c);
                    messagePxSize += isBold ? fontInfo.getBoldLength() : fontInfo.getLength();
                    messagePxSize++;
                }
            }

            int toCompensate = CENTER_PIXELS - messagePxSize / 2;
            int spaceLength = DefaultFontInfo.SPACE.getLength() + 1;

            int spaces = Math.max(0, toCompensate / spaceLength);

            String prefixSpaces = " ".repeat(spaces);
            Component spacesComponent = Component.text(prefixSpaces);
            Component originalComponent = Component.text(sanitizedString);

            return spacesComponent.append(originalComponent);
        }

        return current;
    }

}