package com.peepersoak.adventurecraftcore.combat.events;

import com.peepersoak.adventurecraftcore.utils.Utils;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.entity.Skeleton;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class SkeletonEvent implements Listener {

    @EventHandler
    public void onArrowHit(EntityDamageByEntityEvent e) {
        if (!(e.getEntity() instanceof Player player)) return;
        if (!(e.getDamager() instanceof Arrow arrow)) return;
        if (!(arrow.getShooter() instanceof Skeleton)) return;
        if (Utils.getRandom(100) > 25) return;

        player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 100, 4));
    }
}
