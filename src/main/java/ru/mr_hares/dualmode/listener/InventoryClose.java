package ru.mr_hares.dualmode.listener;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import ru.mr_hares.dualmode.menu.ChooseMenu;
import ru.mr_hares.dualmode.menu.ConfirmMenu;

import static ru.mr_hares.dualmode.DualMode.*;

public class InventoryClose implements Listener {

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        Player player = (Player) event.getPlayer();

        if (event.getInventory().getHolder() instanceof ChooseMenu) {
            if (!player.isOnline()) {
                return;
            }

            Bukkit.getScheduler().runTaskLater(getInstance(), () -> {
                if (!player.isOnline()) return;

                if (getDB().getMode(player.getUniqueId()) == null && !ChooseMenu.isForceClosed()) {
                    if (!(player.getOpenInventory().getTopInventory().getHolder() instanceof ChooseMenu)) {
                        player.openInventory(new ChooseMenu().getInventory());
                    }
                }
            }, 1L);
        } else if (event.getInventory().getHolder() instanceof ConfirmMenu) {
            if (!player.isOnline()) {
                return;
            }

            Bukkit.getScheduler().runTaskLater(getInstance(), () -> {
                if (!player.isOnline()) return;

                if (!ConfirmMenu.isForceClosed()) {
                    if (!(player.getOpenInventory().getTopInventory().getHolder() instanceof ChooseMenu)) {
                        player.openInventory(new ChooseMenu().getInventory());
                    }
                }
            }, 1L);
        }
    }
}
