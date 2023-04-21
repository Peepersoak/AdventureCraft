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
            StateFlag allowScroll = new StateFlag("ac-allow-scroll", false);
            StateFlag allowBreakZombie = new StateFlag("ac-allow-zombie-break", false);
            StateFlag allowLevelMobs = new StateFlag("ac-allow-level-mobs", false);
            StateFlag allowCustomMobs = new StateFlag("ac-allow-custom-mobs", false);
            StateFlag allowScrollTP = new StateFlag("ac-allow-scroll-tp", true);
            StateFlag allowCustomDrops = new StateFlag("ac-allow-custom-drops", false);
            StateFlag allowDungeons = new StateFlag("ac-allow-dungeons", false);
            StateFlag allowDungeonSpawnerOnly = new StateFlag("ac-dungeon-spawner-only", false);
            StateFlag allowDungeonLife = new StateFlag("ac-use-dungeon-life", false);
            StateFlag allowBoomerDestroy = new StateFlag("ac-allow-boomer-destroy", false);
            StateFlag allowCusCraftEnchant = new StateFlag("ac-allow-custom-craft-enchant", true);
            StateFlag allowMobsOnCaves = new StateFlag("ac-allow-mobs-on-cave", true);
            StateFlag isDungeonWOrld = new StateFlag("ac-is-dungeon-world", false);
            StateFlag allowFireWorks = new StateFlag("ac-allow-fireworks", false);

            IntegerFlag mobLevelThreshold = new IntegerFlag("ac-mob-threshold");
            IntegerFlag normalEnchantChance = new IntegerFlag("ac-normal-enchant-chance");
            IntegerFlag customEnchantChance = new IntegerFlag("ac-custom-enchant-chance");
            IntegerFlag skillEnchantChance = new IntegerFlag("ac-skill-enchant-chance");
            IntegerFlag maxEntityCount = new IntegerFlag("ac-max-entity-count");
            IntegerFlag scrollChance = new IntegerFlag("ac-scroll-chance");
            IntegerFlag wardChance = new IntegerFlag("ac-ward-chance");
            IntegerFlag arrowChance = new IntegerFlag("ac-arrow-chance");
            IntegerFlag bossMinLevel = new IntegerFlag("ac-boss-min-level");
            IntegerFlag bossMaxLevel = new IntegerFlag("ac-boss-max-level");
            IntegerFlag dungeonBossChance = new IntegerFlag("ac-dungeon-boss-chance");

            registry.register(allowScroll);
            registry.register(allowBreakZombie);
            registry.register(allowLevelMobs);
            registry.register(allowCustomMobs);
            registry.register(allowScrollTP);
            registry.register(allowCustomDrops);
            registry.register(allowDungeons);
            registry.register(allowDungeonSpawnerOnly);
            registry.register(allowDungeonLife);
            registry.register(allowBoomerDestroy);
            registry.register(allowCusCraftEnchant);
            registry.register(allowMobsOnCaves);
            registry.register(isDungeonWOrld);
            registry.register(allowFireWorks);

            registry.register(maxEntityCount);
            registry.register(mobLevelThreshold);
            registry.register(scrollChance);
            registry.register(wardChance);
            registry.register(arrowChance);
            registry.register(normalEnchantChance);
            registry.register(customEnchantChance);
            registry.register(skillEnchantChance);
            registry.register(bossMinLevel);
            registry.register(bossMaxLevel);
            registry.register(dungeonBossChance);

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
            Flags.BOOMER_BLOCK_DESTROY = allowBoomerDestroy;
            Flags.ALLOW_CUSTOM_CRAFT_ENCHANT = allowCusCraftEnchant;
            Flags.BOSS_MINIMUM_LEVEL = bossMinLevel;
            Flags.BOSS_MAXIMUM_LEVEL = bossMaxLevel;
            Flags.ALLOW_MOBS_ON_CAVES = allowMobsOnCaves;
            Flags.DUNGEON_BOSS_SPAWN_CHANCE = dungeonBossChance;
            Flags.IS_DUNGEON_WORLD = isDungeonWOrld;
            Flags.ALLOW_FIREWORKS = allowFireWorks;
        } catch (FlagConflictException e) {
            AdventureCraftCore.getInstance().getLogger().warning("Failed to register some flags!");
        }
    }
}
