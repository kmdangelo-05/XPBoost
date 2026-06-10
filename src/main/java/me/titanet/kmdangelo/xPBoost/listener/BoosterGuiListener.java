package me.titanet.kmdangelo.xPBoost.listener;

import lombok.AllArgsConstructor;
import me.titanet.kmdangelo.xPBoost.XPBoost;
import me.titanet.kmdangelo.xPBoost.boosters.PlayerBooster;
import me.titanet.kmdangelo.xPBoost.gui.BoosterGui;
import me.titanet.kmdangelo.xPBoost.gui.GuiType;
import me.titanet.kmdangelo.xPBoost.utility.Messages;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.net.http.WebSocket;
import java.util.UUID;

@AllArgsConstructor
public class BoosterGuiListener implements Listener {

    private final XPBoost plugin;

    @EventHandler
    public void guiInventoryClickEvent(InventoryClickEvent e) {
        String title = e.getView().getTitle();

        if (!GuiType.isPluginGui(title)) return;
        e.setCancelled(true);

        if (e.getCurrentItem() == null) return;
        if (e.getCurrentItem().getType() == Material.AIR) return;

        Player viewer = (Player) e.getWhoClicked();
        if (e.getClickedInventory().getHolder() == null) return;
        Player owner = (Player) e.getClickedInventory().getHolder();

        BoosterGui boosterGui = plugin.getBoosterManager().getBoosterGuiMap().get(owner.getUniqueId());
        PlayerBooster ownerBooster = plugin.getBoosterManager().getPlayerBoosterMap().get(owner.getUniqueId());

        ItemStack selectedItem = e.getCurrentItem();
        ItemMeta meta = selectedItem.getItemMeta();

        if (meta == null) return;

        GuiType guiType = null;
        for (GuiType gui : GuiType.values()) {
            if (gui.getTitle().equals(title)) {
                guiType = gui;
                break;
            }
        }

        if (guiType == null) return;

        switch (selectedItem.getType()) {
            case EXPERIENCE_BOTTLE -> {

                PersistentDataContainer pdc = meta.getPersistentDataContainer();

                NamespacedKey boosterUuidNK = new NamespacedKey(plugin, "booster_uuid");
                String stringUuid = pdc.get(boosterUuidNK, PersistentDataType.STRING);

                if (stringUuid == null) return;

                UUID boosterUuid = UUID.fromString(stringUuid);


                if (guiType == GuiType.REMOTION_GUI) {
                    double boosterMultiplier = ownerBooster.get(boosterUuid).getMultiplier();
                    ownerBooster.removeBooster(boosterUuid);
                    Messages.successfullyRemovedBoosterMessage(viewer, boosterMultiplier, owner);
                    boosterGui.openRemotionGui(viewer);
                }
            }
            case GREEN_STAINED_GLASS_PANE -> {
                if (guiType == GuiType.PREFERENCES_GUI) {
                    NamespacedKey preferenceNK = new NamespacedKey(plugin, "preference");
                    if (meta.getPersistentDataContainer().isEmpty()) return;
                    switch (meta.getPersistentDataContainer().get(preferenceNK, PersistentDataType.STRING)) {
                        case "xp_chat_message" -> {
                            ownerBooster.getPlayerPreferences().setXpChatMessage(false);
                        }
                        case "booster_activation" -> {
                            ownerBooster.getPlayerPreferences().setActiveBoosters(false);
                        }
                        case "action_bar" -> {
                            ownerBooster.getPlayerPreferences().setActionBar(false);
                        }
                        case null -> {
                            return;
                        }
                        default ->
                                throw new IllegalStateException(
                                        "Unexpected value: " + meta.getPersistentDataContainer().get(preferenceNK, PersistentDataType.STRING)
                                );

                    }
                    BoosterGui tempGui = new BoosterGui(plugin, owner.getUniqueId());
                    Messages.modificationOccurredSuccessfullyMessage(viewer);
                    tempGui.openPreferences();
                    return;
                }
                NamespacedKey pageNK = new NamespacedKey(plugin, "page");
                PersistentDataContainer pdc = meta.getPersistentDataContainer();
                Integer destinationPage = pdc.get(pageNK, PersistentDataType.INTEGER);
                if (destinationPage == null) return;
                viewer.openInventory(boosterGui.getPageList().get(destinationPage - 1).getPage());
            }
            case RED_STAINED_GLASS_PANE -> {
                if (guiType == GuiType.PREFERENCES_GUI) {
                    NamespacedKey preferenceNK = new NamespacedKey(plugin, "preference");
                    if (meta.getPersistentDataContainer().isEmpty()) return;
                    switch (meta.getPersistentDataContainer().get(preferenceNK, PersistentDataType.STRING)) {
                        case "xp_chat_message" -> {
                            ownerBooster.getPlayerPreferences().setXpChatMessage(true);
                        }
                        case "booster_activation" -> {
                            ownerBooster.getPlayerPreferences().setActiveBoosters(true);
                        }
                        case "action_bar" -> {
                            ownerBooster.getPlayerPreferences().setActionBar(true);
                        }
                        case null -> {
                            return;
                        }
                        default ->
                                throw new IllegalStateException(
                                        "Unexpected value: " + meta.getPersistentDataContainer().get(preferenceNK, PersistentDataType.STRING)
                                );
                    }
                    BoosterGui tempGui = new BoosterGui(plugin, owner.getUniqueId());
                    Messages.modificationOccurredSuccessfullyMessage(viewer);
                    tempGui.openPreferences();
                }
            }
        }




    }
}
