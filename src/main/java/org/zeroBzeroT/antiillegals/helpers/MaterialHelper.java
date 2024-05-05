package org.zeroBzeroT.antiillegals.helpers;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;
import org.zeroBzeroT.antiillegals.MaterialSets;

public class MaterialHelper {

    private MaterialHelper() {

    }

    public static boolean isIllegalBlock(@Nullable final Material material) {
        return material != null && MaterialSets.ILLEGAL_BLOCKS.contains(material);
    }

    public static boolean isArmor(@Nullable final ItemStack itemStack) {
        return itemStack != null && MaterialSets.ARMOR_MATERIALS.contains(itemStack.getType());
    }

    public static boolean isWeapon(@Nullable final ItemStack itemStack) {
        return itemStack != null && MaterialSets.WEAPON_MATERIALS.contains(itemStack.getType());
    }

}
