package org.zeroBzeroT.antiillegals.helpers;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.zeroBzeroT.antiillegals.AntiIllegals;

import java.util.*;
import java.util.stream.Collectors;

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
            Material.GOLDEN_HELMET,
            Material.GOLDEN_CHESTPLATE,
            Material.GOLDEN_LEGGINGS,
            Material.GOLDEN_BOOTS,
            Material.DIAMOND_HELMET,
            Material.DIAMOND_CHESTPLATE,
            Material.DIAMOND_LEGGINGS,
            Material.DIAMOND_BOOTS,
            Material.LEATHER_HELMET,
            Material.LEATHER_CHESTPLATE,
            Material.LEATHER_LEGGINGS,
            Material.LEATHER_BOOTS,
            Material.ELYTRA,
            Material.NETHERITE_HELMET,
            Material.NETHERITE_CHESTPLATE,
            Material.NETHERITE_LEGGINGS,
            Material.NETHERITE_BOOTS,
            Material.TURTLE_HELMET
    );

    @NotNull
    public static final Set<Material> WEAPON_MATERIALS = Set.of(
            Material.WOODEN_AXE,
            Material.STONE_AXE,
            Material.IRON_AXE,
            Material.GOLDEN_AXE,
            Material.DIAMOND_AXE,
            Material.WOODEN_SWORD,
            Material.STONE_SWORD,
            Material.IRON_SWORD,
            Material.GOLDEN_SWORD,
            Material.DIAMOND_SWORD,
            Material.BOW,
            Material.NETHERITE_AXE,
            Material.NETHERITE_SWORD,
            Material.CROSSBOW,
            Material.MACE,
            Material.TRIDENT
    );

    @NotNull
    public static final Set<Material> TOOLS_MATERIALS = Set.of(
            Material.WOODEN_SHOVEL,
            Material.STONE_SHOVEL,
            Material.IRON_SHOVEL,
            Material.GOLDEN_SHOVEL,
            Material.DIAMOND_SHOVEL,
            Material.WOODEN_HOE,
            Material.STONE_HOE,
            Material.IRON_HOE,
            Material.GOLDEN_HOE,
            Material.DIAMOND_HOE,
            Material.WOODEN_PICKAXE,
            Material.STONE_PICKAXE,
            Material.IRON_PICKAXE,
            Material.GOLDEN_PICKAXE,
            Material.DIAMOND_PICKAXE,
            Material.FLINT_AND_STEEL,
            Material.FISHING_ROD,
            Material.SHEARS,
            Material.SHIELD,
            Material.NETHERITE_PICKAXE,
            Material.NETHERITE_SHOVEL,
            Material.NETHERITE_HOE,
            Material.BRUSH,
            Material.WARPED_FUNGUS_ON_A_STICK
    );

    @Nullable
    public static Set<Material> ILLEGAL_MATERIALS;

    @NotNull
    private static final Set<Material> NON_SHULKER_CONTAINERS = Set.of(
            Material.BEACON,
            Material.BREWING_STAND,
            Material.CHEST,
            Material.TRAPPED_CHEST,
            Material.DISPENSER,
            Material.DROPPER,
            Material.FURNACE,
            Material.HOPPER,
            Material.BARREL,
            Material.BUNDLE,
            Material.SMOKER,
            Material.BLAST_FURNACE,
            Material.MINECART_WITH_HOPPER,
            Material.MINECART_WITH_CHEST
    );

    public static void loadIllegalMaterials() {
        ILLEGAL_MATERIALS = AntiIllegals.config().getStringList("illegalMaterials")
                .stream()
                .map(MaterialHelper::matchMaterialsByPattern)
                .flatMap(Collection::stream)
                .collect(Collectors.toSet());
    }

    @NotNull
    public static Set<Material> requireIllegalMaterials() {
        return Optional.ofNullable(ILLEGAL_MATERIALS).orElseThrow();
    }

    /**
     * finds a given material by its name. no wildcard support
     * @param name the name of the material to find (case-insensitive)
     * @return an optional material. present if the material was found by name, empty if not
     */
    @NotNull
    public static Optional<Material> findMaterial(@NotNull final String name) {
        return Arrays.stream(Material.values())
                .filter(m -> m.name().equalsIgnoreCase(name))
                .findAny();
    }

    /**
     * matches all materials which match the specified pattern by name
     * @param pattern the pattern to match materials for (case-insensitive)
     * @return the set of materials that matched the pattern
     */
    @NotNull
    public static Set<Material> matchMaterialsByPattern(@NotNull final String pattern) {
        return Arrays.stream(Material.values())
                .filter(m -> matchPattern(pattern.toLowerCase(), m.name().toLowerCase()))
                .collect(Collectors.toSet());
    }

    /**
     * slightly optimized version of matchMaterialsByPattern. if no wildcard match is used, no extra work is done.
     * @param pattern the pattern to match materials for (case-insensitive)
     * @return the set of materials that matched the pattern
     */
    @NotNull
    private static Set<Material> findMatchingMaterials(@NotNull final String pattern) {
        if (!pattern.contains("*")) {
            final Optional<Material> singleMatch = findMaterial(pattern);
            return singleMatch
                    .map(Collections::singleton)
                    .orElse(Collections.emptySet());
        }
        return matchMaterialsByPattern(pattern);
    }

    private static boolean matchPattern(@NotNull final String pattern, @NotNull final String str) {
        if (pattern.length() == 0 && str.length() == 0) return true;
        if (pattern.length() > 1 && pattern.charAt(0) == '*' && str.length() == 0) return false;

        if ((pattern.length() > 1 && pattern.charAt(0) == '?')
                || (pattern.length() != 0 && str.length() != 0 && pattern.charAt(0) == str.charAt(0)))
            return matchPattern(pattern.substring(1), str.substring(1));

        if (pattern.length() > 0 && pattern.charAt(0) == '*')
            return matchPattern(pattern.substring(1), str) || matchPattern(pattern, str.substring(1));
        return false;
    }

    public static boolean isIllegalMaterial(@NotNull final ItemStack itemStack) {
        return isIllegalMaterial(itemStack.getType());
    }

    public static boolean isIllegalMaterial(@NotNull final Material material) {
        if (ILLEGAL_MATERIALS == null)
            throw new IllegalStateException("The illegal materials list was not loaded yet!");

        return ILLEGAL_MATERIALS.contains(material);
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

    public static boolean isNonShulkerContainer(@NotNull final ItemStack itemStack) {
        return NON_SHULKER_CONTAINERS.contains(itemStack.getType());
    }

}
