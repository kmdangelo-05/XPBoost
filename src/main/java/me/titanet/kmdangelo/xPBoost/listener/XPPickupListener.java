package me.titanet.kmdangelo.xPBoost.listener;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import me.titanet.kmdangelo.xPBoost.XPBoost;
import me.titanet.kmdangelo.xPBoost.boosters.PlayerBooster;
import me.titanet.kmdangelo.xPBoost.task.XpChatMessageDelayTask;
import me.titanet.kmdangelo.xPBoost.utility.Messages;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerExpChangeEvent;

@Data
@RequiredArgsConstructor
public class XPPickupListener implements Listener {

    private final XPBoost plugin;

    @EventHandler
    public void PlayerPickupXPEvent(PlayerExpChangeEvent e) {

        double globalMultiplier = plugin.getConfig().getDouble("settings.global_xp_multiplier", 1);

        if (e.getAmount() <= 0) return;

        PlayerBooster playerBooster = plugin.getBoosterManager().getPlayerBoosterMap().get(e.getPlayer().getUniqueId());

        if (!playerBooster.getPlayerPreferences().isActiveBoosters()) return;

        boolean xpChatMessage = playerBooster.getPlayerPreferences().isXpChatMessage();

        int xpAmount = e.getAmount();
        int newAmount = (int) Math.round(xpAmount * (playerBooster.getTotalMultiplier() + globalMultiplier));
        e.setAmount(newAmount);

        if (plugin.getBoosterManager().getPlayersWithXpChatMessageDelay().contains(e.getPlayer().getUniqueId())) return;

        if (xpChatMessage && !playerBooster.getBoosterList().isEmpty()) {
            XpChatMessageDelayTask delay = new XpChatMessageDelayTask(plugin, e.getPlayer().getUniqueId());
            Messages.xpChatMessage(e.getPlayer(), (int) (xpAmount*globalMultiplier), newAmount);
            delay.runTaskTimer(plugin, 0L, 20L);
        }

    }
}
