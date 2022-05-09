package com.peepersoak.adventurecraftcore.utils;

import com.peepersoak.adventurecraftcore.AdventureCraftCore;
import com.peepersoak.adventurecraftcore.combat.events.CreeperEvents;
import com.peepersoak.adventurecraftcore.combat.events.SkeletonEvent;
import com.peepersoak.adventurecraftcore.combat.events.ZombieEvents;
import com.peepersoak.adventurecraftcore.items.wards.WardEvents;
import com.peepersoak.adventurecraftcore.world.DeathLocation;
import com.peepersoak.adventurecraftcore.world.DropItem;
import com.peepersoak.adventurecraftcore.world.WorldEvents;
import org.bukkit.plugin.PluginManager;

public class EventHandler {

    public void registerEvents(AdventureCraftCore instance, PluginManager pm) {
        pm.registerEvents(new ZombieEvents(), instance);
        pm.registerEvents(new CreeperEvents(), instance);
        pm.registerEvents(new SkeletonEvent(), instance);

        pm.registerEvents(new WorldEvents(), instance);
        pm.registerEvents(new DeathLocation(), instance);
        pm.registerEvents(new DropItem(), instance);

        pm.registerEvents(new WardEvents(), instance);
    }
}
