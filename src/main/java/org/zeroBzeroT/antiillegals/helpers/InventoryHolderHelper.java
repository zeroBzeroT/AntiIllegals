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

import java.util.Optional;

public class InventoryHolderHelper {

    private InventoryHolderHelper() {

    }

    @Nullable
    public static ItemStack @NotNull [] getInventoryContents(@NotNull final ItemStack itemStack) {
        return getInventory(itemStack)
                .map(Inventory::getContents)
                .orElse(new ItemStack[0]);
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

        return Optional.ofNullable(inventoryHolder.getInventory());
    }

    @NotNull
    public static Optional<Inventory> getInventory(@NotNull final Entity entity) {
        if (!(entity instanceof final InventoryHolder inventoryHolder))
            return Optional.empty();

        return Optional.ofNullable(inventoryHolder.getInventory());
    }

}
