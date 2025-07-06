package org.zeroBzeroT.antiillegals;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.apache.fory.util.function.ToByteFunction;
import org.bstats.bukkit.Metrics;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.zeroBzeroT.antiillegals.helpers.MaterialHelper;

import java.util.stream.Collectors;

public class AntiIllegals extends JavaPlugin {
    public static AntiIllegals INSTANCE;

    /**
     * constructor
     */
    public AntiIllegals() {
        // save the plugin instance for logging
        INSTANCE = this;

        config().options().copyDefaults();
        saveDefaultConfig();
    }

    /**
     * fired when the plugin gets enabled
     */
    public void onEnable() {
        System.out.println(org.apache.fory.util.function.ToByteFunction.class.getName());

        getServer().getPluginManager().registerEvents(new Events(), this);

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

        MaterialHelper.loadIllegalMaterials();

        log("illegalMaterials", MaterialHelper.requireIllegalMaterials()
                .stream()
                .map(Material::toString)
                .collect(Collectors.joining(", ")));

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
        final Component component = Component.text("[AntiIllegals] ")
                .append(Component.text("[" + module + "] ").color(NamedTextColor.GREEN))
                .append(Component.text(message).color(NamedTextColor.YELLOW));

        Bukkit.getConsoleSender().sendMessage(component);
    }

}
