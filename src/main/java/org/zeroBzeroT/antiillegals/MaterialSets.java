package org.zeroBzeroT.antiillegals;

import org.bukkit.Material;

import java.util.HashSet;

public class MaterialSets {
    public static final HashSet<Material> armorMaterials;
    public static final HashSet<Material> weaponMaterials;
    public static final HashSet<Material> illegalBlocks;

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
                this.add(Material.GOLD_HELMET);
                this.add(Material.GOLD_CHESTPLATE);
                this.add(Material.GOLD_LEGGINGS);
                this.add(Material.GOLD_BOOTS);
                this.add(Material.DIAMOND_HELMET);
                this.add(Material.DIAMOND_CHESTPLATE);
                this.add(Material.DIAMOND_LEGGINGS);
                this.add(Material.DIAMOND_BOOTS);
            }
        };
        weaponMaterials = new HashSet<Material>() {
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
        illegalBlocks = new HashSet<Material>() {
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
