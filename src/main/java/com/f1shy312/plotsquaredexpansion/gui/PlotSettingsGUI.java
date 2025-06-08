package com.f1shy312.plotsquaredexpansion.gui;

import com.f1shy312.plotsquaredexpansion.PlotSquaredExpansion;
import com.f1shy312.plotsquaredexpansion.managers.ConfigManager;
import com.f1shy312.plotsquaredexpansion.managers.MessageManager;
import com.f1shy312.plotsquaredexpansion.utils.PlotUtils;
import com.plotsquared.core.plot.Plot;
import com.plotsquared.core.plot.flag.PlotFlag;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class PlotSettingsGUI {
    
    private final PlotSquaredExpansion plugin;
    private final ConfigManager configManager;
    private final MessageManager messageManager;
    
    public PlotSettingsGUI(PlotSquaredExpansion plugin) {
        this.plugin = plugin;
        this.configManager = plugin.getConfigManager();
        this.messageManager = plugin.getMessageManager();
    }
    
    // main gui
    public void openMainGUI(Player player, Plot plot) {
        String title = messageManager.colorize(configManager.getGuiTitle());
        Inventory gui = Bukkit.createInventory(null, 27, title);
        
        // bool flag s
        ItemStack booleanItem = createGuiItem(
            Material.GREEN_STAINED_GLASS_PANE,
            messageManager.getMessage("gui.items.boolean-flags.name"),
            configManager.getLangConfig().getStringList("gui.items.boolean-flags.lore")
        );
        gui.setItem(11, booleanItem);
        
        // custom flags
        ItemStack customItem = createGuiItem(
            Material.BLUE_STAINED_GLASS_PANE,
            messageManager.getMessage("gui.items.custom-flags.name"),
            configManager.getLangConfig().getStringList("gui.items.custom-flags.lore")
        );
        gui.setItem(13, customItem);
        
        // search
        ItemStack searchItem = createGuiItem(
            Material.YELLOW_STAINED_GLASS_PANE,
            messageManager.getMessage("gui.items.search-flags.name"),
            configManager.getLangConfig().getStringList("gui.items.search-flags.lore")
        );
        gui.setItem(15, searchItem);
        
        // close btn
        ItemStack closeItem = createGuiItem(
            Material.RED_STAINED_GLASS_PANE,
            messageManager.getMessage("gui.items.close.name"),
            configManager.getLangConfig().getStringList("gui.items.close.lore")
        );
        gui.setItem(26, closeItem);
        
        player.openInventory(gui);
    }
    
    public void openBooleanFlagsGUI(Player player, Plot plot) {
        openBooleanFlagsGUI(player, plot, 0);
    }
    
    // bool flags w/ pages
    public void openBooleanFlagsGUI(Player player, Plot plot, int page) {
        String title = messageManager.colorize(messageManager.getMessage("gui.title.boolean"));
        Inventory gui = Bukkit.createInventory(null, 54, title);
        
        List<String> booleanFlags = configManager.getBooleanFlags();
        Map<String, Class<? extends PlotFlag<?, ?>>> flagClasses = PlotUtils.getCommonFlags();
        
        List<String> validFlags = new ArrayList<>();
        for (String flagName : booleanFlags) {
            Class<? extends PlotFlag<?, ?>> flagClass = flagClasses.get(flagName);
            if (flagClass != null && PlotUtils.isBooleanFlag(flagClass)) {
                validFlags.add(flagName);
            }
        }
        
        // page switch logic
        int flagsPerPage = 45;
        int totalPages = (int) Math.ceil((double) validFlags.size() / flagsPerPage);
        int startIndex = page * flagsPerPage;
        int endIndex = Math.min(startIndex + flagsPerPage, validFlags.size());
        
        List<String> pageFlags = validFlags.subList(startIndex, endIndex);
        
        int slot = 0;
        int addedCount = 0;
        
        for (String flagName : pageFlags) {
            if (slot >= 45) break;
            
            Class<? extends PlotFlag<?, ?>> flagClass = flagClasses.get(flagName);
            
            if (flagClass != null && PlotUtils.isBooleanFlag(flagClass)) {
                Object value = PlotUtils.getFlagValue(plot, flagClass);
                boolean enabled = value instanceof Boolean && (Boolean) value;
                
                Material material = enabled ? Material.GREEN_CONCRETE : Material.RED_CONCRETE;
                String status = enabled ? 
                    messageManager.getMessage("flags.enabled") : 
                    messageManager.getMessage("flags.disabled");
                
                List<String> lore = new ArrayList<>();
                lore.add("");
                lore.add(messageManager.getMessage("flags.current-value", "value", status));
                lore.add("");
                lore.add(messageManager.getMessage("gui.flag.click-to-toggle"));
                
                ItemStack item = createGuiItem(material, messageManager.getMessage("gui.flag.name-color") + flagName, lore);
                gui.setItem(slot, item);
                slot++;
                addedCount++;
            }
        }
        
        // nav buttons
        if (page > 0) {
            ItemStack prevItem = createGuiItem(
                Material.ARROW,
                messageManager.getMessage("gui.items.previous-page.name"),
                messageManager.getLore("gui.items.previous-page.lore", "page", String.valueOf(page))
            );
            gui.setItem(45, prevItem);
        }
        
        if (page < totalPages - 1) {
            ItemStack nextItem = createGuiItem(
                Material.ARROW,
                messageManager.getMessage("gui.items.next-page.name"),
                messageManager.getLore("gui.items.next-page.lore", "page", String.valueOf(page + 2))
            );
            gui.setItem(53, nextItem);
        }
        
        // back btn
        ItemStack backItem = createGuiItem(
            Material.BARRIER,
            messageManager.getMessage("gui.items.back.name"),
            configManager.getLangConfig().getStringList("gui.items.back.lore")
        );
        gui.setItem(49, backItem);
        
        player.openInventory(gui);
    }
    
    public void openCustomFlagsGUI(Player player, Plot plot) {
        openCustomFlagsGUI(player, plot, 0);
    }
    
    // custom flags w/ pages
    public void openCustomFlagsGUI(Player player, Plot plot, int page) {
        String title = messageManager.colorize(messageManager.getMessage("gui.title.custom"));
        Inventory gui = Bukkit.createInventory(null, 54, title);
        
        List<String> customFlags = configManager.getCustomFlags();
        Map<String, Class<? extends PlotFlag<?, ?>>> flagClasses = PlotUtils.getCommonFlags();
        
        List<String> validFlags = new ArrayList<>();
        for (String flagName : customFlags) {
            Class<? extends PlotFlag<?, ?>> flagClass = flagClasses.get(flagName);
            if (flagClass != null && !PlotUtils.isBooleanFlag(flagClass)) {
                validFlags.add(flagName);
            }
        }
        
        int flagsPerPage = 45;
        int totalPages = (int) Math.ceil((double) validFlags.size() / flagsPerPage);
        int startIndex = page * flagsPerPage;
        int endIndex = Math.min(startIndex + flagsPerPage, validFlags.size());
        
        List<String> pageFlags = validFlags.subList(startIndex, endIndex);
        
        int slot = 0;
        int addedCount = 0;
        
        for (String flagName : pageFlags) {
            if (slot >= 45) break;
            
            Class<? extends PlotFlag<?, ?>> flagClass = flagClasses.get(flagName);
            if (flagClass != null && !PlotUtils.isBooleanFlag(flagClass)) {
                Object value = PlotUtils.getFlagValue(plot, flagClass);
                String valueStr = value != null ? value.toString() : messageManager.getMessage("flags.not-set");
                
                List<String> lore = new ArrayList<>();
                lore.add("");
                lore.add(messageManager.getMessage("flags.current-value", "value", valueStr));
                lore.add("");
                lore.add(messageManager.getMessage("gui.flag.click-to-edit"));
                
                ItemStack item = createGuiItem(Material.PAPER, messageManager.getMessage("gui.flag.name-color") + flagName, lore);
                gui.setItem(slot, item);
                slot++;
                addedCount++;
            }
        }
        
        if (page > 0) {
            ItemStack prevItem = createGuiItem(
                Material.ARROW,
                messageManager.getMessage("gui.items.previous-page.name"),
                messageManager.getLore("gui.items.previous-page.lore", "page", String.valueOf(page))
            );
            gui.setItem(45, prevItem);
        }
        
        if (page < totalPages - 1) {
            ItemStack nextItem = createGuiItem(
                Material.ARROW,
                messageManager.getMessage("gui.items.next-page.name"),
                messageManager.getLore("gui.items.next-page.lore", "page", String.valueOf(page + 2))
            );
            gui.setItem(53, nextItem);
        }
        
        ItemStack backItem = createGuiItem(
            Material.BARRIER,
            messageManager.getMessage("gui.items.back.name"),
            configManager.getLangConfig().getStringList("gui.items.back.lore")
        );
        gui.setItem(49, backItem);
        
        player.openInventory(gui);
    }
    
    // filtered bool flags
    public void openFilteredBooleanFlagsGUI(Player player, Plot plot, List<String> filteredFlags, String searchQuery) {
        openFilteredBooleanFlagsGUI(player, plot, filteredFlags, searchQuery, 0);
    }
    
    // filtered bool flags w/ pages
    public void openFilteredBooleanFlagsGUI(Player player, Plot plot, List<String> filteredFlags, String searchQuery, int page) {
        String title = messageManager.colorize(messageManager.getMessage("gui.title.boolean-search", "query", searchQuery));
        Inventory gui = Bukkit.createInventory(null, 54, title);
        
        Map<String, Class<? extends PlotFlag<?, ?>>> flagClasses = PlotUtils.getCommonFlags();
        
        // bool only
        List<String> validBooleanFlags = new ArrayList<>();
        for (String flagName : filteredFlags) {
            Class<? extends PlotFlag<?, ?>> flagClass = flagClasses.get(flagName);
            if (flagClass != null && PlotUtils.isBooleanFlag(flagClass)) {
                validBooleanFlags.add(flagName);
            }
        }
        
        int flagsPerPage = 36;
        int totalPages = (int) Math.ceil((double) validBooleanFlags.size() / flagsPerPage);
        int startIndex = page * flagsPerPage;
        int endIndex = Math.min(startIndex + flagsPerPage, validBooleanFlags.size());
        
        List<String> pageFlags = validBooleanFlags.subList(startIndex, endIndex);
        
        int slot = 0;
        for (String flagName : pageFlags) {
            if (slot >= 36) break;
            
            Class<? extends PlotFlag<?, ?>> flagClass = flagClasses.get(flagName);
            if (flagClass != null && PlotUtils.isBooleanFlag(flagClass)) {
                Object value = PlotUtils.getFlagValue(plot, flagClass);
                boolean enabled = value instanceof Boolean && (Boolean) value;
                
                Material material = enabled ? Material.GREEN_CONCRETE : Material.RED_CONCRETE;
                String status = enabled ? 
                    messageManager.getMessage("flags.enabled") : 
                    messageManager.getMessage("flags.disabled");
                
                List<String> lore = new ArrayList<>();
                lore.add("");
                lore.add(messageManager.getMessage("flags.current-value", "value", status));
                lore.add("");
                lore.add(messageManager.getMessage("gui.flag.click-to-toggle"));
                
                ItemStack item = createGuiItem(material, messageManager.getMessage("gui.flag.name-color") + flagName, lore);
                gui.setItem(slot, item);
                slot++;
            }
        }
        
        // custom flags check
        List<String> customFlags = new ArrayList<>();
        for (String flagName : filteredFlags) {
            Class<? extends PlotFlag<?, ?>> flagClass = flagClasses.get(flagName);
            if (flagClass != null && !PlotUtils.isBooleanFlag(flagClass)) {
                customFlags.add(flagName);
            }
        }
        
        // switch btn
        if (!customFlags.isEmpty()) {
            ItemStack switchItem = createGuiItem(
                Material.PAPER,
                messageManager.getMessage("gui.items.switch-to-custom.name"),
                messageManager.getLore("gui.items.switch-to-custom.lore", "count", String.valueOf(customFlags.size()))
            );
            gui.setItem(45, switchItem);
        }
        
        if (page > 0) {
            ItemStack prevItem = createGuiItem(
                Material.ARROW,
                messageManager.getMessage("gui.items.previous-page.name"),
                messageManager.getLore("gui.items.previous-page.lore", "page", String.valueOf(page))
            );
            gui.setItem(47, prevItem);
        }
        
        if (page < totalPages - 1) {
            ItemStack nextItem = createGuiItem(
                Material.ARROW,
                messageManager.getMessage("gui.items.next-page.name"),
                messageManager.getLore("gui.items.next-page.lore", "page", String.valueOf(page + 2))
            );
            gui.setItem(51, nextItem);
        }
        
        ItemStack backItem = createGuiItem(
            Material.BARRIER,
            messageManager.getMessage("gui.items.back.name"),
            configManager.getLangConfig().getStringList("gui.items.back.lore")
        );
        gui.setItem(49, backItem);
        
        player.openInventory(gui);
    }
    
    public void openFilteredCustomFlagsGUI(Player player, Plot plot, List<String> filteredFlags, String searchQuery) {
        openFilteredCustomFlagsGUI(player, plot, filteredFlags, searchQuery, 0);
    }
    
    // filtered custom
    public void openFilteredCustomFlagsGUI(Player player, Plot plot, List<String> filteredFlags, String searchQuery, int page) {
        String title = messageManager.colorize(messageManager.getMessage("gui.title.custom-search", "query", searchQuery));
        Inventory gui = Bukkit.createInventory(null, 54, title);
        
        Map<String, Class<? extends PlotFlag<?, ?>>> flagClasses = PlotUtils.getCommonFlags();
        
        List<String> validCustomFlags = new ArrayList<>();
        for (String flagName : filteredFlags) {
            Class<? extends PlotFlag<?, ?>> flagClass = flagClasses.get(flagName);
            if (flagClass != null && !PlotUtils.isBooleanFlag(flagClass)) {
                validCustomFlags.add(flagName);
            }
        }
        
        int flagsPerPage = 36;
        int totalPages = (int) Math.ceil((double) validCustomFlags.size() / flagsPerPage);
        int startIndex = page * flagsPerPage;
        int endIndex = Math.min(startIndex + flagsPerPage, validCustomFlags.size());
        
        List<String> pageFlags = validCustomFlags.subList(startIndex, endIndex);
        
        int slot = 0;
        for (String flagName : pageFlags) {
            if (slot >= 36) break;
            
            Class<? extends PlotFlag<?, ?>> flagClass = flagClasses.get(flagName);
            if (flagClass != null && !PlotUtils.isBooleanFlag(flagClass)) {
                Object value = PlotUtils.getFlagValue(plot, flagClass);
                String valueStr = value != null ? value.toString() : messageManager.getMessage("flags.not-set");
                
                List<String> lore = new ArrayList<>();
                lore.add("");
                lore.add(messageManager.getMessage("flags.current-value", "value", valueStr));
                lore.add("");
                lore.add(messageManager.getMessage("gui.flag.click-to-edit"));
                
                ItemStack item = createGuiItem(Material.PAPER, messageManager.getMessage("gui.flag.name-color") + flagName, lore);
                gui.setItem(slot, item);
                slot++;
            }
        }
        
        List<String> booleanFlags = new ArrayList<>();
        for (String flagName : filteredFlags) {
            Class<? extends PlotFlag<?, ?>> flagClass = flagClasses.get(flagName);
            if (flagClass != null && PlotUtils.isBooleanFlag(flagClass)) {
                booleanFlags.add(flagName);
            }
        }
        
        if (!booleanFlags.isEmpty()) {
            ItemStack switchItem = createGuiItem(
                Material.GREEN_STAINED_GLASS_PANE,
                messageManager.getMessage("gui.items.switch-to-boolean.name"),
                messageManager.getLore("gui.items.switch-to-boolean.lore", "count", String.valueOf(booleanFlags.size()))
            );
            gui.setItem(45, switchItem);
        }
        
        if (page > 0) {
            ItemStack prevItem = createGuiItem(
                Material.ARROW,
                messageManager.getMessage("gui.items.previous-page.name"),
                messageManager.getLore("gui.items.previous-page.lore", "page", String.valueOf(page))
            );
            gui.setItem(47, prevItem);
        }
        
        if (page < totalPages - 1) {
            ItemStack nextItem = createGuiItem(
                Material.ARROW,
                messageManager.getMessage("gui.items.next-page.name"),
                messageManager.getLore("gui.items.next-page.lore", "page", String.valueOf(page + 2))
            );
            gui.setItem(51, nextItem);
        }
        
        ItemStack backItem = createGuiItem(
            Material.BARRIER,
            messageManager.getMessage("gui.items.back.name"),
            configManager.getLangConfig().getStringList("gui.items.back.lore")
        );
        gui.setItem(49, backItem);
        
        player.openInventory(gui);
    }
    
    // create items
    private ItemStack createGuiItem(Material material, String name, List<String> lore) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        
        if (meta != null) {
            meta.setDisplayName(messageManager.colorize(name));
            
            if (lore != null && !lore.isEmpty()) {
                List<String> colorizedLore = new ArrayList<>();
                for (String line : lore) {
                    colorizedLore.add(messageManager.colorize(line));
                }
                meta.setLore(colorizedLore);
            }
            
            item.setItemMeta(meta);
        }
        
        return item;
    }
    
    // gui check
    public boolean isPluginGUI(Inventory inventory) {
        if (inventory == null || inventory.getHolder() != null) {
            return false;
        }
        
        String title = inventory.getViewers().isEmpty() ? "" : 
            inventory.getViewers().get(0).getOpenInventory().getTitle();
        
        return title.contains("Plot Settings") ||
               title.contains("Boolean Flags") ||
               title.contains("Custom Flags") ||
               title.contains("Search Flags");
    }
} 