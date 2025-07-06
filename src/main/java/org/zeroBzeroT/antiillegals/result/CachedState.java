package org.zeroBzeroT.antiillegals.result;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.util.io.BukkitObjectOutputStream;
import org.jetbrains.annotations.NotNull;
import org.zeroBzeroT.antiillegals.helpers.InventoryHolderHelper;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;

/**
 * Represents a cached state of an ItemStack, including its original item and its state.
 * Provides methods to hash item stacks based on their identity and NBT data,
 * and to apply a cached state back to an ItemStack.
 */
public record CachedState(@NotNull ItemStack revertedStack, @NotNull ItemState revertedState) {
    /**
     * Initialize a Gson instance for serializing ItemMeta objects
     */
    @NotNull
    public static final Gson NBT_GSON = new GsonBuilder()
            .serializeSpecialFloatingPointValues()
            .setFieldNamingStrategy(f -> f.getDeclaringClass() + "@" + f.getName())
            .create();

    /**
     * hashes the item identity, not the object reference itself
     *
     * @param itemStack the itemstack to find the hashcode of
     * @return the hashcode
     */
    public static int itemStackHashCode(@NotNull final ItemStack itemStack) throws Exception {
        return Objects.hash(
                itemStack.getType().ordinal(),
                itemStack.getAmount(),
                bukkitObjectNbtHashCode(itemStack)
        );
    }

    /**
     * A method to get a hash code for an {@link ItemStack}.
     *
     * @param itemStack to turn into a hash code.
     * @return hash code of the item.
     */
    public static Object bukkitObjectNbtHashCode(ItemStack itemStack) throws IllegalStateException, IOException {
        final ItemMeta nbt = itemStack.clone().getItemMeta();
        if (nbt == null) return 0;

        final Optional<Component> nameComponent = Optional.ofNullable(nbt.displayName());
        final String name = nameComponent
                .map(LegacyComponentSerializer.legacySection()::serialize)
                .orElse(null);

        nbt.displayName(null);

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        BukkitObjectOutputStream dataOutput = new BukkitObjectOutputStream(outputStream);
        dataOutput.writeObject(itemStack);
        dataOutput.close();

        return Objects.hash(Arrays.hashCode(outputStream.toByteArray()), name);
    }


    /**
     * Order-independent nbt hashcode implementation.
     *
     * @param itemStack the items tack of which the nbt json will be used
     * @return the hashcode of the nbt, ignoring the order of keys/values to ensure proper identity
     */
    private static Object nbtHashCode(@NotNull ItemStack itemStack) {
        final ItemMeta nbt = itemStack.clone().getItemMeta();
        if (nbt == null) return 0;

        final Optional<Component> nameComponent = Optional.ofNullable(nbt.displayName());
        final String name = nameComponent
                .map(LegacyComponentSerializer.legacySection()::serialize)
                .orElse(null);

        nbt.displayName(null);

        return Objects.hash(NBT_GSON.toJsonTree(nbt), name);
    }

    /**
     * Applies the cached reverted state to a given ItemStack.
     * If the reverted state is CLEAN, no changes are made.
     * Otherwise, it restores the item meta, inventory contents, and amount.
     *
     * @param cached the ItemStack to which the cached state should be applied
     */
    public void applyRevertedState(@NotNull final ItemStack cached) {
        if (revertedState == ItemState.CLEAN) return; // nothing to change

        cached.setItemMeta(revertedStack.getItemMeta());
        InventoryHolderHelper.copyInventoryContents(revertedStack, cached);
        cached.setAmount(revertedStack.getAmount());
    }
}
