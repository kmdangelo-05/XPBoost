package me.titanet.kmdangelo.xPBoost.database;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.intellij.lang.annotations.Language;

@RequiredArgsConstructor
@Getter
public enum Query {

    GET_PLAYER_BOOSTERS("""
        SELECT *
        FROM player_boosters
        WHERE player_uuid = ?
        ORDER BY multiplier ASC
    """),

    DELETE_BOOSTER("""
        DELETE FROM player_boosters
        WHERE player_uuid = ?
        AND multiplier = ?
    """),

    INSERT_ALL_PLAYER_BOOSTERS("""
        INSERT INTO player_boosters (player_uuid, multiplier, remaining_duration, is_active)
        VALUES (?,?,?,?)
        ON DUPLICATE KEY UPDATE
        remaining_duration = VALUES(remaining_duration),
        multiplier = VALUES(multiplier),
        is_active = VALUES(is_active)
    """),

    INSERT_ALL_PLAYER_PREFERENCES("""
        INSERT INTO preferences (player_uuid, action_bar, xp_chat_message)
        VALUES(?,?,?)
        ON DUPLICATE KEY UPDATE
        player_uuid = VALUES(player_uuid),
        action_bar = VALUES(action_bar),
        xp_chat_message = VALUES(xp_chat_message)
    """),

    GET_ALL_PLAYER_PREFERNCES("""
        SELECT *
        FROM preferences
        WHERE player_uuid = ?
    """);


    private final String query;


}