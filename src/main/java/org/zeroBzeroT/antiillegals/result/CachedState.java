package org.zeroBzeroT.antiillegals.result;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import org.zeroBzeroT.antiillegals.helpers.InventoryHolderHelper;

import java.util.Objects;
import java.util.Optional;

public record CachedState(@NotNull ItemStack revertedStack, @NotNull ItemState revertedState) {

    /**
     * hashes the item identity, not the object reference itself
     * @param itemStack the itemstack to find the hashcode of
     * @return the hashcode
     */
    public static int itemStackHashCode(@NotNull final ItemStack itemStack) {
        return Objects.hash(
                itemStack.getType().ordinal(),
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

        final Optional<Component> nameComponent = Optional.ofNullable(nbt.displayName());
        final String name = nameComponent
                .map(LegacyComponentSerializer.legacySection()::serialize)
                .orElse(null);

        nbt.displayName(null);

        return Objects.hash(NBT_GSON.toJsonTree(nbt), name);
    }

    public void applyRevertedState(@NotNull final ItemStack cached) {
        if (revertedState == ItemState.CLEAN) return; // nothing to change

        cached.setItemMeta(revertedStack.getItemMeta());
        InventoryHolderHelper.copyInventoryContents(revertedStack, cached);
        cached.setAmount(revertedStack.getAmount());
    }

}
