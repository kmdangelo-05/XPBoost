package me.titanet.kmdangelo.xPBoost.commands;

import lombok.AllArgsConstructor;
import lombok.Getter;
import me.titanet.kmdangelo.xPBoost.XPBoost;
import me.titanet.kmdangelo.xPBoost.boosters.Booster;
import me.titanet.kmdangelo.xPBoost.boosters.PlayerBooster;
import me.titanet.kmdangelo.xPBoost.gui.BoosterGui;
import me.titanet.kmdangelo.xPBoost.utility.Messages;
import me.titanet.kmdangelo.xPBoost.utility.TimeStamp;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Getter
@AllArgsConstructor
public class PluginCommands implements TabExecutor {

    private XPBoost plugin;

    @Override
    public boolean onCommand(
            @NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args
    ) {

        if (!(sender instanceof Player)) return true;

        Player player = (Player) sender;
        PlayerBooster playerBooster = plugin.getBoosterManager().getPlayerBoosterMap().get(player.getUniqueId());
        BoosterGui playerGui = new BoosterGui(plugin, player.getUniqueId());

        if (args.length == 0) {
            Messages.noArgsIntoCommandMessage(player);
            return true;
        }

        switch (args[0]) {
            case "add":
                double multiplier;
                long lengthInMillis;
                boolean onlineOnly;

                if (args.length <= 3) {
                    Messages.incompleteCommandMessage(player);
                    return true;
                } else if (args.length == 4) {
                    multiplier = Double.parseDouble(args[1]);
                    lengthInMillis = TimeStamp.fromFormattedToSeconds(args[2]) * 1000;
                    onlineOnly = Boolean.parseBoolean(args[3]);

                    playerBooster.addBooster(new Booster(UUID.randomUUID(), multiplier, lengthInMillis, onlineOnly));

                    Messages.successfullyAddedBoosterMessage(player, multiplier, lengthInMillis, player);
                    break;
                } else {
                    multiplier = Double.parseDouble(args[1]);
                    lengthInMillis = TimeStamp.fromFormattedToSeconds(args[2]) * 1000;
                    onlineOnly = Boolean.parseBoolean(args[3]);
                    Player receiver = plugin.getServer().getPlayer(args[4]);

                    if (receiver == null) {
                        Messages.noPlayerFoundMessage(player);
                        return true;
                    }

                    PlayerBooster receiverBooster = plugin.getBoosterManager().getPlayerBoosterMap().get(receiver.getUniqueId());

                    receiverBooster.addBooster(new Booster(UUID.randomUUID() ,multiplier, lengthInMillis, onlineOnly));

                    Messages.successfullyAddedBoosterMessage(player, multiplier, lengthInMillis, receiver);
                    break;
                }

            case "remove":
                if (args.length == 1) {
                    if (!playerBooster.hasBoosters()) {
                        Messages.playerHasNoBoostersMessage(player);
                        return true;
                    }
                    playerGui.openRemotionGui();
                    return true;
                } else if (args.length == 2) {
                    Player receiver = plugin.getServer().getPlayer(args[1]);

                    if (receiver == null) {
                        Messages.noPlayerFoundMessage(player);
                        return true;
                    }

                    BoosterGui receiverGui = new BoosterGui(plugin, receiver.getUniqueId());
                    PlayerBooster receiverBooster = plugin.getBoosterManager().getPlayerBoosterMap().get(receiver.getUniqueId());

                    if (!receiverBooster.hasBoosters()) {
                        Messages.playerHasNoBoostersMessage(player);
                        return true;
                    }

                    receiverGui.openRemotionGui(player);
                    return true;

                } else {
                    String identifier = args[2];
                    Player receiver = plugin.getServer().getPlayer(args[1]);

                    if (receiver == null) {
                        Messages.noPlayerFoundMessage(player);
                        return true;
                    }

                    PlayerBooster receiverBooster = plugin.getBoosterManager().getPlayerBoosterMap().get(receiver.getUniqueId());

                    for (Booster booster : receiverBooster.getBoosterList()) {
                        if (booster.toString().equals(identifier)) {
                            receiverBooster.removeBooster(booster);
                            Messages.successfullyRemovedBoosterMessage(player, booster.getMultiplier());
                            return true;
                        }
                        Messages.boosterNotFoundMessage(player);
                        return true;
                    }

                    return true;
                }

            case "reset":
                if (args.length == 1) {
                    Messages.confirmBoosterResetMessage(player);
                    return true;
                }

                if (args[1].equals("confirm")) {

                    if (args.length == 2) {

                        playerBooster.clearBoosterList();

                        Messages.confirmedResetMessage(player);

                    } else {

                        Player receiver = plugin.getServer().getPlayer(args[2]);

                        if (receiver == null) {
                            Messages.noPlayerFoundMessage(player);
                            return true;
                        }

                        PlayerBooster receiverBooster = plugin.getBoosterManager().getPlayerBoosterMap().get(receiver.getUniqueId());

                        receiverBooster.clearBoosterList();

                        Messages.confirmedResetMessage(player);

                    }
                }
                break;

            case "preferences":

                if (args.length == 1) {
                    BoosterGui boosterGui = new BoosterGui(plugin, player.getUniqueId());
                    boosterGui.openPreferences();
                    return true;
                }

                if (args.length >= 2) {
                    switch (args[1]) {
                        case "action_bar":
                            if (args.length == 3) {
                                switch (args[2]) {
                                    case "true":
                                        playerBooster.getPlayerPreferences().setActionBar(true);
                                        Messages.modificationOccurredSuccessfullyMessage(player);
                                        break;
                                    case "false":
                                        playerBooster.getPlayerPreferences().setActionBar(false);
                                        Messages.modificationOccurredSuccessfullyMessage(player);
                                        break;
                                    default:
                                        Messages.commandNotFoundMessage(player);
                                }
                            }
                            break;

                        case "xp_chat_message":
                            if (args.length == 3) {
                                switch (args[2]) {
                                    case "true":
                                        playerBooster.getPlayerPreferences().setXpChatMessage(true);
                                        Messages.modificationOccurredSuccessfullyMessage(player);
                                        break;
                                    case "false":
                                        playerBooster.getPlayerPreferences().setXpChatMessage(false);
                                        Messages.modificationOccurredSuccessfullyMessage(player);
                                        break;
                                    default:
                                        Messages.commandNotFoundMessage(player);
                                }
                                break;
                            }
                            break;
                        case "toggle_boosters" :
                            if (args.length == 3) {
                                Player receiver = plugin.getServer().getPlayer(args[2]);

                                if (receiver == null) {
                                    Messages.noPlayerFoundMessage(player);
                                    return true;
                                }

                                PlayerBooster receiverBooster = plugin.getBoosterManager().getPlayerBoosterMap().get(receiver.getUniqueId());

                                receiverBooster.getPlayerPreferences().toggleBoosters();
                                Messages.successfullyToggledOthersBoostersMessage(player, receiverBooster.getPlayerPreferences().isActiveBoosters(), receiver);
                                return true;
                            }
                            playerBooster.getPlayerPreferences().toggleBoosters();
                            Messages.successfullyToggledSelfBoostersMessage(player, playerBooster.getPlayerPreferences().isActiveBoosters());
                            break;
                        default:
                            Messages.commandNotFoundMessage(player);
                            break;
                    }
                }
                break;

            case "help":
                Messages.helpCommandMessage(player);
                break;

            case "list":
                if (args.length < 2) {
                    if (playerBooster == null) {
                        Messages.playerHasNoBoostersMessage(player);
                        return true;
                    }
                    playerGui.openListGui();
                } else {
                    Player receiver = plugin.getServer().getPlayer(args[1]);

                    if (receiver == null) {
                        Messages.noPlayerFoundMessage(player);
                        return true;
                    }

                    BoosterGui receiverGui = new BoosterGui(plugin, receiver.getUniqueId());
                    PlayerBooster receiverBooster = plugin.getBoosterManager().getPlayerBoosterMap().get(receiver.getUniqueId());

                    if (receiverBooster == null) {
                        Messages.playerHasNoBoostersMessage(player);
                        return true;
                    }

                    receiverGui.openListGui(player);

                }
                break;

            default:
                Messages.commandNotFoundMessage(player);

        }

        return true;
    }

