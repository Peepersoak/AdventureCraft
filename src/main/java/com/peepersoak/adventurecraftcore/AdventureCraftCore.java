package com.peepersoak.adventurecraftcore;

import com.peepersoak.adventurecraftcore.combat.levelmobs.Nightmare;
import com.peepersoak.adventurecraftcore.commands.*;
import com.peepersoak.adventurecraftcore.dungeon.DungeonEvents;
import com.peepersoak.adventurecraftcore.dungeon.DungeonSettings;
import com.peepersoak.adventurecraftcore.enchantment.crafting.events.CraftingHandler;
import com.peepersoak.adventurecraftcore.utils.Data;
import com.peepersoak.adventurecraftcore.utils.EventHandler;
import com.peepersoak.adventurecraftcore.utils.FileName;
import com.peepersoak.adventurecraftcore.utils.OpenInventory;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.flags.registry.FlagConflictException;
import com.sk89q.worldguard.protection.flags.registry.FlagRegistry;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;
import java.util.Objects;

public final class AdventureCraftCore extends JavaPlugin {

    private static Economy econ = null;
    public static StateFlag PRISON_FLAG;
    public static StateFlag ZOMBIE_BREAK;
    private static AdventureCraftCore instance;
    private final EventHandler eventHandler = new EventHandler();
    private final CraftingHandler craftingHandler = new CraftingHandler();
    private final Nightmare nightmare = new Nightmare();
    private DungeonEvents dungeonEvents;
    private Data dungeonSetting;


    @Override
    public void onEnable() {
        instance = this;
        getConfig().options().copyDefaults();
        saveDefaultConfig();

        loadYMLFiles();
        loadAllWords();

        Objects.requireNonNull(getCommand("open")).setExecutor(new OpenInventory());
        Objects.requireNonNull(getCommand("scroll")).setExecutor(new Scroll());
        Objects.requireNonNull(getCommand("scroll")).setTabCompleter(new ScrollAutoComplete());
        Objects.requireNonNull(getCommand("ward")).setExecutor(new Wards());
        Objects.requireNonNull(getCommand("ward")).setTabCompleter(new WardsAutoComplete());
        Objects.requireNonNull(getCommand("arrow")).setExecutor(new Arrow());
        Objects.requireNonNull(getCommand("arrow")).setTabCompleter(new ArrowAutoComplete());
        Objects.requireNonNull(getCommand("books")).setExecutor(new Books());

        dungeonEvents = new DungeonEvents();
        Objects.requireNonNull(getCommand("dungeon")).setExecutor(dungeonEvents);
        Bukkit.getPluginManager().registerEvents(dungeonEvents, this);

        eventHandler.registerEvents(this, Bukkit.getPluginManager());
        craftingHandler.registerCraftingEvents(Bukkit.getPluginManager(), this);

//        worldTime.runTaskTimer(this, 0, 20);
        nightmare.runTaskTimer(this, 0, 60);

        if (!setupEconomy()) getServer().getPluginManager().disablePlugin(this);
    }

    @Override
    public void onLoad() {
        registerFlags();
    }

    @Override
    public void onDisable() {
        dungeonEvents.removeAllEntities();
    }

    private void registerFlags() {
        FlagRegistry registry = WorldGuard.getInstance().getFlagRegistry();
        try {
            StateFlag flag = new StateFlag("allow-scroll", true);
            StateFlag allowBreakZombie = new StateFlag("allow-zombie-break", true);
            registry.register(flag);
            registry.register(allowBreakZombie);
            PRISON_FLAG = flag;
            ZOMBIE_BREAK = allowBreakZombie;
        } catch (FlagConflictException e) {
            //
        }
    }

    public void loadYMLFiles() {
        dungeonSetting = new Data(FileName.DUNGEON_SETTINGS);
    }

    public Data getDungeonSetting() {
        return dungeonSetting;
    }

    public static AdventureCraftCore getInstance() {
        return instance;
    }

    private void loadAllWords() {
        List<String> getWorldList = AdventureCraftCore.getInstance().getDungeonSetting().getConfig().getStringList(DungeonSettings.DUNGEON_TYPE);
        for (String worldName : getWorldList) {
            if (Bukkit.getWorld(worldName) == null) {
               WorldCreator creator = new WorldCreator(worldName);
               creator.environment(World.Environment.NETHER);
               World world = creator.createWorld();
               if (world != null) {
                   world.setKeepSpawnInMemory(false);
               }
            } else {
                World world =  Bukkit.getWorld(worldName);
                if (world != null) {
                    world.setKeepSpawnInMemory(false);
                    Bukkit.getServer().getWorlds().add(world);
                }
            }
        }
    }

    private boolean setupEconomy() {
        if (getServer().getPluginManager().getPlugin("Vault") == null) {
            return false;
        }
        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            return false;
        }
        econ = rsp.getProvider();
        return econ != null;
    }

    public static Economy getEconomy() {
        return econ;
    }
}