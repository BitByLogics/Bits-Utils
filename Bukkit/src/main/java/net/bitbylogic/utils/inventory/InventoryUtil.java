package net.bitbylogic.utils.inventory;

import net.bitbylogic.utils.item.ItemMatchUtil;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class InventoryUtil {

    /**
     * Check whether an inventory has space.
     *
     * @param inventory The inventory to check.
     * @return Whether the inventory has space.
     */
    public static boolean hasSpace(Inventory inventory) {
        return hasSpace(inventory, null, new ArrayList<>());
    }

    /**
     * Check whether an inventory has space for
     * an ItemStack.
     *
     * @param inventory The inventory to check.
     * @param itemStack The ItemStack to check space for, nullable.
     * @return Whether the inventory has space.
     */
    public static boolean hasSpace(Inventory inventory, @Nullable ItemStack itemStack) {
        return hasSpace(inventory, itemStack, new ArrayList<>());
    }

    /**
     * Check whether an inventory has space for
     * an ItemStack.
     *
     * @param inventory  The inventory to check.
     * @param itemStack  The ItemStack to check space for, nullable.
     * @param validSlots The slots that are valid to check, nullable.
     * @return Whether the inventory has space.
     */
    public static boolean hasSpace(Inventory inventory, @Nullable ItemStack itemStack, @Nullable List<Integer> validSlots) {
        if (itemStack == null || itemStack.getType() == Material.AIR) {
            return validSlots == null || validSlots.isEmpty() ? inventory.firstEmpty() != -1 : validSlots.stream().anyMatch(slot -> inventory.getItem(slot) == null);
        }

        int maxStackSize = itemStack.getMaxStackSize();
        int availableSpace = 0;

        for (int i = 0; i < inventory.getSize(); i++) {
            if (validSlots != null && !validSlots.isEmpty() && !validSlots.contains(i)) {
                continue;
            }

            if (inventory instanceof PlayerInventory) {
                if (i >= 36 && i <= 39) continue;
                if (i == 40) continue;
            }

            ItemStack content = inventory.getItem(i);

            if (content == null || content.getType() == Material.AIR) {
                availableSpace += maxStackSize;
            } else if (ItemMatchUtil.isSimilar(itemStack, content, true, true, true)) {
                int space = content.getMaxStackSize() - content.getAmount();

                if (space > 0) {
                    availableSpace += space;
                }
            }
        }

        return availableSpace >= itemStack.getAmount();
    }

    /**
     * Get the amount of space an inventory has left.
     *
     * @param inventory The inventory to check.
     * @return Whether the inventory has space.
     */
    public static int getAvailableSpace(Inventory inventory) {
        return getAvailableSpace(inventory, null, new ArrayList<>());
    }

    /**
     * Get the amount of space an inventory has left.
     *
     * @param inventory The inventory to check.
     * @param itemStack The ItemStack to check space for, nullable.
     * @return Whether the inventory has space.
     */
    public static int getAvailableSpace(Inventory inventory, @Nullable ItemStack itemStack) {
        return getAvailableSpace(inventory, itemStack, new ArrayList<>());
    }

    /**
     * Get the amount of space an inventory has left.
     *
     * @param inventory  The inventory to check.
     * @param itemStack  The ItemStack to check space for, nullable.
     * @param validSlots The slots that are valid to check, nullable.
     * @return Whether the inventory has space.
     */
    public static int getAvailableSpace(Inventory inventory, @Nullable ItemStack itemStack, @Nullable List<Integer> validSlots) {
        int maxStackSize = itemStack == null ? -1 : itemStack.getMaxStackSize();
        int availableSpace = 0;

        for (int i = 0; i < inventory.getSize(); i++) {
            if (validSlots != null && !validSlots.isEmpty() && !validSlots.contains(i)) {
                continue;
            }

            if (inventory instanceof PlayerInventory && i >= 36 && i <= 40) {
                continue;
            }

            ItemStack content = inventory.getItem(i);

            if (content == null || content.getType() == Material.AIR) {
                availableSpace += (maxStackSize == -1 ? 64 : maxStackSize);
                continue;
            }

            if (maxStackSize == -1 || !ItemMatchUtil.isSimilar(itemStack, content, true, true, true)) {
                continue;
            }

            if (content.getAmount() >= maxStackSize) {
                continue;
            }

            availableSpace += content.getMaxStackSize() - content.getAmount();
        }

        return availableSpace;
    }

    /**
     * Get next valid slot for an Inventory.
     * Returns -1 for no available slots.
     *
     * @param inventory The inventory to check.
     * @return The next available slot.
     */
    public static int getNextAvailableSlot(Inventory inventory) {
        return getNextAvailableSlot(inventory, null, null);
    }

    /**
     * Get next valid slot for an ItemStack, or in general.
     * Returns -1 for no available slots.
     *
     * @param inventory The inventory to check.
     * @param itemStack The ItemStack to check space for, nullable.
     * @return The next available slot.
     */
    public static int getNextAvailableSlot(Inventory inventory, @Nullable ItemStack itemStack) {
        return getNextAvailableSlot(inventory, itemStack, null);
    }

    /**
     * Get next valid slot for an ItemStack, or in general.
     * Returns -1 for no available slots.
     *
     * @param inventory  The inventory to check.
     * @param itemStack  The ItemStack to check space for, nullable.
     * @param validSlots The slots to check for, nullable.
     * @return The next available slot.
     */
    public static int getNextAvailableSlot(Inventory inventory, @Nullable ItemStack itemStack, @Nullable List<Integer> validSlots) {
        if (!hasSpace(inventory, itemStack, validSlots)) {
            return -1;
        }

        int maxStackSize = itemStack == null ? -1 : itemStack.getMaxStackSize();

        for (int i = 0; i < inventory.getSize(); i++) {
            if (validSlots != null && !validSlots.isEmpty() && !validSlots.contains(i)) {
                continue;
            }

            ItemStack content = inventory.getItem(i);

            if (content == null || content.getType() == Material.AIR) {
                return i;
            }

            if (maxStackSize == -1 || !ItemMatchUtil.isSimilar(itemStack, content, true, true, true)) {
                continue;
            }

            if (content.getAmount() >= maxStackSize) {
                continue;
            }

            return i;
        }

        return -1;
    }

    /**
     * Check whether an inventory has an ItemStack.
     *
     * @param inventory         Inventory to check.
     * @param itemStack         ItemStack to check for.
     * @param amount            The amount to check for.
     * @param compareFlags      Whether to match the flags.
     * @param compareName       Whether to match the name.
     * @param compareLore       Whether to match the lore.
     * @param compareDurability Whether to match the durability.
     * @return Whether the inventory has the items.
     */
    public static boolean hasItems(Inventory inventory, ItemStack itemStack, int amount, boolean compareFlags, boolean compareName, boolean compareLore, boolean compareDurability) {
        return getItems(inventory, itemStack, compareFlags, compareName, compareLore, compareDurability) >= amount;
    }

    /**
     * Check whether an inventory has an ItemStack.
     * <p>
     * NOTE: This is a very basic matching. It does
     * not take name, lore, flags or durability
     * into account. For that use {@link #hasItems(Inventory, ItemStack, int, boolean, boolean, boolean, boolean)}.
     *
     * @param inventory Inventory to check.
     * @param itemStack ItemStack to check for.
     * @param amount    The amount to check for.
     * @return Whether the inventory has the items.
     */
    public static boolean hasItems(Inventory inventory, ItemStack itemStack, int amount) {
        return hasItems(inventory, itemStack, amount, false, false, false, false);
    }

    /**
     * Get how many of a certain ItemStack is in the
     * provided inventory.
     *
     * @param inventory         Inventory to check.
     * @param itemStack         ItemStack to check for.
     * @param compareFlags      Whether to match the flags.
     * @param compareName       Whether to match the name.
     * @param compareLore       Whether to match the lore.
     * @param compareDurability Whether to match the durability.
     * @return The amount of items discovered.
     */
    public static int getItems(Inventory inventory, ItemStack itemStack, boolean compareFlags, boolean compareName, boolean compareLore, boolean compareDurability) {
        if (itemStack == null) {
            return 0;
        }

        int found = 0;

        for (ItemStack item : inventory.getContents()) {
            if (item == null || item.getType() == Material.AIR) {
                continue;
            }

            if (!ItemMatchUtil.isSimilar(item, itemStack, compareFlags, compareName, compareLore)) {
                continue;
            }

            if (itemStack.getType() == Material.SPAWNER && !ItemMatchUtil.spawnerMatches(itemStack, item)) {
                continue;
            }

            if (compareDurability && item.getDurability() != itemStack.getDurability()) {
                continue;
            }

            found += item.getAmount();
        }

        return found;
    }

    /**
     * Get how many of a certain ItemStack is in the
     * provided inventory.
     * <p>
     * NOTE: This is a very basic matching. It does
     * not take name, lore, flags or durability
     * into account. For that use {@link #getItems(Inventory, ItemStack, boolean, boolean, boolean, boolean)}.
     *
     * @param inventory Inventory to check.
     * @param itemStack ItemStack to check for.
     * @return The amount of items discovered.
     */
    public static int getItems(Inventory inventory, ItemStack itemStack) {
        return getItems(inventory, itemStack, false, false, false, false);
    }

    /**
     * Add an ItemStack to an Inventory if space is available.
     *
     * @param inventory The inventory to add to.
     * @param itemStack The ItemStack to add.
     */
    public static void addItem(Inventory inventory, ItemStack itemStack) {
        addItem(inventory, itemStack, null);
    }

    /**
     * Add an ItemStack to an Inventory if space is available.
     *
     * @param inventory  The inventory to add to.
     * @param itemStack  The ItemStack to add.
     * @param validSlots The valid slots to check, nullable.
     */
    public static void addItem(Inventory inventory, ItemStack itemStack, @Nullable List<Integer> validSlots) {
        if (!hasSpace(inventory, itemStack, validSlots)) {
            return;
        }

        ItemStack clonedStack = itemStack.clone();
        int maxStackSize = itemStack.getMaxStackSize();

        for (int i = 0; i < inventory.getSize(); i++) {
            if (validSlots != null && !validSlots.isEmpty() && !validSlots.contains(i)) {
                continue;
            }

            if (clonedStack == null) {
                break;
            }

            ItemStack content = inventory.getItem(i);

            if (content == null || content.getType() == Material.AIR) {
                ItemStack newItem;

                if (clonedStack.getAmount() > maxStackSize) {
                    newItem = clonedStack.clone();
                    newItem.setAmount(maxStackSize);
                    clonedStack.setAmount(clonedStack.getAmount() - maxStackSize);
                } else {
                    newItem = clonedStack.clone();
                    clonedStack = null;
                }

                inventory.setItem(i, newItem);
                continue;
            }

            if (maxStackSize == -1 || !ItemMatchUtil.isSimilar(itemStack, content, true, true, true)) {
                continue;
            }

            if (content.getAmount() >= maxStackSize) {
                continue;
            }

            int maxAvailable = maxStackSize - content.getAmount();
            int newAmount = Math.min(maxAvailable, clonedStack.getAmount());
            content.setAmount(content.getAmount() + newAmount);

            if (clonedStack.getAmount() - newAmount <= 0) {
                clonedStack = null;
            } else {
                clonedStack.setAmount(clonedStack.getAmount() - newAmount);
            }
        }
    }

    public static boolean removeMaterial(Player player, Material material, int amount) {
        PlayerInventory inv = player.getInventory();
        ItemStack[] contents = inv.getContents();
        int available = 0;

        for (ItemStack item : contents) {
            if (item == null || item.getType() != material) {
                continue;
            }

            available += item.getAmount();
        }

        if (available < amount) {
            return false;
        }

        int toRemove = amount;

        for (int slot = 0; slot < contents.length && toRemove > 0; slot++) {
            ItemStack item = contents[slot];

            if (item == null || item.getType() != material) {
                continue;
            }

            if (item.getAmount() <= toRemove) {
                toRemove -= item.getAmount();
                inv.setItem(slot, null);
                continue;
            }

            item.setAmount(item.getAmount() - toRemove);
            inv.setItem(slot, item);
            toRemove = 0;
        }

        return true;
    }

}
