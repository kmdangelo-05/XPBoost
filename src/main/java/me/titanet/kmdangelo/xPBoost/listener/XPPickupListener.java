package me.titanet.kmdangelo.xPBoost.listener;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import me.titanet.kmdangelo.xPBoost.XPBoost;
import me.titanet.kmdangelo.xPBoost.boosters.Booster;
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

        if (plugin.getBoosterManager().getActiveBooster(e.getPlayer().getUniqueId()) == null) return;

        boolean xpChatMessage = plugin.getBoosterManager().getPlayerBoosterMap().get(e.getPlayer().getUniqueId()).getPlayerPreferences().isXpChatMessage();
        Booster booster = plugin.getBoosterManager().getActiveBooster(e.getPlayer().getUniqueId());

        if (booster == null) return;

        int xpAmount = e.getAmount();
        int newAmount = (int) Math.round(xpAmount * booster.getMultiplier() * globalMultiplier);
        e.setAmount(newAmount);

        if (xpChatMessage) {
            Messages.xpChatMessage(plugin, e.getPlayer(), xpAmount, newAmount);
        }

    }
}
