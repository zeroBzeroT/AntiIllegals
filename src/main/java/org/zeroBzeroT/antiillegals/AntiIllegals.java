package org.zeroBzeroT.antiillegals;

import de.tr7zw.changeme.nbtapi.NBTItem;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.BlockState;
import org.bukkit.block.ShulkerBox;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BlockStateMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class AntiIllegals extends JavaPlugin {
    private static AntiIllegals instance;

    public AntiIllegals() {
        (AntiIllegals.instance = this).saveDefaultConfig();
    }

    public static void checkInventory(final Inventory inventory, final Location location, final boolean checkRecursive) {
        checkInventory(inventory, location, checkRecursive, false);
    }

    public static void checkInventory(final Inventory inventory, final Location location, final boolean checkRecursive, final boolean isInsideShulker) {
        final List<ItemStack> removeItemStacks = new ArrayList<>();
        final List<ItemStack> bookItemStacks = new ArrayList<>();
        boolean wasFixed = false;
        int fixesIllegals = 0;
        int fixesBooks = 0;
        for (final ItemStack itemStack : inventory.getContents()) {
            switch (checkItemStack(itemStack, location, checkRecursive)) {
                case illegal: {
                    removeItemStacks.add(itemStack);
                    break;
                }
                case wasFixed: {
                    wasFixed = true;
                    break;
                }
                case written_book: {
                    if (isInsideShulker || inventory.getHolder() instanceof ShulkerBox) {
                        bookItemStacks.add(itemStack);
                        break;
                    }
                    break;
                }
            }
        }
        for (final ItemStack itemStack2 : removeItemStacks) {
            itemStack2.setAmount(0);
            inventory.remove(itemStack2);
            ++fixesIllegals;
        }
        if (bookItemStacks.size() > AntiIllegals.instance.getConfig().getInt("maxBooksInShulker", 10)) {
            if (location != null) {
                for (final ItemStack itemStack2 : bookItemStacks) {
                    inventory.remove(itemStack2);
                    ++fixesBooks;
                    new BukkitRunnable() {
                        public void run() {
                            try {
                                location.getWorld().dropItem(location, itemStack2).setPickupDelay(100);
                            } catch (NullPointerException exception) {
                                this.cancel();
                            }
                        }
                    }.runTaskLater(AntiIllegals.instance, 0L);
                }
            } else {
                log("checkInventory", "Found too many books in shulker but could not find location to drop them.");
            }
        }
        if (wasFixed || fixesIllegals > 0 || fixesBooks > 0) {
            log("checkInventory", "Illegal Blocks: " + fixesIllegals + " - Dropped Books: " + fixesBooks + " - Wrong Enchants: " + wasFixed + ".");
        }
    }

    public static ItemState checkItemStack(ItemStack itemStack, final Location location, final boolean checkRecursive) {
        boolean wasFixed = false;
        if (itemStack == null) {
            return ItemState.empty;
        }
        if (AntiIllegals.instance.getConfig().getBoolean("nameColors", true) && itemStack.getType() != Material.WRITTEN_BOOK && itemStack.hasItemMeta()) {
            final ItemMeta itemMeta = itemStack.getItemMeta();
            itemMeta.setDisplayName(ChatColor.stripColor(itemMeta.getDisplayName()));
            itemStack.setItemMeta(itemMeta);
        }
        if (AntiIllegals.instance.getConfig().getBoolean("unbreakables", false) && itemStack.getType().isItem() && !itemStack.getType().isEdible() && !itemStack.getType().isBlock() && (itemStack.getDurability() > itemStack.getType().getMaxDurability() || itemStack.getDurability() < 0 || itemStack.getItemMeta().isUnbreakable())) {
            itemStack.setDurability((short) 0);
            itemStack.getItemMeta().setUnbreakable(false);
            itemStack.setAmount(0);
        }
        if (AntiIllegals.instance.getConfig().getBoolean("illegalBlocks", true) && Checks.isIllegalBlock(itemStack.getType())) {
            itemStack.setAmount(0);
            return ItemState.illegal;
        }
        if (AntiIllegals.instance.getConfig().getBoolean("nbtFurnaces", true) && itemStack.getType() == Material.FURNACE && itemStack.toString().contains("internal=")) {
            itemStack.setAmount(0);
            return ItemState.illegal;
        }
        if (AntiIllegals.instance.getConfig().getBoolean("overstackedItems", true) && itemStack.getAmount() > itemStack.getMaxStackSize()) {
            itemStack.setAmount(itemStack.getMaxStackSize());
            wasFixed = true;
        }
        if (AntiIllegals.instance.getConfig().getBoolean("conflictingEnchantments", true) && (Checks.isArmor(itemStack) || Checks.isWeapon(itemStack))) {
            final List<Enchantment> keys = new ArrayList<>(itemStack.getEnchantments().keySet());
            Collections.shuffle(keys);
            for (int kI1 = 0; kI1 < keys.size(); ++kI1) {
                for (int kI2 = kI1 + 1; kI2 < keys.size(); ++kI2) {
                    final Enchantment e1 = keys.get(kI1);
                    if (e1.conflictsWith(keys.get(kI2))) {
                        itemStack.removeEnchantment(e1);
                        keys.remove(e1);
                        if (kI1 > 0) {
                            --kI1;
                            break;
                        }
                    }
                }
            }
        }
        if (AntiIllegals.instance.getConfig().getBoolean("attributeModifiers", true)) {
            if (itemStack.getType() != Material.AIR) {
                NBTItem nbt = new NBTItem(itemStack);

                if (nbt.hasKey("AttributeModifiers")) {
                    nbt.removeKey("AttributeModifiers");
                    nbt.applyNBT(itemStack);
                    wasFixed = true;
                    log("AttributeModifiers", "Removed attribute modifiers of " + itemStack);
                }
            }
        }
        if (AntiIllegals.instance.getConfig().getBoolean("customPotionEffects", true)) {
            if (itemStack.getType() == Material.POTION || itemStack.getType() == Material.SPLASH_POTION || itemStack.getType() == Material.LINGERING_POTION) {
                PotionMeta meta = (PotionMeta) itemStack.getItemMeta();
                if (meta.getCustomEffects().size() > 0) {
                    meta.clearCustomEffects();
                    itemStack.setItemMeta(meta);
                    log("CustomPotionEffects", "Removed potion effects from " + itemStack);
                    wasFixed = true;
                }
            }
        }
        if (AntiIllegals.instance.getConfig().getBoolean("maxEnchantments", true)) {
            for (final Enchantment enchantment : itemStack.getEnchantments().keySet()) {
                if (!enchantment.canEnchantItem(itemStack) && !Checks.isArmor(itemStack) && !Checks.isWeapon(itemStack) && itemStack.getEnchantmentLevel(enchantment) > 1) {
                    wasFixed = true;
                    itemStack.removeEnchantment(enchantment);
                    itemStack.addUnsafeEnchantment(enchantment, 1);
                } else {
                    if (itemStack.getEnchantmentLevel(enchantment) <= enchantment.getMaxLevel()) {
                        continue;
                    }
                    wasFixed = true;
                    itemStack.removeEnchantment(enchantment);
                    itemStack.addEnchantment(enchantment, enchantment.getMaxLevel());
                }
            }
        }
        if (AntiIllegals.instance.getConfig().getBoolean("shulkerBoxes", true) && itemStack.getType().toString().contains("SHULKER_BOX") && checkRecursive && itemStack.getItemMeta() instanceof BlockStateMeta) {
            final BlockStateMeta blockMeta = (BlockStateMeta) itemStack.getItemMeta();
            if (blockMeta.getBlockState() instanceof ShulkerBox) {
                final ShulkerBox shulker = (ShulkerBox) blockMeta.getBlockState();
                final Inventory inventoryShulker = shulker.getInventory();
                checkInventory(inventoryShulker, location, true, true);
                shulker.getInventory().setContents(inventoryShulker.getContents());
                blockMeta.setBlockState(shulker);
                try {
                    itemStack.setItemMeta(blockMeta);
                } catch (Exception e2) {
                    log("checkItem", "Exception " + e2.getMessage());
                }
            }
        }
        if (itemStack.getType() == Material.WRITTEN_BOOK || itemStack.getType() == Material.BOOK_AND_QUILL) {
            return ItemState.written_book;
        }
        return wasFixed ? ItemState.wasFixed : ItemState.clean;
    }

    public static void log(final String module, final String message) {
        AntiIllegals.instance.getLogger().info("§a[" + module + "] §e" + message + "§r");
    }

    public void onEnable() {
        this.getServer().getPluginManager().registerEvents(new Events(), this);
        log("nameColors", "" + this.getConfig().getBoolean("nameColors", false));
        log("unbreakables", "" + this.getConfig().getBoolean("unbreakables", false));
        log("illegalBlocks", "" + this.getConfig().getBoolean("illegalBlocks", true));
        log("nbtFurnaces", "" + this.getConfig().getBoolean("nbtFurnaces", true));
        log("overstackedItems", "" + this.getConfig().getBoolean("overstackedItems", true));
        log("itemsWithLore", "" + this.getConfig().getBoolean("itemsWithLore", true));
        log("conflictingEnchantments", "" + this.getConfig().getBoolean("conflictingEnchantments", true));
        log("maxEnchantments", "" + this.getConfig().getBoolean("maxEnchantments", true));
        log("shulkerBoxes", "" + this.getConfig().getBoolean("shulkerBoxes", true));
        log("maxBooksInShulker", "" + this.getConfig().getInt("maxBooksInShulker", 10));
        log("attributeModifiers", "" + this.getConfig().getBoolean("attributeModifiers", true));
        log("customPotionEffects", "" + this.getConfig().getBoolean("customPotionEffects", true));
    }

    public enum ItemState {
        empty,
        clean,
        wasFixed,
        illegal,
        written_book
    }
}
