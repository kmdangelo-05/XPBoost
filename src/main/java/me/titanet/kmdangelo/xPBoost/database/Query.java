package me.titanet.kmdangelo.xPBoost.database;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum Query {

    GET_PLAYER_BOOSTERS("""
        SELECT b.multiplier, b.remaining_duration, b.final_time_millis, b.online_only, b.booster_uuid, b.starting_duration_millis
        FROM player_boosters pb
        JOIN boosters b ON pb.booster_uuid = b.booster_uuid
        WHERE pb.player_uuid = ?
        ORDER BY b.remaining_duration ASC
    """),

    DELETE_PLAYER_BOOSTER("""
        DELETE FROM player_boosters WHERE booster_uuid = ?
    """),

    DELETE_BOOSTER("""
        DELETE FROM boosters WHERE booster_uuid = ?
    """),

    INSERT_ALL_PLAYER_BOOSTERS("""
        INSERT IGNORE INTO player_boosters (player_uuid, booster_uuid)
        VALUES (?,?)
    """),

    INSERT_ALL_BOOSTERS("""
        INSERT INTO boosters (booster_uuid, multiplier, remaining_duration, final_time_millis, starting_duration_millis, online_only)
        VALUES (?,?,?,?,?,?)
        ON DUPLICATE KEY UPDATE
        booster_uuid = VALUES(booster_uuid),
        multiplier = VALUES(multiplier),
        remaining_duration = VALUES(remaining_duration),
        final_time_millis = VALUES(final_time_millis),
        starting_duration_millis = VALUES(starting_duration_millis),
        online_only = VALUES(online_only)
    """),

    INSERT_ALL_PLAYER_PREFERENCES("""
        INSERT INTO preferences (player_uuid, action_bar, xp_chat_message, active_boosters)
        VALUES(?,?,?,?)
        ON DUPLICATE KEY UPDATE
        player_uuid = VALUES(player_uuid),
        action_bar = VALUES(action_bar),
        xp_chat_message = VALUES(xp_chat_message),
        active_boosters = VALUES(active_boosters)
    """),

    GET_PLAYER_PREFERNCES("""
        SELECT *
        FROM preferences
        WHERE player_uuid = ?
    """);


    private final String query;


}