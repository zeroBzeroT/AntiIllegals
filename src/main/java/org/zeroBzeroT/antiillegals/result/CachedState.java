package org.zeroBzeroT.antiillegals.result;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.apache.fory.Fory;
import org.apache.fory.ThreadSafeFory;
import org.apache.fory.config.Language;
import org.apache.fory.util.function.ToByteFunction;
import org.bukkit.block.BlockState;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BlockStateMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import org.zeroBzeroT.antiillegals.helpers.InventoryHolderHelper;

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
     * Initialize a thread-safe Fory instance for serializing ItemMeta objects
     */
    private static final ThreadSafeFory fory = Fory.builder().withLanguage(Language.JAVA)
            .requireClassRegistration(false)
            .buildThreadSafeFory();

    // Register ItemStack class for serialization/deserialization
    //static {
    //    fory.register(ItemMeta.class);
    //    fory.register(BlockState.class);
    //    fory.register(BlockStateMeta.class);
    //}

    private int test() {
        return 1;
    }

    /**
     * hashes the item identity, not the object reference itself
     *
     * @param itemStack the items tack to find the hashcode of
     * @return the hashcode
     */
    public static int itemStackHashCode(@NotNull final ItemStack itemStack) {
        return Objects.hash(
                itemStack.getType().ordinal(),
                itemStack.getAmount(),
                nbtHashCode(itemStack)
        );
    }

    /**
     * order-independent nbt hashcode implementation
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

        return Objects.hash(Arrays.hashCode(fory.serialize(nbt)), name);
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
