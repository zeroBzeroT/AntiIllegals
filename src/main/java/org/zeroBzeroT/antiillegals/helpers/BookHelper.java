package org.zeroBzeroT.antiillegals.helpers;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.zeroBzeroT.antiillegals.AntiIllegals;

import java.util.Arrays;
import java.util.Collection;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

public class BookHelper {

    private BookHelper() {

    }

    public static int maxBookShulkers() {
        return AntiIllegals.config().getInt("maxBooksShulkersInInventory");
    }

    public static int maxBookItemsInShulker() {
        return AntiIllegals.config().getInt("maxBooksInShulker");
    }

    public static void dropBookShulkerItem(@NotNull final Location location, @NotNull final ItemStack itemStack) {
        location.getWorld().dropItem(location, itemStack).setPickupDelay(100);
    }

    public static int cleanBookItems(@NotNull final Inventory inventory, @Nullable final Location location,
                                     @NotNull final Collection<ItemStack> shulkerWithBooksItemStack,
                                     final int initialCount) {
        return cleanOversizedItems(inventory, location, shulkerWithBooksItemStack, maxBookItemsInShulker(), initialCount);
    }

    public static int cleanBookShulkers(@NotNull final Inventory inventory, @Nullable final Location location,
                                        @NotNull final Collection<ItemStack> shulkerWithBooksItemStack) {
        return cleanOversizedItems(inventory, location, shulkerWithBooksItemStack, maxBookShulkers(), 0);
    }

    public static boolean isBookItem(@NotNull final ItemStack itemStack) {
        return isBookItem(itemStack.getType());
    }

    public static boolean isBookItem(@NotNull final Material material) {
        return material == Material.WRITTEN_BOOK || material == Material.BOOK_AND_QUILL;
    }

    /**
     * filters all books from an inventory into a stream
     * @param inventory the inventory to iterate over
     * @return a stream of all book itemstacks
     */
    @NotNull
    public static Stream<ItemStack> filterBooks(@NotNull final Inventory inventory) {
        return Arrays.stream(inventory.getContents())
                .filter(Objects::nonNull)
                .filter(BookHelper::isBookItem);
    }

    public static int cleanOversizedItems(@NotNull final Inventory inventory, @Nullable final Location location,
                                     @NotNull final Collection<ItemStack> shulkerWithBooksItemStack,
                                     final int maxItems, final int initialCount) {
        if (location == null || maxItems < 0) return 0;

        int counter = initialCount;

        for (final ItemStack shulkerItemStack : shulkerWithBooksItemStack) {
            if (counter > maxItems) {
                inventory.remove(shulkerItemStack);
                Bukkit.getScheduler().runTask(AntiIllegals.INSTANCE, () -> dropBookShulkerItem(location, shulkerItemStack));
            }
            counter++;
        }
        return counter;
    }

    public static void checkEnderChest(@NotNull final InventoryOpenEvent inventoryOpenEvent,
                                       @NotNull final Location location) {
        final Inventory inventory = inventoryOpenEvent.getInventory();
        final ItemStack[] inventoryContents = inventory.getContents();

        final AtomicInteger bookCount = new AtomicInteger();
        for (final ItemStack itemStack : inventoryContents) {
            if (itemStack == null) continue;

            InventoryHolderHelper.iterateInventory(itemStack, inv ->
                    bookCount.set(BookHelper.cleanBookItems(inv, location,
                            BookHelper.filterBooks(inv).toList(), bookCount.get()))
            );
        }
    }

}
