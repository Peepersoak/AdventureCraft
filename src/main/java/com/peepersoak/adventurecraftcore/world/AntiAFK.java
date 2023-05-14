package com.peepersoak.adventurecraftcore.world;

import com.peepersoak.adventurecraftcore.AdventureCraftCore;
import com.peepersoak.adventurecraftcore.utils.StringPath;
import com.peepersoak.adventurecraftcore.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class AntiAFK implements Listener {

    public AntiAFK() {
        runnable();
    }

    private final HashMap<UUID, String> lastLook = new HashMap<>();
    private final HashMap<UUID, Long> timer = new HashMap<>();

    @EventHandler
    public void onMove(PlayerMoveEvent e) {
        Player player = e.getPlayer();
        UUID uuid = player.getUniqueId();
        Location loc = player.getLocation();
        float yaw = loc.getYaw();
        float pitch = loc.getPitch();

        String lastLookLocation = yaw + ":" + pitch;
        if (lastLook.containsKey(uuid)) {
            String value = lastLook.get(uuid);
            if (!value.equalsIgnoreCase(lastLookLocation)) {
                lastLook.replace(uuid, lastLookLocation);
                timer.put(uuid, System.currentTimeMillis());
            }
        } else {
            lastLook.put(uuid, lastLookLocation);
            timer.put(uuid, System.currentTimeMillis());
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        UUID uuid = e.getPlayer().getUniqueId();
        timer.remove(uuid);
        lastLook.remove(uuid);
    }

    private void runnable() {
        long fiveMinutesInMillis = 5 * 60 * 1000;
        new BukkitRunnable() {
            @Override
            public void run() {
                long time = System.currentTimeMillis();

                List<UUID> remove = new ArrayList<>();

                for (UUID uuid : timer.keySet()) {
                    long value = timer.get(uuid);
                    if (time - value > fiveMinutesInMillis) {
                        Player player = Bukkit.getPlayer(uuid);
                        if (player != null && player.isOnline() && !player.hasPermission(StringPath.AFK_BYPASS_PERMISSION)) {
                            player.kickPlayer("Kick on the server because of AFK");
                            informOP(player.getName());
                            remove.add(uuid);
                        }
                    }
                }

                for (UUID uuid : remove) {
                    timer.remove(uuid);
                    lastLook.remove(uuid);
                }
            }
        }.runTaskTimer(AdventureCraftCore.getInstance(), 0, 20);
    }

    private void informOP(String name) {
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (player.hasPermission(StringPath.MODERATOR_PERMISSION)) {
                player.sendMessage(Utils.color("&c" + name + " &6has been kicked due to AFK"));
            }
        }
    }
}
