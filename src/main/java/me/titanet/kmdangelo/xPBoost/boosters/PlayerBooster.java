package me.titanet.kmdangelo.xPBoost.boosters;

import lombok.*;
import me.titanet.kmdangelo.xPBoost.boosters.settings.PlayerPreferences;

import java.util.*;

@AllArgsConstructor
@Getter
public class PlayerBooster {
    private final UUID playerUUID;
    private final List<Booster> boosterList;
    private List<Booster> removedBooster;
    @Setter
    private Booster activeBooster;
    @Setter
    private PlayerPreferences playerPreferences;



    public Booster getMultiplier(double multiplier) {
        for (Booster booster : boosterList) {
            System.out.println("Analizzando booster "+ booster.getMultiplier());
            if (booster.getMultiplier() == multiplier) {
                System.out.println("Booster trovato!");
                return booster;
            }
        }
        return null;
    }

    public void addBooster(Booster booster,boolean active){
        boosterList.add(booster);
        boosterList.sort(Comparator.comparingDouble(Booster::getMultiplier));
        if(active){
            activeBooster = booster;
        }
    }

    public Booster removeBooster(double multiplier) {
        Booster removed = null;
        for (Booster booster: boosterList) {
            if (booster.getMultiplier() == multiplier) {
                removed = booster;
                removedBooster.add(removed);
                break;
            }
        }
        boosterList.removeIf(booster -> booster.getMultiplier() == multiplier);
        return removed;
    }
}
