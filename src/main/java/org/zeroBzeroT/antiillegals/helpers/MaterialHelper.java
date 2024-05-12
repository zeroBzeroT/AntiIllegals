package org.zeroBzeroT.antiillegals.helpers;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

public class MaterialHelper {

    private MaterialHelper() {

    }

    @NotNull
    public static final Set<Material> ARMOR_MATERIALS = Set.of(
            Material.CHAINMAIL_HELMET,
            Material.CHAINMAIL_CHESTPLATE,
            Material.CHAINMAIL_LEGGINGS,
            Material.CHAINMAIL_BOOTS,
            Material.IRON_HELMET,
            Material.IRON_CHESTPLATE,
            Material.IRON_LEGGINGS,
            Material.IRON_BOOTS,
            Material.GOLD_HELMET,
            Material.GOLD_CHESTPLATE,
            Material.GOLD_LEGGINGS,
            Material.GOLD_BOOTS,
            Material.DIAMOND_HELMET,
            Material.DIAMOND_CHESTPLATE,
            Material.DIAMOND_LEGGINGS,
            Material.DIAMOND_BOOTS,
            Material.LEATHER_HELMET,
            Material.LEATHER_CHESTPLATE,
            Material.LEATHER_LEGGINGS,
            Material.LEATHER_BOOTS,
            Material.ELYTRA
    );

    @NotNull
    public static final Set<Material> WEAPON_MATERIALS = Set.of(
            Material.WOOD_AXE,
            Material.STONE_AXE,
            Material.IRON_AXE,
            Material.GOLD_AXE,
            Material.DIAMOND_AXE,
            Material.WOOD_SWORD,
            Material.STONE_SWORD,
            Material.IRON_SWORD,
            Material.GOLD_SWORD,
            Material.DIAMOND_SWORD,
            Material.BOW
    );

    @NotNull
    public static final Set<Material> TOOLS_MATERIALS = Set.of(
            Material.WOOD_SPADE,
            Material.STONE_SPADE,
            Material.IRON_SPADE,
            Material.GOLD_SPADE,
            Material.DIAMOND_SPADE,
            Material.WOOD_HOE,
            Material.STONE_HOE,
            Material.IRON_HOE,
            Material.GOLD_HOE,
            Material.DIAMOND_HOE,
            Material.WOOD_PICKAXE,
            Material.STONE_PICKAXE,
            Material.IRON_PICKAXE,
            Material.GOLD_PICKAXE,
            Material.DIAMOND_PICKAXE,
            Material.FLINT_AND_STEEL,
            Material.FISHING_ROD,
            Material.SHEARS
    );

    @NotNull
    public static Set<Material> ILLEGAL_BLOCKS = Set.of(
            Material.BEDROCK,
            Material.ENDER_PORTAL_FRAME,
            Material.BARRIER,
            Material.STRUCTURE_BLOCK,
            Material.STRUCTURE_VOID,
            Material.MOB_SPAWNER,
            Material.MONSTER_EGG,
            Material.COMMAND,
            Material.COMMAND_CHAIN,
            Material.COMMAND_MINECART,
            Material.COMMAND_REPEATING
    );
    public static boolean isIllegalBlock(@NotNull final ItemStack itemStack) {
        return isIllegalBlock(itemStack.getType());
    }

    public static boolean isIllegalBlock(@NotNull final Material material) {
        return ILLEGAL_BLOCKS.contains(material);
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
        return ARMOR_MATERIALS.contains(material);
    }

    public static boolean isWeapon(@NotNull final ItemStack itemStack) {
        return isWeapon(itemStack.getType());
    }

    public static boolean isWeapon(@NotNull final Material material) {
        return WEAPON_MATERIALS.contains(material);
    }

    public static boolean isTool(@NotNull final ItemStack itemStack) {
        return isTool(itemStack.getType());
    }

    public static boolean isTool(@NotNull final Material material) {
        return TOOLS_MATERIALS.contains(material);
    }

}
