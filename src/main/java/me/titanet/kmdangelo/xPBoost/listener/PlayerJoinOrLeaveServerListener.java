package me.titanet.kmdangelo.xPBoost.listener;

import me.titanet.kmdangelo.xPBoost.XPBoost;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerJoinOrLeaveServerListener implements Listener {

    private XPBoost plugin;

    public PlayerJoinOrLeaveServerListener(XPBoost plugin) {this.plugin = plugin;}

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        plugin.getBoosterManager().loadUser(e.getPlayer().getUniqueId());
    }

    @EventHandler
    public void onLeave(PlayerQuitEvent e) {
        plugin.getBoosterManager().unLoad(e.getPlayer().getUniqueId());
    }
}
