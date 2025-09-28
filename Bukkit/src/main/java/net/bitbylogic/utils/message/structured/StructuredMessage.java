package net.bitbylogic.utils.message.structured;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.command.CommandSender;

import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
public class StructuredMessage {

    private final String header;
    private final Map<Integer, List<String>> pages;

    public void send(@NonNull CommandSender sender) {
        send(sender, 1);
    }

    public void send(@NonNull CommandSender sender, int page) {
        if(page < 0 || page - 1 >= pages.size()) {
            return;
        }

        List<String> pageMessages = pages.get(page - 1);

        if(pageMessages == null || pageMessages.isEmpty()) {
            return;
        }

        sender.sendMessage(header);
        pageMessages.forEach(sender::sendMessage);
    }

}
