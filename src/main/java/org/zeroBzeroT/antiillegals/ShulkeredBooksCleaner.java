package org.zeroBzeroT.antiillegals;

import org.bukkit.Location;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Collection;

public class ShulkeredBooksCleaner {
    public static int clean(Inventory inventory, Location location, Collection<ItemStack> shulkerWithBooksItemStack) {
        int counter = 0;

        if (AntiIllegals.instance.getConfig().getInt("maxBooksShulkersInInventory") >= 0 && shulkerWithBooksItemStack.size() > AntiIllegals.instance.getConfig().getInt("maxBooksShulkersInInventory")) {
            if (location != null) {
                for (final ItemStack shulkerItemStack : shulkerWithBooksItemStack) {
                    inventory.remove(shulkerItemStack);
                    ++counter;

                    new BukkitRunnable() {
                        public void run() {
                            try {
                                location.getWorld().dropItem(location, shulkerItemStack).setPickupDelay(100);
                            } catch (NullPointerException exception) {
                                this.cancel();
                            }
                        }
                    }.runTaskLater(AntiIllegals.instance, 0L);
                }
            } else {
                AntiIllegals.log("checkInventory", "Found too many shulkers with books but could not find location to drop them.");
            }
        }

        return counter;
    }
}
