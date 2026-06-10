package me.titanet.kmdangelo.xPBoost.task;

import lombok.RequiredArgsConstructor;
import me.titanet.kmdangelo.xPBoost.XPBoost;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.UUID;

@RequiredArgsConstructor
public class XpChatMessageDelayTask extends BukkitRunnable {

    private final XPBoost plugin;
    private final UUID playerUuid;
    private final long finalTimeMillis;

    public XpChatMessageDelayTask(XPBoost plugin, UUID playerUuid) {
        this.plugin = plugin;
        this.playerUuid = playerUuid;
        finalTimeMillis = System.currentTimeMillis() + plugin.getConfig().getLong("settings.xp_message_delay")*1000;
        plugin.getBoosterManager().getPlayersWithXpChatMessageDelay().add(playerUuid);
    }

    @Override
    public void run() {
        if (finalTimeMillis <= System.currentTimeMillis()) {
            plugin.getBoosterManager().getPlayersWithXpChatMessageDelay().remove(playerUuid);
            this.cancel();
        }
    }
}
