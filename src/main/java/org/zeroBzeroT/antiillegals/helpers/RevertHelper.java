package org.zeroBzeroT.antiillegals.helpers;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import de.tr7zw.changeme.nbtapi.NBTItem;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.ShulkerBox;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.BlockStateMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.zeroBzeroT.antiillegals.AntiIllegals;
import org.zeroBzeroT.antiillegals.MaterialSets;
import org.zeroBzeroT.antiillegals.result.CachedState;
import org.zeroBzeroT.antiillegals.result.ItemState;
import org.zeroBzeroT.antiillegals.result.RevertionResult;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

public class RevertHelper {

    @NotNull
    private static final Cache<Integer, CachedState> REVERTED_ITEM_CACHE = CacheBuilder.newBuilder()
            .expireAfterAccess(1, TimeUnit.MINUTES)
            .build();

    private RevertHelper() {

    }

    /**
     * use this to revert a single itemstack
     * @param itemStack the item to revert
     * @param location the location where books / book shulkers should drop (if any)
     * @param checkRecursive whether containers in item form should be checked for their content recursively
     * @return whether the item was illegal in any way (by type or by nbt)
     */
    public static boolean revert(@NotNull final ItemStack itemStack, @Nullable final Location location,
                                 final boolean checkRecursive) {
        return checkItemStack(itemStack, location, checkRecursive).wasReverted();
    }

    /**
     * use this to revert multiple items
     * @param items the items to revert
     * @param location the location where books / book shulkers should drop (if any)
     * @param checkRecursive whether containers in item form should be checked for their content recursively
     * @return whether any of the items were illegal in any way (by type or by nbt)
     */
    public static boolean revertAll(@Nullable final Location location, final boolean checkRecursive,
                                    @NotNull final ItemStack @NotNull ... items) {
        return revertAll(location, checkRecursive, Arrays.stream(items));
    }

    /**
     * use this to revert multiple items
     * @param items the items to revert
     * @param location the location where books / book shulkers should drop (if any)
     * @param checkRecursive whether containers in item form should be checked for their content recursively
     * @return whether any of the items were illegal in any way (by type or by nbt)
     */
    @SuppressWarnings("UnusedReturnValue")
    public static boolean revertAll(@Nullable final Location location, final boolean checkRecursive,
                                    @NotNull final Collection<ItemStack> items) {
        return revertAll(location, checkRecursive, items.stream());
    }

    /**
     * use this to revert multiple items from a stream
     * @param items the items to revert
     * @param location the location where books / book shulkers should drop (if any)
     * @param checkRecursive whether containers in item form should be checked for their content recursively
     * @return whether any of the items were illegal in any way (by type or by nbt)
     */
    public static boolean revertAll(@Nullable final Location location, final boolean checkRecursive,
                                    @NotNull final Stream<ItemStack> items) {
        return items.anyMatch(i -> revert(i, location, checkRecursive));
    }

    /**
     * use this method to check and remove illegal items from inventories
     * (overload of checkInventory with isInsideShulker set to false)
     *
     * @param inventory      the inventory that should be checked
     * @param location       location of the inventory holder for possible item drops
     * @param checkRecursive true, if items inside containers should be checked
     */
    public static void checkInventory(@NotNull final Inventory inventory, @Nullable final Location location,
                                      final boolean checkRecursive) {
        checkInventory(inventory, location, checkRecursive, false);
    }

    /**
     * use this method to check and remove illegal items from inventories
     *
     * @param inventory      the inventory that should be checked
     * @param location       location of the inventory holder for possible item drops
     * @param checkRecursive true, if items inside containers should be checked
     * @return number of books in that inventory (worst code I have ever written)
     */
    @NotNull
    public static RevertionResult checkInventory(@NotNull final Inventory inventory, @Nullable final Location location,
                                                 final boolean checkRecursive, final boolean isInsideShulker) {
        final List<ItemStack> removeItemStacks = new ArrayList<>();
        final List<ItemStack> bookItemStacks = new ArrayList<>();
        final List<ItemStack> shulkerWithBooksItemStack = new ArrayList<>();

        boolean wasFixed = false;

        for (final ItemStack itemStack : inventory.getContents()) {
            switch (checkItemStack(itemStack, location, checkRecursive)) {
                case ILLEGAL -> removeItemStacks.add(itemStack);
                case WAS_FIXED -> wasFixed = true;
                case IS_BOOK -> bookItemStacks.add(itemStack);
                case IS_SHULKER_WITH_BOOKS -> shulkerWithBooksItemStack.add(itemStack);
            }
        }
        removeItemStacks.forEach(i -> i.setAmount(0));

        final int fixesIllegals = removeItemStacks.size();
        final int fixesBooks = isInsideShulker ? BookHelper.cleanBookItems(inventory, location, bookItemStacks) : 0;
        final int fixesBookShulkers = BookHelper.cleanBookShulkers(inventory, location, shulkerWithBooksItemStack);

        final boolean wasReverted = wasFixed || fixesIllegals > 0;
        final boolean wasChecked = wasReverted && fixesBooks + fixesBookShulkers > 0;

        if (wasChecked)
            AntiIllegals.log("checkInventory", "Illegal Blocks: " + fixesIllegals
                    + " - Dropped Books: " + fixesBooks
                    + " - Dropped Shulkers: " + fixesBookShulkers
                    + " - Illegal NBT: " + wasFixed + ".");

        return new RevertionResult(bookItemStacks.size(), wasReverted);
    }

