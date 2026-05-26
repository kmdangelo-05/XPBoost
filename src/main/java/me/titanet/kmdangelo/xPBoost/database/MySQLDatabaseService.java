package me.titanet.kmdangelo.xPBoost.database;

import lombok.Data;
import me.titanet.kmdangelo.xPBoost.boosters.PlayerBooster;
import me.titanet.kmdangelo.xPBoost.XPBoost;
import me.titanet.kmdangelo.xPBoost.boosters.Booster;
import me.titanet.kmdangelo.xPBoost.boosters.settings.PlayerPreferences;
import org.intellij.lang.annotations.Language;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

@Data
public class MySQLDatabaseService {
    private final MySQLDatabaseConnector connector;
    private Executor executor = Executors.newFixedThreadPool(2);
    private final XPBoost plugin;

    @Language("MySQL")
    private static final String CREATE_PLAYER_BOOSTERS_TABLE = """
        CREATE TABLE IF NOT EXISTS player_boosters (
            player_uuid       VARCHAR(36)      NOT NULL,
            multiplier        DOUBLE           NOT NULL,
            remaining_duration BIGINT UNSIGNED NOT NULL,
            is_active         TINYINT(1)       NOT NULL DEFAULT 0,
            created_at        DATETIME         NOT NULL DEFAULT CURRENT_TIMESTAMP,
            PRIMARY KEY (player_uuid, multiplier)
        ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
    """;

    @Language("MySQL")
    private static final String CREATE_PREFERENCES_TABLE = """
    CREATE TABLE IF NOT EXISTS preferences (
        player_uuid       VARCHAR(36) NOT NULL,
        action_bar        BOOLEAN     NOT NULL DEFAULT TRUE,
        xp_chat_message   BOOLEAN     NOT NULL DEFAULT TRUE,
        PRIMARY KEY (player_uuid)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
""";

    public MySQLDatabaseService(XPBoost javaPlugin) {
        this.plugin = javaPlugin;
        this.connector = new MySQLDatabaseConnector(plugin);
        connector.connect();
    }

