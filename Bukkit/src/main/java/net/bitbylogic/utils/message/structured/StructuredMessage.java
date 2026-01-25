package net.bitbylogic.utils.message.structured;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import net.bitbylogic.utils.message.MessageUtil;
import net.kyori.adventure.text.Component;
import org.bukkit.command.CommandSender;

import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
public class StructuredMessage {

    private final Component header;
    private final Map<Integer, List<Component>> pages;

    public void send(@NonNull CommandSender sender) {
        send(sender, 1);
    }

    public void send(@NonNull CommandSender sender, int page) {
        if(page < 0 || page - 1 >= pages.size()) {
            return;
        }

        List<Component> pageMessages = pages.get(page - 1);

        if(pageMessages == null || pageMessages.isEmpty()) {
            return;
        }

        MessageUtil.send(sender, header);
        pageMessages.forEach(component -> MessageUtil.send(sender, component));
    }

}
