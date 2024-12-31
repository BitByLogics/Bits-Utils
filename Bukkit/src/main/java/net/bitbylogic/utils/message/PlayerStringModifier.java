package net.bitbylogic.utils.message;

import net.bitbylogic.utils.StringModifier;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

public interface PlayerStringModifier extends StringModifier {

    String modify(String string, @Nullable Player player);

    @Override
    default String modify(String string) {
        return modify(string, null);
    }

}
