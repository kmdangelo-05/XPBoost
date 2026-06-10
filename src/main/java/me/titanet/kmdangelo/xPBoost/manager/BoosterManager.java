package me.titanet.kmdangelo.xPBoost.manager;

import lombok.Getter;
import lombok.Setter;
import me.titanet.kmdangelo.xPBoost.XPBoost;
import me.titanet.kmdangelo.xPBoost.boosters.PlayerBooster;
import me.titanet.kmdangelo.xPBoost.boosters.Booster;
import me.titanet.kmdangelo.xPBoost.database.MySQLDatabaseService;
import me.titanet.kmdangelo.xPBoost.boosters.settings.PlayerPreferences;
import me.titanet.kmdangelo.xPBoost.gui.BoosterGui;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Getter
public class BoosterManager {

    @Setter
    private MySQLDatabaseService sqlDatabaseService = null;

    private final Map<UUID, PlayerBooster> playerBoosterMap = new ConcurrentHashMap<>();

    private final Map<UUID, BoosterGui> boosterGuiMap = new HashMap<>();

    private final List<UUID> playersWithXpChatMessageDelay = new ArrayList<>();

    private final XPBoost plugin;

    public BoosterManager(XPBoost plugin) {
        this.plugin = plugin;
    }

    public void loadUser(UUID uuid) {

        sqlDatabaseService.getPlayerBoosters(uuid).thenAccept((user) -> {

            if (user == null) {
                playerBoosterMap.put(
                        uuid,new PlayerBooster(
                                uuid,new ArrayList<>(), new ArrayList<>(), new PlayerPreferences()
                        )
                );
            } else {
                playerBoosterMap.put(uuid, user);
            }

        });
    }

    public void unLoad(UUID uuid) {
        sqlDatabaseService.updatePlayer(uuid);
        playerBoosterMap.remove(uuid);
    }


    public boolean addBooster(UUID uuid, double multiplier, long duration, boolean onlineOnly) {

        PlayerBooster player = playerBoosterMap.get(uuid);

        if (player == null) return false;

        Booster booster = new Booster(UUID.randomUUID() ,multiplier, 0, onlineOnly);

        player.getBoosterList().add(booster);

        booster.addDuration(duration);
        booster.update();

        return true;
    }

    public void update(){
        sqlDatabaseService.updatePlayerBoosters();
    }


}
