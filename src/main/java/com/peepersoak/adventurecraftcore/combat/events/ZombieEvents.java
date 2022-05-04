package com.peepersoak.adventurecraftcore.combat.events;

import com.peepersoak.adventurecraftcore.combat.levelmobs.MobFactory;
import com.peepersoak.adventurecraftcore.combat.levelmobs.zombie.ZombieType;
import com.peepersoak.adventurecraftcore.combat.levelmobs.zombie.variation.AggresiveVirusZombie;
import com.peepersoak.adventurecraftcore.combat.levelmobs.zombie.variation.BoomerZombie;
import com.peepersoak.adventurecraftcore.utils.StringPath;
import com.peepersoak.adventurecraftcore.utils.Utils;
import net.minecraft.server.level.ServerLevel;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_18_R2.CraftWorld;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Zombie;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.persistence.PersistentDataType;

import java.util.Objects;

public class ZombieEvents implements Listener {

    @EventHandler
    public void onMobSpawn(CreatureSpawnEvent e) {
        if (Utils.getNonLevelledMobs().contains(e.getEntityType())) return;
        if (!(e.getEntity() instanceof Monster monster)) return;
        if (e.getSpawnReason() == CreatureSpawnEvent.SpawnReason.SPAWNER) return;

        if (e.getEntity() instanceof Zombie zombie) {
            if (zombie.getType() == EntityType.ZOMBIE) {
                if (Utils.getPDC(zombie).has(StringPath.CUSTOM_ZOMBIE, PersistentDataType.STRING)) return;
                spawnRandomZombie(zombie.getLocation());
                e.setCancelled(true);
                return;
            }
        }

        new MobFactory(monster);
    }

    @EventHandler
    public void onDamage(EntityDamageEvent e) {
        if (e.getEntity() instanceof Zombie zombie) {
            if (!Utils.getPDC(zombie).has(StringPath.CUSTOM_ZOMBIE, PersistentDataType.STRING)) return;
            String type = Utils.getPDC(zombie).get(StringPath.CUSTOM_ZOMBIE, PersistentDataType.STRING);
            if (type == null) return;
            if (!type.equalsIgnoreCase(ZombieType.BOOMER_ZOMBIE)) return;
            explodeZombie(zombie);
        }
    }

    @EventHandler
    public void onZombieKill(EntityDamageByEntityEvent e) {
        if (!(e.getEntity() instanceof LivingEntity entity)) return;

        if (e.getDamager() instanceof Zombie zombie) {
            if (!Utils.getPDC(zombie).has(StringPath.CUSTOM_ZOMBIE, PersistentDataType.STRING)) return;

            if (e.getDamage() > entity.getHealth()) {
                Location loc = entity.getLocation();
                spawnRandomZombie(loc);
                Utils.zombieLevelUp(zombie);
            }
        }
    }

    private void spawnRandomZombie(Location location) {
        if (location == null) return;
        ServerLevel world = ((CraftWorld) Objects.requireNonNull(location.getWorld())).getHandle();

        if (Utils.getRandom(100, 1) < 15) {
            world.addFreshEntityWithPassengers(new BoomerZombie(location));
            return;
        }

        world.addFreshEntityWithPassengers(new AggresiveVirusZombie(location));
    }

    private void explodeZombie(Zombie zombie) {
        Location location = zombie.getLocation();
        zombie.remove();
        zombie.getWorld().createExplosion(location, 4.0F);
    }
}
