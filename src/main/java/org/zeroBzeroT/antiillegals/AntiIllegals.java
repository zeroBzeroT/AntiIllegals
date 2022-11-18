package org.zeroBzeroT.antiillegals;

import de.tr7zw.changeme.nbtapi.NBTItem;
import org.bstats.bukkit.Metrics;
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
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

public class AntiIllegals extends JavaPlugin {
    private static AntiIllegals instance;

    /**
     * constructor
     */
    public AntiIllegals() {
        // save the plugin instance for logging
        instance = this;

        getConfig().addDefault("bStats", true);
        getConfig().addDefault("nameColors", false);
        getConfig().addDefault("unbreakables", false);
        getConfig().addDefault("illegalBlocks", true);
        getConfig().addDefault("nbtFurnaces", true);
        getConfig().addDefault("overstackedItems", true);
        getConfig().addDefault("itemsWithLore", true);
        getConfig().addDefault("conflictingEnchantments", true);
        getConfig().addDefault("maxEnchantments", true);
        getConfig().addDefault("shulkerBoxes", true);
        getConfig().addDefault("maxBooksInShulker", 10);
        getConfig().addDefault("attributeModifiers", true);
        getConfig().addDefault("customPotionEffects", true);
        getConfig().addDefault("illegalMaterials", MaterialSets.illegalBlocks.stream().map(Material::toString).collect(Collectors.toList()));

        getConfig().options().copyDefaults(true);

        saveConfig();
    }

