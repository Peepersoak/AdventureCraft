package com.peepersoak.adventurecraftcore.enchantment.crafting.events;

import com.peepersoak.adventurecraftcore.AdventureCraftCore;
import org.bukkit.plugin.PluginManager;

public class CraftingHandler {

	public void registerCraftingEvents(PluginManager pm, AdventureCraftCore plugin) {
		pm.registerEvents(new AnvilCraft(), plugin);
		pm.registerEvents(new CraftingTableCraft(), plugin);
	}
}
