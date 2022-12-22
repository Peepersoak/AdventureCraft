package com.peepersoak.adventurecraftcore.combat;

import com.peepersoak.adventurecraftcore.combat.levelmobs.MobFactory;
import com.peepersoak.adventurecraftcore.utils.StringPath;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Monster;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.persistence.PersistentDataType;

public class GeneralMobs implements Listener {

    @EventHandler
    public void onSpawn(CreatureSpawnEvent e) {
        if (e.getSpawnReason() == CreatureSpawnEvent.SpawnReason.SPAWNER) return;
        if (e.getEntity().getType() == EntityType.ZOMBIE) return;
        if (!(e.getEntity() instanceof Monster monster)) return;
        if (e.getEntity().getPersistentDataContainer().has(StringPath.WARD_WITCH, PersistentDataType.STRING)) return;
        new MobFactory(monster);
    }
}
