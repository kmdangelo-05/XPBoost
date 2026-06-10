package me.titanet.kmdangelo.xPBoost.boosters.settings;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import me.titanet.kmdangelo.xPBoost.XPBoost;


@Getter
@Setter
@AllArgsConstructor
public class PlayerPreferences {

    private boolean actionBar;
    private boolean xpChatMessage;
    private boolean activeBoosters;

    public PlayerPreferences() {
        actionBar = true;
        xpChatMessage = true;
        activeBoosters = true;
    }

    public boolean toggleBoosters() {
        if (isActiveBoosters()) {
            setActiveBoosters(false);
        } else {
            setActiveBoosters(true);
        }
        return activeBoosters;
    }
}
