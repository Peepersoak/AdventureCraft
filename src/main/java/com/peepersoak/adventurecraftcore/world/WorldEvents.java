package com.peepersoak.adventurecraftcore.world;

import com.peepersoak.adventurecraftcore.utils.Utils;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerPortalEvent;
import org.bukkit.event.player.PlayerTeleportEvent;

import java.util.HashMap;
import java.util.UUID;

public class WorldEvents implements Listener {

    private final HashMap<UUID, Long> cooldown = new HashMap<>();

    @EventHandler
    public void onPortalEntry(PlayerPortalEvent e) {
        Player player = e.getPlayer();
        if (player.isOp()) return;

        long duration = Utils.getSessionDuration(player);
        boolean allow = true;
        if (e.getCause() == PlayerTeleportEvent.TeleportCause.NETHER_PORTAL) {
            // Will only be allowed to enter nether if they played for more than 24 hours
            if (duration <= 86400) {
                allow = false;
            }
        } else if (e.getCause() == PlayerTeleportEvent.TeleportCause.END_PORTAL) {
            // Will only be allowed to enter nether if they played for more than 48 hours
            if (duration <= 172800) {
                allow = false;
            }
        }

        if (!allow) {
            if (cooldown.containsKey(player.getUniqueId())) {
                if (Utils.getRemainingCooldown(cooldown.get(player.getUniqueId())) <= 0) {
                    Utils.sendSyncMessage(player, "&cYou have not played enough to enter this portal!");
                    cooldown.put(player.getUniqueId(), System.currentTimeMillis() + (1000 * 5));
                }
            } else {
                cooldown.put(player.getUniqueId(), System.currentTimeMillis() + (1000 * 5));
                Utils.sendSyncMessage(player, "&cYou have not played enough to enter this portal!");
            }
            e.setCancelled(true);
        }
    }
}
