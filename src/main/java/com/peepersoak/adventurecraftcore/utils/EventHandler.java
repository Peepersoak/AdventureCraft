package com.peepersoak.adventurecraftcore.utils;

import com.peepersoak.adventurecraftcore.AdventureCraftCore;
import com.peepersoak.adventurecraftcore.combat.MainSpawnEvent;
import com.peepersoak.adventurecraftcore.combat.events.*;
import com.peepersoak.adventurecraftcore.enchantment.SkillHandler;
import com.peepersoak.adventurecraftcore.items.arrows.ArrowEvents;
import com.peepersoak.adventurecraftcore.items.wards.WardEvents;
import com.peepersoak.adventurecraftcore.world.DeathLocation;
import com.peepersoak.adventurecraftcore.world.DropItem;
import com.peepersoak.adventurecraftcore.world.ScrollEvents;
import com.peepersoak.adventurecraftcore.world.WorldEvents;
import org.bukkit.plugin.PluginManager;

public class EventHandler {

    public void registerEvents(AdventureCraftCore instance, PluginManager pm) {
        pm.registerEvents(new ZombieEvents(), instance);
        pm.registerEvents(new CreeperEvents(), instance);
        pm.registerEvents(new ProjectileEvents(), instance);
        pm.registerEvents(new MainSpawnEvent(), instance);
        pm.registerEvents(new SpiderEvents(), instance);
        pm.registerEvents(new PhantomEvent(), instance);
        pm.registerEvents(new DrownedEvents(), instance);
        pm.registerEvents(new EndermanEvents(), instance);

        pm.registerEvents(new WorldEvents(), instance);
        pm.registerEvents(new DeathLocation(), instance);
        pm.registerEvents(new DropItem(), instance);
        pm.registerEvents(new ScrollEvents(), instance);
        pm.registerEvents(new ArrowEvents(), instance);

        pm.registerEvents(new InteractiveChat(), instance);

        pm.registerEvents(new WardEvents(), instance);

        new SkillHandler().registerSkillEvents(pm, instance);
    }
}
