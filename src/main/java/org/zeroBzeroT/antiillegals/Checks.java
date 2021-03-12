package org.zeroBzeroT.antiillegals;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import static org.zeroBzeroT.antiillegals.MaterialSets.*;

public class Checks {
    public static boolean isIllegalBlock(final Material material) {
        if (material == null) {
            return false;
        }

        return illegalBlocks.contains(material);
    }

    public static boolean isArmor(final ItemStack itemStack) {
        if (itemStack == null) {
            return false;
        }

        return armorMaterials.contains(itemStack.getType());
    }

    public static boolean isWeapon(final ItemStack itemStack) {
        if (itemStack == null) {
            return false;
        }

        return weaponMaterials.contains(itemStack.getType());
    }
}
