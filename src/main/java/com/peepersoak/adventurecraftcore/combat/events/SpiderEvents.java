package com.peepersoak.adventurecraftcore.combat.events;

import com.peepersoak.adventurecraftcore.AdventureCraftCore;
import com.peepersoak.adventurecraftcore.utils.Utils;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Spider;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

public class SpiderEvents implements Listener {

    @EventHandler
    public void onAttack(EntityDamageByEntityEvent e) {
        if (!(e.getDamager() instanceof Spider spider)) return;
        if (spider.getType() != EntityType.SPIDER) return;
        if (!(e.getEntity() instanceof Player player)) return;
        if (Utils.getRandom(100) >= 25) return;
        setBlock(player.getLocation(), player);
        player.getWorld().spawnEntity(player.getLocation(), EntityType.CAVE_SPIDER);
    }

    private void setBlock(Location location, Player player) {
        location.getBlock().setType(Material.COBWEB);
        player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 100, 1, false, false, false));

        new BukkitRunnable() {
            int count = 5;
            @Override
            public void run() {
                if (count <= 0) {
                    location.getBlock().setType(Material.AIR);
                    this.cancel();
                    return;
                }
                count--;
            }
        }.runTaskTimer(AdventureCraftCore.getInstance(), 0, 20);
    }
}
