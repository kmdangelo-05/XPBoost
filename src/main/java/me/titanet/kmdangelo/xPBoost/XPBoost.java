package me.titanet.kmdangelo.xPBoost;

import com.zaxxer.hikari.HikariDataSource;
import lombok.Getter;
import me.titanet.kmdangelo.xPBoost.commands.PluginCommands;
import me.titanet.kmdangelo.xPBoost.database.MySQLDatabaseConnector;
import me.titanet.kmdangelo.xPBoost.database.MySQLDatabaseService;
import me.titanet.kmdangelo.xPBoost.boosters.Booster;
import me.titanet.kmdangelo.xPBoost.listener.PlayerJoinOrLeaveServerListener;
import me.titanet.kmdangelo.xPBoost.listener.XPPickupListener;
import me.titanet.kmdangelo.xPBoost.manager.BoosterManager;
import me.titanet.kmdangelo.xPBoost.task.AutoSaveTask;
import me.titanet.kmdangelo.xPBoost.task.BoosterCheckTask;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

@Getter
public final class XPBoost extends JavaPlugin {

    private FileConfiguration messagesConfig;

    private BoosterManager boosterManager;

    private MySQLDatabaseService dataService;

    @Override
    public void onEnable() {

        this.boosterManager = new BoosterManager();

        this.dataService = new MySQLDatabaseService(this);

        PluginCommands pluginCommands = new PluginCommands(this);

        this.saveDefaultConfig();
        File file = new File(this.getDataFolder(), "messages.yml");
        if (!file.exists()) this.saveResource("messages.yml", false);
        messagesConfig = YamlConfiguration.loadConfiguration(file);

        AutoSaveTask autoSaveTask = new AutoSaveTask(this);
        autoSaveTask.runTaskTimerAsynchronously(this,
                (long) getConfig().getInt("database.update_delay_from_server_start", 0)*20,
                (long) getConfig().getInt("database.update_every_seconds", 300)*20
        );

        getServer().getPluginManager().registerEvents(new PlayerJoinOrLeaveServerListener(this), this);
        getServer().getPluginManager().registerEvents(new XPPickupListener(this), this);

        getCommand("xpboost").setExecutor(pluginCommands);
        getCommand("xpboost").setTabCompleter(pluginCommands);

        dataService.createTable();

        boosterManager.setSqlDatabaseService(dataService);

        for (Player player : this.getServer().getOnlinePlayers()) {
            boosterManager.loadUser(player.getUniqueId());
        }

        BoosterCheckTask boosterCheckTask = new BoosterCheckTask(this);
        boosterCheckTask.runTaskTimer(this, 0L, 20L);

    }

    @Override
    public void onDisable() {

        boosterManager.update();
        dataService.getConnector().disconnect();

    }


}
