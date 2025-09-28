package net.bitbylogic.utils.message.messages;

import java.util.List;

public abstract class MessageGroup implements MessageRegistry {

    private final String pathPrefix;

    public MessageGroup(String pathPrefix) {
        this.pathPrefix = pathPrefix.endsWith(".") ? pathPrefix : pathPrefix + ".";
    }

    protected MessageKey register(String key, String defaultValue) {
        return Messages.register(pathPrefix + key, defaultValue);
    }

    protected MessageKey register(String key, List<String> defaultValues) {
        return Messages.register(pathPrefix + key, defaultValues);
    }

}
