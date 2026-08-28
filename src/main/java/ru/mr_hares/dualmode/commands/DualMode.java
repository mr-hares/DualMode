package ru.mr_hares.dualmode.commands;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import ru.mr_hares.dualmode.utils.CommandTemplate;

import java.util.List;
import static ru.mr_hares.dualmode.DualMode.*;

public class DualMode extends CommandTemplate {
    public DualMode() {
        super("dualmode", getInstance());
    }

    @Override
    public void execute(CommandSender sender, String label, String[] args) {
        if (args.length == 0) {
            sender.sendMessage(colorize(String.join("\n", getMessages().getYAML().getStringList("help.commands"))));
            return;
        }

        if (args[0].equalsIgnoreCase("reload")) {
            if (!sender.hasPermission("dualmode.admin.reload")) {
                sender.sendMessage(colorize(getMessages().getString("not-permission")));
                return;
            }

            getMessages().reload();
            getInstance().reloadConfig();
            sender.sendMessage(colorize(getMessages().getString("reload")));
        } else if (args[0].equalsIgnoreCase("change")) {
            if (!sender.hasPermission("dualmode.admin.change")) {
                sender.sendMessage(colorize(getMessages().getString("not-permission")));
                return;
            }

            if (args.length < 3) {
                sender.sendMessage(colorize(getMessages().getString("help.use-change")));
                return;
            }

            OfflinePlayer player = Bukkit.getOfflinePlayer(args[1]);
            if (!player.hasPlayedBefore()) {
                sender.sendMessage(colorize(getMessages().getString("change.not-found-player")));
                return;
            }

            String mode = args[2];
            if (!mode.equals("PVP") && !mode.equals("PVE")) {
                sender.sendMessage(colorize(getMessages().getString("help.use-change")));
                return;
            }

            getDB().setMode(player.getUniqueId(), mode.toLowerCase());
            sender.sendMessage(colorize(getMessages().getString("change.message")
                    .replace("{player}", player.getName())
                    .replace("{mode}", getInstance().getConfig().getString("placeholder" +
                            ".status." + mode.toLowerCase()))
            ));

            if (player.isOnline()) {
                player.getPlayer().sendMessage(colorize(getMessages().getString("change.player-notify")
                        .replace("{player}", sender.getName())
                        .replace("{mode}", getInstance().getConfig().getString(
                                "placeholder.status." + mode.toLowerCase()))
                ));
            }

        } else if (args[0].equalsIgnoreCase("stats")) {
            if (!sender.hasPermission("dualmode.admin.stats")) {
                sender.sendMessage(colorize(getMessages().getString("not-permission")));
                return;
            }

            List<String[]> stats = getDB().getList();

            int total = stats.size();
            long pve = stats.stream().filter(f -> f[1].equals("pve")).count();
            long pvp = stats.stream().filter(f -> f[1].equals("pvp")).count();

            String message = String.join("\n", getMessages().getYAML().getStringList("stats"));
            message = message.replace("{total}", String.valueOf(total))
                    .replace("{pve}", String.valueOf(pve))
                    .replace("{pvp}", String.valueOf(pvp));

            sender.sendMessage(colorize(message));
        } else {
            sender.sendMessage(colorize(String.join("\n", getMessages().getYAML().getStringList("help.commands"))));
        }
    }

    @Override
    public List<String> complete(CommandSender sender, String[] args) {
        if (args.length == 1) {
            return List.of("reload", "change", "stats");
        }

        if (args.length == 2) {
            if (args[0].equals("change")) {
                return null;
            }
        }

        if (args.length == 3) {
            if (args[0].equals("change")) {
                return List.of("PVP", "PVE");
            }
        }

        return List.of();
    }
}
