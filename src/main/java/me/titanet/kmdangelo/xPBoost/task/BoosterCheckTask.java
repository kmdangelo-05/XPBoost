package me.titanet.kmdangelo.xPBoost.task;

import me.titanet.kmdangelo.xPBoost.boosters.PlayerBooster;
import me.titanet.kmdangelo.xPBoost.XPBoost;
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

        for (PlayerBooster playerBooster : plugin.getBoosterManager().getPlayerBoosterMap().values()) {

            if (playerBooster.getBoosterList().isEmpty()) return;

            if (!playerBooster.getPlayerPreferences().isActiveBoosters()) return;

            Player player = Bukkit.getPlayer(playerBooster.getPlayerUUID());

            playerBooster.reduceAllBoostersDuration(1000);

            if (plugin.getMessagesConfig().getString("Lang.action_bar") == null) return;
            if (!plugin.getBoosterManager().getPlayerBoosterMap().get(player.getUniqueId())
                    .getPlayerPreferences().isActionBar()) return;

            playerBooster.updateLongestDuration();
            long longestDuration = playerBooster.getLongestDuration();
            double totalMultiplier = playerBooster.getTotalMultiplier();
            
            longestDuration /= 1000;

            String duration = TimeStamp.formattingTime(longestDuration);

            BaseComponent textComponent = TextComponent.fromLegacy(
                    ChatColor.translateAlternateColorCodes(
                            '&', plugin.getMessagesConfig().getString("Lang.action_bar")
                                    .replace("%multiplier%", String.format("%.1f", totalMultiplier))
                                    .replace("%remaining_time%", duration)
                    )
            );

            player.spigot().sendMessage(ChatMessageType.ACTION_BAR, textComponent);

        }

    }
}
