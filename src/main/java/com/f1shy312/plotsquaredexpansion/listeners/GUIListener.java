package com.f1shy312.plotsquaredexpansion.listeners;

import com.f1shy312.plotsquaredexpansion.PlotSquaredExpansion;
import com.f1shy312.plotsquaredexpansion.managers.ConfigManager;
import com.f1shy312.plotsquaredexpansion.managers.MessageManager;
import com.f1shy312.plotsquaredexpansion.gui.PlotSettingsGUI;
import com.f1shy312.plotsquaredexpansion.utils.PlotUtils;
import com.plotsquared.core.plot.Plot;
import com.plotsquared.core.plot.flag.PlotFlag;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.ChatColor;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;

public class GUIListener implements Listener {
    
    private final PlotSquaredExpansion plugin;
    private final ConfigManager configManager;
    private final MessageManager messageManager;
    private final PlotSettingsGUI plotSettingsGUI;
    private final ChatListener chatListener;
    
    public GUIListener(PlotSquaredExpansion plugin) {
        this.plugin = plugin;
        this.configManager = plugin.getConfigManager();
        this.messageManager = plugin.getMessageManager();
        this.plotSettingsGUI = plugin.getPlotSettingsGUI();
        this.chatListener = plugin.getChatListener();
    }
    
    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player)) return;
        
        Player player = (Player) event.getWhoClicked();
        Inventory inventory = event.getInventory();
        
        if (!plotSettingsGUI.isPluginGUI(inventory)) return;
        
        event.setCancelled(true);
        
        ItemStack clickedItem = event.getCurrentItem();
        if (clickedItem == null || clickedItem.getType() == Material.AIR) return;
        
        String title = event.getView().getTitle();
        int slot = event.getSlot();
        
        Plot plot = PlotUtils.getPlotAtLocation(player);
        if (plot == null) {
            messageManager.sendMessage(player, "errors.no-plot");
            return;
        }
        
        // nav buttons first
        if (handleNavButtons(player, plot, title, clickedItem, slot)) {
            return;
        }
        
        // main menu
        if (title.contains("Plot Settings")) {
            switch (slot) {
                case 11: // bool
                    plotSettingsGUI.openBooleanFlagsGUI(player, plot);
                    break;
                case 13: // custom
                    plotSettingsGUI.openCustomFlagsGUI(player, plot);
                    break;
                case 15: // search
                    chatListener.startSearchInput(player, plot);
                    player.closeInventory();
                    break;
                case 26: // close
                    player.closeInventory();
                    break;
            }
            return;
        }
        
        // flag clicks
        handleClickOnFlag(player, plot, title, clickedItem, slot);
    }
    
    // nav & special buttons
    private boolean handleNavButtons(Player player, Plot plot, String title, ItemStack clickedItem, int slot) {
        String itemName = clickedItem.getItemMeta() != null ? 
            ChatColor.stripColor(clickedItem.getItemMeta().getDisplayName()) : "";
        
        // back btns
        if (slot == 49 && itemName.contains("Back")) {
            plotSettingsGUI.openMainGUI(player, plot);
            return true;
        }
        
        // pagination for main boolean/custom flags
        if (title.contains("Boolean Flags") && !title.contains(":")) {
            if (slot == 45 && itemName.contains("Previous Page")) {
                int page = extractPageFromLore(clickedItem, false);
                plotSettingsGUI.openBooleanFlagsGUI(player, plot, page);
                return true;
            }
            if (slot == 53 && itemName.contains("Next Page")) {
                int page = extractPageFromLore(clickedItem, true);
                plotSettingsGUI.openBooleanFlagsGUI(player, plot, page);
                return true;
            }
        }
        
        if (title.contains("Custom Flags") && !title.contains(":")) {
            if (slot == 45 && itemName.contains("Previous Page")) {
                int page = extractPageFromLore(clickedItem, false);
                plotSettingsGUI.openCustomFlagsGUI(player, plot, page);
                return true;
            }
            if (slot == 53 && itemName.contains("Next Page")) {
                int page = extractPageFromLore(clickedItem, true);
                plotSettingsGUI.openCustomFlagsGUI(player, plot, page);
                return true;
            }
        }
        
        // search results nav & switching
        if (title.contains(":")) {
            String searchQuery = extractSearchQuery(title);
            List<String> filteredFlags = getStoredSearchResults(player, searchQuery);
            
            if (filteredFlags != null) {
                // pagination
                if ((slot == 47 && itemName.contains("Previous Page")) || 
                    (slot == 51 && itemName.contains("Next Page"))) {
                    int page = extractPageFromLore(clickedItem, itemName.contains("Next"));
                    
                    if (title.contains("Boolean Flags:")) {
                        plotSettingsGUI.openFilteredBooleanFlagsGUI(player, plot, filteredFlags, searchQuery, page);
                    } else if (title.contains("Custom Flags:")) {
                        plotSettingsGUI.openFilteredCustomFlagsGUI(player, plot, filteredFlags, searchQuery, page);
                    }
                    return true;
                }
                
                // type switching
                if (slot == 45) {
                    if (itemName.contains("Switch to Custom Flags")) {
                        plotSettingsGUI.openFilteredCustomFlagsGUI(player, plot, filteredFlags, searchQuery);
                        return true;
                    } else if (itemName.contains("Switch to Boolean Flags")) {
                        plotSettingsGUI.openFilteredBooleanFlagsGUI(player, plot, filteredFlags, searchQuery);
                        return true;
                    }
                }
            }
        }
        
        return false;
    }
    
    // extract page
    private int extractPageFromLore(ItemStack item, boolean isNext) {
        if (item.getItemMeta() == null || item.getItemMeta().getLore() == null) {
            return isNext ? 1 : 0;
        }
        
        for (String lore : item.getItemMeta().getLore()) {
            String stripped = ChatColor.stripColor(lore);
            if (stripped.contains("Click to go to page")) {
                try {
                    String[] parts = stripped.split("page ");
                    if (parts.length > 1) {
                        return Integer.parseInt(parts[1].trim()) - 1;
                    }
                } catch (NumberFormatException ignored) {}
            }
        }
        
        return isNext ? 1 : 0;
    }
    
    // extract search query
    private String extractSearchQuery(String title) {
        int start = title.indexOf("'");
        int end = title.lastIndexOf("'");
        if (start != -1 && end != -1 && start < end) {
            return title.substring(start + 1, end);
        }
        return "";
    }
    
    // stored search results
    private List<String> getStoredSearchResults(Player player, String searchQuery) {
        // regen search results for now
        return performSearch(searchQuery);
    }
    
    // search logic
    private List<String> performSearch(String query) {
        List<String> results = new ArrayList<>();
        List<String> booleanFlags = configManager.getBooleanFlags();
        List<String> customFlags = configManager.getCustomFlags();
        
        String lowerQuery = query.toLowerCase();
        
        // search boolean
        for (String flagName : booleanFlags) {
            if (flagName.toLowerCase().contains(lowerQuery)) {
                results.add(flagName);
            }
        }
        
        // search custom
        for (String flagName : customFlags) {
            if (flagName.toLowerCase().contains(lowerQuery)) {
                results.add(flagName);
            }
        }
        
        return results;
    }
    
    // handle flag click
    private void handleClickOnFlag(Player player, Plot plot, String title, ItemStack clickedItem, int slot) {
        // skip nav slots
        if (slot >= 45) return;
        
        String flagName = extractFlagNameFromItem(clickedItem);
        if (flagName == null) return;
        
        Map<String, Class<? extends PlotFlag<?, ?>>> flagClasses = PlotUtils.getCommonFlags();
        Class<? extends PlotFlag<?, ?>> flagClass = flagClasses.get(flagName);
        
        if (flagClass == null) return;
        
        if (PlotUtils.isBooleanFlag(flagClass)) {
            // toggle boolean
            Object currentValue = PlotUtils.getFlagValue(plot, flagClass);
            boolean currentBool = currentValue instanceof Boolean && (Boolean) currentValue;
            
            if (PlotUtils.setFlagValue(plot, flagName, !currentBool)) {
                String newStatus = !currentBool ? 
                    messageManager.getMessage("flags.enabled") : 
                    messageManager.getMessage("flags.disabled");
                messageManager.sendMessage(player, "flags.changed", 
                    "flag", flagName, "value", newStatus);
                
                // refresh gui
                if (configManager.shouldAutoRefresh()) {
                    if (title.contains("Boolean Flags:")) {
                        String searchQuery = extractSearchQuery(title);
                        List<String> filteredFlags = getStoredSearchResults(player, searchQuery);
                        plotSettingsGUI.openFilteredBooleanFlagsGUI(player, plot, filteredFlags, searchQuery);
                    } else {
                        plotSettingsGUI.openBooleanFlagsGUI(player, plot);
                    }
                }
            } else {
                messageManager.sendMessage(player, "errors.flag-change-failed");
            }
        } else {
            // custom flag input
            chatListener.startCustomFlagInput(player, plot, flagName);
            player.closeInventory();
        }
    }
    
    // extract flag name
    private String extractFlagNameFromItem(ItemStack item) {
        if (item.getItemMeta() == null) return null;
        
        String displayName = item.getItemMeta().getDisplayName();
        if (displayName == null) return null;
        
        // remove colors & extract name
        String stripped = ChatColor.stripColor(displayName);
        return stripped.trim();
    }
} 