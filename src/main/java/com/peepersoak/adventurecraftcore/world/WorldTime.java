package com.peepersoak.adventurecraftcore.world;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.scheduler.BukkitRunnable;

public class WorldTime extends BukkitRunnable {

    private final World world = Bukkit.getWorld("world");

    @Override
    public void run() {
        if (world == null) {
            this.cancel();
            return;
        }
        long current = world.getTime();
        current += 13;
        world.setTime(current);
    }
}
