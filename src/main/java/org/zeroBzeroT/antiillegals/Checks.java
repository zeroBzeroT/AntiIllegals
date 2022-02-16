package org.zeroBzeroT.antiillegals;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class Checks {
    public static boolean isIllegalBlock(final Material material) {
        return material != null && MaterialSets.illegalBlocks.contains(material);
    }

    public static boolean isArmor(final ItemStack itemStack) {
        return itemStack != null && MaterialSets.armorMaterials.contains(itemStack.getType());
    }

    public static boolean isWeapon(final ItemStack itemStack) {
        return itemStack != null && MaterialSets.weaponMaterials.contains(itemStack.getType());
    }
}
