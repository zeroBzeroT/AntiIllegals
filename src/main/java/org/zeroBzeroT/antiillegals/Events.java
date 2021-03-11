package org.zeroBzeroT.antiillegals;

import org.bukkit.Location;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;
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

public class Events implements Listener {
    @EventHandler(ignoreCancelled = true)
    public void onBlockBreak(BlockBreakEvent event) {
        if (!(event.getBlock().getState() instanceof InventoryHolder)) return;

        Inventory inventory = ((InventoryHolder) event.getBlock().getState()).getInventory();
        Location location = event.getBlock().getLocation();

        AntiIllegals.checkInventory(inventory, location, true);
    }

    @EventHandler(ignoreCancelled = true)
    public void onEntityDeath(EntityDeathEvent event) {
        if (event.getDrops() == null || event.getDrops().isEmpty()) return;

        for (ItemStack drop : event.getDrops()) {
            AntiIllegals.checkItemStack(drop, event.getEntity().getLocation(), false);
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onVehicleDestroy(VehicleDestroyEvent event) {
        // todo: check! not sure if inventory instanceof InventoryHolder is true for both cart types with inventory
        if (event.getVehicle() instanceof InventoryHolder) {
            Inventory inventory = ((InventoryHolder) event.getVehicle()).getInventory();
            Location location = event.getVehicle().getLocation();

            AntiIllegals.checkInventory(inventory, location, true);
        }
    }

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void onPlaceBlock(BlockPlaceEvent event) {
        for (int i = 0; i < 9; i++) {
            if (AntiIllegals.checkItemStack(event.getPlayer().getInventory().getItem(i), event.getPlayer().getLocation(), false) == AntiIllegals.ItemState.illegal) {
                event.setCancelled(true);
                AntiIllegals.log(event.getEventName(), "Stopped " + event.getPlayer().getName() + " from placing " + event.getPlayer().getInventory().getItem(i) + "");
            }
        }

        // TODO: isn't that redundant? check parts and then the whole inventory?
        Inventory inventory = event.getPlayer().getInventory();
        Location location = event.getPlayer().getLocation();

        AntiIllegals.checkInventory(inventory, location, false);
    }

    @EventHandler(ignoreCancelled = true)
    public void onItemDrop(PlayerDropItemEvent event) {
        if (event.getItemDrop() == null)
            return;

        AntiIllegals.checkItemStack(event.getItemDrop().getItemStack(), event.getItemDrop().getLocation(), false);
    }

    @EventHandler(ignoreCancelled = true)
    private void onEntityPickupItem(EntityPickupItemEvent event) {
        if (event.getItem() == null)
            return;

        if (!(event.getEntity() instanceof Player))
            return;

        Player player = (Player) event.getEntity();

        // TODO: We probably want to check the whole inventory instead of just the hotbar, right?
        for (int i = 0; i < 9; i++) {
            AntiIllegals.checkItemStack(player.getInventory().getItem(i), player.getLocation(), false);
        }

        // TODO: check the picked up item -> event.getItem() ?
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerSwapHandItemsEvent(PlayerSwapHandItemsEvent event) {
        if (event.getMainHandItem() == null)
            if (AntiIllegals.checkItemStack(event.getMainHandItem(), event.getPlayer().getLocation(), false) == AntiIllegals.ItemState.illegal)
                event.setCancelled(true);

        if (event.getOffHandItem() == null)
            if (AntiIllegals.checkItemStack(event.getOffHandItem(), event.getPlayer().getLocation(), false) == AntiIllegals.ItemState.illegal)
                event.setCancelled(true);
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerItemHeldEvent(PlayerItemHeldEvent event) {
        if (event.getPlayer().getInventory() == null)
            return;

        if (event.getPlayer().getInventory().getItem(event.getNewSlot()) != null)
            if (AntiIllegals.checkItemStack(event.getPlayer().getInventory().getItem(event.getNewSlot()), event.getPlayer().getLocation(), false) == AntiIllegals.ItemState.illegal)
                event.setCancelled(true);

        if (event.getPlayer().getInventory().getItem(event.getPreviousSlot()) != null)
            if (AntiIllegals.checkItemStack(event.getPlayer().getInventory().getItem(event.getPreviousSlot()), event.getPlayer().getLocation(), false) == AntiIllegals.ItemState.illegal)
                event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onInventoryMoveItem(InventoryMoveItemEvent event) {
        if (event.getItem() == null) return;

        if (AntiIllegals.checkItemStack(event.getItem(), event.getSource().getLocation(), false) == AntiIllegals.ItemState.illegal)
            event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onPlayerInteractEntity(PlayerInteractEntityEvent event) {
        if (event.getRightClicked() == null) return;

        // Item Frame check only
        if (!(event.getRightClicked() instanceof ItemFrame)) return;

        ItemStack mainHandStack = event.getPlayer().getInventory().getItemInMainHand();

        if (AntiIllegals.checkItemStack(mainHandStack, event.getPlayer().getLocation(), false) == AntiIllegals.ItemState.illegal) {
            event.setCancelled(true);
            AntiIllegals.log(event.getEventName(), "Deleted Illegal " + mainHandStack.toString() + " from " + event.getPlayer().getName());
        }

        ItemStack frameStack = ((ItemFrame) event.getRightClicked()).getItem();

        if (AntiIllegals.checkItemStack(frameStack, event.getPlayer().getLocation(), false) == AntiIllegals.ItemState.illegal) {
            event.setCancelled(true);
            AntiIllegals.log(event.getEventName(), "Deleted Illegal " + frameStack.toString() + " from " + event.getPlayer().getName());
        }
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onHangingBreak(HangingBreakEvent event) {
        if (event.getEntity() == null) return;
        if (!(event.getEntity() instanceof ItemFrame)) return;

        ItemStack item = ((ItemFrame) event.getEntity()).getItem();

        if (AntiIllegals.checkItemStack(item, event.getEntity().getLocation(), false) == AntiIllegals.ItemState.illegal) {
            event.setCancelled(true);
            AntiIllegals.log(event.getEventName(), "Deleted Illegal " + item.toString() + " from " + event.getEntity().getName());
        }
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        if (!(event.getEntity() instanceof ItemFrame)) return;

        ItemStack item = ((ItemFrame) event.getEntity()).getItem();

        if (AntiIllegals.checkItemStack(item, event.getEntity().getLocation(), false) == AntiIllegals.ItemState.illegal) {
            event.setCancelled(true);
            AntiIllegals.log(event.getEventName(), "Stopped " + event.getEntity().getName() + " from dealing damage with " + item.toString());
        }
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onInventoryClick(InventoryClickEvent event) {
        if (event.getClickedInventory() == null) return;
        if (!(event.getWhoClicked() instanceof Player)) return;

        if (AntiIllegals.checkItemStack(event.getCurrentItem(), event.getWhoClicked().getLocation(), false) == AntiIllegals.ItemState.illegal)
            event.setCancelled(true);

        if (AntiIllegals.checkItemStack(event.getCursor(), event.getWhoClicked().getLocation(), false) == AntiIllegals.ItemState.illegal)
            event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onInventoryOpen(InventoryOpenEvent event) {
        AntiIllegals.checkInventory(event.getInventory(), event.getPlayer().getLocation(), false);
    }
}
