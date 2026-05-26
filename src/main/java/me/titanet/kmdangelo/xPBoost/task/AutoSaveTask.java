package me.titanet.kmdangelo.xPBoost.task;

import lombok.RequiredArgsConstructor;
import me.titanet.kmdangelo.xPBoost.XPBoost;
import org.bukkit.scheduler.BukkitRunnable;

@RequiredArgsConstructor
public class AutoSaveTask extends BukkitRunnable {

    private final XPBoost plugin;


    @Override
    public void run() {
        plugin.getBoosterManager().update();
    }
}
