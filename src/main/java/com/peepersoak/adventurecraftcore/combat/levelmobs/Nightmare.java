package com.peepersoak.adventurecraftcore.combat.levelmobs;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Monster;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;

public class Nightmare extends BukkitRunnable {

    private final World world = Bukkit.getWorld("world");

    @Override
    public void run() {
        if (world == null) {
            return;
        }

        long time = world.getTime();
        if (time < 12300 || time > 23850) return;
        List<LivingEntity> mobs = world.getLivingEntities();

        for (LivingEntity mob : mobs) {
            if (!(mob instanceof Monster)) continue;
            mob.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 100, 0));
        }
    }
}
