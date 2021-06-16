package org.zeroBzeroT.antiillegals;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockDispenseEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.hanging.HangingBreakEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryMoveItemEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.player.*;
import org.bukkit.event.vehicle.VehicleDestroyEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

public class Events implements Listener {
    @EventHandler(ignoreCancelled = true)
    public void onBlockBreak(BlockBreakEvent event) {
        if (!(event.getBlock().getState() instanceof InventoryHolder)) return;

        // inventory of the block
        Inventory inventory = ((InventoryHolder) event.getBlock().getState()).getInventory();
        Location location = event.getBlock().getLocation();

        AntiIllegals.checkInventory(inventory, location, true);
    }

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void onPlaceBlock(BlockPlaceEvent event) {
        // placed block - stop placing if its an illegal
        if (Checks.isIllegalBlock(event.getBlockPlaced().getType())) {
            event.setCancelled(true);
            AntiIllegals.log(event.getEventName(), "Stopped " + event.getPlayer().getName() + " from placing " + event.getBlockPlaced() + "");
        }

        AntiIllegals.checkItemStack(event.getItemInHand(), event.getPlayer().getLocation(), true);
    }

    @EventHandler(ignoreCancelled = true)
    public void onVehicleDestroy(VehicleDestroyEvent event) {
        if (event.getVehicle() instanceof InventoryHolder) {
            // inventory of the vehicle
            Inventory inventory = ((InventoryHolder) event.getVehicle()).getInventory();
            Location location = event.getVehicle().getLocation();

            AntiIllegals.checkInventory(inventory, location, true);
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerItemDrop(PlayerDropItemEvent event) {
        if (event.getItemDrop() == null || event.getItemDrop().getItemStack() == null)
            return;

        AntiIllegals.checkItemStack(event.getItemDrop().getItemStack(), event.getItemDrop().getLocation(), true);
    }

    @EventHandler(ignoreCancelled = true)
    private void onEntityPickupItem(EntityPickupItemEvent event) {
        if (event.getItem() == null || event.getItem().getItemStack() == null)
            return;

        if (!(event.getEntity() instanceof Player))
            return;

        Player player = (Player) event.getEntity();

        if (AntiIllegals.checkItemStack(event.getItem().getItemStack(), player.getLocation(), true) == AntiIllegals.ItemState.illegal) {
            event.setCancelled(true);
            AntiIllegals.log(event.getEventName(), "Stopped " + event.getEntity().getName() + " from picking up an illegal item");
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onEntityDeath(EntityDeathEvent event) {
        if (event.getDrops() == null || event.getDrops().isEmpty()) return;

        for (ItemStack drop : event.getDrops()) {
            AntiIllegals.checkItemStack(drop, event.getEntity().getLocation(), false);
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerSwapHandItems(PlayerSwapHandItemsEvent event) {
        if (event.getMainHandItem() == null)
            if (AntiIllegals.checkItemStack(event.getMainHandItem(), event.getPlayer().getLocation(), true) == AntiIllegals.ItemState.illegal)
                event.setCancelled(true);

        if (event.getOffHandItem() == null)
            if (AntiIllegals.checkItemStack(event.getOffHandItem(), event.getPlayer().getLocation(), true) == AntiIllegals.ItemState.illegal)
                event.setCancelled(true);
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerItemHeld(PlayerItemHeldEvent event) {
        if (event.getPlayer().getInventory() == null)
            return;

        if (event.getPlayer().getInventory().getItem(event.getNewSlot()) != null)
            if (AntiIllegals.checkItemStack(event.getPlayer().getInventory().getItem(event.getNewSlot()), event.getPlayer().getLocation(), true) == AntiIllegals.ItemState.illegal)
                event.setCancelled(true);

        if (event.getPlayer().getInventory().getItem(event.getPreviousSlot()) != null)
            if (AntiIllegals.checkItemStack(event.getPlayer().getInventory().getItem(event.getPreviousSlot()), event.getPlayer().getLocation(), true) == AntiIllegals.ItemState.illegal)
                event.setCancelled(true);
    }

    @EventHandler(ignoreCancelled = true)
    public void onInventoryMoveItem(InventoryMoveItemEvent event) {
        if (event.getItem() == null) return;

        if (AntiIllegals.checkItemStack(event.getItem(), event.getSource().getLocation(), true) == AntiIllegals.ItemState.illegal)
            event.setCancelled(true);
    }

    @SuppressWarnings("IsCancelled")
    @EventHandler(ignoreCancelled = true)
    public void onPlayerInteractEntity(PlayerInteractEntityEvent event) {
        if (event.getRightClicked() == null) return;

        // Item Frame check only
        if (!(event.getRightClicked() instanceof ItemFrame)) return;

        ItemStack mainHandStack = event.getPlayer().getInventory().getItemInMainHand();

        if (AntiIllegals.checkItemStack(mainHandStack, event.getPlayer().getLocation(), false) == AntiIllegals.ItemState.illegal)
            event.setCancelled(true);

        ItemStack offhandHandStack = event.getPlayer().getInventory().getItemInOffHand();

        if (AntiIllegals.checkItemStack(offhandHandStack, event.getPlayer().getLocation(), false) == AntiIllegals.ItemState.illegal)
            event.setCancelled(true);

        /*ItemStack frameStack = ((ItemFrame) event.getRightClicked()).getItem();

        if (AntiIllegals.checkItemStack(frameStack, event.getPlayer().getLocation(), false) == AntiIllegals.ItemState.illegal)
            event.setCancelled(true);*/

        if (event.isCancelled())
            AntiIllegals.log(event.getEventName(), "Stopped " + event.getPlayer().getName() + " from placing an illegal item in an item frame");
    }

    @EventHandler(ignoreCancelled = true)
    public void onHangingBreak(HangingBreakEvent event) {
        if (event.getEntity() == null) return;
        if (!(event.getEntity() instanceof ItemFrame)) return;

        ItemStack item = ((ItemFrame) event.getEntity()).getItem();

        if (AntiIllegals.checkItemStack(item, event.getEntity().getLocation(), true) == AntiIllegals.ItemState.illegal) {
            event.setCancelled(true);
          ((ItemFrame) event.getEntity()).setItem(new ItemStack(Material.AIR));
            AntiIllegals.log(event.getEventName(), "Deleted Illegal from " + event.getEntity().getName());
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        // only if an item frame get hit
        if (!(event.getEntity() instanceof ItemFrame)) return;

        ItemFrame itemFrame = (ItemFrame) event.getEntity();

        if (AntiIllegals.checkItemStack(itemFrame.getItem(), event.getEntity().getLocation(), false) == AntiIllegals.ItemState.illegal) {
            itemFrame.setItem(new ItemStack(Material.AIR));
            AntiIllegals.log(event.getEventName(), "Removed illegal item from " + itemFrame.toString());
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onInventoryClick(InventoryClickEvent event) {
        if (event.getClickedInventory() == null) return;
        if (!(event.getWhoClicked() instanceof Player)) return;

        if (AntiIllegals.checkItemStack(event.getCurrentItem(), event.getWhoClicked().getLocation(), true) == AntiIllegals.ItemState.illegal)
            event.setCancelled(true);

        if (AntiIllegals.checkItemStack(event.getCursor(), event.getWhoClicked().getLocation(), true) == AntiIllegals.ItemState.illegal)
            event.setCancelled(true);
    }

    @EventHandler(ignoreCancelled = true)
    public void onInventoryOpen(InventoryOpenEvent event) {
        if (event.getInventory().equals(event.getPlayer().getEnderChest())) return;

        AntiIllegals.checkInventory(event.getInventory(), event.getPlayer().getLocation(), true);
    }

    // from cloudanarchy core
    // dropper / dispenser
    // This event does not get canceled on purpose because the item handling on event cancel is so wonky!
    @EventHandler(ignoreCancelled = true)
    public void onBlockDispense(BlockDispenseEvent event) {
        if (AntiIllegals.checkItemStack(event.getItem(), event.getBlock().getLocation(), false) == AntiIllegals.ItemState.illegal) {
            event.setCancelled(true);
            event.setItem(new ItemStack(Material.AIR));
            event.getBlock().getState().update(true, false);
            AntiIllegals.log(event.getEventName(), "Stopped dispensing of an illegal block.");
        }
    }
}
