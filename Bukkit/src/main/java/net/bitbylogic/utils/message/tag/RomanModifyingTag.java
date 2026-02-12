package net.bitbylogic.utils.message.tag;

import net.bitbylogic.utils.NumberUtil;
import net.bitbylogic.utils.roman.RomanConverter;
import net.bitbylogic.utils.smallcaps.SmallCapsConverter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.minimessage.tag.Modifying;
import org.jetbrains.annotations.NotNull;

public class RomanModifyingTag implements Modifying {

    @Override
    public Component apply(@NotNull Component current, int depth) {
        if (current instanceof TextComponent textComponent) {
            String content = textComponent.content();

            if(!NumberUtil.isNumber(content)) {
                return current;
            }

            String roman = RomanConverter.convert(Integer.parseInt(content));
            return Component.text(roman).style(textComponent.style());
        }

        return current;
    }

}
