package com.peepersoak.adventurecraftcore.enchantment;

import com.peepersoak.adventurecraftcore.AdventureCraftCore;
import com.peepersoak.adventurecraftcore.enchantment.skills.*;
import org.bukkit.plugin.PluginManager;

public class SkillHandler {
	
	public void registerSkillEvents(PluginManager pm, AdventureCraftCore plugin) {
		pm.registerEvents(new Arise(), plugin);
		pm.registerEvents(new Critical(), plugin);
		pm.registerEvents(new LifeSteal(), plugin);
		pm.registerEvents(new HeadShot(), plugin);
		pm.registerEvents(new LightningStrike(), plugin);
		pm.registerEvents(new Explosion(), plugin);
		pm.registerEvents(new Gravity(), plugin);
		pm.registerEvents(new LastResort(), plugin);
		pm.registerEvents(new HealthBoost(), plugin);
		pm.registerEvents(new SpeedBoost(), plugin);
		pm.registerEvents(new Rejuvination(), plugin);
		pm.registerEvents(new SoulBound(), plugin);
		pm.registerEvents(new Rage(), plugin);
		pm.registerEvents(new FullCounter(), plugin);
	}
}
