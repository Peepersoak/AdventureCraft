package com.peepersoak.adventurecraftcore.combat.events;

import com.peepersoak.adventurecraftcore.AdventureCraftCore;
import org.bukkit.Bukkit;
import org.bukkit.entity.Drowned;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.Trident;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.util.Vector;

public class DrownedEvents implements Listener {

   @EventHandler
    public void onAttack(EntityDamageByEntityEvent e) {
       if (!(e.getEntity() instanceof Player player)) return;
       if (e.getDamager() instanceof Drowned) {
          drown(player);
          return;
       }

       if (e.getDamager() instanceof Projectile projectile) {
          if (projectile instanceof Trident trident && trident.getShooter() instanceof Drowned) {
             drown(player);
          }
       }
   }

   private void drown(Player player) {
      Bukkit.getScheduler().runTask(AdventureCraftCore.getInstance(), () -> player.setVelocity(new Vector(0, -2,0).multiply(4)));
   }
}
