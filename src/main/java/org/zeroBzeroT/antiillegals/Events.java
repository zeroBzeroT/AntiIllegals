package org.zeroBzeroT.antiillegals;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Container;
import org.bukkit.block.ShulkerBox;
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
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;
import org.bukkit.event.vehicle.VehicleDestroyEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BlockStateMeta;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;

public class Events implements Listener {

    @EventHandler(ignoreCancelled = true)
    public void onBlockBreak(@NotNull final BlockBreakEvent event) {
        if (!(event.getBlock().getState() instanceof InventoryHolder))
            return;


        final Inventory inventory = ((InventoryHolder) event.getBlock().getState()).getInventory();
        final Location location = event.getBlock().getLocation();
        AntiIllegals.checkInventory(inventory, location, true);
    }

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void onPlaceBlock(final BlockPlaceEvent event) {
        if (event.getBlockPlaced().getType().equals(Material.ENDER_PORTAL_FRAME) && (
                (event.getPlayer().getInventory().getItemInMainHand().getType().equals(Material.EYE_OF_ENDER)
                        && !event.getPlayer().getInventory().getItemInOffHand().getType().equals(Material.ENDER_PORTAL_FRAME))
                        || (event.getPlayer().getInventory().getItemInOffHand().getType().equals(Material.EYE_OF_ENDER)
                        && !event.getPlayer().getInventory().getItemInMainHand().getType().equals(Material.ENDER_PORTAL_FRAME))
        )) {
            // workaround - normally it should be getBlock instead of getBlockPlaced I think but im to lazy to test
            AntiIllegals.log(event.getEventName(), event.getPlayer().getName() + " put an ender eye on a portal frame.");
        }
        if (AntiIllegals.checkItemStack(event.getItemInHand(), event.getBlockPlaced().getLocation(), true) == AntiIllegals.ItemState.illegal) {
            event.setCancelled(true);
            AntiIllegals.log(event.getEventName(), "Stopped " + event.getPlayer().getName() + " from placing " + event.getBlockPlaced() + ".");
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onVehicleDestroy(@NotNull final VehicleDestroyEvent event) {
        if (event.getVehicle() instanceof InventoryHolder) {
            final Inventory inventory = ((InventoryHolder) event.getVehicle()).getInventory();
            final Location location = event.getVehicle().getLocation();
            AntiIllegals.checkInventory(inventory, location, true);
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerItemDrop(@NotNull final PlayerDropItemEvent event) {
        if (event.getItemDrop() == null || event.getItemDrop().getItemStack() == null)
            return;

        AntiIllegals.checkItemStack(event.getItemDrop().getItemStack(), event.getItemDrop().getLocation(), true);
    }

    @EventHandler(ignoreCancelled = true)
    private void onEntityPickupItem(@NotNull final EntityPickupItemEvent event) {
        if (event.getItem() == null || event.getItem().getItemStack() == null)
            return;

        if (!(event.getEntity() instanceof final Player player))
            return;

        if (AntiIllegals.checkItemStack(event.getItem().getItemStack(), player.getLocation(), true) == AntiIllegals.ItemState.illegal) {
            event.setCancelled(true);
            AntiIllegals.log(event.getEventName(), "Stopped " + event.getEntity().getName() + " from picking up an illegal item");
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onEntityDeath(@NotNull final EntityDeathEvent event) {
        if (event.getDrops() == null || event.getDrops().isEmpty())
            return;

        for (final ItemStack drop : event.getDrops()) {
            AntiIllegals.checkItemStack(drop, event.getEntity().getLocation(), false);
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerSwapHandItems(@NotNull final PlayerSwapHandItemsEvent event) {
        if (event.getMainHandItem() == null && AntiIllegals.checkItemStack(event.getMainHandItem(), event.getPlayer().getLocation(), true) == AntiIllegals.ItemState.illegal) {
            event.setCancelled(true);
        }

        if (event.getOffHandItem() == null && AntiIllegals.checkItemStack(event.getOffHandItem(), event.getPlayer().getLocation(), true) == AntiIllegals.ItemState.illegal) {
            event.setCancelled(true);
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerItemHeld(@NotNull final PlayerItemHeldEvent event) {
        if (event.getPlayer().getInventory() == null)
            return;

        if (event.getPlayer().getInventory().getItem(event.getNewSlot()) != null && AntiIllegals.checkItemStack(event.getPlayer().getInventory().getItem(event.getNewSlot()), event.getPlayer().getLocation(), true) == AntiIllegals.ItemState.illegal) {
            event.setCancelled(true);
        }
        if (event.getPlayer().getInventory().getItem(event.getPreviousSlot()) != null && AntiIllegals.checkItemStack(event.getPlayer().getInventory().getItem(event.getPreviousSlot()), event.getPlayer().getLocation(), true) == AntiIllegals.ItemState.illegal) {
            event.setCancelled(true);
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onInventoryMoveItem(@NotNull final InventoryMoveItemEvent event) {
        if (event.getItem() == null)
            return;

        // TODO: do not deep check if tps is too low
        if (AntiIllegals.checkItemStack(event.getItem(), event.getSource().getLocation(), true) == AntiIllegals.ItemState.illegal) {
            event.setCancelled(true);
        }
    }

    @SuppressWarnings("IsCancelled")
    @EventHandler(ignoreCancelled = true)
    public void onPlayerInteractEntity(@NotNull final PlayerInteractEntityEvent event) {
        if (event.getRightClicked() == null)
            return;

        if (!(event.getRightClicked() instanceof ItemFrame))
            return;

        final ItemStack mainHandStack = event.getPlayer().getInventory().getItemInMainHand();
        if (AntiIllegals.checkItemStack(mainHandStack, event.getPlayer().getLocation(), true) == AntiIllegals.ItemState.illegal) {
            event.setCancelled(true);
        }

        final ItemStack offhandHandStack = event.getPlayer().getInventory().getItemInOffHand();
        if (AntiIllegals.checkItemStack(offhandHandStack, event.getPlayer().getLocation(), true) == AntiIllegals.ItemState.illegal) {
            event.setCancelled(true);
        }

        if (event.isCancelled()) {
            AntiIllegals.log(event.getEventName(), "Stopped " + event.getPlayer().getName() + " from placing an illegal item in an item frame");
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onHangingBreak(@NotNull final HangingBreakEvent event) {
        if (event.getEntity() == null)
            return;

        if (!(event.getEntity() instanceof ItemFrame))
            return;

        final ItemStack item = ((ItemFrame) event.getEntity()).getItem();
        if (AntiIllegals.checkItemStack(item, event.getEntity().getLocation(), true) == AntiIllegals.ItemState.illegal) {
            event.setCancelled(true);
            ((ItemFrame) event.getEntity()).setItem(new ItemStack(Material.AIR));
            AntiIllegals.log(event.getEventName(), "Deleted Illegal from " + event.getEntity().getName());
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onEntityDamageByEntity(@NotNull final EntityDamageByEntityEvent event) {
        if (!(event.getEntity() instanceof final ItemFrame itemFrame))
            return;

        if (AntiIllegals.checkItemStack(itemFrame.getItem(), event.getEntity().getLocation(), false) == AntiIllegals.ItemState.illegal) {
            itemFrame.setItem(new ItemStack(Material.AIR));
            AntiIllegals.log(event.getEventName(), "Removed illegal item from " + itemFrame);
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onInventoryClick(@NotNull final InventoryClickEvent event) {
        if (event.getClickedInventory() == null)
            return;

        if (!(event.getWhoClicked() instanceof Player))
            return;

        if (AntiIllegals.checkItemStack(event.getCurrentItem(), event.getWhoClicked().getLocation(), true) == AntiIllegals.ItemState.illegal) {
            event.setCancelled(true);
        }

        if (AntiIllegals.checkItemStack(event.getCursor(), event.getWhoClicked().getLocation(), true) == AntiIllegals.ItemState.illegal) {
            event.setCancelled(true);
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onInventoryOpen(@NotNull final InventoryOpenEvent event) {
        if (event.getInventory().equals(event.getPlayer().getEnderChest())) {
            if (!AntiIllegals.instance.getConfig().getBoolean("shulkerBoxes", true)) {
                return;
            }

            Collection<ItemStack> shulkersWithBooks = new ArrayList<>();

            for (ItemStack itemStack : event.getInventory().getContents()) {
                if (itemStack == null) continue;
                if (!(itemStack.getItemMeta() instanceof final BlockStateMeta blockMeta)) continue;

                if (!(blockMeta.getBlockState() instanceof final Container container)) continue;

                for (ItemStack shulkerStack : container.getInventory().getContents()) {
                    if (shulkerStack == null) continue;
                    if (shulkerStack.getType() == Material.WRITTEN_BOOK || shulkerStack.getType() == Material.BOOK_AND_QUILL) {
                        shulkersWithBooks.add(itemStack);
                        break;
                    }
                }
            }

            ShulkeredBooksCleaner.clean(event.getInventory(), event.getPlayer().getLocation(), shulkersWithBooks);

            return;
        }

        AntiIllegals.checkInventory(event.getInventory(), event.getPlayer().getLocation(), true);
        AntiIllegals.checkArmorContents(event.getPlayer().getInventory(), event.getPlayer().getLocation(), true);
    }

    @EventHandler(ignoreCancelled = true)
    public void onBlockDispense(@NotNull final BlockDispenseEvent event) {
        if (AntiIllegals.checkItemStack(event.getItem(), event.getBlock().getLocation(), false) == AntiIllegals.ItemState.illegal) {
            event.setCancelled(true);
            event.setItem(new ItemStack(Material.AIR));
            event.getBlock().getState().update(true, false);
            AntiIllegals.log(event.getEventName(), "Stopped dispensing of an illegal block.");
        }
    }
}
