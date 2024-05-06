package org.zeroBzeroT.antiillegals.helpers;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.zeroBzeroT.antiillegals.AntiIllegals;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

public class BookHelper {

    private BookHelper() {

    }

    public static int maxBookShulkers() {
        return AntiIllegals.config().getInt("maxBooksShulkersInInventory");
    }

    public static int maxBookItemsInShulker() {
        return AntiIllegals.config().getInt("maxBooksInShulker");
    }

    public static boolean shouldCleanBookShulkers() {
        return AntiIllegals.config().getBoolean("shulkerBoxes", true);
    }

    public static void dropBookShulkerItem(@NotNull final Location location, @NotNull final ItemStack itemStack) {
        location.getWorld().dropItem(location, itemStack).setPickupDelay(100);
    }

    public static int cleanBookItems(@NotNull final Inventory inventory, @Nullable final Location location,
                                     @NotNull final Collection<ItemStack> shulkerWithBooksItemStack) {
        return cleanOversizedItems(inventory, location, shulkerWithBooksItemStack, maxBookItemsInShulker());
    }

    public static int cleanBookShulkers(@NotNull final Inventory inventory, @Nullable final Location location,
                                        @NotNull final Collection<ItemStack> shulkerWithBooksItemStack) {
        return cleanOversizedItems(inventory, location, shulkerWithBooksItemStack, maxBookShulkers());
    }

    public static boolean isBookItem(@NotNull final ItemStack itemStack) {
        return isBookItem(itemStack.getType());
    }

    public static boolean isBookItem(@NotNull final Material material) {
        return material == Material.WRITTEN_BOOK || material == Material.BOOK_AND_QUILL;
    }

    public static int cleanBookShulkers(@NotNull final Inventory inventory, @Nullable final Location location) {
        if (!shouldCleanBookShulkers()) return 0;
        final Collection<ItemStack> shulkersWithBooks = new ArrayList<>();

        for (final ItemStack itemStack : inventory.getContents()) {
            if (itemStack == null) continue;

            final ItemStack[] contents = InventoryHolderHelper.getInventoryContents(itemStack);

            if (Arrays.stream(contents).anyMatch(BookHelper::isBookItem))
                shulkersWithBooks.add(itemStack);
        }
        return BookHelper.cleanBookShulkers(inventory, location, shulkersWithBooks);
    }

    public static int cleanOversizedItems(@NotNull final Inventory inventory, @Nullable final Location location,
                                     @NotNull final Collection<ItemStack> shulkerWithBooksItemStack,
                                     final int maxItems) {
        if (location == null || maxItems < 0 || shulkerWithBooksItemStack.size() <= maxItems) return 0;

        int counter = 0;

        for (final ItemStack shulkerItemStack : shulkerWithBooksItemStack) {
            inventory.remove(shulkerItemStack);
            Bukkit.getScheduler().runTask(AntiIllegals.INSTANCE, () -> dropBookShulkerItem(location, shulkerItemStack));
            counter++;
        }
        return counter;
    }

}
