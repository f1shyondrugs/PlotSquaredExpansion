package com.f1shy312.plotsquaredexpansion;

import com.f1shy312.plotsquaredexpansion.gui.PlotSettingsGUI;
import com.f1shy312.plotsquaredexpansion.listeners.ChatListener;
import com.f1shy312.plotsquaredexpansion.listeners.GUIListener;
import com.f1shy312.plotsquaredexpansion.listeners.CommandInterceptor;
import com.f1shy312.plotsquaredexpansion.managers.ConfigManager;
import com.f1shy312.plotsquaredexpansion.managers.MessageManager;
import com.f1shy312.plotsquaredexpansion.utils.PlotUtils;
import org.bukkit.plugin.java.JavaPlugin;

public class PlotSquaredExpansion extends JavaPlugin {
    
    private static PlotSquaredExpansion instance;
    private ConfigManager configManager;
    private MessageManager messageManager;
    private PlotSettingsGUI plotSettingsGUI;
    private ChatListener chatListener;
    private CommandInterceptor commandInterceptor;
    
    @Override
    public void onEnable() {
        instance = this;
        
        // setup managers
        this.configManager = new ConfigManager(this);
        this.messageManager = new MessageManager(this);
        this.plotSettingsGUI = new PlotSettingsGUI(this);
        this.chatListener = new ChatListener(this);
        this.commandInterceptor = new CommandInterceptor(this);
        
        configManager.loadConfigs();
        
        // plotsquared check
        checkPlotSquaredIntegration();
        
        registerCommands();
        registerListeners();
        
        getLogger().info("PlotSquaredExpansion has been enabled!");
        getLogger().info("Use /p settings or /plot settings to open the GUI");
    }
    
    @Override
    public void onDisable() {
        getLogger().info("PlotSquaredExpansion has been disabled!");
    }
    
    private void checkPlotSquaredIntegration() {
        if (PlotUtils.isPlotSquaredAvailable()) {
            getLogger().info("✓ PlotSquared integration is active - all features available");
        } else {
            getLogger().warning("⚠ PlotSquared not found - plugin will work in limited mode");
            getLogger().warning("  → Flag changes will not be saved to plots");
            getLogger().warning("  → Install PlotSquared for full functionality");
        }
    }
    
    private void registerCommands() {
        // No commands registered - using CommandInterceptor instead
        // This allows PlotSquared to maintain full control of /plot and /p commands
        // while we intercept only the "settings" subcommand
    }
    
    private void registerListeners() {
        getServer().getPluginManager().registerEvents(new GUIListener(this), this);
        getServer().getPluginManager().registerEvents(chatListener, this);
        getServer().getPluginManager().registerEvents(commandInterceptor, this);
    }
    
    // getters
    public static PlotSquaredExpansion getInstance() {
        return instance;
    }
    
    public ConfigManager getConfigManager() {
        return configManager;
    }
    
    public MessageManager getMessageManager() {
        return messageManager;
    }
    
    public PlotSettingsGUI getPlotSettingsGUI() {
        return plotSettingsGUI;
    }
    
    public ChatListener getChatListener() {
        return chatListener;
    }
    
    public CommandInterceptor getCommandInterceptor() {
        return commandInterceptor;
    }
} 