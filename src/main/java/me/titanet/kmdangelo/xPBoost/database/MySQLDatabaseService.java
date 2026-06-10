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
                    booster_uuid      VARCHAR(36)      NOT NULL,
                    PRIMARY KEY (booster_uuid)
                ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
            """;

    @Language("MySQL")
    private static final String CREATE_BOOSTERS_TABLE = """
                CREATE TABLE IF NOT EXISTS boosters (
                    booster_uuid                VARCHAR(36)         NOT NULL,
                    multiplier                  DOUBLE              NOT NULL,
                    remaining_duration          BIGINT UNSIGNED     NOT NULL,
                    final_time_millis           BIGINT              NOT NULL,
                    starting_duration_millis    BIGINT              NOT NULL,
                    online_only                 BOOLEAN             NOT NULL,
                    PRIMARY KEY (booster_uuid)
                ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
            """;

    @Language("MySQL")
    private static final String CREATE_PREFERENCES_TABLE = """
                CREATE TABLE IF NOT EXISTS preferences (
                    player_uuid       VARCHAR(36) NOT NULL,
                    action_bar        BOOLEAN     NOT NULL DEFAULT TRUE,
                    xp_chat_message   BOOLEAN     NOT NULL DEFAULT TRUE,
                    active_boosters   BOOLEAN     NOT NULL DEFAULT TRUE,
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
             PreparedStatement createPreferences = connection.prepareStatement(CREATE_PREFERENCES_TABLE);
             PreparedStatement createBoosters = connection.prepareStatement(CREATE_BOOSTERS_TABLE)
        ) {
            createPlayerBoosters.executeUpdate();
            createPreferences.executeUpdate();
            createBoosters.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public CompletableFuture<PlayerBooster> getPlayerBoosters(UUID playerUUID) {
        return CompletableFuture.supplyAsync(() -> {
            List<Booster> boosters = new ArrayList<>();

            try (Connection connection = connector.getConnection();
                 PreparedStatement playerBoostersStatement = connection.prepareStatement(Query.GET_PLAYER_BOOSTERS.getQuery());
                 PreparedStatement playerPreferencesStatement = connection.prepareStatement(Query.GET_PLAYER_PREFERNCES.getQuery())) {

                playerBoostersStatement.setString(1, playerUUID.toString());
                ResultSet rs = playerBoostersStatement.executeQuery();

                if (rs == null) return null;

                while (rs.next()) {
                    UUID uuid = UUID.fromString(rs.getString("booster_uuid"));
                    double multiplier = rs.getDouble("multiplier");
                    long remainingDuration = rs.getLong("remaining_duration");
                    long finalTimeMillis = rs.getLong("final_time_millis");
                    long startingDurationMillis = rs.getLong("starting_duration_millis");
                    boolean onlineOnly = rs.getBoolean("online_only");

                    Booster booster;

                    if (onlineOnly) {
                        booster = new Booster(uuid ,multiplier, remainingDuration, startingDurationMillis, true);
                    } else {
                        if (finalTimeMillis <= System.currentTimeMillis()) continue;
                        booster = new Booster(uuid,multiplier, finalTimeMillis-System.currentTimeMillis(), startingDurationMillis, false);
                    }

                    boosters.add(booster);
                }

                playerPreferencesStatement.setString(1, playerUUID.toString());
                rs = playerPreferencesStatement.executeQuery();

                if (rs == null) return null;

                PlayerPreferences playerPreferences = new PlayerPreferences();

                if (rs.next()) {
                    boolean actionBar = rs.getBoolean("action_bar");
                    boolean xpChatMessage = rs.getBoolean("xp_chat_message");
                    boolean activeBoosters = rs.getBoolean("active_boosters");

                    playerPreferences = new PlayerPreferences(actionBar, xpChatMessage, activeBoosters);
                }
                PlayerBooster playerBooster = new PlayerBooster(playerUUID, boosters, new ArrayList<>(), playerPreferences);

                playerBooster.updateTotalMultiplier();
                playerBooster.updateLongestDuration();

                return playerBooster;

            } catch (SQLException e) {
                plugin.getLogger().severe(e.getMessage() + " at getPlayerBoosters");
            }

            PlayerBooster playerBooster = new PlayerBooster(playerUUID, boosters, new ArrayList<>(), new PlayerPreferences());

            playerBooster.updateTotalMultiplier();
            playerBooster.updateLongestDuration();

            return playerBooster;

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

                try (PreparedStatement insertPlayerBoosterStatement = connection.prepareStatement(Query.INSERT_ALL_PLAYER_BOOSTERS.getQuery());
                     PreparedStatement deletedPlayerBoosterStatement = connection.prepareStatement(Query.DELETE_PLAYER_BOOSTER.getQuery());
                     PreparedStatement deletedBoosterStatement = connection.prepareStatement(Query.DELETE_BOOSTER.getQuery());
                     PreparedStatement insertBoostersStatement = connection.prepareStatement(Query.INSERT_ALL_BOOSTERS.getQuery())) {

                    playerBoosterMap.forEach(((uuid, playerBooster) -> {

                        playerBooster.getRemovedBooster().forEach((removedBooster -> {

                            try {
                                deletedPlayerBoosterStatement.setString(1, removedBooster.getBoosterUuid().toString());
                                deletedBoosterStatement.setString(1, removedBooster.getBoosterUuid().toString());
                                deletedPlayerBoosterStatement.addBatch();
                                deletedBoosterStatement.addBatch();
                            } catch (SQLException e) {
                                plugin.getLogger().severe(e.getMessage() + " at updatePlayerBoosters");
                            }

                        }));

                    }));
                    deletedPlayerBoosterStatement.executeBatch();
                    deletedBoosterStatement.executeBatch();

                    playerBoosterMap.forEach((uuid, playerBooster) -> {
                        playerBooster.getRemovedBooster().clear();
                    });

                    playerBoosterMap.forEach(((uuid, playerBooster) -> {

                        playerBooster.getBoosterList().forEach((booster -> {
                            if (booster.isUpdated()) {
                                try {
                                    insertPlayerBoosterStatement.setString(1, uuid.toString());
                                    insertPlayerBoosterStatement.setString(2, booster.getBoosterUuid().toString());
                                    insertBoostersStatement.setString(1, booster.getBoosterUuid().toString());
                                    insertBoostersStatement.setDouble(2, booster.getMultiplier());
                                    insertBoostersStatement.setLong(3, booster.getDurationInMillis());
                                    insertBoostersStatement.setLong(4, System.currentTimeMillis() + booster.getDurationInMillis());
                                    insertBoostersStatement.setLong(5, booster.getStartingDurationInMillis());
                                    insertBoostersStatement.setBoolean(6, booster.isOnlineOnly());

                                    insertPlayerBoosterStatement.addBatch();
                                    insertBoostersStatement.addBatch();
                                } catch (SQLException e) {
                                    plugin.getLogger().severe(e.getMessage() + " at updatePlayerBoosters");
                                }
                            }

                        }));

                    }));

                    insertPlayerBoosterStatement.executeBatch();
                    insertBoostersStatement.executeBatch();

                    playerBoosterMap.forEach(((uuid, playerBooster) -> {

                        playerBooster.getBoosterList().forEach((booster -> {
                            if (booster.isUpdated()) {
                                booster.unUpdate();
                            }
                        }));
                    }));

                } catch (SQLException e) {
                    connection.rollback();
                    plugin.getLogger().severe(e.getMessage() + " at updatePlayerBoosters");
                }

                connection.commit();

            } catch (SQLException e) {
                plugin.getLogger().severe(e.getMessage() + " at updatePlayerBoosters");
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

                try (PreparedStatement deletePlayerBoosterStatement = connection.prepareStatement(Query.DELETE_PLAYER_BOOSTER.getQuery());
                     PreparedStatement deleteBoosterStatement = connection.prepareStatement(Query.DELETE_BOOSTER.getQuery());
                     PreparedStatement insertPlayerBoostersStatement = connection.prepareStatement(Query.INSERT_ALL_PLAYER_BOOSTERS.getQuery());
                     PreparedStatement insertBoostersStatement = connection.prepareStatement(Query.INSERT_ALL_BOOSTERS.getQuery());
                     PreparedStatement insertPreferencesStatement = connection.prepareStatement(Query.INSERT_ALL_PLAYER_PREFERENCES.getQuery()))
                {
                    playerBooster.getRemovedBooster().forEach((removedBooster -> {

                        try {
                            deletePlayerBoosterStatement.setString(1, removedBooster.getBoosterUuid().toString());
                            deleteBoosterStatement.setString(1, removedBooster.getBoosterUuid().toString());
                            deletePlayerBoosterStatement.addBatch();
                            deleteBoosterStatement.addBatch();
                        } catch (SQLException e) {
                            plugin.getLogger().severe(e.getMessage() + " at updatePlayer");
                        }

                    }));

                    deletePlayerBoosterStatement.executeBatch();
                    deleteBoosterStatement.executeBatch();

                    playerBooster.getBoosterList().forEach((booster -> {
                        if (booster.isUpdated()) {
                            try {
                                insertPlayerBoostersStatement.setString(1, playerUuid.toString());
                                insertPlayerBoostersStatement.setString(2, booster.getBoosterUuid().toString());
                                insertBoostersStatement.setString(1, booster.getBoosterUuid().toString());
                                insertBoostersStatement.setDouble(2, booster.getMultiplier());
                                insertBoostersStatement.setLong(3, booster.getDurationInMillis());
                                insertBoostersStatement.setLong(4, System.currentTimeMillis() + booster.getDurationInMillis());
                                insertBoostersStatement.setLong(5, booster.getStartingDurationInMillis());
                                insertBoostersStatement.setBoolean(6, booster.isOnlineOnly());
                                insertPlayerBoostersStatement.addBatch();
                                insertBoostersStatement.addBatch();
                            } catch (SQLException e) {
                                plugin.getLogger().severe(e.getMessage() + " at updatePlayer");
                            }
                        }

                    }));

                    insertPlayerBoostersStatement.executeBatch();
                    insertBoostersStatement.executeBatch();

                    insertPreferencesStatement.setString(1, playerBooster.getPlayerUUID().toString());
                    insertPreferencesStatement.setBoolean(2, playerBooster.getPlayerPreferences().isActionBar());
                    insertPreferencesStatement.setBoolean(3, playerBooster.getPlayerPreferences().isXpChatMessage());
                    insertPreferencesStatement.setBoolean(4, playerBooster.getPlayerPreferences().isActiveBoosters());
                    insertPreferencesStatement.executeUpdate();

                    playerBooster.getRemovedBooster().clear();
                } catch (SQLException e) {
                    connection.rollback();
                    plugin.getLogger().severe(e.getMessage() + " at updatePlayer");
                }

                connection.commit();

            } catch (SQLException e) {
                plugin.getLogger().severe(e.getMessage() + " at updatePlayer");
            }
            return true;
        }, executor).exceptionally(e -> {
            plugin.getLogger().severe("Errore updatePlayer: " + e.getMessage());
            return null;
        });

    }
}