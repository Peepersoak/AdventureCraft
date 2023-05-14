package com.peepersoak.adventurecraftcore.items.arrows;

import com.peepersoak.adventurecraftcore.AdventureCraftCore;
import com.peepersoak.adventurecraftcore.utils.StringPath;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.*;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

public class ArrowEffects {

    public ArrowEffects(LivingEntity target, Player player) {
        this.player = player;
        this.target = target;
        if (target != null) {
            world = target.getWorld();
        }
    }

    private final Player player;
    private final LivingEntity target;
    private World world;

    public void strikeLightning() {
        world.strikeLightning(target.getLocation());
    }

    public void explosion() {
        world.createExplosion(target.getLocation(), 4.0F, false , false);
    }

    public void gravity() {
        int baseX = target.getLocation().getChunk().getX();
        int baseZ = target.getLocation().getChunk().getZ();
        int radius = 2;
        for (int x = -radius; x < radius; x++) {
            for (int z = -radius; z < radius; z++) {
                Entity[] entities = world.getChunkAt(baseX + x, baseZ + z).getEntities();
                for (Entity ent : entities) {
                    if (ent instanceof Monster monster) {
                        if (monster.getPersistentDataContainer().has(StringPath.WARD_WITCH, PersistentDataType.STRING)) continue;
                        monster.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 100, 20));
                        monster.addPotionEffect(new PotionEffect(PotionEffectType.LEVITATION, 100, 0));
                        monster.teleport(target);
                    }
                }
            }
        }
    }

    public void headShot() {
        target.damage(15);
    }

    public void burrow() {
        target.teleport(target.getLocation().add(0,-2,0));
    }

    public void poison() {
        target.addPotionEffect(new PotionEffect(PotionEffectType.WITHER, 200, 2));
    }

    public void portal() {
        player.teleport(target);
    }

    public void shotgun() {
        for (int i = 0; i < 3; i++) {
            Arrow arrow = player.launchProjectile(Arrow.class);
            arrow.setPickupStatus(AbstractArrow.PickupStatus.DISALLOWED);
        }
    }

    public void burst() {
        new BukkitRunnable() {
            int count = 3;
            @Override
            public void run() {
                if (count <= 0) this.cancel();
                Arrow arrow = player.launchProjectile(Arrow.class);
                arrow.setPickupStatus(AbstractArrow.PickupStatus.DISALLOWED);
                count--;
            }
        }.runTaskTimer(AdventureCraftCore.getInstance(), 0, 8);
    }

    public void laval(Location location) {
        location.getBlock().setType(Material.LAVA);
    }

    public void water(Location location) {
        location.getBlock().setType(Material.WATER);
    }

    public void freeze() {
        target.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 100, 100));
    }

    public void feather() {
        target.addPotionEffect(new PotionEffect(PotionEffectType.LEVITATION, 100, 0));
    }

    public void web(Location location) {
        location.getBlock().setType(Material.COBWEB);
    }

    public void flying(Arrow arrow) {
        arrow.addPassenger(player);
    }
}
