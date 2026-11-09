package net.bitbylogic.utils.inventory;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParseException;
import com.google.gson.reflect.TypeToken;
import lombok.NonNull;
import org.bukkit.Bukkit;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class InventorySerializer {

    private static final Gson GSON = new GsonBuilder().create();
    private static final Type DATA_TYPE = new TypeToken<Map<String, Object>>() {}.getType();

    private InventorySerializer() {

    }

    public static String serialize(@NonNull PlayerInventory inventory) {
        Map<String, Object> data = new HashMap<>();

        data.put("contents", serializeItems(inventory.getContents()));
        data.put("armor", serializeItems(inventory.getArmorContents()));
        data.put("offhand", serializeItem(inventory.getItemInOffHand()));

        return GSON.toJson(data);
    }

    public static boolean deserialize(@NonNull PlayerInventory inventory, @NonNull String json) {
        Map<String, Object> data;

        try {
            data = GSON.fromJson(json, DATA_TYPE);
        } catch (JsonParseException e) {
            Bukkit.getLogger().warning("(InventorySerializer): Invalid inventory data: " + e.getMessage());
            return false;
        }

        if (data == null) {
            Bukkit.getLogger().warning("(InventorySerializer): Inventory data was empty.");
            return false;
        }

        ItemStack[] contents = deserializeItems(data.get("contents"));
        ItemStack[] armor = deserializeItems(data.get("armor"));

        if (contents == null || armor == null) {
            Bukkit.getLogger().warning("(InventorySerializer): Inventory data is missing contents or armor.");
            return false;
        }

        inventory.setContents(contents);
        inventory.setArmorContents(armor);
        inventory.setItemInOffHand(deserializeItem(data.get("offhand")));
        return true;
    }

    private static List<Map<String, Object>> serializeItems(ItemStack[] items) {
        List<Map<String, Object>> list = new ArrayList<>();

        for (ItemStack item : items) {
            list.add(serializeItem(item));
        }

        return list;
    }

    private static @Nullable Map<String, Object> serializeItem(@Nullable ItemStack item) {
        if (item == null) {
            return null;
        }

        return item.serialize();
    }

    private static @Nullable ItemStack[] deserializeItems(Object value) {
        if (!(value instanceof List<?> list)) {
            return null;
        }

        ItemStack[] items = new ItemStack[list.size()];

        for (int i = 0; i < list.size(); i++) {
            items[i] = deserializeItem(list.get(i));
        }

        return items;
    }

    @SuppressWarnings("unchecked")
    private static @Nullable ItemStack deserializeItem(Object value) {
        if (!(value instanceof Map<?, ?> map)) {
            return null;
        }

        return ItemStack.deserialize((Map<String, Object>) map);
    }

}
