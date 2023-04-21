package com.peepersoak.adventurecraftcore.combat;

import com.peepersoak.adventurecraftcore.combat.levelmobs.MobFactory;
import com.peepersoak.adventurecraftcore.utils.Flags;
import com.peepersoak.adventurecraftcore.utils.StringPath;
import com.peepersoak.adventurecraftcore.utils.Utils;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.persistence.PersistentDataType;


public class MainSpawnEvent implements Listener {

    @EventHandler
    public void onSpawn(CreatureSpawnEvent e) {
        if (Utils.checkWGState(e.getEntity(), Flags.IS_DUNGEON_WORLD)) return;
        if (e.getSpawnReason() == CreatureSpawnEvent.SpawnReason.SPAWNER || e.getSpawnReason() == CreatureSpawnEvent.SpawnReason.CUSTOM) return;
        if (Utils.getPDC(e.getEntity()).has(StringPath.MOB_LEVEL_KEY, PersistentDataType.INTEGER)) return;

        if (e.getEntity() instanceof Zombie zombie && zombie.getType() == EntityType.ZOMBIE) {
            if (!Utils.checkWGState(zombie, Flags.ALLOW_CUSTOM_MOBS)) return;
            if (Utils.getPDC(zombie).has(StringPath.CUSTOM_ZOMBIE, PersistentDataType.STRING)) return;
            Utils.spawnRandomZombie(zombie.getLocation());
            e.setCancelled(true);
            return;
        }
        if (!(e.getEntity() instanceof Monster monster)) {
            if (!Utils.checkWGState(e.getEntity(), Flags.ALLOW_CUSTOM_MOBS)) return;
            registerNonMonsterMobs(e.getEntity());
            return;
        }
        if (e.getEntity().getPersistentDataContainer().has(StringPath.WARD_WITCH, PersistentDataType.STRING)) return;
        if (Utils.checkWGState(e.getEntity(), Flags.ALLOW_CUSTOM_MOBS)) {
            new MobFactory(monster);
        }
    }

    private void registerNonMonsterMobs(LivingEntity entity) {
        if (entity instanceof Hoglin || entity instanceof Zoglin) {
            new MobFactory(entity);
        }
    }
}
