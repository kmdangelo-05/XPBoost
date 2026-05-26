package me.titanet.kmdangelo.xPBoost.manager;

import lombok.Getter;
import lombok.Setter;
import me.titanet.kmdangelo.xPBoost.boosters.PlayerBooster;
import me.titanet.kmdangelo.xPBoost.boosters.Booster;
import me.titanet.kmdangelo.xPBoost.database.MySQLDatabaseService;
import me.titanet.kmdangelo.xPBoost.boosters.settings.PlayerPreferences;

import java.util.ArrayList;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Getter
public class BoosterManager {



    @Setter
    private MySQLDatabaseService sqlDatabaseService = null;

    private final Map<UUID, PlayerBooster> playerBoosterMap = new ConcurrentHashMap<>();

    public void loadUser(UUID uuid) {

        sqlDatabaseService.getPlayerBoosters(uuid).thenAccept((user) -> {

            if (user == null) {
                playerBoosterMap.put(uuid,new PlayerBooster(uuid,new ArrayList<>(), new ArrayList<>(),null, new PlayerPreferences()));
            } else {
                playerBoosterMap.put(uuid, user);
            }

        });
    }

    public void unLoad(UUID uuid) {
        sqlDatabaseService.updatePlayer(uuid);
        playerBoosterMap.remove(uuid);
    }


    public boolean addBooster(UUID uuid, double multiplier, long duration, boolean active) {

        PlayerBooster player = playerBoosterMap.get(uuid);

        if (player == null) return false;

        Booster booster = player.getMultiplier(multiplier);

        if (booster == null) {

            booster = new Booster(multiplier, 0);
            player.addBooster(booster, active);
        }

        System.out.println("UpdateBefore: " + booster.isUpdated());

        booster.addDuration(duration);
        booster.update();

        System.out.println("UpdateAfter: " + booster.isUpdated());

        if (active) {
            player.setActiveBooster(booster);
        }
        return true;
    }

    public Booster getActiveBooster(UUID uuid) {
        PlayerBooster player = playerBoosterMap.get(uuid);
        if (player == null) return null;

        return player.getActiveBooster();

    }

    public void update(){
        sqlDatabaseService.updatePlayerBoosters();
    }


}
