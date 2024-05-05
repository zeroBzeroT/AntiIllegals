package org.zeroBzeroT.antiillegals;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

public class Checks {
    public static boolean isIllegalBlock(@Nullable final Material material) {
        return material != null && MaterialSets.illegalBlocks.contains(material);
    }

    public static boolean isArmor(@Nullable final ItemStack itemStack) {
        return itemStack != null && MaterialSets.armorMaterials.contains(itemStack.getType());
    }

    public static boolean isWeapon(@Nullable final ItemStack itemStack) {
        return itemStack != null && MaterialSets.weaponMaterials.contains(itemStack.getType());
    }
}
