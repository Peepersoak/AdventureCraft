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
        int dayPassed = Utils.getDayPassed();

        boolean shouldEnter = true;
        int remainingDay = 0;

        if (e.getCause() == PlayerTeleportEvent.TeleportCause.NETHER_PORTAL) {
            if (dayPassed < 2000) {
                remainingDay = 2000 - dayPassed;
                shouldEnter = false;
            }
        }

        if (e.getCause() == PlayerTeleportEvent.TeleportCause.END_PORTAL) {
            if (dayPassed < 3000) {
                remainingDay = 3000 - dayPassed;
                shouldEnter = false;
            }
        }

        if (!shouldEnter) {
            Player player = e.getPlayer();

            if (cooldown.containsKey(player.getUniqueId())) {
                if (Utils.getRemainingCooldown(cooldown.get(player.getUniqueId())) <= 0) {
                    Utils.sendSyncMessage(player, "&cCan't Enter!! &b" + remainingDay + " &cdays left before you can enter!");
                    cooldown.put(player.getUniqueId(), System.currentTimeMillis() + (1000 * 5));
                }
            } else {
                cooldown.put(player.getUniqueId(), System.currentTimeMillis() + (1000 * 5));
            }

            e.setCancelled(true);
        }
    }
}
