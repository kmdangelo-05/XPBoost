package me.titanet.kmdangelo.xPBoost.utility;

import me.titanet.kmdangelo.xPBoost.XPBoost;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.UUID;

public class Messages {

    public static void noPlayerFoundMessage(XPBoost plugin, Player messageReceiver) {
        messageReceiver.sendMessage(ChatColor.translateAlternateColorCodes(
                '&', plugin.getMessagesConfig().getString(
                        "Lang.not_found_player_message", "&4Player not found&f"
                )));
    }

    public static void confirmBoosterResetMessage(XPBoost plugin, Player messageReceiver) {
        messageReceiver.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.getMessagesConfig().getString(
                "Lang.confirm_boosters_reset",
                "&4&lAre you sure to reset all your boosters?\nSend &r&2/xpboost reset confirm &4&lto confirm&f&r"
        )));
    }

    public static void commandNotFoundMessage(XPBoost plugin, Player messageReceiver) {
        messageReceiver.sendMessage(ChatColor.translateAlternateColorCodes(
                '&', plugin.getMessagesConfig().getString(
                        "Lang.default_command_message", "&4The command does not exist&f"
                )));
    }

    public static void incompleteCommandMessage(XPBoost plugin, Player messageReceiver) {
        messageReceiver.sendMessage(ChatColor.translateAlternateColorCodes(
                '&', plugin.getMessagesConfig().getString(
                        "Lang.incomplete_command_message", "&4The command is invalid&f"
                )));
    }

    public static void noArgsIntoCommandMessage(XPBoost plugin, Player messageReceiver) {
        messageReceiver.sendMessage(ChatColor.translateAlternateColorCodes(
                '&', plugin.getMessagesConfig().getString(
                        "Lang.no_args_command_message", "&4No args found. Send /xpboost help for help&f"
                )));
    }

    public static void helpCommandMessage(XPBoost plugin, Player messageReceiver) {
        messageReceiver.sendMessage(ChatColor.translateAlternateColorCodes(
                '&', plugin.getMessagesConfig().getString(
                        "Lang.help_command_message", "&4No help message found.&f"
                )));
    }

    public static void xpChatMessage(XPBoost plugin, Player messageReceiver, int oldXp, int newXp) {
        messageReceiver.sendMessage(ChatColor.translateAlternateColorCodes(
                '&', plugin.getMessagesConfig().getString(
                        "Lang.xp_chat_message", "&4No xp message found.&f"
                )
                        .replace("%old_xp%", String.valueOf(oldXp))
                        .replace("%new_xp%", String.valueOf(newXp))));
    }

    public static void successfullyAddedBoosterMessage(XPBoost plugin, Player messageReceiver, double multiplier, long duration) {
        messageReceiver.sendMessage(ChatColor.translateAlternateColorCodes(
                '&', plugin.getMessagesConfig().getString(
                                "Lang.successfully_added_booster_message", "&4No message found.&f"
                        )
                        .replace("%multiplier%", String.valueOf(multiplier))
                        .replace("%duration%", String.valueOf(duration/1000))));
    }

    public static void successfullyAddedBoosterMessage(XPBoost plugin, Player messageReceiver, double multiplier, long duration, Player boosterReceiver) {
        messageReceiver.sendMessage(ChatColor.translateAlternateColorCodes(
                '&', plugin.getMessagesConfig().getString(
                                "Lang.successfully_added_booster_message_with_receiver_name", "&4No message found.&f"
                        )
                        .replace("%multiplier%", String.valueOf(multiplier))
                        .replace("%duration%", String.valueOf(duration/1000))
                        .replace("%receiver_name%", boosterReceiver.getName())));
    }

    public static void successfullyRemovedBoosterMessage(XPBoost plugin, Player messageReceiver, double multiplier) {
        messageReceiver.sendMessage(ChatColor.translateAlternateColorCodes(
                '&', plugin.getMessagesConfig().getString(
                                "Lang.successfully_removed_booster_message", "&4No message found.&f"
                        )
                        .replace("%multiplier%", String.valueOf(multiplier))));
    }

    public static void successfullyRemovedBoosterMessage(XPBoost plugin, Player messageReceiver, double multiplier, Player boosterReceiver) {
        messageReceiver.sendMessage(ChatColor.translateAlternateColorCodes(
                '&', plugin.getMessagesConfig().getString(
                                "Lang.successfully_removed_booster_message_with_receiver_name", "&4No message found.&f"
                        )
                        .replace("%multiplier%", String.valueOf(multiplier))
                        .replace("%receiver_name%", boosterReceiver.getName())));
    }

    public static void noBoosterFoundMessage(XPBoost plugin, Player messageReceiver) {
        messageReceiver.sendMessage(ChatColor.translateAlternateColorCodes(
                '&', plugin.getMessagesConfig().getString(
                        "Lang.no_booster_found_message", "&4No message found.&f"
                )));
    }

    public static void modificationOccurredSuccessfullyMessage(XPBoost plugin, Player messageReceiver) {
        messageReceiver.sendMessage(ChatColor.translateAlternateColorCodes(
                '&', plugin.getMessagesConfig().getString(
                        "Lang.modification_occurred_successfully_message", "&3Modification occurred successfully&f"
                )));
    }

    public static void boosterNotFoundMessage(XPBoost plugin, Player messageReceiver) {
        messageReceiver.sendMessage(ChatColor.translateAlternateColorCodes(
                '&', plugin.getMessagesConfig().getString(
                        "Lang.booster_not_found_message", "&4Booster not found&f"
                )));
    }

    public static void confirmedResetMessage(XPBoost plugin, Player messageReceiver) {
        messageReceiver.sendMessage(ChatColor.translateAlternateColorCodes(
                '&', plugin.getMessagesConfig().getString(
                        "Lang.confirmed_reset_message", "&4Booster not found&f"
                )));
    }

}
