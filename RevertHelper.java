package org.zeroBzeroT.antiillegals.helpers;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import de.tr7zw.changeme.nbtapi.NBTItem;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.ItemFrame;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.zeroBzeroT.antiillegals.AntiIllegals;
import org.zeroBzeroT.antiillegals.result.CachedState;
import org.zeroBzeroT.antiillegals.result.ItemState;
import org.zeroBzeroT.antiillegals.result.RevertionResult;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.function.Predicate;
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
     * @param predicate the predicate to match the returned ItemState against
     * @return whether the predicate holds true for the returned ItemState
     */
    public static boolean revert(@Nullable final ItemStack itemStack, @Nullable final Location location,
                                 final boolean checkRecursive, @NotNull final Predicate<ItemState> predicate) {
        return predicate.test(checkItemStack(itemStack, location, checkRecursive));
    }

    /**
     * use this to revert a single itemstack
     * @param itemStack the item to revert
     * @param location the location where books / book shulkers should drop (if any)
     * @param checkRecursive whether containers in item form should be checked for their content recursively
     * @return whether the item was illegal in any way (by type or by nbt)
     */
    public static boolean revert(@Nullable final ItemStack itemStack, @Nullable final Location location,
                                 final boolean checkRecursive) {
        return revert(itemStack, location, checkRecursive, ItemState::wasReverted);
    }

    /**
     * use this to revert multiple items
     * @param items the items to revert
     * @param location the location where books / book shulkers should drop (if any)
     * @param checkRecursive whether containers in item form should be checked for their content recursively
     * @return whether any of the items were illegal in any way (by type or by nbt)
     */
    public static boolean revertAll(@Nullable final Location location, final boolean checkRecursive,
                                    @Nullable final ItemStack @NotNull ... items) {
        return revertAll(location, checkRecursive, Arrays.stream(items));
    }

    /**
     * use this to revert multiple items
     * @param items the items to revert
     * @param location the location where books / book shulkers should drop (if any)
     * @param checkRecursive whether containers in item form should be checked for their content recursively
     * @param predicate the predicate to match the returned ItemState against
     * @return whether any of the itemstates matched the predicate
     */
    public static boolean revertAll(@Nullable final Location location, final boolean checkRecursive,
                                    @NotNull final Predicate<ItemState> predicate,
                                    @Nullable final ItemStack @NotNull ... items) {
        return revertAll(location, checkRecursive, predicate, Arrays.stream(items));
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
     * use this to revert multiple items
     * @param items the items to revert
     * @param location the location where books / book shulkers should drop (if any)
     * @param predicate the predicate to match the returned ItemState against
     * @return whether any of the itemstates matched the predicate
     */
    @SuppressWarnings("UnusedReturnValue")
    public static boolean revertAll(@Nullable final Location location, final boolean checkRecursive,
                                    @NotNull final Predicate<ItemState> predicate,
                                    @NotNull final Collection<ItemStack> items) {
        return revertAll(location, checkRecursive, predicate, items.stream());
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
     * use this to revert multiple items from a stream
     * @param items the items to revert
     * @param location the location where books / book shulkers should drop (if any)
     * @param checkRecursive whether containers in item form should be checked for their content recursively
     * @param predicate the predicate to match the returned ItemState against
     * @return whether any of the itemstates matched the predicate
     */
    public static boolean revertAll(@Nullable final Location location, final boolean checkRecursive,
                                    @NotNull final Predicate<ItemState> predicate,
                                    @NotNull final Stream<ItemStack> items) {
        return items.anyMatch(i -> revert(i, location, checkRecursive, predicate));
    }

    public static <E extends Event & Cancellable> void revertEntity(@NotNull final Entity entity, @NotNull final E event) {
        if (!(entity instanceof final ItemFrame itemFrame)) return;

        revertItemFrame(itemFrame, event);
    }

    public static <E extends Event & Cancellable> void revertItemFrame(@NotNull final ItemFrame itemFrame, @NotNull final E event) {
        final ItemStack item = itemFrame.getItem();
        final Location location = itemFrame.getLocation();

        if (RevertHelper.revert(item, location, true, ItemState::isIllegal)) {
            event.setCancelled(true);
            itemFrame.setItem(new ItemStack(Material.AIR));
            AntiIllegals.log(event.getEventName(), "Deleted Illegal from item frame.");
        }
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
     * removes all kinds of currently existent illegal nbt data
     * @param itemStack the item to revert
     * @return whether the nbt data was modified
     */
    private static boolean revertIllegalNBTData(@NotNull final ItemStack itemStack) {
        return revertColoredName(itemStack)
                | revertIllegalDurability(itemStack)
                | revertUnbreakableTag(itemStack)
                | revertOverstackedItem(itemStack)
                | revertnbtFurnaces(itemStack)
                | removeConflictingEnchantments(itemStack)
                | removeAttributes(itemStack)
                | removeCustomPotionEffects(itemStack)
                | removeIllegalEnchantmentLevels(itemStack)
                | removeIllegalFlightTime(itemStack);
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
        if (deleteIllegalItem(itemStack))
            return ItemState.ILLEGAL;

        final boolean wasFixed = revertIllegalNBTData(itemStack);

        if (BookHelper.isBookItem(itemStack))
            return ItemState.IS_BOOK;

        if (checkRecursive && AntiIllegals.config().getBoolean("shulkerBoxes")) {
            final Optional<RevertionResult> result = InventoryHolderHelper.mapInventory(itemStack,
                    inventory -> checkInventory(inventory, location, true, true)
            );
            if (result.isPresent()) {
                if (result.get().books() > 0)
                    return ItemState.IS_SHULKER_WITH_BOOKS;

                return result.get().wasReverted() || wasFixed ? ItemState.WAS_FIXED : ItemState.CLEAN;
            }
        }
        return wasFixed ? ItemState.WAS_FIXED : ItemState.CLEAN;
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
        if (itemStack == null || itemStack.getType() == Material.AIR || itemStack.getAmount() == 0)
            return ItemState.EMPTY;

        final int metaHash = CachedState.itemStackHashCode(itemStack);
        final CachedState cachedRevertedItem = REVERTED_ITEM_CACHE.getIfPresent(metaHash);

        if (cachedRevertedItem == null) {
            final ItemState revertedState = checkItemStackUncached(itemStack, location, checkRecursive);
            if (revertedState.wasModified())
                REVERTED_ITEM_CACHE.put(metaHash, new CachedState(itemStack.clone(), revertedState));

            return revertedState;
        }
        cachedRevertedItem.applyRevertedState(itemStack);
        return cachedRevertedItem.revertedState();
    }

    /**
     * strips the color of the display name of an item
     * note: written book names are not affected by this; only the display name is checked
     * @param itemStack the item to revert
     * @return whether the name was stripped from color
     */
    private static boolean revertColoredName(@NotNull final ItemStack itemStack) {
        if (!AntiIllegals.config().getBoolean("nameColors") || !itemStack.hasItemMeta())
            return false;

        final ItemMeta itemMeta = itemStack.getItemMeta();
        final Component displayName = itemMeta.displayName();

        if (displayName == null)
            return false;

        final String plainTextDisplayName = PlainTextComponentSerializer.plainText().serialize(displayName);
        final String legacyDisplayName = LegacyComponentSerializer.legacySection().serialize(displayName);

        if (plainTextDisplayName.length() != legacyDisplayName.length())
            return false;

        itemMeta.displayName(Component.text(plainTextDisplayName));
        itemStack.setItemMeta(itemMeta);
        return true;
    }

    /**
     * reverts the illegal durability of an item. the durability is clamped into the range from 0 to the max durability
     * @param itemStack the item to revert
     * @return whether the durability was reverted
     */
    private static boolean revertIllegalDurability(@NotNull final ItemStack itemStack) {
        if (!AntiIllegals.config().getBoolean("durability"))
            return false;

        final Material material = itemStack.getType();
        if (!MaterialHelper.hasDurability(material))
            return false;

        final ItemMeta meta = itemStack.getItemMeta();
        if (!(meta instanceof final Damageable damageable))
            return false;

        final int durability = damageable.getDamage();
        final int maxDurability = material.getMaxDurability();

        if (durability > maxDurability) {
            damageable.setDamage(maxDurability);
            itemStack.setItemMeta(damageable);
            return true;
        }
        if (durability < 0) {
            damageable.setDamage(0);
            itemStack.setItemMeta(damageable);
            return true;
        }
        return false;
    }

    /**
     * remove the unbreakable tag from a tool, armor or weapon (if given)
     * @param itemStack the item to revert
     * @return whether the unbreakable tag was removed
     */
    private static boolean revertUnbreakableTag(@NotNull final ItemStack itemStack) {
        if (!AntiIllegals.config().getBoolean("unbreakables") || !itemStack.hasItemMeta())
            return false;

        if (!MaterialHelper.hasDurability(itemStack.getType()))
            return false;

        final ItemMeta meta = itemStack.getItemMeta();
        if (!meta.isUnbreakable())
            return false;

        meta.setUnbreakable(false);
        itemStack.setItemMeta(meta);
        return true;
    }

    /**
     * deletes an item if the item type is illegal
     * @param itemStack the item to revert
     * @return whether the item was deleted
     */
    private static boolean deleteIllegalItem(@NotNull final ItemStack itemStack) {
        if (!AntiIllegals.config().getBoolean("illegalBlocks"))
            return false;

        if (!MaterialHelper.isIllegalMaterial(itemStack)) return false;

        itemStack.setAmount(0);
        return true;
    }

    /**
     * removes the BlockEntityTag from any non-shulkerbox container items
     * @param itemStack the item to revert
     * @return whether the nbt tag was removed
     */
    private static boolean revertnbtFurnaces(@NotNull final ItemStack itemStack) {
        if (!AntiIllegals.config().getBoolean("nbtFurnaces"))
            return false;

        if (!MaterialHelper.isNonShulkerContainer(itemStack))
            return false;

        final NBTItem nbtItem = new NBTItem(itemStack);

        if (!nbtItem.hasTag("BlockEntityTag"))
            return false;

        nbtItem.removeKey("BlockEntityTag");
        nbtItem.applyNBT(itemStack);
        return true;
    }

    /**
     * sets the stack size of an item to its maximum if the maximum was exceeded
     * @param itemStack the item to revert
     * @return whether the stack size was modified
     */
    private static boolean revertOverstackedItem(@NotNull final ItemStack itemStack) {
        if (!AntiIllegals.config().getBoolean("overstackedItems"))
            return false;

        final int amount = itemStack.getAmount();
        final int maxAmount = itemStack.getMaxStackSize();

        if (amount > maxAmount) {
            itemStack.setAmount(maxAmount);
            return true;
        }
        return false;
    }

    /**
     * removes conflicting enchantments from an item and only keeps a single one randomly, to resolve the conflict
     * @param itemStack the item to revert
     * @return whether the enchantments were modified
     */
    private static boolean removeConflictingEnchantments(@NotNull final ItemStack itemStack) {
        if (!AntiIllegals.config().getBoolean("conflictingEnchantments"))
            return false;

        if (!MaterialHelper.isArmor(itemStack) && !MaterialHelper.isWeapon(itemStack))
            return false;

        final List<Enchantment> keys = new ArrayList<>(itemStack.getEnchantments().keySet());
        Collections.shuffle(keys);

        boolean wasFixed = false;
        for (int i = 0; i < keys.size(); ++i) {
            for (int j = i + 1; j < keys.size(); ++j) {
                final Enchantment base = keys.get(i);
                final Enchantment compare = keys.get(j);
                if (!base.conflictsWith(compare)) continue;

                final int removedLevel = itemStack.removeEnchantment(base);

                wasFixed = wasFixed || removedLevel > 0;
            }
        }
        return wasFixed;
    }

    /**
     * removes any attribute modifiers from an item
     * @param itemStack the item to revert
     * @return whether attribute modifiers were removed
     */
    private static boolean removeAttributes(@NotNull final ItemStack itemStack) {
        if (!AntiIllegals.config().getBoolean("attributeModifiers"))
            return false;

        final NBTItem nbtItem = new NBTItem(itemStack);
        if (!nbtItem.hasTag("AttributeModifiers")) return false;

        nbtItem.removeKey("AttributeModifiers");
        nbtItem.applyNBT(itemStack);
        return true;
    }

    /**
     * removes any custom potion effects from an item
     * @param itemStack the item to revert
     * @return whether any effects were removed
     */
    private static boolean removeCustomPotionEffects(@NotNull final ItemStack itemStack) {
        if (!AntiIllegals.config().getBoolean("customPotionEffects"))
            return false;

        if (!(itemStack.getItemMeta() instanceof final PotionMeta meta))
            return false;

        if (!meta.hasCustomEffects())
            return false;

        meta.clearCustomEffects();
        itemStack.setItemMeta(meta);
        return true;
    }

    /**
     * removes any illegal enchantment levels from an item.
     * if the item is enchantable with the enchant, the level is clamped down to the maximum.
     * if the item is not enchantable and collectibles are enabled, the enchantment level will
     * be clamped down to 1 if greater than 1, or to 0 if less than 0.
     * if the item is not enchantable and collectibles are disabled, the enchantment is removed.
     * @param itemStack the item to revert
     * @return whether any enchantments were modified
     */
    private static boolean removeIllegalEnchantmentLevels(@NotNull final ItemStack itemStack) {
        if (!AntiIllegals.config().getBoolean("maxEnchantments")) return false;
        final boolean allowCollectibles = AntiIllegals.INSTANCE.getConfig().getBoolean("allowCollectibles");

        boolean wasFixed = false;
        for (final Enchantment enchantment : itemStack.getEnchantments().keySet()) {
            final int level = itemStack.getEnchantmentLevel(enchantment);
            if (enchantment.canEnchantItem(itemStack)) {
                // if the items is enchant-able by the enchantment, then force the maximum leve
                if (level < 0 || level > enchantment.getMaxLevel()) {
                    wasFixed = true;
                    itemStack.removeEnchantment(enchantment);
                    itemStack.addUnsafeEnchantment(enchantment, enchantment.getMaxLevel());
                }
                continue;
            }
            if (allowCollectibles && !MaterialHelper.isArmor(itemStack) && !MaterialHelper.isWeapon(itemStack)) {
                // item is not enchant-able by the enchantment, is not a weapon or armor and lore items are enabled
                if (level < 0 || level > 1) {
                    wasFixed = true;
                    itemStack.removeEnchantment(enchantment);
                    itemStack.addUnsafeEnchantment(enchantment, 1);
                }
                continue;
            }
            // item is not enchant-able by the enchantment
            wasFixed = true;
            itemStack.removeEnchantment(enchantment);
        }
        return wasFixed;
    }

    /**
     * removes illegal flight duration from firework rockets
     * @param itemStack the item to revert
     * @return whether the flight duration (if any) was modified
     */
    private static boolean removeIllegalFlightTime(@NotNull final ItemStack itemStack) {
        if (!AntiIllegals.config().getBoolean("flightTime"))
            return false;

        final ItemMeta meta = itemStack.getItemMeta();
        if (!(meta instanceof final FireworkMeta fireworkMeta))
            return false;

        final int flightTime = fireworkMeta.getPower();
        if (flightTime <= 0) {
            fireworkMeta.setPower(1);
            itemStack.setItemMeta(fireworkMeta);
            return true;
        }
        if (flightTime > 3) {
            fireworkMeta.setPower(3);
            itemStack.setItemMeta(fireworkMeta);
            return true;
        }
        return false;
    }

}
