package me.titanet.kmdangelo.xPBoost.boosters;

import lombok.*;
import me.titanet.kmdangelo.xPBoost.boosters.settings.PlayerPreferences;
import me.titanet.kmdangelo.xPBoost.utility.Messages;
import me.titanet.kmdangelo.xPBoost.utility.TimeStamp;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;

@RequiredArgsConstructor
@Getter
@Setter
public class PlayerBooster {

    private final UUID playerUUID;
    private final List<Booster> boosterList;
    private final List<Booster> removedBooster;
    private final PlayerPreferences playerPreferences;

    private double totalMultiplier = 0;
    private long longestDuration = 0;

    public void reduceAllBoostersDuration(long timeInMillis) {
        Player player = Bukkit.getPlayer(playerUUID);
        if (player == null) return;
        List<Booster> expiringBoosters = new ArrayList<>();
        for (Booster booster : boosterList) {
            booster.removeDuration(timeInMillis);
            booster.update();
            if (booster.isDurationExpired()) {
                expiringBoosters.add(booster);
            }
        }
        if (!expiringBoosters.isEmpty()) {
            removeAll(expiringBoosters);
            for (Booster booster: expiringBoosters) {
                Messages.expiredBoosterMessage(player, booster.getMultiplier());
            }
        }
    }

    public boolean updateTotalMultiplier() {
        boolean updated = false;
        for (Booster booster : boosterList) {
            totalMultiplier += booster.getMultiplier();
            updated = true;
        }
        return updated;
    }

    public boolean updateLongestDuration() {
        boolean updated = false;
        longestDuration = -1;
        for (Booster booster : boosterList) {
            if (booster.getDurationInMillis() >= longestDuration) {
                longestDuration = booster.getDurationInMillis();
                updated = true;
            }
        }
        return updated;
    }

    public void addBooster(Booster booster) {
        boosterList.add(booster);
        boosterList.sort(Comparator.comparingLong(Booster::getDurationInMillis));
        if (booster.getDurationInMillis() > longestDuration) {
            longestDuration = booster.getDurationInMillis();
        }
        totalMultiplier += booster.getMultiplier();
    }

    public Booster removeBooster(UUID uuid) {
        Booster removed = null;
        for (Booster booster : boosterList) {
            if (booster.getBoosterUuid().equals(uuid)) {
                removed = booster;
                removedBooster.add(removed);
                totalMultiplier -= booster.getMultiplier();
            }
        }
        boosterList.removeIf(booster -> booster.getBoosterUuid().equals(uuid));
        return removed;
    }

    public void clearBoosterList() {
        for (Booster booster : boosterList) {
            removedBooster.add(booster);
            totalMultiplier -= booster.getMultiplier();
        }
        boosterList.clear();
        }

    public Booster removeBooster(Booster booster) {
        Booster removed = null;
        if (boosterList.contains(booster)) {
            removed = booster;
            removedBooster.add(removed);
            boosterList.remove(booster);
            totalMultiplier -= booster.getMultiplier();
        }
        return removed;
    }

    public boolean removeAll(Collection<Booster> collection) {
        boolean removedAtLeastOne = false;
        for (Booster booster: collection) {
            if (!boosterList.contains(booster)) break;
            removeBooster(booster);
            removedAtLeastOne = true;
        }
        return removedAtLeastOne;
    }

    public Booster get(UUID boosterUuid) {
       for (Booster booster: boosterList){
            if (booster.getBoosterUuid().equals(boosterUuid)) {
                return booster;
            }
       }
       return null;
    }

    public boolean hasBoosters() {
        if (!boosterList.isEmpty()) return true;
        return false;
    }

}