    public void createTable() {

        try (Connection connection = connector.getConnection();
             PreparedStatement createPlayerBoosters = connection.prepareStatement(CREATE_PLAYER_BOOSTERS_TABLE);
             PreparedStatement createPreferences = connection.prepareStatement(CREATE_PREFERENCES_TABLE)) {
            createPlayerBoosters.executeUpdate();
            createPreferences.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void insert(UUID playerUuid) {
        CompletableFuture.runAsync(() -> {

        }, executor);
    }

    public CompletableFuture<String> getUser(UUID playerUuid) {
        return CompletableFuture.supplyAsync(() -> {
            return "ciao";
        }, executor);
    }

    public CompletableFuture<PlayerBooster> getPlayerBoosters(UUID playerUUID) {
        return CompletableFuture.supplyAsync(() -> {
            List<Booster> boosters = new ArrayList<>();
            Booster activeBooster = null;

            try (Connection connection = connector.getConnection()) {
                try (PreparedStatement ps = connection.prepareStatement(Query.GET_PLAYER_BOOSTERS.getQuery()))
                {

                    ps.setString(1, playerUUID.toString());
                    ResultSet rs = ps.executeQuery();

                    if (rs == null) return null;

                    while (rs.next()) {
                        double multiplier = rs.getDouble("multiplier");
                        long remainingDuration = rs.getLong("remaining_duration");

                        Booster booster = new Booster(multiplier, remainingDuration);

                        if (rs.getBoolean("is_active")) {
                            activeBooster = booster;
                        }

                        boosters.add(booster);

                    }

                } catch (SQLException exception) {
                    plugin.getLogger().severe(exception.getMessage());
                    return null;
                }

                try (PreparedStatement ps = connection.prepareStatement(Query.GET_ALL_PLAYER_PREFERNCES.getQuery())){

                    ps.setString(1, playerUUID.toString());
                    ResultSet rs = ps.executeQuery();

                    if (rs == null) return null;

                    PlayerPreferences playerPreferences = new PlayerPreferences();

                    if (rs.next()) {
                        boolean actionBar = rs.getBoolean("action_bar");
                        boolean xpChatMessage = rs.getBoolean("xp_chat_message");

                        playerPreferences = new PlayerPreferences(actionBar, xpChatMessage);
                    }

                    return new PlayerBooster(playerUUID, boosters, new ArrayList<>(), activeBooster, playerPreferences);

                }
            } catch (SQLException e) {
                plugin.getLogger().severe(e.getMessage());
            }

            return new PlayerBooster(playerUUID, boosters, new ArrayList<>(), activeBooster, new PlayerPreferences());

        }, executor).exceptionally(e -> {
            plugin.getLogger().severe("Errore getPlayerBoosters: " + e.getMessage());
            return null;
        });
    }

    public void updatePlayerBoosters() {
        Map<UUID, PlayerBooster> playerBoosterMap = plugin.getBoosterManager().getPlayerBoosterMap();

        CompletableFuture.runAsync(() -> {
            try (Connection connection = connector.getConnection()) {
                connection.setAutoCommit(false);

                try (PreparedStatement ps = connection.prepareStatement(Query.DELETE_BOOSTER.getQuery())
                ) {
                    playerBoosterMap.forEach(((uuid, playerBooster) -> {

                        System.out.println("Updating boosters of " + plugin.getServer().getPlayer(uuid));

                        playerBooster.getBoosterList().forEach((booster -> {

                            try {

                                if (booster.isUpdated()) {
                                    ps.setString(1, uuid.toString());
                                    ps.setDouble(2, booster.getMultiplier());
                                    ps.addBatch();
                                    System.out.println("Batch added of booster " + booster.getMultiplier());
                                }

                            } catch (SQLException e) {
                                plugin.getLogger().severe(e.getMessage());
                            }

                        }));

                        playerBooster.getRemovedBooster().forEach((removedBooster -> {

                            try {
                                ps.setString(1, uuid.toString());
                                ps.setDouble(2, removedBooster.getMultiplier());
                                ps.addBatch();
                                System.out.println("Batch added of booster " + removedBooster.getMultiplier()) ;
                            } catch (SQLException e) {
                                plugin.getLogger().severe(e.getMessage());
                            }

                        }));

                    }));
                    ps.executeBatch();

                    playerBoosterMap.forEach((uuid, playerBooster) -> {
                        playerBooster.getRemovedBooster().clear();
                    });

                    System.out.println("Batch executed of updatePlayerBoosters");
                } catch (SQLException e) {
                    plugin.getLogger().severe(e.getMessage());
                }

                try (PreparedStatement ps = connection.prepareStatement(Query.INSERT_ALL_PLAYER_BOOSTERS.getQuery())) {

                    playerBoosterMap.forEach(((uuid, playerBooster) -> {

                        Booster activeBooster = playerBooster.getActiveBooster();

                        playerBooster.getBoosterList().forEach((booster -> {
                            if (booster.isUpdated()) {
                                try {
                                    ps.setString(1, uuid.toString());
                                    ps.setDouble(2, booster.getMultiplier());
                                    ps.setLong(3, booster.getDurationInMillis());
                                    ps.setBoolean(4, booster.equals(activeBooster));
                                    ps.addBatch();
                                } catch (SQLException e) {
                                    plugin.getLogger().severe(e.getMessage());
                                }
                            }

                        }));

                    }));

                    ps.executeBatch();

                    playerBoosterMap.forEach(((uuid, playerBooster) -> {

                        Booster activeBooster = playerBooster.getActiveBooster();

                        playerBooster.getBoosterList().forEach((booster -> {
                            if (booster.isUpdated()) {
                                booster.unUpdate();
                            }
                        }));
                    }));

                } catch (SQLException e) {
                    plugin.getLogger().severe(e.getMessage());
                }

                connection.commit();

            } catch (SQLException e) {
                plugin.getLogger().severe(e.getMessage());
            }


        }, executor).exceptionally(e -> {
            plugin.getLogger().severe("Errore updatePlayerBoosters: " + e.getMessage());
            return null;
        });

    }

    public void updatePlayer(UUID playerUuid) {
        PlayerBooster playerBooster = plugin.getBoosterManager().getPlayerBoosterMap().get(playerUuid);

        CompletableFuture.supplyAsync(() -> {
            try (Connection connection = connector.getConnection()) {
                connection.setAutoCommit(false);

                try (PreparedStatement ps = connection.prepareStatement(Query.DELETE_BOOSTER.getQuery())
                ) {
                    playerBooster.getBoosterList().forEach((booster -> {

                        try {

                            if (booster.isUpdated()) {
                                ps.setString(1, playerUuid.toString());
                                ps.setDouble(2, booster.getMultiplier());
                                ps.addBatch();
                            }

                        } catch (SQLException e) {
                            plugin.getLogger().severe(e.getMessage());
                        }

                    }));

                    playerBooster.getRemovedBooster().forEach((removedBooster -> {

                        try {
                            ps.setString(1, playerUuid.toString());
                            ps.setDouble(2, removedBooster.getMultiplier());
                            ps.addBatch();
                            System.out.println("Batch added of booster " + removedBooster.getMultiplier()) ;
                        } catch (SQLException e) {
                            plugin.getLogger().severe(e.getMessage());
                        }

                    }));

                    ps.executeBatch();

                    playerBooster.getRemovedBooster().clear();
                }

                try (PreparedStatement ps = connection.prepareStatement(Query.INSERT_ALL_PLAYER_BOOSTERS.getQuery())) {

                    Booster activeBooster = playerBooster.getActiveBooster();

                    playerBooster.getBoosterList().forEach((booster -> {
                        if (booster.isUpdated()) {
                            try {
                                ps.setString(1, playerUuid.toString());
                                ps.setDouble(2, booster.getMultiplier());
                                ps.setLong(3, booster.getDurationInMillis());
                                ps.setBoolean(4, booster.equals(activeBooster));
                                ps.addBatch();
                            } catch (SQLException e) {
                                plugin.getLogger().severe(e.getMessage());
                            }
                        }

                    }));

                    ps.executeBatch();
                }

                try (PreparedStatement ps = connection.prepareStatement(Query.INSERT_ALL_PLAYER_PREFERENCES.getQuery())) {
                    ps.setString(1, playerBooster.getPlayerUUID().toString());
                    ps.setBoolean(2, playerBooster.getPlayerPreferences().isActionBar());
                    ps.setBoolean(3, playerBooster.getPlayerPreferences().isXpChatMessage());
                    ps.executeUpdate();
                }

                connection.commit();

            } catch (SQLException e) {
                plugin.getLogger().severe(e.getMessage());
            }
            return true;
        }, executor).exceptionally(e -> {
            plugin.getLogger().severe("Errore updatePlayer: " + e.getMessage());
            return null;
        });

    }
}