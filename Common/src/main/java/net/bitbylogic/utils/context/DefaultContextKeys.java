package net.bitbylogic.utils.context;

import net.bitbylogic.utils.action.data.ActionData;
import net.bitbylogic.utils.reflection.TypeToken;

public class DefaultContextKeys {

    public static final ContextKey<ActionData<?>> ACTION_DATA = ContextKey.key("action_data", ActionData.class, new TypeToken<ActionData<?>>() {}.getType());

}
