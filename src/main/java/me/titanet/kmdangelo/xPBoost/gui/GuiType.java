package me.titanet.kmdangelo.xPBoost.gui;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;

@RequiredArgsConstructor
@Getter
public enum GuiType {
    REMOTION_GUI,
    LIST_GUI,
    PREFERENCES_GUI;

    private String title;

    public static boolean isPluginGui(String title) {
        boolean present = false;
        for (GuiType guiType : values()) {
            if (guiType.getTitle().equals(title)) return true;
        }
        return false;
    }

    public static void init(FileConfiguration config) {
        REMOTION_GUI.title = ChatColor.translateAlternateColorCodes('&', config.getString("settings.booster_gui_appearance.remotion_gui_title", ""));
        LIST_GUI.title = ChatColor.translateAlternateColorCodes('&', config.getString("settings.booster_gui_appearance.list_gui_title", ""));
        PREFERENCES_GUI.title = ChatColor.translateAlternateColorCodes('&', config.getString("settings.booster_gui_appearance.preferences_gui_title", ""));
    }

}
