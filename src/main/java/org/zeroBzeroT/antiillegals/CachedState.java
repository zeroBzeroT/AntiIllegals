package org.zeroBzeroT.antiillegals;

import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public record CachedState(@NotNull ItemStack revertedStack, @NotNull AntiIllegals.ItemState revertedState) {

    public void applyRevertedState(@NotNull final ItemStack cached) {
        if (revertedState == AntiIllegals.ItemState.clean) return; // nothing to change

        cached.setItemMeta(revertedStack.getItemMeta());
        cached.setDurability(revertedStack.getDurability());
        cached.setData(revertedStack.getData());
        cached.setAmount(revertedStack.getAmount());
    }

}
