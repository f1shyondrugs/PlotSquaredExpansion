package com.f1shy312.plotsquaredexpansion.utils;

import com.plotsquared.bukkit.util.BukkitUtil;
import com.plotsquared.core.PlotAPI;
import com.plotsquared.core.player.PlotPlayer;
import com.plotsquared.core.plot.Plot;
import com.plotsquared.core.plot.flag.PlotFlag;
import com.plotsquared.core.plot.flag.implementations.*;
import com.plotsquared.core.plot.flag.GlobalFlagContainer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;

public class PlotUtils {
    
    private static PlotAPI plotAPI;
    private static boolean plotSquaredAvailable = false;
    
    private static Map<String, PlotFlag<?, ?>> flagInstances = new HashMap<>();
    
    static {
        Plugin plotSquared = Bukkit.getPluginManager().getPlugin("PlotSquared");
        if (plotSquared != null && plotSquared.isEnabled()) {
            try {
                plotAPI = new PlotAPI();
                plotSquaredAvailable = true;
            } catch (Exception e) {
                plotSquaredAvailable = false;
            }
        }
    }
    
    // plotsquared check
    public static boolean isPlotSquaredAvailable() {
        return plotSquaredAvailable;
    }
    
    public static PlotAPI getPlotAPI() {
        return plotAPI;
    }
    
    // player location
    public static Plot getPlotAtLocation(Player player) {
        if (!isPlotSquaredAvailable()) {
            return null;
        }
        
        try {
            PlotPlayer<?> plotPlayer = BukkitUtil.adapt(player);
            return plotPlayer.getCurrentPlot();
        } catch (Exception e) {
            return null;
        }
    }
    
    // modify check
    public static boolean canModifyPlot(Player player, Plot plot) {
        if (!isPlotSquaredAvailable() || plot == null) {
            return false;
        }
        
        try {
            PlotPlayer<?> plotPlayer = BukkitUtil.adapt(player);
            return plot.isOwner(plotPlayer.getUUID()) || 
                   plot.getTrusted().contains(plotPlayer.getUUID()) || 
                   plot.getMembers().contains(plotPlayer.getUUID());
        } catch (Exception e) {
            return false;
        }
    }
    
    // owner check
    public static boolean isPlotOwner(Player player, Plot plot) {
        if (!isPlotSquaredAvailable() || plot == null) {
            return false;
        }
        
        try {
            PlotPlayer<?> plotPlayer = BukkitUtil.adapt(player);
            return plot.isOwner(plotPlayer.getUUID());
        } catch (Exception e) {
            return false;
        }
    }
    
    // common flags
    @SuppressWarnings("unchecked")
    public static Map<String, Class<? extends PlotFlag<?, ?>>> getCommonFlags() {
        Map<String, Class<? extends PlotFlag<?, ?>>> flags = new HashMap<>();
        flagInstances.clear();
        
        Collection<PlotFlag<?, ?>> availableFlags = GlobalFlagContainer.getInstance().getRecognizedPlotFlags();
        
        for (PlotFlag<?, ?> flag : availableFlags) {
            try {
                String flagName = flag.getName();
                flags.put(flagName, (Class<? extends PlotFlag<?, ?>>) flag.getClass());
                flagInstances.put(flagName, flag);
            } catch (Exception e) {
                continue;
            }
        }
        
        return flags;
    }
    
    // boolean flag check
    public static boolean isBooleanFlag(Class<? extends PlotFlag<?, ?>> flagClass) {
        if (flagClass == null) return false;
        
        try {
            PlotFlag<?, ?> flag = null;
            for (PlotFlag<?, ?> instance : flagInstances.values()) {
                if (instance.getClass().equals(flagClass)) {
                    flag = instance;
                    break;
                }
            }
            
            if (flag == null) {
                return false;
            }
            
            String example = flag.getExample().toString();
            boolean isBoolean = example.equals("true") || example.equals("false");
            
            return isBoolean;
        } catch (Exception e) {
            return false;
        }
    }
    
    // get flag value
    public static Object getFlagValue(Plot plot, Class<? extends PlotFlag<?, ?>> flagClass) {
        if (!isPlotSquaredAvailable() || plot == null || flagClass == null) {
            return null;
        }
        
        try {
            if (flagClass == PvpFlag.class) {
                try {
                    Object flag = plot.getFlagContainer().queryLocal(PvpFlag.class);
                    if (flag != null && flag instanceof PvpFlag) {
                        return ((PvpFlag) flag).getValue();
                    }
                    return false;
                } catch (Exception e) {
                    try {
                        PvpFlag pvpFlag = plot.getFlagContainer().getFlag(PvpFlag.class);
                        return pvpFlag.getValue();
                    } catch (Exception e2) {
                        return false;
                    }
                }
            }
            
            // boolean flags
            if (isBooleanFlag(flagClass)) {
                try {
                    Object flag = plot.getFlagContainer().queryLocal(flagClass);
                    if (flag != null) {
                        return true;
                    }
                    return false;
                } catch (Exception e) {
                    return false;
                }
            } else {
                // custom flags
                try {
                    Object flag = plot.getFlagContainer().queryLocal(flagClass);
                    if (flag != null) {
                        if (flag instanceof PlotFlag) {
                            Object value = ((PlotFlag<?, ?>) flag).getValue();
                            return value;
                        } else {
                            return flag.toString();
                        }
                    }
                    return null;
                } catch (Exception e) {
                    return null;
                }
            }
        } catch (Exception e) {
            if (isBooleanFlag(flagClass)) {
                return false;
            }
            return null;
        }
    }
    
    // set flag value
    public static boolean setFlagValue(Plot plot, String flagName, Object value) {
        if (!isPlotSquaredAvailable() || plot == null) {
            return false;
        }
        
        try {
            Map<String, Class<? extends PlotFlag<?, ?>>> flagClasses = getCommonFlags();
            Class<? extends PlotFlag<?, ?>> flagClass = flagClasses.get(flagName);
            
            if (flagClass == null) {
                return false;
            }
            
            // boolean fix
            if (isBooleanFlag(flagClass) && value instanceof Boolean) {
                boolean boolValue = (Boolean) value;
                if (boolValue) {
                    return plot.setFlag(flagClass, "true");
                } else {
                    // remove flag for false
                    return plot.removeFlag(flagClass);
                }
            }
            
            // custom flags
            if (!isBooleanFlag(flagClass)) {
                if (value instanceof String) {
                    String stringValue = ((String) value).trim();
                    if (stringValue.equalsIgnoreCase("DEFAULT") || 
                        stringValue.equalsIgnoreCase("NONE") || 
                        stringValue.isEmpty()) {
                        return plot.removeFlag(flagClass);
                    } else {
                        return plot.setFlag(flagClass, stringValue);
                    }
                } else if (value == null) {
                    return plot.removeFlag(flagClass);
                } else {
                    return plot.setFlag(flagClass, value.toString());
                }
            }
            
            return false;
        } catch (Exception e) {
            return false;
        }
    }
} 