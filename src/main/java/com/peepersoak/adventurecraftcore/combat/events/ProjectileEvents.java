package com.peepersoak.adventurecraftcore.combat.events;

import com.peepersoak.adventurecraftcore.utils.Flags;
import com.peepersoak.adventurecraftcore.utils.StringPath;
import com.peepersoak.adventurecraftcore.utils.Utils;
import org.bukkit.Material;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ProjectileEvents implements Listener {

    public ProjectileEvents() {
        effect.add(PotionEffectType.POISON);
        effect.add(PotionEffectType.SLOW);
        effect.add(PotionEffectType.HARM);
        effect.add(PotionEffectType.WEAKNESS);
        effect.add(PotionEffectType.BLINDNESS);
        effect.add(PotionEffectType.HUNGER);
    }

    private final List<PotionEffectType> effect = new ArrayList<>();

    @EventHandler
    public void onArrowHit(EntityDamageByEntityEvent e) {
        if (!(e.getEntity() instanceof Player player)) return;
        if (!(e.getDamager() instanceof Arrow arrow)) return;

        if (arrow.getShooter() instanceof Skeleton skeleton) {
            if (Utils.checkWGState(player, Flags.LEVEL_MOBS)) {
                Utils.setProjectileDamage(skeleton, e);
            }
            if (Utils.getRandom(100) > 25) return;
            player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 100, 4));
        }
    }

    @EventHandler
    public void onBlazeDamage(EntityDamageByEntityEvent e) {
        if (!(e.getEntity() instanceof Player player)) return;
        if (!(e.getDamager() instanceof Fireball fireball)) return;

        if (fireball.getShooter() instanceof Ghast || fireball.getShooter() instanceof Blaze) {
            if (Utils.checkWGState(player, Flags.LEVEL_MOBS)) {
                Utils.setProjectileDamage((LivingEntity) fireball.getShooter(), e);
            }
        }
    }

    @EventHandler
    public void onWitchAttack(ProjectileLaunchEvent e) {
        if (!(e.getEntity() instanceof ThrownPotion splashPotion)) return;
        if (!(splashPotion.getShooter() instanceof Witch w)) return;
        if (!Utils.checkWGState(w, Flags.LEVEL_MOBS)) return;

        Integer level = Utils.getPDC(w).get(StringPath.MOB_LEVEL_KEY, PersistentDataType.INTEGER);
        level = level != null ? level : 0;

        int amplifier = level / 5;

        ItemStack potion = new ItemStack(Material.SPLASH_POTION);
        PotionMeta meta = (PotionMeta) potion.getItemMeta();
        if (meta == null) return;

        meta.addCustomEffect(new PotionEffect(getPotionEffect(), 200, amplifier), true);
        potion.setItemMeta(meta);
        splashPotion.setItem(potion);
    }

    private PotionEffectType getPotionEffect() {
        Collections.shuffle(effect);
        return effect.get(0);
    }
}
