package me.titanet.kmdangelo.xPBoost.gui;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import me.titanet.kmdangelo.xPBoost.XPBoost;
import me.titanet.kmdangelo.xPBoost.boosters.settings.PlayerPreferences;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@Getter
public class GuiPage {
    private XPBoost plugin;

    private final int pageNumber;
    private final int guiSize;
    private final String title;
    private final UUID ownerUuid;

    private final int itemLimit;
    private final Inventory page;

    public GuiPage(XPBoost plugin ,UUID ownerUuid, String title, int guiSize, int pageNumber, boolean lastPage) {
        this.plugin = plugin;
        this.ownerUuid = ownerUuid;
        this.title = title;
        this.guiSize = guiSize;
        this.pageNumber = pageNumber;
        itemLimit = guiSize - 9;
        page = Bukkit.createInventory(Bukkit.getPlayer(ownerUuid), guiSize, title);
        createListPage(lastPage);
    }




    private Inventory createListPage(boolean lastPage) {
        Player owner = Bukkit.getPlayer(ownerUuid);

        ItemStack grayGlassPane = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
        ItemStack redGlassPane = new ItemStack(Material.RED_STAINED_GLASS_PANE);
        ItemStack greenGlassPane = new ItemStack(Material.GREEN_STAINED_GLASS_PANE);

        NamespacedKey pageNK = new NamespacedKey(plugin, "page");

        ItemMeta grayMeta = grayGlassPane.getItemMeta();
        if (grayMeta == null) return null;

        grayMeta.setItemName("");
        grayGlassPane.setItemMeta(grayMeta);

        for (int i = guiSize - 9; i < guiSize; i++ ) {
            page.setItem(i, grayGlassPane);
        }

        if (pageNumber == 1) {
            ItemMeta redMeta = redGlassPane.getItemMeta();

            if (redMeta == null) return null;

            redMeta.setItemName("§4§LPrima pagina");
            redGlassPane.setItemMeta(redMeta);

            page.setItem(guiSize - 8, redGlassPane);
        } else {
            ItemMeta greenMeta = greenGlassPane.getItemMeta();

            if (greenMeta == null) return null;

            greenMeta.setItemName("§a§LPagina precedente");

            PersistentDataContainer greenMetaPdc = greenMeta.getPersistentDataContainer();
            greenMetaPdc.set(pageNK, PersistentDataType.INTEGER, pageNumber-1);

            greenGlassPane.setItemMeta(greenMeta);

            page.setItem(guiSize - 8, greenGlassPane);
        }

        if (lastPage) {
            ItemMeta redMeta = redGlassPane.getItemMeta();

            if (redMeta == null) return null;

            redMeta.setItemName("§4§LUltima pagina");
            redGlassPane.setItemMeta(redMeta);

            page.setItem(guiSize - 2, redGlassPane);
        } else {
            ItemMeta greenMeta = greenGlassPane.getItemMeta();

            if (greenMeta == null) return null;

            greenMeta.setItemName("§a§LPagina successiva");

            PersistentDataContainer greenMetaPdc = greenMeta.getPersistentDataContainer();
            greenMetaPdc.set(pageNK, PersistentDataType.INTEGER, pageNumber+1);

            greenGlassPane.setItemMeta(greenMeta);

            page.setItem(guiSize - 2, greenGlassPane);
        }

        ItemStack playerHead = new ItemStack(Material.PLAYER_HEAD, 1);
        SkullMeta skullMeta = (SkullMeta) playerHead.getItemMeta();

        if (skullMeta == null) return null;

        skullMeta.setOwningPlayer(owner);

        if (owner == null) return null;

        skullMeta.setDisplayName("§6Booster di §c" + owner.getName());
        playerHead.setItemMeta(skullMeta);

        page.setItem(guiSize - 5, playerHead);

        return page;

    }

    public void populateListPage(List<ItemStack> list) throws ItemLimitExcededException {
        if (list.size() > itemLimit) throw new ItemLimitExcededException("Item limit exceded for this page");
        int i = 0;
        for (ItemStack item: list) {
            if (list.get(i) == null) return;
            page.setItem(i, list.get(i));
            i++;
        }
    }

    public Inventory createPreferencesPage() {
        PlayerPreferences preferences = plugin.getBoosterManager().getPlayerBoosterMap().get(ownerUuid).getPlayerPreferences();

        NamespacedKey preferenceNK = new NamespacedKey(plugin, "preference");

        boolean xpChatPreference = preferences.isXpChatMessage();
        boolean actionBarPreference = preferences.isActionBar();
        boolean activationPreference = preferences.isActiveBoosters();

        ItemStack greenGlassPane = new ItemStack(Material.GREEN_STAINED_GLASS_PANE);
        ItemStack redGlassPane = new ItemStack(Material.RED_STAINED_GLASS_PANE);
        ItemStack greyGlassPane = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);

        for (int i = 0; i<9; i++) {
            page.setItem(i,greyGlassPane);
        }

        ItemMeta greenMeta = greenGlassPane.getItemMeta();
        ItemMeta redMeta = greenGlassPane.getItemMeta();

        if (greenMeta == null || redMeta == null) return null;

        PersistentDataContainer greenPdc = greenMeta.getPersistentDataContainer();
        PersistentDataContainer redPdc = redMeta.getPersistentDataContainer();

        List<String> lore = new ArrayList<>();

        if (xpChatPreference) {
            greenMeta.setItemName("§6§lXp chat message");
            lore.add("§a§lACTIVATED");
            greenMeta.setLore(lore);
            greenPdc.set(preferenceNK, PersistentDataType.STRING, "xp_chat_message");
            greenGlassPane.setItemMeta(greenMeta);

            page.setItem(1, greenGlassPane);
        } else {
            redMeta.setItemName("§6§lXp chat message");
            lore.add("§4§lDEACTIVATED");
            redMeta.setLore(lore);
            redPdc.set(preferenceNK, PersistentDataType.STRING, "xp_chat_message");
            redGlassPane.setItemMeta(redMeta);

            page.setItem(1, redGlassPane);
        }

        lore.clear();

        if (activationPreference) {
            greenMeta.setItemName("§6§lBooster activation");
            lore.add("§a§lACTIVATED");
            greenMeta.setLore(lore);
            greenPdc.set(preferenceNK, PersistentDataType.STRING, "booster_activation");
            greenGlassPane.setItemMeta(greenMeta);

            page.setItem(4, greenGlassPane);
        } else {
            redMeta.setItemName("§6§lBooster activation");
            lore.add("§4§lDEACTIVATED");
            redMeta.setLore(lore);
            redPdc.set(preferenceNK, PersistentDataType.STRING, "booster_activation");
            redGlassPane.setItemMeta(redMeta);

            page.setItem(4, redGlassPane);
        }

        lore.clear();

        if (actionBarPreference) {
            greenMeta.setItemName("§6§lAction bar");
            lore.add("§a§lACTIVATED");
            greenMeta.setLore(lore);
            greenPdc.set(preferenceNK, PersistentDataType.STRING, "action_bar");
            greenGlassPane.setItemMeta(greenMeta);

            page.setItem(7, greenGlassPane);
        } else {
            redMeta.setItemName("§6§lAction bar");
            lore.add("§4§lDEACTIVATED");
            redMeta.setLore(lore);
            redPdc.set(preferenceNK, PersistentDataType.STRING, "action_bar");
            redGlassPane.setItemMeta(redMeta);

            page.setItem(7, redGlassPane);
        }

        return page;
    }

}
