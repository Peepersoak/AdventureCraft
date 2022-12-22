package com.peepersoak.adventurecraftcore.combat.events;

import com.peepersoak.adventurecraftcore.utils.Utils;
import org.bukkit.entity.Phantom;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class PhantomEvent implements Listener {

    @EventHandler
    public void onAttact(EntityDamageByEntityEvent e) {
        if (!(e.getEntity() instanceof Player player)) return;
        if (!(e.getDamager() instanceof Phantom phantom)) return;

        if (Utils.getRandom(100) <= 100) {
            phantom.addPassenger(player);
            phantom.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 100, 5));
        }
    }
}