    /**
     * use this to check and remove illegal items from all of a player's worn armor
     *
     * @param playerInventory the inventory that should be checked
     * @param location        location of the inventory holder for possible item drops
     * @param checkRecursive  true, if items inside containers should be checked
     */
    public static void checkArmorContents(@NotNull final PlayerInventory playerInventory,
                                          @Nullable final Location location, final boolean checkRecursive) {
        revertAll(location, checkRecursive, playerInventory.getArmorContents());
    }

    /**
     * use this to revert a single itemstack
     * @param itemStack the item to revert
     * @param location the location where books / book shulkers should drop (if any)
     * @param checkRecursive whether containers in item form should be checked for their content recursively
     * @return the state of the item that was checked
     */
    @NotNull
    public static ItemState checkItemStack(@Nullable final ItemStack itemStack, @Nullable final Location location,
                                           final boolean checkRecursive)  {
        if (itemStack == null) return ItemState.EMPTY;

        final int metaHash = CachedState.itemStackHashCode(itemStack);
        final CachedState cachedRevertedItem = REVERTED_ITEM_CACHE.getIfPresent(metaHash);

        if (cachedRevertedItem == null) {
            final ItemState revertedState = checkItemStackUncached(itemStack, location, checkRecursive);
            if (revertedState.shouldCache())
                REVERTED_ITEM_CACHE.put(metaHash, new CachedState(itemStack.clone(), revertedState));

            return revertedState;
        }
        cachedRevertedItem.applyRevertedState(itemStack);
        return cachedRevertedItem.revertedState();
    }

