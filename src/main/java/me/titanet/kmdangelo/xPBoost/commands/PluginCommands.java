package me.titanet.kmdangelo.xPBoost.commands;

import lombok.AllArgsConstructor;
import lombok.Getter;
import me.titanet.kmdangelo.xPBoost.XPBoost;
import me.titanet.kmdangelo.xPBoost.boosters.Booster;
import me.titanet.kmdangelo.xPBoost.boosters.PlayerBooster;
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

        if (args.length == 0) {
            Messages.noArgsIntoCommandMessage(plugin, player);
            return true;
        }

        switch (args[0]) {
            case "give":
                final double multiplier;
                final long lengthInMillis;

                if (args.length <= 2) {
                    Messages.incompleteCommandMessage(plugin, player);
                    return true;
                } else if (args.length == 3) {
                    multiplier = Double.parseDouble(args[1]);
                    lengthInMillis = Long.parseLong(args[2]) * 1000;
                    boolean success = plugin.getBoosterManager().addBooster(player.getUniqueId(), multiplier, lengthInMillis, true);
                    System.out.println("command addbooster: " + success);
                    Messages.successfullyAddedBoosterMessage(plugin, player, multiplier, lengthInMillis);
                    break;
                } else {
                    multiplier = Double.parseDouble(args[1]);
                    lengthInMillis = Long.parseLong(args[2]) * 1000;
                    Player receiver = plugin.getServer().getPlayer(args[3]);
                    if (receiver == null) {
                        Messages.noPlayerFoundMessage(plugin, player);
                        return true;
                    }
                    Messages.successfullyAddedBoosterMessage(plugin, player, multiplier, lengthInMillis, receiver);
                    plugin.getBoosterManager().addBooster(receiver.getUniqueId(), multiplier, lengthInMillis, true);
                    break;
                }

            case "add":

                if (args.length <= 2) {
                    Messages.incompleteCommandMessage(plugin, player);
                    return true;
                } else if (args.length == 3) {
                    multiplier = Double.parseDouble(args[1]);
                    lengthInMillis = Long.parseLong(args[2]) * 1000;

                    if (plugin.getBoosterManager().getPlayerBoosterMap().get(player.getUniqueId()).getBoosterList().isEmpty()) {
                        plugin.getBoosterManager().addBooster(player.getUniqueId(), multiplier, lengthInMillis, true);
                    } else {
                        plugin.getBoosterManager().addBooster(player.getUniqueId(), multiplier, lengthInMillis, false);
                    }

                    Messages.successfullyAddedBoosterMessage(plugin, player, multiplier, lengthInMillis);
                    break;
                } else {
                    multiplier = Double.parseDouble(args[1]);
                    lengthInMillis = Long.parseLong(args[2]) * 1000;
                    Player receiver = plugin.getServer().getPlayer(args[3]);

                    if (receiver == null) {
                        Messages.noPlayerFoundMessage(plugin, player);
                        return true;
                    }

                    if (plugin.getBoosterManager().getPlayerBoosterMap().get(receiver.getUniqueId()).getBoosterList().isEmpty()) {
                        plugin.getBoosterManager().addBooster(receiver.getUniqueId(), multiplier, lengthInMillis, true);
                    } else {
                        plugin.getBoosterManager().addBooster(receiver.getUniqueId(), multiplier, lengthInMillis, false);
                    }

                    Messages.successfullyAddedBoosterMessage(plugin, player, multiplier, lengthInMillis, receiver);
                    break;
                }

            case "remove":
                if (args.length == 1) {
                    Messages.incompleteCommandMessage(plugin, player);
                    return true;
                } else if (args.length == 2) {
                    multiplier = Double.parseDouble(args[1]);

                    if (plugin.getBoosterManager().getPlayerBoosterMap().get(player.getUniqueId()).getMultiplier(multiplier) == null) {
                        Messages.boosterNotFoundMessage(plugin, player);
                        return true;
                    }

                    plugin.getBoosterManager().getPlayerBoosterMap()
                            .get(player.getUniqueId())
                            .removeBooster(multiplier);
                    Messages.successfullyRemovedBoosterMessage(plugin, player, multiplier);
                    return true;

                } else {
                    multiplier = Double.parseDouble(args[1]);
                    Player receiver = plugin.getServer().getPlayer(args[2]);

                    if (receiver == null) {
                        Messages.noPlayerFoundMessage(plugin, player);
                        return true;
                    }

                    if (plugin.getBoosterManager().getPlayerBoosterMap().get(receiver.getUniqueId()).getMultiplier(multiplier) == null) {
                        Messages.boosterNotFoundMessage(plugin, player);
                        return true;
                    }

                    plugin.getBoosterManager().getPlayerBoosterMap()
                            .get(receiver.getUniqueId())
                            .removeBooster(multiplier);
                    Messages.successfullyRemovedBoosterMessage(plugin, player, multiplier, receiver);
                    return true;
                }

            case "reset":
                if (args.length == 1) {
                    Messages.confirmBoosterResetMessage(plugin, player);
                    return true;
                }

                if (args[1].equals("confirm")) {

                    if (args.length == 2) {

                        for (Booster booster : plugin.getBoosterManager().getPlayerBoosterMap().get(player.getUniqueId()).getBoosterList()) {
                            plugin.getBoosterManager().getPlayerBoosterMap().get(player.getUniqueId()).getRemovedBooster().add(booster);
                        }

                        plugin.getBoosterManager().getPlayerBoosterMap().get(player.getUniqueId()).getBoosterList().clear();
                        Messages.confirmedResetMessage(plugin, player);


                    } else {

                        Player receiver = plugin.getServer().getPlayer(args[2]);

                        if (receiver == null) {
                            Messages.noPlayerFoundMessage(plugin, player);
                            return true;
                        }

                        for (Booster booster : plugin.getBoosterManager().getPlayerBoosterMap().get(receiver.getUniqueId()).getBoosterList()) {
                            plugin.getBoosterManager().getPlayerBoosterMap().get(receiver.getUniqueId()).getRemovedBooster().add(booster);
                        }

                        plugin.getBoosterManager().getPlayerBoosterMap().get(receiver.getUniqueId()).getBoosterList().clear();
                        Messages.confirmedResetMessage(plugin, player);
                    }
                }
                break;

            case "preferences":

                if (args.length >= 2) {
                    switch (args[1]) {
                        case "actionbar":
                            if (args.length == 3) {
                                switch (args[2]) {
                                    case "true":
                                        plugin.getBoosterManager().getPlayerBoosterMap().get(player.getUniqueId()).getPlayerPreferences().setActionBar(true);
                                        Messages.modificationOccurredSuccessfullyMessage(plugin, player);
                                        break;
                                    case "false":
                                        plugin.getBoosterManager().getPlayerBoosterMap().get(player.getUniqueId()).getPlayerPreferences().setActionBar(false);
                                        Messages.modificationOccurredSuccessfullyMessage(plugin, player);
                                        break;
                                    default:
                                        Messages.commandNotFoundMessage(plugin, player);
                                }
                            }
                            break;

                        case "xpchatmessage":
                            if (args.length == 3) {
                                switch (args[2]) {
                                    case "true":
                                        plugin.getBoosterManager().getPlayerBoosterMap().get(player.getUniqueId()).getPlayerPreferences().setXpChatMessage(true);
                                        Messages.modificationOccurredSuccessfullyMessage(plugin, player);
                                        break;
                                    case "false":
                                        plugin.getBoosterManager().getPlayerBoosterMap().get(player.getUniqueId()).getPlayerPreferences().setXpChatMessage(false);
                                        Messages.modificationOccurredSuccessfullyMessage(plugin, player);
                                        break;
                                    default:
                                        Messages.commandNotFoundMessage(plugin, player);
                                }
                                break;
                            }
                        default:
                            Messages.commandNotFoundMessage(plugin, player);
                            break;
                    }
                }
                break;

            case "help":
                Messages.helpCommandMessage(plugin, player);
                break;

            case "list":
                if (args.length < 2) {
                    if (plugin.getBoosterManager().getPlayerBoosterMap().get(player.getUniqueId()) == null) {
                        Messages.noBoosterFoundMessage(plugin, player);
                        return true;
                    }
                    sender.sendMessage("Booster list of " + player.getName());
                    plugin.getBoosterManager().getPlayerBoosterMap().get(player.getUniqueId()).getBoosterList().forEach((booster) -> {
                        sender.sendMessage("Multiplier: " + booster.getMultiplier() + "; duration: " + TimeStamp.fromSecondsToWeeksDaysHoursMinutesSeconds(booster.getDurationInMillis() / 1000) + "; updated: " + booster.isUpdated());
                    });
                } else {
                    Player receiver = plugin.getServer().getPlayer(args[1]);
                    if (receiver == null) {
                        Messages.noPlayerFoundMessage(plugin, player);
                        return true;
                    }
                    if (plugin.getBoosterManager().getPlayerBoosterMap().get(receiver.getUniqueId()) == null) {
                        Messages.noBoosterFoundMessage(plugin, player);
                        return true;
                    }
                    sender.sendMessage("Booster list of " + receiver.getName());
                    plugin.getBoosterManager().getPlayerBoosterMap().get(receiver.getUniqueId()).getBoosterList().forEach((booster) -> {
                        sender.sendMessage("Multiplier: " + booster.getMultiplier() + "; duration: " + TimeStamp.fromSecondsToWeeksDaysHoursMinutesSeconds(booster.getDurationInMillis() / 1000) + '\n');
                    });
                }
                break;

            default:
                Messages.commandNotFoundMessage(plugin, player);

        }

        return true;
    }

    @Nullable
    @Override
    public List<String> onTabComplete(
            @NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args
    ) {
        List<String> completions = new ArrayList<>();
        List<String> onlinePlayers = Bukkit.getOnlinePlayers().stream()
                .map(Player::getName)
                .collect(Collectors.toList());

        switch (args.length) {

            case 1: // primo argomento — sottocomandi
                completions.addAll(List.of("give", "add", "remove", "reset", "list", "preferences", "help"));
                break;

            case 2: // secondo argomento — dipende dal sottocomando
                switch (args[0]) {
                    case "give":
                    case "add":
                        completions.add("<moltiplicatore>"); // es. 2.0
                        break;
                    case "remove":
                        // suggerisci i moltiplicatori dei booster che il player ha già
                        PlayerBooster pb = plugin.getBoosterManager().getPlayerBoosterMap().get(((Player) sender).getUniqueId());
                        if (pb != null) {
                            pb.getBoosterList().forEach(b -> completions.add(String.valueOf(b.getMultiplier())));
                        }
                        break;
                    case "reset":
                        completions.add("confirm");
                        break;
                    case "list":
                        completions.addAll(onlinePlayers);
                        break;
                    case "preferences":
                        completions.addAll(List.of("actionbar", "xpchatmessage"));
                        break;
                }
                break;

            case 3: // terzo argomento
                switch (args[0]) {
                    case "give":
                    case "add":
                        completions.add("<durata in secondi>"); // es. 3600
                        break;
                    case "remove":
                    case "reset":
                        completions.addAll(onlinePlayers);
                        break;
                    case "preferences":
                        completions.addAll(List.of("true", "false"));
                        break;
                }
                break;

            case 4: // quarto argomento — solo per give/add (username opzionale)
                switch (args[0]) {
                    case "give":
                    case "add":
                        completions.addAll(onlinePlayers);
                        break;
                }
                break;
        }

        // filtra i suggerimenti in base a quello che ha già scritto
        String current = args[args.length - 1].toLowerCase();
        return completions.stream()
                .filter(s -> s.toLowerCase().startsWith(current))
                .collect(Collectors.toList());
    }


}
