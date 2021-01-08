package org.zeroBzeroT.antiillegals;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.ShulkerBox;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryMoveItemEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BlockStateMeta;
import org.bukkit.inventory.meta.BookMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.nio.charset.CharsetEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AntiIllegals extends JavaPlugin implements Listener {
    static final List<Material> IllegalBlocks = Arrays.asList(Material.ENDER_PORTAL_FRAME, Material.BARRIER,
            Material.BEDROCK, Material.MONSTER_EGG, Material.COMMAND, Material.STRUCTURE_BLOCK, Material.STRUCTURE_VOID,
            Material.MOB_SPAWNER, Material.COMMAND_CHAIN, Material.COMMAND_MINECART, Material.COMMAND_REPEATING);
    private static final int maxLoreEnchantmentLevel = 1;
    private static final CharsetEncoder validCharsetEncoder = StandardCharsets.US_ASCII.newEncoder();
    private static AntiIllegals instance;

    public static AntiIllegals getInstance() {
        return instance;
    }

    public static void log(String module, String message) {
        getInstance().getLogger().info("§a[" + module + "] §e" + message + "§r");
    }

    public void onEnable() {
        instance = this;

        getServer().getPluginManager().registerEvents(this, this);

        log("onEnable", "");
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onPlaceBlock(BlockPlaceEvent event) {
        Block block = event.getBlockPlaced();

        BlockState state = event.getBlockReplacedState();

        String userName = event.getPlayer().getName();
        String eventName = event.getEventName();

        if (state != null && state.getData() != null && event.getItemInHand() != null &&
                event.getItemInHand().getType() == Material.EYE_OF_ENDER &&
                state.getData().getItemType() == Material.ENDER_PORTAL_FRAME) {
            log(eventName, userName + " placed " + event.getItemInHand().getType() + " on " + block.getType().name());
        } else if (IllegalBlocks.contains(block.getType())) {
            log(eventName, userName + " tried to place " + block.getType().name());
            event.setCancelled(true);

            CheckInventoryAndFix(event.getPlayer().getInventory(), eventName, userName, true, event.getPlayer());
        }
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onItemDrop(PlayerDropItemEvent event) {
        if (event.getItemDrop() == null)
            return;

        String eventName = event.getEventName();
        String userName = event.getPlayer().getName();

        ItemState result = fixItem(event.getItemDrop().getItemStack(), true, eventName, userName, event.getPlayer());

        if (result == ItemState.illegal)
            event.getItemDrop().remove();

        // Log
        if (result == ItemState.wasFixed || result == ItemState.illegal) {
            log(eventName, userName + " - " + result.name() + ".");
        }
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    private void onEntityPickupItem(EntityPickupItemEvent event) {
        if (event.getItem() == null)
            return;

        String eventName = event.getEventName();
        String userName = event.getEntity().getName();

        ItemState result = fixItem(event.getItem().getItemStack(), true, eventName, userName, event.getEntity());

        if (result == ItemState.illegal) {
            event.getItem().remove();
        }

        // Log
        if (result == ItemState.wasFixed || result == ItemState.illegal) {
            log(eventName, userName + " - " + result.name() + ".");
        }
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onPlayerItemHeldEvent(PlayerItemHeldEvent event) {
        if (event.getPlayer().getInventory() == null)
            return;

        ItemStack itemStack = event.getPlayer().getInventory().getItem(event.getNewSlot());

        if (itemStack == null)
            return;

        String eventName = event.getEventName();
        String userName = event.getPlayer().getName();

        ItemState result = fixItem(itemStack, true, eventName, userName, event.getPlayer());

        if (result == ItemState.illegal) {
            event.setCancelled(true);

            CheckInventoryAndFix(event.getPlayer().getInventory(), eventName, userName, true, event.getPlayer());
        }

        // Log
        if (result == ItemState.wasFixed || result == ItemState.illegal) {
            log(eventName, userName + " - " + result.name() + ".");
        }
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onInventoryMoveItem(InventoryMoveItemEvent event) {
        if (event.getItem() == null)
            return;

        String eventName = event.getEventName();
        String userName = event.getInitiator().getName();

        ItemState result = fixItem(event.getItem(), true, eventName, userName, null);

        if (result == ItemState.illegal) {
            event.setCancelled(true);

            CheckInventoryAndFix(event.getSource(), eventName, userName, true, null);
        }
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onInventoryClick(InventoryClickEvent event) {
        if (event.getClickedInventory() == null)
            return;

        String eventName = event.getEventName();
        String userName = event.getWhoClicked().getName();

        CheckInventoryAndFix(event.getClickedInventory(), eventName, userName, true, event.getWhoClicked());

        if (!(event.getWhoClicked() instanceof Player))
            return;

        Player player = (Player) event.getWhoClicked();

        ItemState result = fixItem(player.getInventory().getItemInOffHand(), true, eventName, userName,
                event.getWhoClicked());

        if (result == ItemState.illegal) {
            player.getInventory().setItemInOffHand(new ItemStack(Material.AIR));
        }

        // Log
        if (result == ItemState.wasFixed || result == ItemState.illegal) {
            log(eventName, userName + " - " + result.name() + ".");
        }
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onInventoryOpen(InventoryOpenEvent event) {
        if (event.getInventory() == null)
            return;

        if (event.getInventory().equals(event.getPlayer().getEnderChest()))
            return;

        CheckInventoryAndFix(event.getInventory(), event.getEventName(), event.getPlayer().getName(), true,
                event.getPlayer());
    }

    private void CheckInventoryAndFix(Inventory inventory, String logModule, String logIssuer, boolean checkShulkers,
                                      Entity issuer) {
        List<ItemStack> removeItemStacks = new ArrayList<>();
        List<ItemStack> bookItemStacks = new ArrayList<>();

        boolean wasFixed = false;
        int fixesIllegals = 0;
        int fixesBooks = 0;

        // Loop through Inventory
        for (ItemStack itemStack : inventory.getContents()) {
            switch (fixItem(itemStack, checkShulkers, logModule, logIssuer, issuer)) {
                case illegal:
                    removeItemStacks.add(itemStack);
                    break;

                case wasFixed:
                    wasFixed = true;
                    break;

                // Book inside a shulker
                case written_book:
                    bookItemStacks.add(itemStack);
                    break;

                default:
                    break;
            }
        }

        // Remove illegal items
        for (ItemStack itemStack : removeItemStacks) {
            inventory.remove(itemStack);
            fixesIllegals++;
        }

        // Remove books
        if (bookItemStacks.size() > 3) {
            Location loc = issuer == null ? null : issuer.getLocation();

            if (loc != null) {
                for (ItemStack itemStack : bookItemStacks) {
                    if (issuer.isOp()) {
                        break;
                    }

                    inventory.remove(itemStack);
                    fixesBooks++;

                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            try {
                                loc.getWorld().dropItem(loc, itemStack).setPickupDelay(20 * 5);
                            } catch (NullPointerException exception) {
                                cancel();
                            }
                        }
                    }.runTaskLater(this, 0);
                }
            } else {
                log(logModule, logIssuer + " found book in shulker but could not find location of inventory.");
            }
        }

        // Log
        if (wasFixed || fixesIllegals > 0 || fixesBooks > 0) {
            log(logModule, logIssuer + " - Illegal Blocks: " + fixesIllegals + " - Dropped Books: " + fixesBooks
                    + " - Wrong Enchants: " + wasFixed + ".");
        }
    }

    private ItemState fixItem(ItemStack itemStack, boolean checkShulkers, String logModule, String logIssuer,
                              Entity issuer) {
        // null Item
        if (itemStack == null)
            return ItemState.empty;

        // Assuming in Shulker and found a book
        if (!checkShulkers && itemStack.getType() == Material.WRITTEN_BOOK) {
            if (itemStack.hasItemMeta() && itemStack.getItemMeta() instanceof BookMeta) {
                BookMeta bm = (BookMeta) itemStack.getItemMeta();

                if (bm.getPages().size() > 0) {
                    return ItemState.written_book;
                }

//				for (String page : bm.getPages()) {
//					if (!validCharsetEncoder.canEncode(ChatColor.stripColor(page))) {
//						log(logModule, logIssuer + " - Removed a written book from a shulker that had the wrong encoding.");
//						return ItemState.illegal;
//					}
//				}
            } else {
                log(logModule, logIssuer + " - Removed a written book from a shulker that had not meta.");
                return ItemState.illegal;
            }
        }

        // Illegal Blocks
        if (IllegalBlocks.contains(itemStack.getType()) && itemStack.getType() != Material.MOB_SPAWNER)
            return ItemState.illegal;

        boolean wasFixed = false;

        // Max Enchantment
        for (Enchantment enchantment : itemStack.getEnchantments().keySet()) {
			/*if (itemStack.getType() == Material.ELYTRA && enchantment.equals(Enchantment.DURABILITY) && itemStack.getEnchantmentLevel(enchantment) > 0) {
				wasFixed = true;

				itemStack.removeEnchantment(enchantment);
			} else */

            if (!enchantment.canEnchantItem(itemStack) && !isArmor(itemStack) && !isWeapon(itemStack)
                    && itemStack.getEnchantmentLevel(enchantment) > maxLoreEnchantmentLevel) {
                wasFixed = true;

                itemStack.removeEnchantment(enchantment);
                itemStack.addUnsafeEnchantment(enchantment, maxLoreEnchantmentLevel);
            } else if (itemStack.getEnchantmentLevel(enchantment) > enchantment.getMaxLevel()) {
                wasFixed = true;

                itemStack.removeEnchantment(enchantment);
                itemStack.addEnchantment(enchantment, enchantment.getMaxLevel());
            }
        }

        // Durability
//		if (itemStack.getDurability() < 0) {
//			itemStack.setDurability((short)0);
//		}
//		else if (itemStack.getDurability() > itemStack.getType().getMaxDurability()) {
//			itemStack.setDurability(itemStack.getType().getMaxDurability());
//		}

        // ShulkerBox Check
        if (checkShulkers && itemStack.getItemMeta() instanceof BlockStateMeta) {
            BlockStateMeta shulkerMeta = (BlockStateMeta) itemStack.getItemMeta();

            if (shulkerMeta.getBlockState() instanceof ShulkerBox) {
                ShulkerBox shulker = (ShulkerBox) shulkerMeta.getBlockState();

                Inventory inventoryShulker = shulker.getInventory();

                CheckInventoryAndFix(inventoryShulker, logModule + "_Shulker", logIssuer, false, issuer);

                shulker.getInventory().setContents(inventoryShulker.getContents());
                shulkerMeta.setBlockState(shulker);

                // JsonParseException
                try {
                    itemStack.setItemMeta(shulkerMeta);
                } catch (Exception e) {
                    log("fixItem", "Exception " + e.getMessage() + " " + logModule + " " + logIssuer);
                }
            }
        }

        return wasFixed ? ItemState.wasFixed : ItemState.clean;
    }

    private boolean isArmor(final ItemStack itemStack) {
        if (itemStack == null)
            return false;

        final String typeNameString = itemStack.getType().name();

        return typeNameString.endsWith("_HELMET") || typeNameString.endsWith("_CHESTPLATE")
                || typeNameString.endsWith("_LEGGINGS") || typeNameString.endsWith("_BOOTS");
    }

    private boolean isWeapon(final ItemStack itemStack) {
        if (itemStack == null)
            return false;

        final String typeNameString = itemStack.getType().name();

        return typeNameString.endsWith("_SWORD") || typeNameString.endsWith("_AXE") || typeNameString.endsWith("BOW");
    }

    private enum ItemState {
        empty, clean, wasFixed, illegal, written_book
    }
}
