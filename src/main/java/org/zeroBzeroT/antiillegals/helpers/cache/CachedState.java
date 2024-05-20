package org.zeroBzeroT.antiillegals.helpers.cache;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.zeroBzeroT.antiillegals.helpers.InventoryHolderHelper;
import org.zeroBzeroT.antiillegals.result.ItemState;

import java.util.Objects;

public record CachedState(@Nullable ItemStack revertedStack, @NotNull ItemState revertedState) {

    /**
     * hashes the item identity, not the object reference itself
     * @param itemStack the itemstack to find the hashcode of
     * @return the hashcode
     */
    public static int itemStackHashCode(@NotNull final ItemStack itemStack) {
        return Objects.hash(
                itemStack.getType().ordinal(),
                itemStack.getDurability(),
                itemStack.getAmount(),
                nbtHashCode(itemStack)
        );
    }

    @NotNull
    public static final Gson NBT_GSON = new GsonBuilder()
            .serializeSpecialFloatingPointValues()
            .setFieldNamingStrategy(f -> f.getDeclaringClass() + "@" + f.getName())
            .create();

    /**
     * order-independent nbt hashcode implementation
     * @param itemStack the itemstack of which the nbt json will be used
     * @return the hashcode of the nbt, ignoring the order of keys/values to ensure proper identity
     */
    public static int nbtHashCode(@NotNull final ItemStack itemStack) {
        final ItemMeta nbt = itemStack.clone().getItemMeta();
        if (nbt == null) return 0;

        final String name = nbt.getDisplayName();
        nbt.setDisplayName(null);

        return Objects.hash(NBT_GSON.toJsonTree(nbt), name);
    }

    public void applyRevertedState(@NotNull final ItemStack cached) {
        if (revertedStack == null) return; // nothing to change, the state was clean

        cached.setItemMeta(revertedStack.getItemMeta());
        InventoryHolderHelper.copyInventoryContents(revertedStack, cached);
        cached.setDurability(revertedStack.getDurability());
        cached.setAmount(revertedStack.getAmount());
    }

}
