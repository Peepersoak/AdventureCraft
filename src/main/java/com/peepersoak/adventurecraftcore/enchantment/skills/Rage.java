package com.peepersoak.adventurecraftcore.enchantment.skills;

import com.peepersoak.adventurecraftcore.enchantment.SkillChance;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Rage implements Listener {

	@EventHandler
	public void onRage(EntityDamageByEntityEvent e) {
		if (!(e.getDamager() instanceof LivingEntity)) return;
		if (!(e.getEntity() instanceof Player player)) return;
		ItemStack chest = player.getInventory().getChestplate();
		if (chest == null) return;
		if (Objects.requireNonNull(chest.getItemMeta()).getLore() == null) return;
		Skill skill = new Skill();
		skill.setItem(chest);
		if (skill.getLoreName() == null || !skill.getLoreName().contains("RAGE")) return;

		if (e.getFinalDamage() < player.getHealth()) return;
		if (!trigger()) return;
		e.setCancelled(true);
		List<Entity> mobs = new ArrayList<>();
		for (Entity ent : player.getNearbyEntities(10, 10, 6)) {
			if (ent instanceof Monster) {
				mobs.add(ent);
			}
		}
		
		int level = mobs.size();
		player.addPotionEffects(addPotionEffect(level));
		player.playSound(player.getLocation(), Sound.ENTITY_GENERIC_EXPLODE, 5.0F, 1.0F);
	}
	
	public List<PotionEffect> addPotionEffect(int level) {
		List<PotionEffect> effect = new ArrayList<>();
		effect.add(new PotionEffect(PotionEffectType.ABSORPTION, 100, level));
		effect.add(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 100, level));
		effect.add(new PotionEffect(PotionEffectType.SPEED, 100, 2));
		return effect;
	}
	
	public boolean trigger() {
		int random = (int) (Math.random() * 100);
		return (random <= SkillChance.RAGE);
	}
}
