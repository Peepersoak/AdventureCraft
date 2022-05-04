package com.peepersoak.adventurecraftcore.world;

import com.peepersoak.adventurecraftcore.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

import java.util.Objects;

public class DeathLocation implements Listener {

    @EventHandler
    public void onDeath(PlayerDeathEvent e) {
        Player player = e.getEntity();
        String name = player.getName();
        Location location = player.getLocation();
        int x = location.getBlockX();
        int y = location.getBlockY();
        int z = location.getBlockZ();
        String world = Objects.requireNonNull(location.getWorld()).getName();

        String msg = "&6" + name + " &7died in " + world + " " + x + " " + y + " " + z;

        Bukkit.broadcastMessage(Utils.color(msg));
    }
}
