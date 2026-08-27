package ru.mr_hares.chooseMode.commands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import ru.mr_hares.chooseMode.menu.ChooseMenu;
import ru.mr_hares.chooseMode.utils.CommandTemplate;

import java.util.List;
import static ru.mr_hares.chooseMode.ChooseMode.*;

public class Mode extends CommandTemplate {
    public Mode() {
        super("mode", getInstance());
    }

    @Override
    public void execute(CommandSender sender, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(color(getMessages().getString("only-player")));
            return;
        }

        Player player = (Player) sender;
        String time = getDB().getTime(player.getUniqueId());

        if (time != null && !player.hasPermission("choosemode.bypass.cooldown")) {
            player.sendMessage(color(getMessages().getString("waiting-cooldown")
                    .replace("{time}", time)));
            return;
        }

        if (combat_list.asMap().containsKey(player.getUniqueId()) || combat_list.asMap().containsValue(player.getUniqueId()) && !player.hasPermission("choosemode.bypass.combat")) {
            player.sendMessage(color(getMessages().getString("in-combat")));
            return;
        }

        player.openInventory(new ChooseMenu().getInventory());
    }

    @Override
    public List<String> complete(CommandSender sender, String[] args) {
        return List.of();
    }
}
