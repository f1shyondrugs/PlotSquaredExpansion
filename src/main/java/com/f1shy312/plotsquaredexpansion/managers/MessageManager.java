package com.f1shy312.plotsquaredexpansion.managers;

import com.f1shy312.plotsquaredexpansion.PlotSquaredExpansion;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import java.util.ArrayList;
import java.util.List;

public class MessageManager {
    
    private final PlotSquaredExpansion plugin;
    private final ConfigManager configManager;
    
    public MessageManager(PlotSquaredExpansion plugin) {
        this.plugin = plugin;
        this.configManager = plugin.getConfigManager();
    }
    
    // basic msg
    public String getMessage(String path) {
        return colorize(configManager.getLangConfig().getString(path, "Message not found: " + path));
    }
    
    // msg w/ placeholder
    public String getMessage(String path, String placeholder, String value) {
        String message = getMessage(path);
        return message.replace("%" + placeholder + "%", value);
    }
    
    // multiple placeholders
    public String getMessage(String path, String... replacements) {
        String message = getMessage(path);
        
        for (int i = 0; i < replacements.length; i += 2) {
            if (i + 1 < replacements.length) {
                message = message.replace("%" + replacements[i] + "%", replacements[i + 1]);
            }
        }
        
        return message;
    }
    
    // send to player
    public void sendMessage(Player player, String path) {
        player.sendMessage(getPrefix() + getMessage(path));
    }
    
    public void sendMessage(Player player, String path, String placeholder, String value) {
        player.sendMessage(getPrefix() + getMessage(path, placeholder, value));
    }
    
    public void sendMessage(Player player, String path, String... replacements) {
        player.sendMessage(getPrefix() + getMessage(path, replacements));
    }
    
    // send to sender
    public void sendMessage(CommandSender sender, String path) {
        sender.sendMessage(getPrefix() + getMessage(path));
    }
    
    public void sendMessage(CommandSender sender, String path, String placeholder, String value) {
        sender.sendMessage(getPrefix() + getMessage(path, placeholder, value));
    }
    
    public void sendMessage(CommandSender sender, String path, String... replacements) {
        sender.sendMessage(getPrefix() + getMessage(path, replacements));
    }
    
    // no prefix
    public void sendRawMessage(Player player, String path) {
        player.sendMessage(getMessage(path));
    }
    
    public void sendRawMessage(Player player, String path, String placeholder, String value) {
        player.sendMessage(getMessage(path, placeholder, value));
    }
    
    public void sendRawMessage(Player player, String path, String... replacements) {
        player.sendMessage(getMessage(path, replacements));
    }
    
    public String getPrefix() {
        return getMessage("general.prefix");
    }
    
    // colors
    public String colorize(String text) {
        return ChatColor.translateAlternateColorCodes('&', text);
    }
    
    // flag stuff
    public void sendFlagSuccess(Player player, String flagName, String value) {
        sendMessage(player, "flags.set-success", "flag", flagName, "value", value);
    }
    
    public void sendFlagError(Player player, String flagName, String error) {
        sendMessage(player, "flags.set-error", "flag", flagName, "error", error);
    }
    
    public void sendToggleMessage(Player player, String flagName, boolean enabled) {
        if (enabled) {
            sendMessage(player, "flags.toggle-enabled", "flag", flagName);
        } else {
            sendMessage(player, "flags.toggle-disabled", "flag", flagName);
        }
    }
    
    public void sendInputPrompt(Player player, String flagName) {
        // check for specific prompt
        String specificPath = "chat-input.prompt." + flagName;
        String specificMessage = configManager.getLangConfig().getString(specificPath);
        
        if (specificMessage != null) {
            sendRawMessage(player, specificPath);
        } else {
            sendRawMessage(player, "chat-input.prompt.generic", "flag", flagName);
        }
        
        sendRawMessage(player, "chat-input.cancel-info");
    }
    
    public void sendSearchPrompt(Player player) {
        sendRawMessage(player, "search.prompt");
        sendRawMessage(player, "chat-input.cancel-info");
    }
    
    // cancel check
    public boolean isCancelWord(String input) {
        String word = input.trim().toLowerCase();
        return word.equals("cancel") || word.equals("stop") || word.equals("exit");
    }
    
    // get lore with placeholders
    public List<String> getLore(String path, String... replacements) {
        List<String> lore = new ArrayList<>();
        List<String> configLore = configManager.getLangConfig().getStringList(path);
        
        for (String line : configLore) {
            String processedLine = line;
            for (int i = 0; i < replacements.length; i += 2) {
                if (i + 1 < replacements.length) {
                    processedLine = processedLine.replace("%" + replacements[i] + "%", replacements[i + 1]);
                }
            }
            lore.add(colorize(processedLine));
        }
        
        return lore;
    }
} 