package me.titanet.kmdangelo.xPBoost.utility;

import me.titanet.kmdangelo.xPBoost.XPBoost;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

public class Messages {

    private static String prefix = "";
    private static FileConfiguration config;

    public static void init(FileConfiguration fileConfiguration) {
        config = fileConfiguration;
        prefix = ChatColor.translateAlternateColorCodes(
                '&', config.getString("Lang.prefix", "&8[&6XPBoost&8] ")
        );
    }

    public static void noPlayerFoundMessage(Player messageReceiver) {
        messageReceiver.sendMessage(prefix + ChatColor.translateAlternateColorCodes(
                '&', config.getString(
                        "Lang.not_found_player_message", "&4Player not found&f"
                )));
    }

    public static void confirmBoosterResetMessage(Player messageReceiver) {
        messageReceiver.sendMessage(prefix + ChatColor.translateAlternateColorCodes('&', config.getString(
                "Lang.confirm_boosters_reset",
                "&4&lAre you sure to reset all your boosters?\nSend &r&2/xpboost reset confirm &4&lto confirm&f&r"
        )));
    }

    public static void commandNotFoundMessage(Player messageReceiver) {
        messageReceiver.sendMessage(prefix + ChatColor.translateAlternateColorCodes(
                '&', config.getString(
                        "Lang.default_command_message", "&4The command does not exist&f"
                )));
    }

    public static void incompleteCommandMessage(Player messageReceiver) {
        messageReceiver.sendMessage(prefix + ChatColor.translateAlternateColorCodes(
                '&', config.getString(
                        "Lang.incomplete_command_message", "&4The command is invalid&f"
                )));
    }

    public static void noArgsIntoCommandMessage(Player messageReceiver) {
        messageReceiver.sendMessage(prefix + ChatColor.translateAlternateColorCodes(
                '&', config.getString(
                        "Lang.no_args_command_message", "&4No args found. Send /xpboost help for help&f"
                )));
    }

    public static void helpCommandMessage(Player messageReceiver) {
        messageReceiver.sendMessage(prefix + ChatColor.translateAlternateColorCodes(
                '&', config.getString(
                        "Lang.help_command_message", "&4No help message found.&f"
                )));
    }

    public static void xpChatMessage(Player messageReceiver, int oldXp, int newXp) {
        messageReceiver.sendMessage(prefix + ChatColor.translateAlternateColorCodes(
                '&', config.getString(
                        "Lang.xp_chat_message", "&4No xp message found.&f"
                )
                        .replace("%old_xp%", String.valueOf(oldXp))
                        .replace("%new_xp%", String.valueOf(newXp))));
    }

    public static void successfullyAddedBoosterMessage(Player messageReceiver, double multiplier, long duration) {
        messageReceiver.sendMessage(prefix + ChatColor.translateAlternateColorCodes(
                '&', config.getString(
                                "Lang.successfully_added_booster_message", "&4No message found.&f"
                        )
                        .replace("%multiplier%", String.valueOf(multiplier))
                        .replace("%duration%", TimeStamp.formattingTime(duration/1000))));
    }

    public static void successfullyAddedBoosterMessage(Player messageReceiver, double multiplier, long duration, Player boosterReceiver) {
        messageReceiver.sendMessage(prefix + ChatColor.translateAlternateColorCodes(
                '&', config.getString(
                                "Lang.successfully_added_booster_message_with_receiver_name", "&4No message found.&f"
                        )
                        .replace("%multiplier%", String.valueOf(multiplier))
                        .replace("%duration%", TimeStamp.formattingTime(duration/1000))
                        .replace("%receiver_name%", boosterReceiver.getName())));
    }

    public static void successfullyRemovedBoosterMessage(Player messageReceiver, double multiplier) {
        messageReceiver.sendMessage(prefix + ChatColor.translateAlternateColorCodes(
                '&', config.getString(
                                "Lang.successfully_removed_booster_message", "&4No message found.&f"
                        )
                        .replace("%multiplier%", String.valueOf(multiplier))));
    }

    public static void successfullyRemovedBoosterMessage(Player messageReceiver, double multiplier, Player boosterReceiver) {
        messageReceiver.sendMessage(prefix + ChatColor.translateAlternateColorCodes(
                '&',config.getString(
                                "Lang.successfully_removed_booster_message_with_receiver_name", "&4No message found.&f"
                        )
                        .replace("%multiplier%", String.valueOf(multiplier))
                        .replace("%receiver_name%", boosterReceiver.getName())));
    }

    public static void playerHasNoBoostersMessage(Player messageReceiver) {
        messageReceiver.sendMessage(prefix + ChatColor.translateAlternateColorCodes(
                '&', config.getString(
                        "Lang.player_has_no_boosters_message", "&4The player has no booster&f"
                )));
    }

    public static void modificationOccurredSuccessfullyMessage(Player messageReceiver) {
        messageReceiver.sendMessage(prefix + ChatColor.translateAlternateColorCodes(
                '&', config.getString(
                        "Lang.modification_occurred_successfully_message", "&3Modification occurred successfully&f"
                )));
    }

    public static void boosterNotFoundMessage(Player messageReceiver) {
        messageReceiver.sendMessage(prefix + ChatColor.translateAlternateColorCodes(
                '&', config.getString(
                        "Lang.booster_not_found_message", "&4Booster not found&f"
                )));
    }

    public static void confirmedResetMessage(Player messageReceiver) {
        messageReceiver.sendMessage(prefix + ChatColor.translateAlternateColorCodes(
                '&', config.getString(
                        "Lang.confirmed_reset_message", "&3reset confirmed&f"
                )));
    }

    public static void successfullyToggledSelfBoostersMessage(Player messageReceiver, boolean active) {
        if (active) {
            messageReceiver.sendMessage(prefix + ChatColor.translateAlternateColorCodes(
                    '&', config.getString(
                            "Lang.confirmed_activated_self_boosters_message", "&3boosters activated&f"
                    )));
        } else {
            messageReceiver.sendMessage(prefix + ChatColor.translateAlternateColorCodes(
                    '&', config.getString(
                            "Lang.confirmed_deactivated_self_boosters_message", "&3boosters deactivated&f"
                    )));
        }

    }

    public static void successfullyToggledOthersBoostersMessage(Player messageReceiver, boolean active, Player receiver) {
        if (active) {
            messageReceiver.sendMessage(prefix + ChatColor.translateAlternateColorCodes(
                    '&', config.getString(
                            "Lang.confirmed_activated_others_boosters_message", "&3boosters activated&f"
                    ).replace("%receiver%", receiver.getName())));
        } else {
            messageReceiver.sendMessage(prefix + ChatColor.translateAlternateColorCodes(
                    '&', config.getString(
                            "Lang.confirmed_deactivated_others_boosters_message", "&3boosters deactivated&f"
                    ).replace("%receiver%", receiver.getName())));
        }

    }

    public static void expiredBoosterMessage(Player messageReceiver, double multiplier) {
        messageReceiver.sendMessage(prefix + ChatColor.translateAlternateColorCodes(
                '&', config.getString(
                                "Lang.expired_booster_message", "&4Your " + multiplier + "x booster is expired!&f"
                        )
                        .replace("%multiplier%", String.valueOf(multiplier))));
    }

}
