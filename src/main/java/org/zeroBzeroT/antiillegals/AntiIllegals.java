package org.zeroBzeroT.antiillegals;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.ShulkerBox;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;
import org.bukkit.entity.minecart.HopperMinecart;
import org.bukkit.entity.minecart.StorageMinecart;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.hanging.HangingBreakEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryMoveItemEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;
import org.bukkit.event.vehicle.VehicleDestroyEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BlockStateMeta;
import org.bukkit.inventory.meta.BookMeta;
import org.bukkit.plugin.java.JavaPlugin;

import java.nio.charset.CharsetEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;

public class AntiIllegals extends JavaPlugin implements Listener {

    static final List<Material> IllegalBlocks = Arrays.asList(Material.ENDER_PORTAL_FRAME, Material.BARRIER,
            Material.BEDROCK, Material.MONSTER_EGG, Material.COMMAND, Material.STRUCTURE_BLOCK, Material.STRUCTURE_VOID,
            Material.MOB_SPAWNER, Material.COMMAND_CHAIN, Material.COMMAND_MINECART, Material.COMMAND_REPEATING);

    private static final int maxLoreEnchantmentLevel = 1;

    private static final CharsetEncoder validCharsetEncoder = StandardCharsets.US_ASCII.newEncoder();

    public void onEnable() {
        getServer().getPluginManager().registerEvents(this, this);
        log("onEnable", "");
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onBlockBreak(BlockBreakEvent event) {
        if (event.getBlock().getState() instanceof InventoryHolder) {
            Inventory inv = ((InventoryHolder) event.getBlock().getState()).getInventory();
            CheckItemsInSlots(inv.getContents(), event.getEventName(), event.getPlayer().getName(), false);
        }
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onEntityDeath(EntityDeathEvent event) {
        ItemStack[] drops = event.getDrops().toArray(new ItemStack[event.getDrops().size()]);
        CheckItemsInSlots(drops, event.getEventName(), event.getEntity().getName(), false);
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onVehicleDestroy(VehicleDestroyEvent event) {
        if (event.getVehicle() instanceof StorageMinecart) {
            StorageMinecart storageMinecart = (StorageMinecart) event.getVehicle();
            Inventory inventory = storageMinecart.getInventory();
            CheckItemsInSlots(inventory.getContents(), event.getEventName(), event.getAttacker().getName(), false);
        }
        if (event.getVehicle() instanceof HopperMinecart) {
            HopperMinecart storageMinecart = (HopperMinecart) event.getVehicle();
            Inventory inventory = storageMinecart.getInventory();
            CheckItemsInSlots(inventory.getContents(), event.getEventName(), event.getAttacker().getName(), false);
        }
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onPlaceBlock(BlockPlaceEvent event) {
        Block block = event.getBlock();

        String userName = event.getPlayer().getName();
        String eventName = event.getEventName();

        ItemStack[] itemStacks = new ItemStack[9];
        for (int i = 0; i < 9; i++) {
            itemStacks[i] = event.getPlayer().getInventory().getItem(i);
        }

        for (ItemStack hotbarSlot : itemStacks) {
            if (hotbarSlot != null) {
                if (IllegalBlocks.contains(hotbarSlot.getType())) {
                    log(eventName, "Deleted an Illegal " + hotbarSlot.getType() + " From " + userName);
                    event.setCancelled(true);
                    hotbarSlot.setAmount(0);
                }

            }

        }


    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onItemDrop(PlayerDropItemEvent event) {
        if (event.getItemDrop() == null)
            return;

        String eventName = event.getEventName();
        String userName = event.getPlayer().getName();

        ItemStack[] itemStacks = {event.getItemDrop().getItemStack()};

        CheckItemsInSlots(itemStacks, eventName, userName, false);

    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    private void onEntityPickupItem(EntityPickupItemEvent event) {
        if (event.getItem() == null)
            return;

        String eventName = event.getEventName();
        String userName = event.getEntity().getName();

        if (event.getEntity() instanceof Player) {

            ItemStack[] itemStacks = new ItemStack[9];
            Player player = (Player) event.getEntity();

            for (int i = 0; i < 9; i++) {
                itemStacks[i] = player.getInventory().getItem(i);
            }

            CheckItemsInSlots(itemStacks, eventName, userName, false);
        }

    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onPlayerSwapHandItemsEvent(PlayerSwapHandItemsEvent event) {

        if (event.getMainHandItem() == null || event.getOffHandItem() == null) return;

        String eventName = event.getEventName();
        String userName = event.getPlayer().getName();


        ItemStack[] hands = {event.getOffHandItem(), event.getMainHandItem()};
        CheckItemsInSlots(hands, eventName, userName, false);

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

        ItemStack[] slots = {event.getPlayer().getInventory().getItem(event.getNewSlot()), event.getPlayer().getInventory().getItem(event.getPreviousSlot())};

        CheckItemsInSlots(slots, eventName, userName, false);

    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onInventoryMoveItem(InventoryMoveItemEvent event) {

        if (event.getItem() == null)
            return;

        String eventName = event.getEventName();
        String userName = event.getInitiator().getName();

        ItemStack[] itemStacks = {event.getItem()};
        CheckItemsInSlots(itemStacks, eventName, userName, false);

    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onPlayerInterractEntityEvent(PlayerInteractEntityEvent event) {

        if (event.getRightClicked() instanceof ItemFrame) {

            ItemStack mainHandStack = event.getPlayer().getInventory().getItemInMainHand();
            if (Checks.isIllegalBlock(mainHandStack)) {
                mainHandStack.setAmount(0);
                log(event.getEventName(), "Deleted Illegal " + mainHandStack.toString() + " from " + event.getPlayer().getName());
                event.setCancelled(true);
            }

            ItemStack frameStack = ((ItemFrame) event.getRightClicked()).getItem();
            if (Checks.isIllegalBlock(frameStack)) {
                frameStack.setAmount(0);
                event.getRightClicked().remove();
                log(event.getEventName(), "Deleted Illegal " + frameStack.toString() + " from " + event.getPlayer().getName());
                event.setCancelled(true);
            }

        }

    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onHangingBreak(HangingBreakEvent event) {

        if (event.getEntity() instanceof ItemFrame) {

            if (IllegalBlocks.contains(((ItemFrame) event.getEntity()).getItem().getType())) {
                log(event.getEventName(), "Deleted an Illegal " + ((ItemFrame) event.getEntity()).getItem().getType() + " From " + event.getEntity().getName());
                event.getEntity().remove();

            }

        }

    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {

        if (!(event.getEntity() instanceof ItemFrame)) return;

        if (IllegalBlocks.contains(((ItemFrame) event.getEntity()).getItem().getType())) {

            log(event.getEventName(), "Deleted an Illegal " + ((ItemFrame) event.getEntity()).getItem().getType() + " From " + event.getDamager().getName());
            event.getEntity().remove();
            event.setCancelled(true);

        }

    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onInventoryClick(InventoryClickEvent event) {

        if (event.getClickedInventory() == null)
            return;

        String eventName = event.getEventName();
        String userName = event.getWhoClicked().getName();

        if (!(event.getWhoClicked() instanceof Player))
            return;

        ItemStack[] itemStacks = {event.getCurrentItem(), event.getCursor()};
        CheckItemsInSlots(itemStacks, event.getEventName(), event.getWhoClicked().getName(), false);

    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onInventoryOpen(InventoryOpenEvent event) {

        if (event.getInventory() == null)
            return;

        if (event.getInventory().equals(event.getPlayer().getEnderChest()))
            return;

        CheckItemsInSlots(event.getInventory().getContents(), event.getEventName(), event.getPlayer().getName(), false);

    }

    private void CheckItemsInSlots(ItemStack[] illegals, String logModule, String issuer, boolean checkShulkers) {

        for (ItemStack item : illegals) {

            ItemState result = checkItem(item, false, logModule, issuer);
            if (result == ItemState.illegal) {
                log(logModule, "Deleted an Illegal " + item.getType().name() + " from " + issuer);
                item.setAmount(0);

            }

        }
    }

//	private void CheckInventoryAndFix(Inventory inventory, String logModule, String logIssuer, boolean checkShulkers,
//			Entity issuer) {
//		List<ItemStack> removeItemStacks = new ArrayList<>();
//		List<ItemStack> bookItemStacks = new ArrayList<>();
//
//		boolean wasFixed = false;
//		int fixesIllegals = 0;
//		int fixesBooks = 0;
//
//		// Loop through Inventory
//		for (ItemStack itemStack : inventory.getContents()) {
//			switch (checkItem(itemStack, checkShulkers, logModule, logIssuer, issuer)) {
//			case illegal:
//				removeItemStacks.add(itemStack);
//				break;
//
//			case wasFixed:
//				wasFixed = true;
//				break;
//
//			// Book inside a shulker
//			case written_book:
//				bookItemStacks.add(itemStack);
//				break;
//
//			default:
//				break;
//			}
//
//
//
//		}
//
//		// Remove illegal items
//		for (ItemStack itemStack : removeItemStacks) {
//			inventory.remove(itemStack);
//			fixesIllegals++;
//		}
//
//		// Remove books
//		if (bookItemStacks.size() > 3) {
//			Location loc = issuer == null ? null : issuer.getLocation();
//
//			if (loc != null) {
//				for (ItemStack itemStack : bookItemStacks) {
//					if (issuer.isOp()) {
//						break;
//					}
//
//					inventory.remove(itemStack);
//					fixesBooks++;
//
//					new BukkitRunnable() {
//						@Override
//						public void run() {
//							try {
//								loc.getWorld().dropItem(loc, itemStack).setPickupDelay(20 * 5);
//							} catch (NullPointerException exception) {
//								cancel();
//							}
//						}
//					}.runTaskLater(this, 0);
//				}
//			} else {
//				log(logModule, logIssuer + " found book in shulker but could not find location of inventory.");
//			}
//		}
//
//		// Log
//		if (wasFixed || fixesIllegals > 0 || fixesBooks > 0)
//
//		{
//			log(logModule, logIssuer + " - Illegal Blocks: " + fixesIllegals + " - Dropped Books: " + fixesBooks
//					+ " - Wrong Enchants: " + wasFixed + ".");
//		}
//	}

    private ItemState checkItem(ItemStack itemStack, boolean checkShulkers, String logModule, String logIssuer) {
        // null Item
        if (itemStack == null) return ItemState.empty;
        if (itemStack.getType() == Material.MAP) return ItemState.clean;

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
        //Unbreakables

        if (itemStack != null && itemStack.getType().isItem() && !itemStack.getType().isEdible() && !itemStack.getType().isBlock()) {
            if (itemStack.getDurability() > itemStack.getType().getMaxDurability() || itemStack.getDurability() < 0 || itemStack.getItemMeta().isUnbreakable()) {
                itemStack.setDurability((short) 0);
                itemStack.getItemMeta().setUnbreakable(false);
                itemStack.setAmount(0);
            }
        }
        // Illegal Blocks
        if (IllegalBlocks.contains(itemStack.getType()))
            return ItemState.illegal;


        boolean wasFixed = false;

        // Max Enchantment
        for (Enchantment enchantment : itemStack.getEnchantments().keySet()) {
			/*if (itemStack.getType() == Material.ELYTRA && enchantment.equals(Enchantment.DURABILITY) && itemStack.getEnchantmentLevel(enchantment) > 0) {
				wasFixed = true;

				itemStack.removeEnchantment(enchantment);
			} else */

            if (!enchantment.canEnchantItem(itemStack) && !Checks.isArmor(itemStack) && !Checks.isWeapon(itemStack)
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

                //CheckInventoryAndFix(inventoryShulker, logModule + "_Shulker", logIssuer, false, issuer);

                shulker.getInventory().setContents(inventoryShulker.getContents());
                shulkerMeta.setBlockState(shulker);

                // JsonParseException
                try {
                    itemStack.setItemMeta(shulkerMeta);
                } catch (Exception e) {
                    log("checkItem", "Exception " + e.getMessage() + " " + logModule + " " + logIssuer);
                }
            }
        }

        return wasFixed ? ItemState.wasFixed : ItemState.clean;
    }

    public void log(String module, String message) {
        getLogger().info("§a[" + module + "] §e" + message + "§r");
    }

    private enum ItemState {
        empty, clean, wasFixed, illegal, written_book
    }

}
