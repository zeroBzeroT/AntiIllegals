package org.zeroBzeroT.antiillegals.helpers;

import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Entity;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BlockStateMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;

public class InventoryHolderHelper {

    private InventoryHolderHelper() {

    }

    @Nullable
    public static ItemStack @NotNull [] getInventoryContents(@NotNull final ItemStack itemStack) {
        return getInventory(itemStack)
                .map(Inventory::getContents)
                .orElse(new ItemStack[0]);
    }

    /**
     * allows modification of the inventory of a container item.
     * does nothing if the item does not have an inventory.
     * @param itemStack the item to change the inventory of
     * @param function what to do with that inventory
     * @return whether the items inventory was changed
     */
    @NotNull
    public static <R> Optional<R> mapInventory(@NotNull final ItemStack itemStack,
                                               @NotNull final Function<Inventory, R> function) {
        if (!(itemStack.getItemMeta() instanceof final BlockStateMeta blockStateMeta))
            return Optional.empty();

        final BlockState blockState = blockStateMeta.getBlockState();
        if (!(blockState instanceof final InventoryHolder inventoryHolder))
            return Optional.empty();

        final Inventory inventory = inventoryHolder.getInventory();

        final int before = Arrays.hashCode(inventory.getContents());
        final R result = function.apply(inventory);
        final int after = Arrays.hashCode(inventory.getContents());

        if (before == after)
            return Optional.ofNullable(result);

        blockStateMeta.setBlockState(blockState);
        itemStack.setItemMeta(blockStateMeta);

        return Optional.ofNullable(result);
    }

    public static void iterateInventory(@NotNull final ItemStack itemStack,
                                        @NotNull final Consumer<Inventory> consumer) {
        mapInventory(itemStack, inventory -> {
            consumer.accept(inventory);
            return Optional.empty();
        });
    }

    @NotNull
    public static Optional<Inventory> getInventory(@NotNull final ItemStack itemStack) {
        if (!(itemStack.getItemMeta() instanceof final BlockStateMeta blockStateMeta))
            return Optional.empty();

        return getInventory(blockStateMeta.getBlockState());
    }

    @NotNull
    public static Optional<Inventory> getInventory(@NotNull final Block block) {
        return getInventory(block.getState());
    }

    @NotNull
    public static Optional<Inventory> getInventory(@NotNull final BlockState blockState) {
        if (!(blockState instanceof final InventoryHolder inventoryHolder))
            return Optional.empty();

        return Optional.of(inventoryHolder.getInventory());
    }

    @NotNull
    public static Optional<Inventory> getInventory(@NotNull final Entity entity) {
        if (!(entity instanceof final InventoryHolder inventoryHolder))
            return Optional.empty();

        return Optional.of(inventoryHolder.getInventory());
    }

    public static boolean copyInventoryContents(@NotNull final ItemStack from, @NotNull final ItemStack to) {
        return getInventory(from).flatMap(fromInventory -> mapInventory(to, toInventory -> {
            for (int i = 0; i < toInventory.getSize(); i++) {
                final ItemStack clone = fromInventory.getItem(i);
                if (clone == null) continue;

                toInventory.setItem(i, clone.clone());
            }
            return true;
        })).orElse(false);
    }

}
