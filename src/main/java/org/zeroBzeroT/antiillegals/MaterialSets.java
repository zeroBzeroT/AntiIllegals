package org.zeroBzeroT.antiillegals;

import org.bukkit.Material;

import java.util.HashSet;

// TODO: Add 1.20.* materials
public class MaterialSets {
    public static final HashSet<Material> armorMaterials;
    public static final HashSet<Material> weaponMaterials;
    public static final HashSet<Material> toolsMaterials;
    public static HashSet<Material> illegalBlocks;

    static {
        armorMaterials = new HashSet<Material>() {
            {
                this.add(Material.CHAINMAIL_HELMET);
                this.add(Material.CHAINMAIL_CHESTPLATE);
                this.add(Material.CHAINMAIL_LEGGINGS);
                this.add(Material.CHAINMAIL_BOOTS);
                this.add(Material.IRON_HELMET);
                this.add(Material.IRON_CHESTPLATE);
                this.add(Material.IRON_LEGGINGS);
                this.add(Material.IRON_BOOTS);
                this.add(Material.GOLDEN_HELMET);
                this.add(Material.GOLDEN_CHESTPLATE);
                this.add(Material.GOLDEN_LEGGINGS);
                this.add(Material.GOLDEN_BOOTS);
                this.add(Material.DIAMOND_HELMET);
                this.add(Material.DIAMOND_CHESTPLATE);
                this.add(Material.DIAMOND_LEGGINGS);
                this.add(Material.DIAMOND_BOOTS);
                this.add(Material.ELYTRA);
                this.add(Material.BRUSH);
            }
        };
        weaponMaterials = new HashSet<Material>() {
            {
                this.add(Material.WOODEN_AXE);
                this.add(Material.STONE_AXE);
                this.add(Material.IRON_AXE);
                this.add(Material.GOLDEN_AXE);
                this.add(Material.DIAMOND_AXE);
                this.add(Material.WOODEN_SWORD);
                this.add(Material.STONE_SWORD);
                this.add(Material.IRON_SWORD);
                this.add(Material.GOLDEN_SWORD);
                this.add(Material.DIAMOND_SWORD);
                this.add(Material.BOW);
            }
        };
        toolsMaterials = new HashSet<Material>() {
            {
                this.add(Material.WOODEN_SHOVEL);
                this.add(Material.STONE_SHOVEL);
                this.add(Material.IRON_SHOVEL);
                this.add(Material.GOLDEN_SHOVEL);
                this.add(Material.DIAMOND_SHOVEL);
                this.add(Material.WOODEN_HOE);
                this.add(Material.STONE_HOE);
                this.add(Material.IRON_HOE);
                this.add(Material.GOLDEN_HOE);
                this.add(Material.DIAMOND_HOE);
                this.add(Material.WOODEN_PICKAXE);
                this.add(Material.STONE_PICKAXE);
                this.add(Material.IRON_PICKAXE);
                this.add(Material.GOLDEN_PICKAXE);
                this.add(Material.DIAMOND_PICKAXE);
                this.add(Material.FLINT_AND_STEEL);
                this.add(Material.FISHING_ROD);
                this.add(Material.SHEARS);
            }
        };
        illegalBlocks = new HashSet<Material>() {
            {
                this.add(Material.BEDROCK);
                this.add(Material.END_PORTAL_FRAME);
                this.add(Material.BARRIER);
                this.add(Material.STRUCTURE_BLOCK);
                this.add(Material.STRUCTURE_VOID);
                this.add(Material.SPAWNER);
                this.add(Material.LEGACY_MONSTER_EGG);
                this.add(Material.COMMAND_BLOCK);
                this.add(Material.CHAIN_COMMAND_BLOCK);
                this.add(Material.COMMAND_BLOCK_MINECART);
                this.add(Material.REPEATING_COMMAND_BLOCK);
                this.add(Material.JIGSAW);
                this.add(Material.LIGHT);
                this.add(Material.REINFORCED_DEEPSLATE);
            }
        };
    }
}
