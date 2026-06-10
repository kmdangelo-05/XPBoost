package me.titanet.kmdangelo.xPBoost.gui;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import me.titanet.kmdangelo.xPBoost.XPBoost;
import me.titanet.kmdangelo.xPBoost.boosters.Booster;
import me.titanet.kmdangelo.xPBoost.boosters.PlayerBooster;
import me.titanet.kmdangelo.xPBoost.utility.TimeStamp;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.*;

@RequiredArgsConstructor
@Getter
public class BoosterGui {

    private final XPBoost plugin;
    private final UUID playerUuid;
    private final List<GuiPage> pageList = new ArrayList<>();

    public void openRemotionGui() {
        Player player = Bukkit.getPlayer(playerUuid);
        if (player == null) return;
        if (!pageList.isEmpty()) pageList.clear();
        if (createGui(GuiType.REMOTION_GUI)) {
            player.openInventory(pageList.getFirst().getPage());
        }

    }

    public void openRemotionGui(Player seeker) {
        if (!pageList.isEmpty()) pageList.clear();
        if (createGui(GuiType.REMOTION_GUI)) {
            seeker.openInventory(pageList.getFirst().getPage());
        }
    }

    public void openListGui() {
        Player player = Bukkit.getPlayer(playerUuid);
        if (player == null) return;
        if (!pageList.isEmpty()) pageList.clear();
        if (createGui(GuiType.LIST_GUI)) {
            player.openInventory(pageList.getFirst().getPage());
        }
    }

    public void openListGui(Player seeker) {
        if (!pageList.isEmpty()) pageList.clear();
        if (createGui(GuiType.LIST_GUI)) {
            seeker.openInventory(pageList.getFirst().getPage());
        }

    }

    public void openPreferences() {
        Player player = Bukkit.getPlayer(playerUuid);
        if (player == null) return;
        GuiPage page = new GuiPage(plugin, playerUuid, GuiType.PREFERENCES_GUI.getTitle(), 9, 1, true);
        player.openInventory(page.createPreferencesPage());
    }

    private boolean createGui(GuiType guiType) {
        plugin.getBoosterManager().getBoosterGuiMap().put(playerUuid, this);
        FileConfiguration config = plugin.getConfig();

        PlayerBooster playerBooster = plugin.getBoosterManager().getPlayerBoosterMap().get(playerUuid);
        List<ItemStack> boosterList = new ArrayList<>();

        int i = 0;

        for (Booster booster : playerBooster.getBoosterList()) {
            ItemStack boosterItem = new ItemStack(Material.EXPERIENCE_BOTTLE);
            ItemMeta boosterMeta = boosterItem.getItemMeta();

            if (boosterMeta == null) return false;

            String itemName= ChatColor.translateAlternateColorCodes(
                    '&', config.getString("settings.booster_gui_appearance.booster_item_title", "")
                            .replace("%booster_number%", String.valueOf(++i))
            );

            List<String> itemLore = new ArrayList<>();

            itemLore.addAll(config.getStringList("settings.booster_gui_appearance.booster_item_lore")
                    .stream()
                    .map(line -> ChatColor.translateAlternateColorCodes('&', line
                            .replace("%starting_time%", TimeStamp.formattingTime(booster.getStartingDurationInMillis()/1000))
                            .replace("%remaining_time%", TimeStamp.formattingTime(booster.getDurationInMillis()/1000))
                            .replace("%multiplier%", String.valueOf(booster.getMultiplier()))))
                    .toList());

            if (booster.isOnlineOnly()) {
                itemLore.add(
                        ChatColor.translateAlternateColorCodes(
                                '&', config.getString("settings.booster_gui_appearance.booster_item_lore_online_only_true", ""
                                )
                        ));
            } else {
                itemLore.add(
                        ChatColor.translateAlternateColorCodes(
                                '&', config.getString("settings.booster_gui_appearance.booster_item_lore_online_only_false", ""
                                )
                        ));
            }

            if (guiType.equals(GuiType.REMOTION_GUI)) {
                itemLore.add(
                        ChatColor.translateAlternateColorCodes(
                                '&', config.getString("settings.booster_gui_appearance.booster_item_lore_remotion_guide", ""
                                )
                        ));
            }

            boosterMeta.setItemName(itemName);
            boosterMeta.setLore(itemLore);

            NamespacedKey boosterUuidNk = new NamespacedKey(plugin, "booster_uuid");

            PersistentDataContainer pdc = boosterMeta.getPersistentDataContainer();
            pdc.set(boosterUuidNk, PersistentDataType.STRING, booster.getBoosterUuid().toString());

            boosterItem.setItemMeta(boosterMeta);

            boosterList.add(boosterItem);
        }

        populatePages(boosterList, 27, guiType);

        return true;
    }

    private List<List<ItemStack>> getRecoursivelyDividedList(List<ItemStack> itemList, int maxListSize) {
        if (itemList.isEmpty()) return new ArrayList<>();

        List<List<ItemStack>> lists = new ArrayList<>();

        int chunkSize = Math.min(maxListSize, itemList.size());

        List<ItemStack> chunk = new ArrayList<>(itemList.subList(0, chunkSize));
        lists.add(chunk);

        List<ItemStack> remainingItems = itemList.subList(chunkSize, itemList.size());
        lists.addAll(getRecoursivelyDividedList(remainingItems, maxListSize));

        return lists;
    }

    private boolean populatePages(List<ItemStack> itemList ,int pageSize, GuiType guiType) {
        List<List<ItemStack>> lists = getRecoursivelyDividedList(itemList, pageSize);
        GuiPage page;
        int i = 0;

        if (itemList.isEmpty()) {
            page = new GuiPage(plugin ,playerUuid, guiType.getTitle(), pageSize+9, ++i, true);
            pageList.add(page);
            return false;
        }

        for (List<ItemStack> list: lists) {

            if (list.equals(lists.getLast())) {
                page = new GuiPage(plugin ,playerUuid, guiType.getTitle(), pageSize+9, ++i, true);
            } else {
                page = new GuiPage(plugin ,playerUuid, guiType.getTitle(), pageSize+9, ++i, false);
            }
            page.populateListPage(list);
            pageList.add(page);
        }

        return true;
    }
}