    @Nullable
    @Override
    public List<String> onTabComplete(
            @NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args
    ) {
        List<String> completions = new ArrayList<>();
        List<String> onlinePlayers = Bukkit.getOnlinePlayers().stream().map(Player::getName).toList();

        switch (args.length) {

            case 1: // primo argomento — sottocomandi
                completions.addAll(List.of("add", "remove", "reset", "list", "preferences", "help"));
                break;

            case 2: // secondo argomento — dipende dal sottocomando
                switch (args[0]) {
                    case "add":
                        completions.add("<multiplier>"); // es. 2.0
                        break;
                    case "remove":
                        completions.addAll(onlinePlayers);
                        break;
                    case "reset":
                        completions.add("confirm");
                        break;
                    case "list":
                        completions.addAll(onlinePlayers);
                        break;
                    case "preferences":
                        completions.addAll(List.of("action_bar", "xp_chat_message", "toggle_boosters"));
                        break;
                }
                break;

            case 3: // terzo argomento
                switch (args[0]) {
                    case "add":
                        completions.add("<duration>"); // es. 1h30m
                        break;
                    case "remove":
                        PlayerBooster pb = plugin.getBoosterManager().getPlayerBoosterMap().get(((Player) sender).getUniqueId());
                        if (pb != null) {
                            pb.getBoosterList().forEach(b -> completions.add(b.toString()));
                        }
                        break;
                    case "reset":
                        completions.addAll(onlinePlayers);
                        break;
                    case "preferences":
                        if (args[2].equals("toggle_boosters")){
                            completions.addAll(onlinePlayers);
                        } else {
                            completions.addAll(List.of("true", "false"));
                        }
                        break;
                }
                break;

            case 4: // quarto argomento — solo per give/add (username opzionale)
                switch (args[0]) {
                    case "add":
                        completions.addAll(List.of("true", "false"));
                        break;
                }
                break;
            case 5:
                switch (args[0]) {
                    case "add":
                        completions.addAll(onlinePlayers);
                }
                break;
        }

        // filtra i suggerimenti in base a quello che ha già scritto
        String current = args[args.length - 1].toLowerCase();
        return completions.stream().filter(s -> s.toLowerCase().startsWith(current)).collect(Collectors.toList());
    }


}
