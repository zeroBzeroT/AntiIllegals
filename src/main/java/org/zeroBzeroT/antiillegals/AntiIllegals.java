package org.zeroBzeroT.antiillegals;

import org.bstats.bukkit.Metrics;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.stream.Collectors;

public class AntiIllegals extends JavaPlugin {
    public static AntiIllegals INSTANCE;

    /**
     * constructor
     */
    public AntiIllegals() {
        // save the plugin instance for logging
        INSTANCE = this;

        getConfig().addDefault("bStats", true);
        getConfig().addDefault("nameColors", false);
        getConfig().addDefault("unbreakables", true);
        getConfig().addDefault("flightTime", true);
        getConfig().addDefault("durability", true);
        getConfig().addDefault("illegalBlocks", true);
        getConfig().addDefault("nbtFurnaces", true);
        getConfig().addDefault("overstackedItems", true);
        getConfig().addDefault("allowCollectibles", true);
        getConfig().addDefault("conflictingEnchantments", true);
        getConfig().addDefault("maxEnchantments", true);
        getConfig().addDefault("shulkerBoxes", true);
        getConfig().addDefault("maxBooksInShulker", 10);
        getConfig().addDefault("maxBooksShulkersInInventory", 3);
        getConfig().addDefault("attributeModifiers", true);
        getConfig().addDefault("customPotionEffects", true);
        getConfig().addDefault("illegalMaterials", MaterialSets.ILLEGAL_BLOCKS.stream().map(Material::toString).collect(Collectors.toList()));

        getConfig().options().copyDefaults(true);

        saveConfig();
    }

    /**
     * fired when the plugin gets enabled
     */
    public void onEnable() {
        this.getServer().getPluginManager().registerEvents(new Events(), this);

        log("bStats", "" + getConfig().getBoolean("bStats"));
        log("nameColors", "" + getConfig().getBoolean("nameColors"));
        log("unbreakables", "" + getConfig().getBoolean("unbreakables"));
        log("durability", "" + getConfig().getBoolean("durability"));
        log("illegalBlocks", "" + getConfig().getBoolean("illegalBlocks"));
        log("nbtFurnaces", "" + getConfig().getBoolean("nbtFurnaces"));
        log("overstackedItems", "" + getConfig().getBoolean("overstackedItems"));
        log("allowCollectibles", "" + getConfig().getBoolean("allowCollectibles"));
        log("conflictingEnchantments", "" + getConfig().getBoolean("conflictingEnchantments"));
        log("maxEnchantments", "" + getConfig().getBoolean("maxEnchantments"));
        log("shulkerBoxes", "" + getConfig().getBoolean("shulkerBoxes"));
        log("maxBooksInShulker", "" + getConfig().getInt("maxBooksInShulker"));
        log("maxBooksShulkersInInventory", "" + getConfig().getInt("maxBooksShulkersInInventory"));
        log("attributeModifiers", "" + getConfig().getBoolean("attributeModifiers"));
        log("customPotionEffects", "" + getConfig().getBoolean("customPotionEffects"));

        MaterialSets.ILLEGAL_BLOCKS = getConfig().getStringList("illegalMaterials").stream().map(Material::getMaterial).collect(Collectors.toCollection(HashSet::new));

        log("illegalMaterials", MaterialSets.ILLEGAL_BLOCKS.stream().map(Material::toString).collect(Collectors.joining(", ")));

        // Load Plugin Metrics
        if (getConfig().getBoolean("bStats")) {
            new Metrics(this, 16227);
        }
    }

    @NotNull
    public static FileConfiguration config() {
        return INSTANCE.getConfig();
    }

    /**
     * log formatting and output
     */
    public static void log(@NotNull final String module, @NotNull final String message) {
        AntiIllegals.INSTANCE.getLogger().info("§a[" + module + "] §e" + message + "§r");
    }

}
