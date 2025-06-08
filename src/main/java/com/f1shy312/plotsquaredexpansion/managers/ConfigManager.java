package com.f1shy312.plotsquaredexpansion.managers;

import com.f1shy312.plotsquaredexpansion.PlotSquaredExpansion;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.List;

public class ConfigManager {
    
    private final PlotSquaredExpansion plugin;
    private FileConfiguration config;
    private FileConfiguration langConfig;
    private FileConfiguration flagsConfig;
    
    public ConfigManager(PlotSquaredExpansion plugin) {
        this.plugin = plugin;
    }
    
    public void loadConfigs() {
        // main config
        plugin.saveDefaultConfig();
        config = plugin.getConfig();
        
        // lang config
        langConfig = loadResourceConfig("lang.yml");
        
        // flags config
        flagsConfig = loadResourceConfig("flags.yml");
        
        plugin.getLogger().info("Configuration files loaded successfully!");
    }
    
    private FileConfiguration loadResourceConfig(String fileName) {
        File file = new File(plugin.getDataFolder(), fileName);
        
        if (!file.exists()) {
            try (InputStream inputStream = plugin.getResource(fileName)) {
                if (inputStream != null) {
                    Files.copy(inputStream, file.toPath());
                    plugin.getLogger().info("Created default " + fileName);
                } else {
                    plugin.getLogger().warning("Could not find default " + fileName + " in plugin resources!");
                    return new YamlConfiguration();
                }
            } catch (IOException e) {
                plugin.getLogger().severe("Failed to create " + fileName + ": " + e.getMessage());
                return new YamlConfiguration();
            }
        }
        
        return YamlConfiguration.loadConfiguration(file);
    }
    
    // config getters
    public String getGuiTitle() {
        return config.getString("gui.title", "&9Plot Settings");
    }
    
    public boolean shouldAutoRefresh() {
        return config.getBoolean("gui.auto-refresh", true);
    }
    
    public boolean shouldCloseAfterChange() {
        return config.getBoolean("gui.close-after-change", false);
    }
    
    public int getChatInputTimeout() {
        return config.getInt("chat-input.timeout", 30);
    }
    
    public boolean requirePlotOwner() {
        return config.getBoolean("permissions.require-plot-owner", true);
    }
    
    public boolean allowTrustedUsers() {
        return config.getBoolean("permissions.allow-trusted-users", true);
    }
    
    // flags stuff
    public List<String> getBooleanFlags() {
        return flagsConfig.getStringList("boolean_flags");
    }
    
    public List<String> getCustomFlags() {
        return flagsConfig.getStringList("custom_flags");
    }
    
    public FileConfiguration getLangConfig() {
        return langConfig;
    }
} 