package me.titanet.kmdangelo.xPBoost.task;

import me.titanet.kmdangelo.xPBoost.boosters.PlayerBooster;
import me.titanet.kmdangelo.xPBoost.XPBoost;
import me.titanet.kmdangelo.xPBoost.boosters.Booster;
import me.titanet.kmdangelo.xPBoost.utility.TimeStamp;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class BoosterCheckTask extends BukkitRunnable {

    private final XPBoost plugin;

    public BoosterCheckTask(XPBoost plugin) {
        this.plugin = plugin;

    }

    @Override
    public void run() {

        for(PlayerBooster playerBooster: plugin.getBoosterManager().getPlayerBoosterMap().values()) {

            Booster booster = playerBooster.getActiveBooster();
            if (booster == null && !playerBooster.getBoosterList().isEmpty()) {
                playerBooster.setActiveBooster(playerBooster.getBoosterList().getLast());
                return;
            }

            if (playerBooster.getBoosterList().isEmpty()) {
                if (playerBooster.getActiveBooster() == null) return;
                playerBooster.setActiveBooster(null);
            }

            if (booster == null) return;

            Player player = Bukkit.getPlayer(playerBooster.getPlayerUUID());

            if (booster.getDurationInMillis() < 0) {
                playerBooster.getBoosterList().remove(booster);
                playerBooster.getRemovedBooster().add(booster);

                if (playerBooster.getBoosterList().isEmpty()) return;

                playerBooster.setActiveBooster(playerBooster.getBoosterList().getLast());
                continue;
            }

            if (playerBooster.getRemovedBooster().contains(booster)) {
                if (playerBooster.getBoosterList().isEmpty()) return;
                playerBooster.setActiveBooster(playerBooster.getBoosterList().getLast());
                continue;
            }

            booster.removeDuration(1000);
            booster.update();
            if (plugin.getMessagesConfig().getString("Lang.action_bar") == null) return;
            if (!plugin.getBoosterManager().getPlayerBoosterMap().get(player.getUniqueId()).getPlayerPreferences().isActionBar()) return;

            long remainedTimeInSeconds = booster.getDurationInMillis()/1000;
            int weeks = Math.toIntExact(remainedTimeInSeconds / 604800);
            int days = Math.toIntExact(remainedTimeInSeconds % 604800 / 86400);
            int hours = Math.toIntExact(remainedTimeInSeconds % 604800 % 86400 / 3600);
            int minutes = Math.toIntExact(remainedTimeInSeconds % 604800 % 86400 % 3600 / 60);
            int seconds = Math.toIntExact(remainedTimeInSeconds % 604800 % 86400 % 3600 % 60);
            String duration = TimeStamp.fromSecondsToWeeksDaysHoursMinutesSeconds(remainedTimeInSeconds);

            BaseComponent textComponent = TextComponent.fromLegacy(
                    ChatColor.translateAlternateColorCodes(
                            '&', plugin.getMessagesConfig().getString("Lang.action_bar")
                                    .replace("%multiplier%", String.valueOf(booster.getMultiplier()))
                                    .replace("%remaining_time%", duration)
                                    .replace("%weeks%", String.valueOf(weeks))
                                    .replace("%days%", String.valueOf(days))
                                    .replace("%hours%", String.valueOf(hours))
                                    .replace("%minutes%", String.valueOf(minutes))
                                    .replace("%seconds%", String.valueOf(seconds))
                    )
            );

            player.spigot().sendMessage(ChatMessageType.ACTION_BAR, textComponent);
        }

    }
}
