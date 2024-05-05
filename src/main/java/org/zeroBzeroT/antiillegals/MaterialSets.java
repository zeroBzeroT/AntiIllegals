package org.zeroBzeroT.antiillegals;

import org.bukkit.Material;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;

public class MaterialSets {

    @NotNull
    public static final HashSet<Material> armorMaterials;

    @NotNull
    public static final HashSet<Material> weaponMaterials;

    @NotNull
    public static final HashSet<Material> toolsMaterials;

    @NotNull
    public static HashSet<Material> illegalBlocks;

    static {
        armorMaterials = new HashSet<>() {
            {
                this.add(Material.CHAINMAIL_HELMET);
                this.add(Material.CHAINMAIL_CHESTPLATE);
                this.add(Material.CHAINMAIL_LEGGINGS);
                this.add(Material.CHAINMAIL_BOOTS);
                this.add(Material.IRON_HELMET);
                this.add(Material.IRON_CHESTPLATE);
                this.add(Material.IRON_LEGGINGS);
                this.add(Material.IRON_BOOTS);
                this.add(Material.GOLD_HELMET);
                this.add(Material.GOLD_CHESTPLATE);
                this.add(Material.GOLD_LEGGINGS);
                this.add(Material.GOLD_BOOTS);
                this.add(Material.DIAMOND_HELMET);
                this.add(Material.DIAMOND_CHESTPLATE);
                this.add(Material.DIAMOND_LEGGINGS);
                this.add(Material.DIAMOND_BOOTS);
                this.add(Material.ELYTRA);
            }
        };
        weaponMaterials = new HashSet<>() {
            {
                this.add(Material.WOOD_AXE);
                this.add(Material.STONE_AXE);
                this.add(Material.IRON_AXE);
                this.add(Material.GOLD_AXE);
                this.add(Material.DIAMOND_AXE);
                this.add(Material.WOOD_SWORD);
                this.add(Material.STONE_SWORD);
                this.add(Material.IRON_SWORD);
                this.add(Material.GOLD_SWORD);
                this.add(Material.DIAMOND_SWORD);
                this.add(Material.BOW);
            }
        };
        toolsMaterials = new HashSet<>() {
            {
                this.add(Material.WOOD_SPADE);
                this.add(Material.STONE_SPADE);
                this.add(Material.IRON_SPADE);
                this.add(Material.GOLD_SPADE);
                this.add(Material.DIAMOND_SPADE);
                this.add(Material.WOOD_HOE);
                this.add(Material.STONE_HOE);
                this.add(Material.IRON_HOE);
                this.add(Material.GOLD_HOE);
                this.add(Material.DIAMOND_HOE);
                this.add(Material.WOOD_PICKAXE);
                this.add(Material.STONE_PICKAXE);
                this.add(Material.IRON_PICKAXE);
                this.add(Material.GOLD_PICKAXE);
                this.add(Material.DIAMOND_PICKAXE);
                this.add(Material.FLINT_AND_STEEL);
                this.add(Material.FISHING_ROD);
                this.add(Material.SHEARS);
            }
        };
        illegalBlocks = new HashSet<>() {
            {
                this.add(Material.BEDROCK);
                this.add(Material.ENDER_PORTAL_FRAME);
                this.add(Material.BARRIER);
                this.add(Material.STRUCTURE_BLOCK);
                this.add(Material.STRUCTURE_VOID);
                this.add(Material.MOB_SPAWNER);
                this.add(Material.MONSTER_EGG);
                this.add(Material.COMMAND);
                this.add(Material.COMMAND_CHAIN);
                this.add(Material.COMMAND_MINECART);
                this.add(Material.COMMAND_REPEATING);
            }
        };
    }
}
