package com.peepersoak.adventurecraftcore.combat.events;

import com.peepersoak.adventurecraftcore.utils.Utils;
import org.bukkit.entity.Creeper;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

public class CreeperEvents implements Listener {

    @EventHandler
    public void onCreeperDamage(EntityDamageByEntityEvent e) {
        if (e.getEntity() instanceof Creeper creeper) {
            if (e.getDamage() > creeper.getHealth()) {
                if (Utils.getRandom(100) < 25) {
                    creeper.explode();
                }
            }
        }
    }
}
