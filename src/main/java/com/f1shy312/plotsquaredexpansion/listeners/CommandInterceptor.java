package com.f1shy312.plotsquaredexpansion.listeners;

import com.f1shy312.plotsquaredexpansion.PlotSquaredExpansion;
import com.f1shy312.plotsquaredexpansion.managers.ConfigManager;
import com.f1shy312.plotsquaredexpansion.managers.MessageManager;
import com.f1shy312.plotsquaredexpansion.utils.PlotUtils;
import com.plotsquared.core.plot.Plot;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.server.TabCompleteEvent;

import java.util.List;

public class CommandInterceptor implements Listener {
    
    private final PlotSquaredExpansion plugin;
    private final ConfigManager configManager;
    private final MessageManager messageManager;
    
    public CommandInterceptor(PlotSquaredExpansion plugin) {
        this.plugin = plugin;
        this.configManager = plugin.getConfigManager();
        this.messageManager = plugin.getMessageManager();
    }
    
    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerCommand(PlayerCommandPreprocessEvent event) {
        String message = event.getMessage().toLowerCase();
        
        // Check if it's a /plot settings or /p settings command
        if (message.equals("/plot settings") || message.startsWith("/plot settings ") ||
            message.equals("/p settings") || message.startsWith("/p settings ")) {
            event.setCancelled(true);
            handleSettingsCommand(event.getPlayer());
        }
    }
    
    @EventHandler(priority = EventPriority.HIGH)
    public void onTabComplete(TabCompleteEvent event) {
        String buffer = event.getBuffer().toLowerCase();
        
        // Add "settings" to tab completions for /plot and /p commands
        if (buffer.equals("/plot ") || buffer.equals("/plot s") || buffer.startsWith("/plot se") ||
            buffer.equals("/p ") || buffer.equals("/p s") || buffer.startsWith("/p se")) {
            
            // Only add if player has permission and the completion doesn't already exist
            if (event.getSender() instanceof Player) {
                Player player = (Player) event.getSender();
                if (player.hasPermission("plotsquaredexpansion.settings")) {
                    List<String> completions = event.getCompletions();
                    if (!completions.contains("settings")) {
                        completions.add("settings");
                    }
                }
            }
        }
    }
    
    private void handleSettingsCommand(Player player) {
        // Permission check
        if (!player.hasPermission("plotsquaredexpansion.settings")) {
            messageManager.sendMessage(player, "general.no-permission");
            return;
        }
        
        // PlotSquared check
        if (!PlotUtils.isPlotSquaredAvailable()) {
            messageManager.sendMessage(player, "general.plotsquared-not-found");
            return;
        }
        
        // Get plot
        Plot plot = PlotUtils.getPlotAtLocation(player);
        if (plot == null) {
            messageManager.sendMessage(player, "plot.not-on-plot");
            return;
        }
        
        // Check if plot is unclaimed (no owner)
        if (!plot.hasOwner()) {
            messageManager.sendMessage(player, "plot.unclaimed");
            return;
        }
        
        // Plot permissions
        boolean isOwner = PlotUtils.isPlotOwner(player, plot);
        boolean canModify = PlotUtils.canModifyPlot(player, plot);
        boolean isAdmin = player.hasPermission("plotsquaredexpansion.admin");
        
        if (configManager.requirePlotOwner() && !isOwner && !isAdmin) {
            messageManager.sendMessage(player, "plot.not-owner");
            return;
        }
        
        if (!configManager.allowTrustedUsers() && !isOwner && !isAdmin) {
            messageManager.sendMessage(player, "plot.not-owner");
            return;
        }
        
        if (!canModify && !isAdmin) {
            messageManager.sendMessage(player, "plot.not-trusted");
            return;
        }
        
        // Open GUI
        plugin.getPlotSettingsGUI().openMainGUI(player, plot);
    }
} 