    /**
     * fired when the plugin gets enabled
     */
    public void onEnable() {
        this.getServer().getPluginManager().registerEvents(new Events(), this);

        log("bStats", "" + getConfig().getBoolean("bStats"));
        log("nameColors", "" + getConfig().getBoolean("nameColors"));
        log("unbreakables", "" + getConfig().getBoolean("unbreakables"));
        log("illegalBlocks", "" + getConfig().getBoolean("illegalBlocks"));
        log("nbtFurnaces", "" + getConfig().getBoolean("nbtFurnaces"));
        log("overstackedItems", "" + getConfig().getBoolean("overstackedItems"));
        log("itemsWithLore", "" + getConfig().getBoolean("itemsWithLore"));
        log("conflictingEnchantments", "" + getConfig().getBoolean("conflictingEnchantments"));
        log("maxEnchantments", "" + getConfig().getBoolean("maxEnchantments"));
        log("shulkerBoxes", "" + getConfig().getBoolean("shulkerBoxes"));
        log("maxBooksInShulker", "" + getConfig().getInt("maxBooksInShulker"));
        log("attributeModifiers", "" + getConfig().getBoolean("attributeModifiers"));
        log("customPotionEffects", "" + getConfig().getBoolean("customPotionEffects"));

        MaterialSets.illegalBlocks = getConfig().getStringList("illegalMaterials").stream().map(Material::getMaterial).collect(Collectors.toCollection(HashSet::new));

        log("illegalMaterials", MaterialSets.illegalBlocks.stream().map(Material::toString).collect(Collectors.joining(", ")));

        // Load Plugin Metrics
        if (getConfig().getBoolean("bStats")) {
            new Metrics(this, 16227);
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
    public static void checkInventory(final Inventory inventory, final Location location, final boolean checkRecursive) {
        checkInventory(inventory, location, checkRecursive, false);
    }

    /**
     * use this method to check and remove illegal items from inventories
     *
     * @param inventory      the inventory that should be checked
     * @param location       location of the inventory holder for possible item drops
     * @param checkRecursive true, if items inside containers should be checked
     */
    public static void checkInventory(final Inventory inventory, final Location location, final boolean checkRecursive, final boolean isInsideShulker) {
        final List<ItemStack> removeItemStacks = new ArrayList<>();
        final List<ItemStack> bookItemStacks = new ArrayList<>();

        boolean wasFixed = false;
        int fixesIllegals = 0;
        int fixesBooks = 0;

        // Loop through Inventory
        for (final ItemStack itemStack : inventory.getContents()) {
            switch (checkItemStack(itemStack, location, checkRecursive)) {
                case illegal:
                    removeItemStacks.add(itemStack);
                    break;

                case wasFixed:
                    wasFixed = true;
                    break;

                // Book inside a shulker
                case written_book:
                    if (isInsideShulker || inventory.getHolder() instanceof ShulkerBox) {
                        bookItemStacks.add(itemStack);
                        break;
                    }
                    break;
            }
        }

        // Remove illegal items
        // TODO: check if 'inventory remove' is needed if setAmount(0) is in place
        for (final ItemStack itemStack2 : removeItemStacks) {
            itemStack2.setAmount(0);
            inventory.remove(itemStack2);
            ++fixesIllegals;
        }

        // Remove books
        if (AntiIllegals.instance.getConfig().getInt("maxBooksInShulker") >= 0 && bookItemStacks.size() > AntiIllegals.instance.getConfig().getInt("maxBooksInShulker")) {
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

        // Log
        if (wasFixed || fixesIllegals > 0 || fixesBooks > 0) {
            log("checkInventory", "Illegal Blocks: " + fixesIllegals + " - Dropped Books: " + fixesBooks + " - Wrong Enchants: " + wasFixed + ".");
        }
    }

    /**
     * use this to check and remove illegal items from all of a player's worn armor
     *
     * @param playerInventory the inventory that should be checked
     * @param location        location of the inventory holder for possible item drops
     * @param checkRecursive  true, if items inside containers should be checked
     */
    public static void checkArmorContents(final PlayerInventory playerInventory, final Location location, final boolean checkRecursive) {
        // Loop through player's worn armor
        for (final ItemStack itemStack : playerInventory.getArmorContents()) {
            checkItemStack(itemStack, location, checkRecursive);
        }
    }

    /**
     * Check an item and try to fix it. If it is an illegal item, then remove it.
     *
     * @param itemStack      Item
     * @param location       Location for item drops
     * @param checkRecursive True, if inventories of containers should be checked
     * @return State of the Item
     */
    public static ItemState checkItemStack(ItemStack itemStack, final Location location, final boolean checkRecursive) {
        boolean wasFixed = false;

        // Null Item
        if (itemStack == null) {
            return ItemState.empty;
        }

        // Name Color Check
        if (AntiIllegals.instance.getConfig().getBoolean("nameColors", true) && itemStack.getType() != Material.WRITTEN_BOOK && itemStack.hasItemMeta()) {
            final ItemMeta itemMeta = itemStack.getItemMeta();
            itemMeta.setDisplayName(ChatColor.stripColor(itemMeta.getDisplayName()));
            itemStack.setItemMeta(itemMeta);
        }

        // Unbreakable & Durability Check
        if (AntiIllegals.instance.getConfig().getBoolean("unbreakables", true) && itemStack.getType().isItem() && !itemStack.getType().isEdible() && !itemStack.getType().isBlock() && (itemStack.getDurability() > itemStack.getType().getMaxDurability() || itemStack.getDurability() < 0 || itemStack.getItemMeta().isUnbreakable())) {

            if (MaterialSets.armorMaterials.contains(itemStack.getType()) || MaterialSets.weaponMaterials.contains(itemStack.getType()) || MaterialSets.toolsMaterials.contains(itemStack.getType())) {
                if (itemStack.getDurability() > itemStack.getType().getMaxDurability())
                    itemStack.setDurability(itemStack.getType().getMaxDurability());
                else if (itemStack.getDurability() < 0)
                    itemStack.setDurability((short) 0);
            }

            NBTItem nbt = new NBTItem(itemStack);

            if (nbt.hasKey("Unbreakable")) {
                nbt.removeKey("Unbreakable");
                nbt.applyNBT(itemStack);
                wasFixed = true;
                log("Unbreakables", "Removed unbreakable of " + itemStack);
            }
        }

        // Illegal blocks
        if (AntiIllegals.instance.getConfig().getBoolean("illegalBlocks", true) && Checks.isIllegalBlock(itemStack.getType())) {
            itemStack.setAmount(0);
            return ItemState.illegal;
        }

        // NBT furnace check
        // TODO: use nbt api instead of toString workaround
        if (AntiIllegals.instance.getConfig().getBoolean("nbtFurnaces", true) && itemStack.getType() == Material.FURNACE && itemStack.toString().contains("internal=")) {
            itemStack.setAmount(0);
            return ItemState.illegal;
        }

        // Revert overly stacked items
        if (AntiIllegals.instance.getConfig().getBoolean("overstackedItems", true) && itemStack.getAmount() > itemStack.getMaxStackSize()) {
            itemStack.setAmount(itemStack.getMaxStackSize());
            wasFixed = true;
        }

        // Conflicting enchantments
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

        // Items with custom modifiers (e.g. maxDamage +100)
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

        // Potions with custom effects
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

        // Max enchantments
        if (AntiIllegals.instance.getConfig().getBoolean("maxEnchantments", true)) {
            for (final Enchantment enchantment : itemStack.getEnchantments().keySet()) {
                if (Checks.isArmor(itemStack) || Checks.isWeapon(itemStack)) {
                    // armor and weapons should not have other enchantments
                    if (!enchantment.canEnchantItem(itemStack)) {
                        wasFixed = true;
                        itemStack.removeEnchantment(enchantment);
                    } else if (itemStack.getEnchantmentLevel(enchantment) > enchantment.getMaxLevel()) {
                        wasFixed = true;
                        itemStack.removeEnchantment(enchantment);
                        itemStack.addUnsafeEnchantment(enchantment, enchantment.getMaxLevel());
                    }
                } else if (enchantment.canEnchantItem(itemStack)) {
                    // if the items is enchant-able by the enchantment, then force the maximum level
                    if (itemStack.getEnchantmentLevel(enchantment) > enchantment.getMaxLevel()) {
                        wasFixed = true;
                        itemStack.removeEnchantment(enchantment);
                        itemStack.addUnsafeEnchantment(enchantment, enchantment.getMaxLevel());
                    }
                } else if (AntiIllegals.instance.getConfig().getBoolean("itemsWithLore")) {
                    // if lore items are enabled (config option), then force lvl 1
                    if (itemStack.getEnchantmentLevel(enchantment) != 1) {
                        wasFixed = true;
                        itemStack.removeEnchantment(enchantment);
                        itemStack.addUnsafeEnchantment(enchantment, 1);
                    }
                } else {
                    // wrong enchantment
                    wasFixed = true;
                    itemStack.removeEnchantment(enchantment);
                }
            }
        }

        // ShulkerBox Check
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

        // books
        if (itemStack.getType() == Material.WRITTEN_BOOK || itemStack.getType() == Material.BOOK_AND_QUILL) {
            return ItemState.written_book;
        }

        return wasFixed ? ItemState.wasFixed : ItemState.clean;
    }

    /**
     * log formatting and output
     */
    public static void log(final String module, final String message) {
        AntiIllegals.instance.getLogger().info("§a[" + module + "] §e" + message + "§r");
    }

    public enum ItemState {empty, clean, wasFixed, illegal, written_book}
}
