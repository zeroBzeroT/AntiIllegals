package org.zeroBzeroT.antiillegals;

import org.bukkit.inventory.ItemStack;

public class Checks {

    public static boolean isIllegalBlock(final ItemStack itemStack) {
        if (itemStack == null) {
            return false;
        }
        return MaterialSets.illegalBlocks.contains(itemStack.getType());
    }

    public static boolean isArmor(final ItemStack itemStack) {
        if (itemStack == null) {
            return false;
        }
        return MaterialSets.armorMaterials.contains(itemStack.getType());
    }

    public static boolean isWeapon(final ItemStack itemStack) {
        if (itemStack == null) {
            return false;
        }
        return MaterialSets.weaponMaterials.contains(itemStack.getType());
    }

}
