package net.bitbylogic.utils.message.progressbar;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.bitbylogic.utils.context.Context;
import net.bitbylogic.utils.context.ContextBuilder;
import net.bitbylogic.utils.message.MessageUtil;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;

@RequiredArgsConstructor
@Getter
public class ProgressBar {

    public static Component create(int length, int progress, TagResolver.Single... placeholders) {
        return create(length, progress, null, null, placeholders);
    }

    public static Component create(int length, int progress, String prefix, String suffix, TagResolver.Single... placeholders) {
        Context context = ContextBuilder.create().build();

        Component symbol = ProgressBarMessages.SYMBOL.get(context, placeholders);
        Component progressColor = ProgressBarMessages.PROGRESS_COLOR.get(context);
        Component defaultColor = ProgressBarMessages.DEFAULT_COLOR.get(context);

        Component result = Component.empty();

        if (prefix != null && !prefix.isEmpty()) {
            result = result.append(MessageUtil.deserialize(prefix, placeholders));
        }

        result = result.append(progressColor);

        for (int i = 0; i < length; i++) {
            result = result.append(symbol).style(i <= progress ? progressColor.style() : defaultColor.style());
        }

        if (suffix != null && !suffix.isEmpty()) {
            result = result.append(MessageUtil.deserialize(suffix, placeholders));
        }

        return result;
    }

}
