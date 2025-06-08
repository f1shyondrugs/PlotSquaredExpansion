package com.f1shy312.plotsquaredexpansion.listeners;

import com.f1shy312.plotsquaredexpansion.PlotSquaredExpansion;
import com.f1shy312.plotsquaredexpansion.managers.ConfigManager;
import com.f1shy312.plotsquaredexpansion.managers.MessageManager;
import com.f1shy312.plotsquaredexpansion.utils.PlotUtils;
import com.plotsquared.core.plot.Plot;
import com.plotsquared.core.plot.flag.PlotFlag;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class ChatListener implements Listener {
    
    private final PlotSquaredExpansion plugin;
    private final ConfigManager configManager;
    private final MessageManager messageManager;
    
    // pending inputs
    private final Map<UUID, ChatInputData> pendingInputs = new HashMap<>();
    
    public ChatListener(PlotSquaredExpansion plugin) {
        this.plugin = plugin;
        this.configManager = plugin.getConfigManager();
        this.messageManager = plugin.getMessageManager();
    }
    
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        UUID playerId = player.getUniqueId();
        
        if (!pendingInputs.containsKey(playerId)) {
            return;
        }
        
        event.setCancelled(true);
        
        ChatInputData inputData = pendingInputs.get(playerId);
        String input = event.getMessage().trim();
        
        // cancel check
        if (messageManager.isCancelWord(input)) {
            pendingInputs.remove(playerId);
            messageManager.sendRawMessage(player, "chat-input.cancelled");
            return;
        }
        
        // handle input
        Bukkit.getScheduler().runTask(plugin, () -> {
            handleChatInput(player, inputData, input);
            pendingInputs.remove(playerId);
        });
    }
    
    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        // cleanup
        pendingInputs.remove(event.getPlayer().getUniqueId());
    }
    
    // search input
    public void startSearchInput(Player player, Plot plot) {
        UUID playerId = player.getUniqueId();
        ChatInputData inputData = new ChatInputData(InputType.SEARCH, plot, null);
        pendingInputs.put(playerId, inputData);
        
        messageManager.sendSearchPrompt(player);
    }
    
    // custom flag input
    public void startCustomFlagInput(Player player, Plot plot, String flagName) {
        UUID playerId = player.getUniqueId();
        ChatInputData inputData = new ChatInputData(InputType.CUSTOM_FLAG, plot, flagName);
        pendingInputs.put(playerId, inputData);
        
        messageManager.sendInputPrompt(player, flagName);
        messageManager.sendRawMessage(player, "chat-input.default-info");
    }
    
    private void handleChatInput(Player player, ChatInputData inputData, String input) {
        switch (inputData.getType()) {
            case SEARCH:
                handleSearchInput(player, inputData.getPlot(), input);
                break;
            case CUSTOM_FLAG:
                handleCustomFlagInput(player, inputData.getPlot(), inputData.getFlagName(), input);
                break;
        }
    }
    
    // search logic
    private void handleSearchInput(Player player, Plot plot, String query) {
        Map<String, Class<? extends PlotFlag<?, ?>>> allFlags = PlotUtils.getCommonFlags();
        List<String> booleanFlags = configManager.getBooleanFlags();
        List<String> customFlags = configManager.getCustomFlags();
        
        List<String> matchingBooleanFlags = new ArrayList<>();
        List<String> matchingCustomFlags = new ArrayList<>();
        
        // search boolean
        for (String flagName : booleanFlags) {
            if (flagName.toLowerCase().contains(query.toLowerCase())) {
                matchingBooleanFlags.add(flagName);
            }
        }
        
        // search custom
        for (String flagName : customFlags) {
            if (flagName.toLowerCase().contains(query.toLowerCase())) {
                matchingCustomFlags.add(flagName);
            }
        }
        
        int totalMatches = matchingBooleanFlags.size() + matchingCustomFlags.size();
        
        if (totalMatches == 0) {
            messageManager.sendRawMessage(player, "search.no-results", "query", query);
            return;
        }
        
        // feedback msg
        if (matchingBooleanFlags.size() > 0 && matchingCustomFlags.size() > 0) {
            messageManager.sendRawMessage(player, "search.mixed-results", 
                "total", String.valueOf(totalMatches),
                "query", query,
                "boolean", String.valueOf(matchingBooleanFlags.size()),
                "custom", String.valueOf(matchingCustomFlags.size()));
        } else if (matchingBooleanFlags.size() > 0) {
            messageManager.sendRawMessage(player, "search.boolean-results",
                "count", String.valueOf(matchingBooleanFlags.size()),
                "query", query);
        } else {
            messageManager.sendRawMessage(player, "search.custom-results",
                "count", String.valueOf(matchingCustomFlags.size()),
                "query", query);
        }
        
        // open gui
        List<String> allFilteredFlags = new ArrayList<>();
        allFilteredFlags.addAll(matchingBooleanFlags);
        allFilteredFlags.addAll(matchingCustomFlags);
        
        if (matchingBooleanFlags.size() > 0 && matchingCustomFlags.size() == 0) {
            plugin.getPlotSettingsGUI().openFilteredBooleanFlagsGUI(player, plot, allFilteredFlags, query);
        } else if (matchingCustomFlags.size() > 0 && matchingBooleanFlags.size() == 0) {
            plugin.getPlotSettingsGUI().openFilteredCustomFlagsGUI(player, plot, allFilteredFlags, query);
        } else {
            // mixed - show boolean first
            plugin.getPlotSettingsGUI().openFilteredBooleanFlagsGUI(player, plot, allFilteredFlags, query);
        }
    }
    
    // custom flag input
    private void handleCustomFlagInput(Player player, Plot plot, String flagName, String input) {
        Object value = validateAndConvertInput(flagName, input);
        
        if (value == null) {
            messageManager.sendRawMessage(player, "chat-input.invalid-value");
            return;
        }
        
        boolean success = PlotUtils.setFlagValue(plot, flagName, value);
        
        if (success) {
            messageManager.sendFlagSuccess(player, flagName, input);
            
            if (configManager.shouldAutoRefresh()) {
                plugin.getPlotSettingsGUI().openCustomFlagsGUI(player, plot);
            }
        } else {
            messageManager.sendFlagError(player, flagName, messageManager.getMessage("errors.set-flag-failed"));
        }
    }
    
    // validate input
    private Object validateAndConvertInput(String flagName, String input) {
        String trimmedInput = input.trim();
        if (trimmedInput.equalsIgnoreCase("DEFAULT") || trimmedInput.equalsIgnoreCase("NONE")) {
            return trimmedInput;
        }
        
        switch (flagName) {
            case "time":
                try {
                    int time = Integer.parseInt(trimmedInput);
                    if (time < 0 || time > 24000) {
                        return null;
                    }
                    return time;
                } catch (NumberFormatException e) {
                    return null;
                }
            
            case "weather":
                String weather = trimmedInput.toLowerCase();
                if ("clear".equals(weather) || "rain".equals(weather) || "thunder".equals(weather)) {
                    return weather;
                }
                return null;
            
            case "greeting":
            case "farewell":
            case "description":
                return trimmedInput.isEmpty() ? null : trimmedInput;
            
            default:
                return trimmedInput.isEmpty() ? null : trimmedInput;
        }
    }
    
    private static class ChatInputData {
        private final InputType type;
        private final Plot plot;
        private final String flagName;
        
        public ChatInputData(InputType type, Plot plot, String flagName) {
            this.type = type;
            this.plot = plot;
            this.flagName = flagName;
        }
        
        public InputType getType() { return type; }
        public Plot getPlot() { return plot; }
        public String getFlagName() { return flagName; }
    }
    
    private enum InputType {
        SEARCH,
        CUSTOM_FLAG
    }
} 