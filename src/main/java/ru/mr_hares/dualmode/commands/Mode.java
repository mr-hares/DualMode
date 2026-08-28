package ru.mr_hares.dualmode.commands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import ru.mr_hares.dualmode.menu.ChooseMenu;
import ru.mr_hares.dualmode.utils.CommandTemplate;

import java.util.List;
import static ru.mr_hares.dualmode.DualMode.*;

public class Mode extends CommandTemplate {
    public Mode() {
        super("mode", getInstance());
    }

    @Override
    public void execute(CommandSender sender, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(colorize(getMessages().getString("only-player")));
            return;
        }

        Player player = (Player) sender;
        String time = getDB().getTime(player.getUniqueId());

        if (time != null && !player.hasPermission("dualmode.bypass.cooldown")) {
            player.sendMessage(colorize(getMessages().getString("waiting-cooldown")
                    .replace("{time}", time)));
            return;
        }

        if (combat_list.asMap().containsKey(player.getUniqueId()) || combat_list.asMap().containsValue(player.getUniqueId()) && !player.hasPermission("dualmode.bypass.combat")) {
            player.sendMessage(colorize(getMessages().getString("in-combat")));
            return;
        }

        player.openInventory(new ChooseMenu().getInventory());
    }

    @Override
    public List<String> complete(CommandSender sender, String[] args) {
        return List.of();
    }
}
