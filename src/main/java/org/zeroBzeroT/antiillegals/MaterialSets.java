package org.zeroBzeroT.antiillegals;

import org.bukkit.Material;

import java.util.HashSet;

public class MaterialSets {
    public static final HashSet<Material> armorMaterials = new HashSet<Material>() {{
        add(Material.CHAINMAIL_HELMET);
        add(Material.CHAINMAIL_CHESTPLATE);
        add(Material.CHAINMAIL_LEGGINGS);
        add(Material.CHAINMAIL_BOOTS);

        add(Material.IRON_HELMET);
        add(Material.IRON_CHESTPLATE);
        add(Material.IRON_LEGGINGS);
        add(Material.IRON_BOOTS);

        add(Material.GOLD_HELMET);
        add(Material.GOLD_CHESTPLATE);
        add(Material.GOLD_LEGGINGS);
        add(Material.GOLD_BOOTS);

        add(Material.DIAMOND_HELMET);
        add(Material.DIAMOND_CHESTPLATE);
        add(Material.DIAMOND_LEGGINGS);
        add(Material.DIAMOND_BOOTS);
    }};

    public static final HashSet<Material> weaponMaterials = new HashSet<Material>() {{
        add(Material.WOOD_AXE);
        add(Material.STONE_AXE);
        add(Material.IRON_AXE);
        add(Material.GOLD_AXE);
        add(Material.DIAMOND_AXE);

        add(Material.WOOD_SWORD);
        add(Material.STONE_SWORD);
        add(Material.IRON_SWORD);
        add(Material.GOLD_SWORD);
        add(Material.DIAMOND_SWORD);

        add(Material.BOW);
    }};

    public static final HashSet<Material> illegalBlocks = new HashSet<Material>() {{
        add(Material.BEDROCK);
        add(Material.ENDER_PORTAL_FRAME);
        add(Material.BARRIER);
        add(Material.STRUCTURE_BLOCK);
        add(Material.STRUCTURE_VOID);
        add(Material.MOB_SPAWNER);
        add(Material.MONSTER_EGG);
        add(Material.COMMAND);
        add(Material.COMMAND_CHAIN);
        add(Material.COMMAND_MINECART);
        add(Material.COMMAND_REPEATING);
    }};
}
