package ru.mr_hares.chooseMode.listener;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import static ru.mr_hares.chooseMode.ChooseMode.*;

public class PlayerDamage implements Listener {
    @EventHandler
    public void onPlayerDamage(EntityDamageByEntityEvent event) {
        Entity damager = event.getDamager();
        Entity damaged = event.getEntity();

        if (!(damager instanceof Player) || !(damaged instanceof Player)) {
            return;
        }

        String dr_status = getDB().getMode(damager.getUniqueId());
        String dd_status = getDB().getMode(damaged.getUniqueId());

        if (dr_status.equals("pvp") && dd_status.equals("pvp")) {
            if (combat_list.asMap().containsKey(damager.getUniqueId())) {
                combat_list.asMap().remove(damager.getUniqueId());
                combat_list.put(damager.getUniqueId(), damaged.getUniqueId());
            } else {
                combat_list.put(damager.getUniqueId(), damaged.getUniqueId());
            }
            return;
        }

        if (dr_status.equals("pve")) {
            ((Player) damager).spigot().sendMessage(ChatMessageType.ACTION_BAR,
                    new TextComponent(color(getMessages().getString("warnings.actionbar.for-pve"))));
            event.setCancelled(true);
        } else if (dd_status.equals("pve")) {
            ((Player) damager).spigot().sendMessage(ChatMessageType.ACTION_BAR,
                    new TextComponent(color(getMessages().getString("warnings.actionbar.for-pvp"))));
            event.setCancelled(true);
        }
    }
}
