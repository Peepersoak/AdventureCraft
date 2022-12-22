package com.peepersoak.adventurecraftcore.enchantment.skills;

import com.peepersoak.adventurecraftcore.AdventureCraftCore;
import com.peepersoak.adventurecraftcore.enchantment.SkillChance;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.Random;

public class Gravity implements Listener {
	
	private final AdventureCraftCore plugin = AdventureCraftCore.getInstance();
	
	@EventHandler
	public void onDamage(EntityDamageByEntityEvent e) {
		if (e.getEntity() instanceof LivingEntity entity) {
			if (e.getDamager() instanceof Arrow arrow) {
				if (arrow.getShooter() instanceof Player player) {

					int baseX = entity.getLocation().getChunk().getX();
					int baseZ = entity.getLocation().getChunk().getZ();
					
					World world = entity.getWorld();

					ItemStack item = player.getInventory().getItemInMainHand();
					if (item.getType() != Material.AIR && item.getItemMeta() != null && item.getItemMeta().getLore() != null) {
						Skill skill = new Skill();
						skill.setItem(item);
						if (skill.getLoreName() != null && skill.getLoreName().contains("GRAVITY")) {
							if (castSkill()) {
								int radius = 2;
								for (int x = -radius; x < radius; x++) {
									for (int z = -radius; z < radius; z++) {
										Entity[] entities = world.getChunkAt(baseX + x, baseZ + z).getEntities();
										for (Entity ent : entities) {
											if (ent instanceof Monster monster) {
												monster.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 100, 20));
												monster.addPotionEffect(new PotionEffect(PotionEffectType.LEVITATION, 100, 0));
												monster.teleport(entity);
											}
										}
									}
								}
							}
						}
					}
				}
			}
		}
	}
	
	
	public boolean castSkill() {
		Random rand = new Random();
		int random = rand.nextInt(100) + 1;
		int chance = SkillChance.GRAVITY;
		return random <= chance;
	}
}