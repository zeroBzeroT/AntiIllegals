package org.zeroBzeroT.antiillegals;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.*;
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
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;
import org.bukkit.event.vehicle.VehicleDestroyEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.jetbrains.annotations.NotNull;
import org.zeroBzeroT.antiillegals.helpers.BookHelper;
import org.zeroBzeroT.antiillegals.helpers.InventoryHolderHelper;
import org.zeroBzeroT.antiillegals.helpers.RevertHelper;
import org.zeroBzeroT.antiillegals.result.ItemState;

public class Events implements Listener {

    @EventHandler(ignoreCancelled = true)
    public void onBlockBreak(@NotNull final BlockBreakEvent event) {
        final Block block = event.getBlock();
        final Location location = block.getLocation();

        InventoryHolderHelper.getInventory(block)
                .ifPresent(inventory -> RevertHelper.checkInventory(inventory, location, true));
    }

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void onPlaceBlock(final BlockPlaceEvent event) {
        final Block placedBlock = event.getBlockPlaced();
        final Material placedBlockType = placedBlock.getType();
        final Location location = placedBlock.getLocation();

        final ItemStack itemStackUsed = event.getItemInHand();
        if (itemStackUsed == null) return;

        final Player player = event.getPlayer();
        final String playerName = player.getName();

        if (placedBlockType == Material.ENDER_PORTAL_FRAME && itemStackUsed.getType() == Material.EYE_OF_ENDER) {
            AntiIllegals.log(event.getEventName(), playerName + " put an ender eye on a portal frame.");
            return;
        }
        if (RevertHelper.revertAll(location, true, itemStackUsed)) {
            event.setCancelled(true);
            AntiIllegals.log(event.getEventName(), "Stopped " + playerName + " from placing "
                    + event.getBlockPlaced().getType() + ".");
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onVehicleDestroy(@NotNull final VehicleDestroyEvent event) {
        final Vehicle vehicle = event.getVehicle();
        final Location location = vehicle.getLocation();

        InventoryHolderHelper.getInventory(vehicle)
                .ifPresent(inventory -> RevertHelper.checkInventory(inventory, location, true));
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerItemDrop(@NotNull final PlayerDropItemEvent event) {
        final Item itemDrop = event.getItemDrop();
        final ItemStack itemStack = itemDrop.getItemStack();

        RevertHelper.checkItemStack(itemStack, itemDrop.getLocation(), true);
    }

    @EventHandler(ignoreCancelled = true)
    private void onEntityPickupItem(@NotNull final EntityPickupItemEvent event) {
        final Entity entity = event.getEntity();
        final ItemStack itemStack = event.getItem().getItemStack();

        if (RevertHelper.revertAll(entity.getLocation(), true, itemStack)) {
            event.setCancelled(true);
            AntiIllegals.log(event.getEventName(), "Stopped " + entity.getName() + " from picking up an illegal item");
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onEntityDeath(@NotNull final EntityDeathEvent event) {
        RevertHelper.revertAll(event.getEntity().getLocation(), false, event.getDrops());
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerSwapHandItems(@NotNull final PlayerSwapHandItemsEvent event) {
        final ItemStack mainHand = event.getMainHandItem();
        final ItemStack offHand = event.getOffHandItem();
        final Location location = event.getPlayer().getLocation();

        if (RevertHelper.revertAll(location, true, mainHand, offHand))
            event.setCancelled(true);
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerItemHeld(@NotNull final PlayerItemHeldEvent event) {
        final Player player = event.getPlayer();
        final PlayerInventory inventory = player.getInventory();
        final Location location = player.getLocation();

        final ItemStack newItem = inventory.getItem(event.getNewSlot());
        final ItemStack previousItem = inventory.getItem(event.getPreviousSlot());

        if (RevertHelper.revertAll(location, true, newItem, previousItem))
            event.setCancelled(true);
    }

    @EventHandler(ignoreCancelled = true)
    public void onInventoryMoveItem(@NotNull final InventoryMoveItemEvent event) {
        // TODO: do not deep check if tps is too low
        if (RevertHelper.revert(event.getItem(), event.getSource().getLocation(), true))
            event.setCancelled(true);
    }

    @EventHandler
    public void onPlayerInteractEntity(@NotNull final PlayerInteractEntityEvent event) {
        if (!(event.getRightClicked() instanceof ItemFrame)) return;

        final Player player = event.getPlayer();
        final PlayerInventory inventory = player.getInventory();
        final Location location = player.getLocation();

        final ItemStack mainHandStack = inventory.getItemInMainHand();
        final ItemStack offhandHandStack = inventory.getItemInOffHand();

        if (RevertHelper.revertAll(location, true, mainHandStack, offhandHandStack)) {
            event.setCancelled(true);
            AntiIllegals.log(event.getEventName(), "Stopped " + player.getName()
                    + " from placing an illegal item in an item frame");
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onHangingBreak(@NotNull final HangingBreakEvent event) {
        RevertHelper.revertEntity(event.getEntity(), event);
    }

    @EventHandler(ignoreCancelled = true)
    public void onEntityDamageByEntity(@NotNull final EntityDamageByEntityEvent event) {
        RevertHelper.revertEntity(event.getEntity(), event);
    }

    @EventHandler(ignoreCancelled = true)
    public void onInventoryClick(@NotNull final InventoryClickEvent event) {
        final HumanEntity player = event.getWhoClicked();
        final Location location = player.getLocation();

        final ItemStack clicked = event.getCurrentItem();
        final ItemStack cursor = event.getCursor();

        if (RevertHelper.revertAll(location, true, ItemState::isIllegal, clicked, cursor))
            event.setCancelled(true);
    }

    @EventHandler(ignoreCancelled = true)
    public void onInventoryOpen(@NotNull final InventoryOpenEvent event) {
        final Inventory inventory = event.getInventory();

        final HumanEntity player = event.getPlayer();
        final Location location = player.getLocation();

        if (inventory.getType() == InventoryType.ENDER_CHEST) {
            BookHelper.checkEnderChest(event, location);
            return;
        }
        RevertHelper.checkInventory(inventory, location, true);
        RevertHelper.checkArmorContents(player, true);
    }

    @EventHandler(ignoreCancelled = true)
    public void onBlockDispense(@NotNull final BlockDispenseEvent event) {
        final ItemStack item = event.getItem();
        final Block block = event.getBlock();
        final Location location = block.getLocation();

        if (RevertHelper.revert(item, location, false)) {
            event.setCancelled(true);
            event.setItem(new ItemStack(Material.AIR));
            block.getState().update(true, false);
            AntiIllegals.log(event.getEventName(), "Stopped dispensing of an illegal block.");
        }
    }
}
