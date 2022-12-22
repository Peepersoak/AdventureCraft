package com.peepersoak.adventurecraftcore.enchantment.skills;

import com.peepersoak.adventurecraftcore.AdventureCraftCore;
import com.peepersoak.adventurecraftcore.achievement.AchievementHandler;
import com.peepersoak.adventurecraftcore.enchantment.SkillChance;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;

public class Arise implements Listener{

	private final AdventureCraftCore plugin = AdventureCraftCore.getInstance();
	private final List<String> playerName = new ArrayList<>();
	
	Random rand = new Random();

	@EventHandler
	public void onPlayerDamage(EntityDamageByEntityEvent e) {
		if (e.getEntity() instanceof Player) {
			if (e.getDamager() instanceof LivingEntity || e.getCause() == DamageCause.PROJECTILE) {
				Player player = (Player) e.getEntity();
				ItemStack helmet = player.getInventory().getHelmet();
				if (helmet != null && Objects.requireNonNull(helmet.getItemMeta()).getLore() != null) {
					Skill skill = new Skill();
					skill.setItem(helmet);
					if (skill.getLoreName() != null && skill.getLoreName().contains("ARISE")) {
						int level = skill.getLoreMap().get("ARISE");
						List<LivingEntity> golems = new ArrayList<>();
						
						if (shouldSpawn()) {
							Location loc = player.getLocation();
							int counter = 0;
							if (playerName.contains(player.getName())) return;
							String name = player.getName();
							playerName.add(name);
							player.sendMessage(ChatColor.DARK_RED + "YOUR FOLLOWER HAVE ARISE!");
							AchievementHandler achievement = new AchievementHandler();
							achievement.addTheMonarch(name);
							while(counter < level) {
								counter++;
								IronGolem golem = (IronGolem) player.getWorld().spawnEntity(loc, EntityType.IRON_GOLEM);
								PersistentDataContainer data = golem.getPersistentDataContainer();
								if (!data.has(new NamespacedKey(plugin, "Arise"), PersistentDataType.STRING)) {
									data.set(new NamespacedKey(plugin, "Arise"), PersistentDataType.STRING, "Arise");
								}
								golem.setPlayerCreated(true);
								golem.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 100, 4));
								golem.setRemoveWhenFarAway(true);
								golems.add(golem);
								if (counter == level) {
									new BukkitRunnable() {
										int counter = 30;
										@Override
										public void run() {
											if (counter <= 1) {
												killGolem(golems);
												playerName.remove(name);
												this.cancel();
											}
											counter--;
										}
									}.runTaskTimer(plugin, 1L, 20L);
								}
							}
						}
					}
				}
			}
		}
	}
	
	@EventHandler
	public void onGolemDeath(EntityDeathEvent e) {
		if (e.getEntity() instanceof Golem golem) {
			PersistentDataContainer data = golem.getPersistentDataContainer();
			if (data.has(new NamespacedKey(plugin, "Arise"), PersistentDataType.STRING)) {
				e.getDrops().clear();
				e.setDroppedExp(0);
			}
		}
	}
	
	public void killGolem(List<LivingEntity> golems) {
		for (LivingEntity g : golems) {
			try {
				g.damage(Integer.MAX_VALUE);
			} catch (Exception e) {
				System.out.println("Can't kill the spawned golems");
			}
		}
	}
	
	public boolean shouldSpawn() {
		int random = rand.nextInt(100) + 1;
		return random <= SkillChance.ARISE;
	}
}
