package ru.mr_hares.dualmode.listener;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import ru.mr_hares.dualmode.menu.ChooseMenu;

import static ru.mr_hares.dualmode.DualMode.*;

public class PlayerJoin implements Listener {

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Bukkit.getScheduler().scheduleSyncDelayedTask(getInstance(), () -> {
            Player player = event.getPlayer();
            if (getDB().getMode(player.getUniqueId()) == null) {
                event.getPlayer().openInventory(new ChooseMenu().getInventory());
            }
        }, 10L);
    }
}
