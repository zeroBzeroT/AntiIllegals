package org.zeroBzeroT.antiillegals;

import org.bukkit.inventory.ItemStack;

public class CachedState {

    private final ItemStack revertedStack;
    private final AntiIllegals.ItemState revertedState;

    public CachedState(final ItemStack revertedStack,
                       final AntiIllegals.ItemState revertedState) {
        this.revertedStack = revertedStack;
        this.revertedState = revertedState;
    }

    public void applyRevertedState(final ItemStack cached) {
        if (revertedState == AntiIllegals.ItemState.clean) return; // nothing to change

        cached.setItemMeta(revertedStack.getItemMeta());
        cached.setDurability(revertedStack.getDurability());
        cached.setData(revertedStack.getData());
        cached.setAmount(revertedStack.getAmount());
    }

    public AntiIllegals.ItemState revertedState() {
        return revertedState;
    }

}
