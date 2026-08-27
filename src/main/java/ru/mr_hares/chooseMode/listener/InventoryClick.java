package ru.mr_hares.chooseMode.listener;

import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import ru.mr_hares.chooseMode.ChooseMode;
import ru.mr_hares.chooseMode.menu.ChooseMenu;
import ru.mr_hares.chooseMode.menu.ConfirmMenu;

import static ru.mr_hares.chooseMode.ChooseMode.*;

public class InventoryClick implements Listener {

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (event.getInventory().getHolder() instanceof ChooseMenu) {
            event.setCancelled(true);

            String mode = ChooseMenu.getMode(event.getCurrentItem());
            if (mode == null) return;

            ((Player) event.getWhoClicked()).playSound(event.getWhoClicked().getLocation(), Sound.UI_BUTTON_CLICK, 1.0f, 1.0f);
            ((ChooseMenu) event.getInventory().getHolder()).setForceClosed(true);
            event.getInventory().close();
            event.getWhoClicked().openInventory(new ConfirmMenu(mode).getInventory());
        }

        if (event.getInventory().getHolder() instanceof ConfirmMenu) {
            event.setCancelled(true);

            String mode = ConfirmMenu.getMode(event.getCurrentItem());
            if (mode == null) return;

            if (mode.equals("back")) {
                ((ConfirmMenu) event.getInventory().getHolder()).setForceClosed(true);
                event.getInventory().close();
                event.getWhoClicked().openInventory(new ChooseMenu().getInventory());
                ((Player) event.getWhoClicked()).playSound(event.getWhoClicked().getLocation(),
                        Sound.ENTITY_VILLAGER_NO, 1.0f,
                        1.0f);
                return;
            }

            ((ConfirmMenu) event.getInventory().getHolder()).setForceClosed(true);
            event.getInventory().close();
            ((Player) event.getWhoClicked()).playSound(event.getWhoClicked().getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1.0f, 1.0f);

            if (getDB().getMode(event.getWhoClicked().getUniqueId()) == null) {
                ChooseMode.getDB().addPlayer(event.getWhoClicked().getUniqueId(), mode);
            } else {
                getDB().setMode(event.getWhoClicked().getUniqueId(), mode);
            }

            String message = getMessages().getString("choose-mode.message");
            message = message.replace("{mode}",
                    getInstance().getConfig().getString("placeholder.status." + mode));

            if (getInstance().getConfig().getBoolean("announce.enabled", true)) {
                for (Player p:
                        Bukkit.getOnlinePlayers()) if (((Player) event.getWhoClicked()).getPlayer() != p) p.sendMessage(color(
                                getInstance().getConfig().getString("announce.message")
                                        .replace("{player}", event.getWhoClicked().getName())
                                        .replace("{mode}", getInstance().getConfig().getString("placeholder.status." + mode))
                ));
            }

            ((Player) event.getWhoClicked()).sendTitle(
                    color(getMessages().getString("choose-mode." + mode + ".title")),
                    color(getMessages().getString("choose-mode." + mode + ".subtitle"))
            );

            event.getWhoClicked().sendMessage(color(message));
        }
    }
}
