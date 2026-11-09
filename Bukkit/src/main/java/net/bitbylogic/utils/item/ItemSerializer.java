package net.bitbylogic.utils.item;

import lombok.NonNull;
import org.bukkit.Bukkit;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;
import org.yaml.snakeyaml.external.biz.base64Coder.Base64Coder;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Map;
import java.util.Optional;

public final class ItemSerializer {

    private ItemSerializer() {

    }

    public static Optional<String> itemStackToBase64(@NonNull ItemStack item) {
        return itemStackArrayToBase64(new ItemStack[]{item});
    }

    public static Optional<ItemStack> itemStackFromBase64(String data) {
        if (data == null || data.isEmpty()) {
            return Optional.empty();
        }

        Optional<ItemStack> legacy = readLegacyItem(data);

        if (legacy.isPresent()) {
            return legacy;
        }

        return itemStackArrayFromBase64(data).flatMap(items -> {
            if (items.length == 0 || items[0] == null) {
                return Optional.empty();
            }

            return Optional.of(items[0]);
        });
    }

    public static Optional<String> itemStackArrayToBase64(@NonNull ItemStack[] items) {
        if (items.length == 0) {
            return Optional.of("");
        }

        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
             BukkitObjectOutputStream dataOutput = new BukkitObjectOutputStream(outputStream)) {

            dataOutput.writeInt(items.length);

            for (ItemStack item : items) {
                dataOutput.writeObject(item);
            }

            dataOutput.flush();
            return Optional.of(Base64Coder.encodeLines(outputStream.toByteArray()));
        } catch (IOException e) {
            Bukkit.getLogger().warning("(ItemSerializer): Failed to serialize items to base64: " + e.getMessage());
            return Optional.empty();
        }
    }

    public static Optional<ItemStack[]> itemStackArrayFromBase64(String data) {
        if (data == null || data.isEmpty()) {
            return Optional.of(new ItemStack[]{});
        }

        try (ByteArrayInputStream inputStream = new ByteArrayInputStream(Base64Coder.decodeLines(data));
             BukkitObjectInputStream dataInput = new BukkitObjectInputStream(inputStream)) {
            ItemStack[] items = new ItemStack[dataInput.readInt()];

            for (int i = 0; i < items.length; i++) {
                items[i] = readItem(dataInput.readObject());
            }

            return Optional.of(items);
        } catch (ClassNotFoundException | IOException | IllegalArgumentException | NegativeArraySizeException e) {
            Bukkit.getLogger().warning("(ItemSerializer): Failed to deserialize items from base64: " + e.getMessage());
            return Optional.empty();
        }
    }

    private static Optional<ItemStack> readLegacyItem(String data) {
        try (ByteArrayInputStream inputStream = new ByteArrayInputStream(Base64Coder.decodeLines(data));
             BukkitObjectInputStream dataInput = new BukkitObjectInputStream(inputStream)) {
            return Optional.ofNullable(readItem(dataInput.readObject()));
        } catch (ClassNotFoundException | IOException | IllegalArgumentException e) {
            return Optional.empty();
        }
    }

    @SuppressWarnings("unchecked")
    private static ItemStack readItem(Object object) {
        if (object instanceof ItemStack item) {
            return item;
        }

        if (object instanceof Map<?, ?> map) {
            return ItemStack.deserialize((Map<String, Object>) map);
        }

        return null;
    }

}