    /**
     * this reverts a single itemstack, but without a cache (slow)
     * @param itemStack the item to revert
     * @param location the location where books / book shulkers should drop (if any)
     * @param checkRecursive whether containers in item form should be checked for their content recursively
     * @return the state of the item that was checked
     */
    @NotNull
    private static ItemState checkItemStackUncached(@NotNull final ItemStack itemStack, @Nullable final Location location,
                                                    final boolean checkRecursive) {
        boolean wasFixed = false;

        // Name Color Check
        if (AntiIllegals.instance.getConfig().getBoolean("nameColors", true) && itemStack.getType() != Material.WRITTEN_BOOK && itemStack.hasItemMeta()) {
            final ItemMeta itemMeta = itemStack.getItemMeta();
            itemMeta.setDisplayName(ChatColor.stripColor(itemMeta.getDisplayName()));
            itemStack.setItemMeta(itemMeta);
            wasFixed = true;
        }

        // Durability Check
        if (AntiIllegals.instance.getConfig().getBoolean("durability", true) && !itemStack.getType().isEdible() && !itemStack.getType().isBlock() && (itemStack.getDurability() > itemStack.getType().getMaxDurability() || itemStack.getDurability() < 0)) {
            if (MaterialSets.ARMOR_MATERIALS.contains(itemStack.getType()) || MaterialSets.WEAPON_MATERIALS.contains(itemStack.getType()) || MaterialSets.TOOLS_MATERIALS.contains(itemStack.getType())) {
                if (itemStack.getDurability() > itemStack.getType().getMaxDurability())
                    itemStack.setDurability(itemStack.getType().getMaxDurability());
                else if (itemStack.getDurability() < 0)
                    itemStack.setDurability((short) 0);

                wasFixed = true;
            }
        }

        // Unbreakable Check
        if (AntiIllegals.instance.getConfig().getBoolean("unbreakables", true) && !itemStack.getType().isEdible() && !itemStack.getType().isBlock() && itemStack.getItemMeta().isUnbreakable()) {
            NBTItem nbt = new NBTItem(itemStack);

            if (nbt.hasTag("Unbreakable")) {
                nbt.removeKey("Unbreakable");
                nbt.applyNBT(itemStack);
                wasFixed = true;
                AntiIllegals.log("Unbreakables", "Removed unbreakable of " + itemStack);
            }
        }

        // Illegal blocks
        if (AntiIllegals.instance.getConfig().getBoolean("illegalBlocks", true) && MaterialHelper.isIllegalBlock(itemStack.getType())) {
            itemStack.setAmount(0);
            return ItemState.ILLEGAL;
        }

        // NBT furnace check
        // TODO: use nbt api instead of toString workaround
        if (AntiIllegals.instance.getConfig().getBoolean("nbtFurnaces", true) && itemStack.getType() == Material.FURNACE && itemStack.toString().contains("internal=")) {
            itemStack.setAmount(0);
            return ItemState.ILLEGAL;
        }

        // Revert overly stacked items
        if (AntiIllegals.instance.getConfig().getBoolean("overstackedItems", true) && itemStack.getAmount() > itemStack.getMaxStackSize()) {
            itemStack.setAmount(itemStack.getMaxStackSize());
            wasFixed = true;
        }

        // Conflicting enchantments
        if (AntiIllegals.instance.getConfig().getBoolean("conflictingEnchantments", true) && (MaterialHelper.isArmor(itemStack) || MaterialHelper.isWeapon(itemStack))) {
            final List<Enchantment> keys = new ArrayList<>(itemStack.getEnchantments().keySet());
            Collections.shuffle(keys);

            for (int kI1 = 0; kI1 < keys.size(); ++kI1) {
                for (int kI2 = kI1 + 1; kI2 < keys.size(); ++kI2) {
                    final Enchantment e1 = keys.get(kI1);

                    if (e1.conflictsWith(keys.get(kI2))) {
                        itemStack.removeEnchantment(e1);
                        wasFixed = true;

                        keys.remove(e1);

                        if (kI1 > 0) {
                            --kI1;
                            break;
                        }
                    }
                }
            }
        }

        // Items with custom modifiers (e.g. maxDamage +100)
        if (AntiIllegals.instance.getConfig().getBoolean("attributeModifiers", true)) {
            if (itemStack.getType() != Material.AIR) {
                NBTItem nbt = new NBTItem(itemStack);

                if (nbt.hasTag("AttributeModifiers")) {
                    nbt.removeKey("AttributeModifiers");
                    nbt.applyNBT(itemStack);
                    wasFixed = true;
                    AntiIllegals.log("AttributeModifiers", "Removed attribute modifiers of " + itemStack);
                }
            }
        }

        // Potions with custom effects
        if (AntiIllegals.instance.getConfig().getBoolean("customPotionEffects", true)) {
            if (itemStack.getType() == Material.POTION || itemStack.getType() == Material.SPLASH_POTION || itemStack.getType() == Material.LINGERING_POTION) {
                PotionMeta meta = (PotionMeta) itemStack.getItemMeta();

                if (!meta.getCustomEffects().isEmpty()) {
                    meta.clearCustomEffects();
                    itemStack.setItemMeta(meta);
                    AntiIllegals.log("CustomPotionEffects", "Removed potion effects from " + itemStack);
                    wasFixed = true;
                }
            }
        }

        // Max enchantments
        if (AntiIllegals.instance.getConfig().getBoolean("maxEnchantments", true)) {
            for (final Enchantment enchantment : itemStack.getEnchantments().keySet()) {
                if (enchantment.canEnchantItem(itemStack)) {
                    // if the items is enchant-able by the enchantment, then force the maximum level
                    if (itemStack.getEnchantmentLevel(enchantment) > enchantment.getMaxLevel()) {
                        wasFixed = true;
                        itemStack.removeEnchantment(enchantment);
                        itemStack.addUnsafeEnchantment(enchantment, enchantment.getMaxLevel());
                    }
                } else if (AntiIllegals.instance.getConfig().getBoolean("allowCollectibles") && !MaterialHelper.isArmor(itemStack) && !MaterialHelper.isWeapon(itemStack)) {
                    // item is not enchant-able by the enchantment, is not a weapon or armor and lore items are enabled
                    if (itemStack.getEnchantmentLevel(enchantment) < 0 || itemStack.getEnchantmentLevel(enchantment) > 1) {
                        wasFixed = true;
                        itemStack.removeEnchantment(enchantment);
                        itemStack.addUnsafeEnchantment(enchantment, 1);
                    }
                } else {
                    // item is not enchant-able by the enchantment
                    wasFixed = true;
                    itemStack.removeEnchantment(enchantment);
                }
            }
        }

        // ShulkerBox Check
        if (AntiIllegals.instance.getConfig().getBoolean("shulkerBoxes", true)
                && itemStack.getType().toString().contains("SHULKER_BOX")
                && checkRecursive && itemStack.getItemMeta() instanceof final BlockStateMeta blockMeta) {

            if (blockMeta.getBlockState() instanceof final ShulkerBox shulker) {
                final Inventory inventoryShulker = shulker.getInventory();

                RevertionResult result = checkInventory(inventoryShulker, location, true, true);
                shulker.getInventory().setContents(inventoryShulker.getContents());
                blockMeta.setBlockState(shulker);

                try {
                    itemStack.setItemMeta(blockMeta);
                } catch (Exception e2) {
                    AntiIllegals.log("checkItem", "Exception " + e2.getMessage());
                }

                if (result.books() > 0)
                    return ItemState.IS_SHULKER_WITH_BOOKS;

                return result.wasReverted() ? ItemState.WAS_FIXED : ItemState.CLEAN;
            }
        }

        // books
        if (itemStack.getType() == Material.WRITTEN_BOOK || itemStack.getType() == Material.BOOK_AND_QUILL) {
            return ItemState.IS_BOOK;
        }

        return wasFixed ? ItemState.WAS_FIXED : ItemState.CLEAN;
    }

}
