package com.peepersoak.adventurecraftcore.utils;

import com.peepersoak.adventurecraftcore.AdventureCraftCore;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.flags.IntegerFlag;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.flags.registry.FlagConflictException;
import com.sk89q.worldguard.protection.flags.registry.FlagRegistry;

public class WorldFlags {

    public WorldFlags() {
        init();
    }

    private void init() {
        FlagRegistry registry = WorldGuard.getInstance().getFlagRegistry();

        try {
            StateFlag allowScroll = new StateFlag("allow-scroll", true);
            StateFlag allowBreakZombie = new StateFlag("allow-zombie-break", true);
            StateFlag allowLevelMobs = new StateFlag("allow-level-mobs", true);
            StateFlag allowCustomMobs = new StateFlag("allow-custom-mobs", true);
            StateFlag allowScrollTP = new StateFlag("allow-scroll-tp", true);
            StateFlag allowCustomDrops = new StateFlag("allow-custom-drops", true);
            StateFlag allowDungeons = new StateFlag("allow-dungeons", true);
            StateFlag allowDungeonSpawnerOnly = new StateFlag("dungeon-spawner-only", false);
            StateFlag allowDungeonLife = new StateFlag("use-dungeon-life", false);

            IntegerFlag mobLevelThreshold = new IntegerFlag("mob-threshold");
            IntegerFlag normalEnchantChance = new IntegerFlag("normal-enchant-chance");
            IntegerFlag customEnchantChance = new IntegerFlag("custom-enchant-chance");
            IntegerFlag skillEnchantChance = new IntegerFlag("skill-enchant-chance");
            IntegerFlag maxEntityCount = new IntegerFlag("max-entity-count");
            IntegerFlag scrollChance = new IntegerFlag("scroll-chance");
            IntegerFlag wardChance = new IntegerFlag("ward-chance");
            IntegerFlag arrowChance = new IntegerFlag("arrow-chance");

            registry.register(allowScroll);
            registry.register(allowBreakZombie);
            registry.register(allowLevelMobs);
            registry.register(allowCustomMobs);
            registry.register(allowScrollTP);
            registry.register(allowCustomDrops);
            registry.register(allowDungeons);
            registry.register(allowDungeonSpawnerOnly);
            registry.register(allowDungeonLife);

            registry.register(maxEntityCount);
            registry.register(mobLevelThreshold);
            registry.register(scrollChance);
            registry.register(wardChance);
            registry.register(arrowChance);
            registry.register(normalEnchantChance);
            registry.register(customEnchantChance);
            registry.register(skillEnchantChance);

            Flags.ALLOW_SCROLL = allowScroll;
            Flags.ZOMBIE_BREAK = allowBreakZombie;
            Flags.LEVEL_MOBS = allowLevelMobs;
            Flags.ALLOW_CUSTOM_MOBS = allowCustomMobs;
            Flags.ALLOW_SCROLL_TP = allowScrollTP;
            Flags.ALLOW_DROPS = allowCustomDrops;
            Flags.MOB_THRESHOLD = mobLevelThreshold;
            Flags.ENABLE_DUNGEON = allowDungeons;
            Flags.NORMAL_ENCHANT_CHANCE = normalEnchantChance;
            Flags.CUSTOM_ENCHANT_CHANCE = customEnchantChance;
            Flags.SKILL_ENCHANT_CHANCE = skillEnchantChance;
            Flags.MAX_ENTITY_COUNT = maxEntityCount;
            Flags.SCROLL_CHANCE = scrollChance;
            Flags.WARD_CHANCE = wardChance;
            Flags.ARROW_CHANCE = arrowChance;
            Flags.DUNGEON_SPAWNER_ONLY = allowDungeonSpawnerOnly;
            Flags.ALLOW_DUNGEON_LIFE = allowDungeonLife;
        } catch (FlagConflictException e) {
            AdventureCraftCore.getInstance().getLogger().warning("Failed to register some flags!");
        }
    }
}
