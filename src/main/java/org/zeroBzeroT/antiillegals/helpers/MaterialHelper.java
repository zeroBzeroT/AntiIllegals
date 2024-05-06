package org.zeroBzeroT.antiillegals.helpers;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.zeroBzeroT.antiillegals.MaterialSets;

public class MaterialHelper {

    private MaterialHelper() {

    }

    public static boolean isIllegalBlock(@NotNull final ItemStack itemStack) {
        return isIllegalBlock(itemStack.getType());
    }

    public static boolean isIllegalBlock(@NotNull final Material material) {
        return MaterialSets.ILLEGAL_BLOCKS.contains(material);
    }

    public static boolean hasDurability(@NotNull final ItemStack itemStack) {
        return hasDurability(itemStack.getType());
    }

    public static boolean usesDamageValue(@NotNull final Material material) {
        return material.isEdible() || material.isBlock();
    }

    public static boolean hasDurability(@NotNull final Material material) {
        if (usesDamageValue(material))
            return false;

        return MaterialHelper.isArmor(material)
                || MaterialHelper.isWeapon(material)
                || MaterialHelper.isTool(material);
    }

    public static boolean isArmor(@NotNull final ItemStack itemStack) {
        return isArmor(itemStack.getType());
    }

    public static boolean isArmor(@NotNull final Material material) {
        return MaterialSets.ARMOR_MATERIALS.contains(material);
    }

    public static boolean isWeapon(@NotNull final ItemStack itemStack) {
        return isWeapon(itemStack.getType());
    }

    public static boolean isWeapon(@NotNull final Material material) {
        return MaterialSets.WEAPON_MATERIALS.contains(material);
    }

    public static boolean isTool(@NotNull final ItemStack itemStack) {
        return isTool(itemStack.getType());
    }

    public static boolean isTool(@NotNull final Material material) {
        return MaterialSets.TOOLS_MATERIALS.contains(material);
    }

}
