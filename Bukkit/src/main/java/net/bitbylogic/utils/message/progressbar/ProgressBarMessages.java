package net.bitbylogic.utils.message.progressbar;

import net.bitbylogic.utils.message.messages.MessageGroup;
import net.bitbylogic.utils.message.messages.MessageKey;

public class ProgressBarMessages extends MessageGroup {

    public static MessageKey SYMBOL;
    public static MessageKey PROGRESS_COLOR;
    public static MessageKey DEFAULT_COLOR;

    public ProgressBarMessages() {
        super("Progress-Bar");
    }

    @Override
    public void register() {
        SYMBOL = register("Symbol", "|");

        PROGRESS_COLOR = register("Progress-Color", "#00AA00");
        DEFAULT_COLOR = register("Default-Color", "#555555");
    }

}
