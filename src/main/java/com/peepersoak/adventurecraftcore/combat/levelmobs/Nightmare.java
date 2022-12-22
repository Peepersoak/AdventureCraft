package com.peepersoak.adventurecraftcore.combat.levelmobs;

import com.peepersoak.adventurecraftcore.utils.StringPath;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Monster;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;

public class Nightmare extends BukkitRunnable {

    public Nightmare() {
        World tempW = null;
        for (World w : Bukkit.getWorlds()) {
            if (w.getEnvironment() == World.Environment.NORMAL) {
                tempW = w;
                break;
            }
        }
        world = tempW;
    }
    private final World world;

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
            if (mob.getPersistentDataContainer().has(StringPath.WARD_WITCH, PersistentDataType.STRING)) continue;
            mob.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 100, 0));
        }
    }
}
