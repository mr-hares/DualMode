package ru.mr_hares.chooseMode.commands;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import ru.mr_hares.chooseMode.utils.CommandTemplate;

import java.util.List;
import static ru.mr_hares.chooseMode.ChooseMode.*;

public class ChooseMode extends CommandTemplate {
    public ChooseMode() {
        super("choosemode", getInstance());
    }

    @Override
    public void execute(CommandSender sender, String label, String[] args) {
        if (args.length == 0) {
            sender.sendMessage(color(String.join("\n", getMessages().getYAML().getStringList("help.commands"))));
            return;
        }

        if (args[0].equalsIgnoreCase("reload")) {
            if (!sender.hasPermission("choosemode.admin.reload")) {
                sender.sendMessage(color(getMessages().getString("not-permission")));
                return;
            }

            getMessages().reload();
            getInstance().reloadConfig();
            sender.sendMessage(color(getMessages().getString("reload")));
        } else if (args[0].equalsIgnoreCase("change")) {
            if (!sender.hasPermission("choosemode.admin.change")) {
                sender.sendMessage(color(getMessages().getString("not-permission")));
                return;
            }

            if (args.length < 3) {
                sender.sendMessage(color(getMessages().getString("help.use-change")));
                return;
            }

            OfflinePlayer player = Bukkit.getOfflinePlayer(args[1]);
            if (!player.hasPlayedBefore()) {
                sender.sendMessage(color(getMessages().getString("change.not-found-player")));
                return;
            }

            String mode = args[2];
            if (!mode.equals("PVP") && !mode.equals("PVE")) {
                sender.sendMessage(color(getMessages().getString("help.use-change")));
                return;
            }

            getDB().setMode(player.getUniqueId(), mode.toLowerCase());
            sender.sendMessage(color(getMessages().getString("change.message")
                    .replace("{player}", player.getName())
                    .replace("{mode}", getInstance().getConfig().getString("placeholder" +
                            ".status." + mode.toLowerCase()))
            ));

            if (player.isOnline()) {
                player.getPlayer().sendMessage(color(getMessages().getString("change.player-notify")
                        .replace("{player}", sender.getName())
                        .replace("{mode}", getInstance().getConfig().getString(
                                "placeholder.status." + mode.toLowerCase()))
                ));
            }

        } else if (args[0].equalsIgnoreCase("stats")) {
            if (!sender.hasPermission("choosemode.admin.stats")) {
                sender.sendMessage(color(getMessages().getString("not-permission")));
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

            sender.sendMessage(color(message));
        } else {
            sender.sendMessage(color(String.join("\n", getMessages().getYAML().getStringList("help.commands"))));
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
