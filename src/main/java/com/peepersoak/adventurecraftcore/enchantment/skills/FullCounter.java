package com.peepersoak.adventurecraftcore.enchantment.skills;

import com.peepersoak.adventurecraftcore.enchantment.SkillChance;
import org.bukkit.Sound;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;

public class FullCounter implements Listener {

	@EventHandler
	public void onDamage(EntityDamageByEntityEvent e) {
		if (!(e.getEntity() instanceof Player player)) return;
		if (!(e.getDamager() instanceof LivingEntity target)) return;
		ItemStack leggings = player.getInventory().getLeggings();
		if (leggings == null) return;
		Skill skill = new Skill();
		skill.setItem(leggings);
		if (skill.getLoreName() == null || !skill.getLoreName().contains("FULL COUNTER")) return;
		if (!trigger()) return;

		double damage = e.getFinalDamage();
		e.setCancelled(true);
		target.damage(damage * 2);
		player.playSound(target.getLocation(), Sound.ENTITY_PLAYER_ATTACK_CRIT, 5.0F, 1.0F);
	}
	
	public boolean trigger() {
		int random = (int) (Math.random() * 100);
		return (random <= SkillChance.FULL_COUNTER);
	}
}
