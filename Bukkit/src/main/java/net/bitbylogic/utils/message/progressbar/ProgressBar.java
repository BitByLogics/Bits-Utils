package net.bitbylogic.utils.message.progressbar;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.bitbylogic.utils.Placeholder;
import net.bitbylogic.utils.context.Context;
import net.bitbylogic.utils.context.ContextBuilder;
import net.bitbylogic.utils.message.format.Formatter;

@RequiredArgsConstructor
@Getter
public class ProgressBar {

    public static String create(int length, int progress, Placeholder... modifiers) {
        return create(length, progress, null, null, modifiers);
    }

    public static String create(int length, int progress, String prefix, String suffix, Placeholder... modifiers) {
        Context context = ContextBuilder.create().build();

        String modifiedSymbol = Formatter.format(ProgressBarMessages.SYMBOL.get(context), modifiers);

        StringBuilder progActionBar = new StringBuilder(prefix == null ? "" : Formatter.format(prefix, modifiers)).append(ProgressBarMessages.PROGRESS_COLOR.get(context));

        for (int i = 0; i < length; i++) {
            if (i == progress) {
                progActionBar.append(ProgressBarMessages.DEFAULT_COLOR.get(context));
            }

            progActionBar.append(modifiedSymbol);
        }

        progActionBar.append(suffix == null ? "" : Formatter.format(suffix, modifiers));
        return progActionBar.toString();
    }

}
