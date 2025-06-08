package com.f1shy312.plotsquaredexpansion.commands;

import com.f1shy312.plotsquaredexpansion.PlotSquaredExpansion;
import com.f1shy312.plotsquaredexpansion.managers.ConfigManager;
import com.f1shy312.plotsquaredexpansion.managers.MessageManager;
import com.f1shy312.plotsquaredexpansion.utils.PlotUtils;
import com.plotsquared.core.plot.Plot;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PlotSettingsCommand implements CommandExecutor, TabCompleter {
    
    private final PlotSquaredExpansion plugin;
    private final ConfigManager configManager;
    private final MessageManager messageManager;
    
    public PlotSettingsCommand(PlotSquaredExpansion plugin) {
        this.plugin = plugin;
        this.configManager = plugin.getConfigManager();
        this.messageManager = plugin.getMessageManager();
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        // players only
        if (!(sender instanceof Player)) {
            messageManager.sendMessage(sender, "general.player-only");
            return true;
        }
        
        Player player = (Player) sender;
        
        // check args
        if (args.length == 0 || !args[0].equalsIgnoreCase("settings")) {
            return false;
        }
        
        // perms
        if (!player.hasPermission("plotsquaredexpansion.settings")) {
            messageManager.sendMessage(player, "general.no-permission");
            return true;
        }
        
        // plotsquared check
        if (!PlotUtils.isPlotSquaredAvailable()) {
            messageManager.sendMessage(player, "general.plotsquared-not-found");
            return true;
        }
        
        // get plot
        Plot plot = PlotUtils.getPlotAtLocation(player);
        if (plot == null) {
            messageManager.sendMessage(player, "plot.not-on-plot");
            return true;
        }
        
        // plot perms
        boolean isOwner = PlotUtils.isPlotOwner(player, plot);
        boolean canModify = PlotUtils.canModifyPlot(player, plot);
        boolean isAdmin = player.hasPermission("plotsquaredexpansion.admin");
        
        if (configManager.requirePlotOwner() && !isOwner && !isAdmin) {
            messageManager.sendMessage(player, "plot.not-owner");
            return true;
        }
        
        if (!configManager.allowTrustedUsers() && !isOwner && !isAdmin) {
            messageManager.sendMessage(player, "plot.not-owner");
            return true;
        }
        
        if (!canModify && !isAdmin) {
            messageManager.sendMessage(player, "plot.not-trusted");
            return true;
        }
        
        // open gui
        plugin.getPlotSettingsGUI().openMainGUI(player, plot);
        return true;
    }
    
    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> completions = new ArrayList<>();
        
        if (args.length == 1) {
            // tab complete
            String partial = args[0].toLowerCase();
            if ("settings".startsWith(partial)) {
                completions.add("settings");
            }
        }
        
        return completions;
    }
} 