package org.zeroBzeroT.antiillegals.result;

import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import org.zeroBzeroT.antiillegals.helpers.InventoryHolderHelper;

import java.util.Objects;

public record CachedState(@NotNull ItemStack revertedStack, @NotNull ItemState revertedState) {

    public static int itemStackHashCode(@NotNull final ItemStack itemStack) {
        final ItemMeta meta = itemStack.getItemMeta();

        // hash the item identity, not the object reference itself
        return Objects.hash(
                itemStack.getType().ordinal(),
                itemStack.getDurability(),
                itemStack.getAmount(),
                String.valueOf(meta)
        );
    }

    public void applyRevertedState(@NotNull final ItemStack cached) {
        if (revertedState == ItemState.CLEAN) return; // nothing to change

        cached.setItemMeta(revertedStack.getItemMeta());
        InventoryHolderHelper.copyInventoryContents(revertedStack, cached);
        cached.setDurability(revertedStack.getDurability());
        cached.setData(revertedStack.getData());
        cached.setAmount(revertedStack.getAmount());
    }

}
