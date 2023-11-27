package com.peepersoak.adventurecraftcore;

import com.peepersoak.adventurecraftcore.combat.levelmobs.Nightmare;
import com.peepersoak.adventurecraftcore.combat.levelmobs.warden.WardenEvent;
import com.peepersoak.adventurecraftcore.commands.*;
import com.peepersoak.adventurecraftcore.dungeon.DungeonEvents;
import com.peepersoak.adventurecraftcore.dungeon.DungeonRunnable;
import com.peepersoak.adventurecraftcore.dungeon.DungeonSettings;
import com.peepersoak.adventurecraftcore.enchantment.crafting.events.CraftingHandler;
import com.peepersoak.adventurecraftcore.enchantment.store.OpenStore;
import com.peepersoak.adventurecraftcore.openAI.*;
import com.peepersoak.adventurecraftcore.utils.*;
import com.peepersoak.adventurecraftcore.world.AntiAFK;
import com.sk89q.worldguard.WorldGuard;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;
import java.util.Objects;

public final class AdventureCraftCore extends JavaPlugin {
    private Economy econ = null;
    private static AdventureCraftCore instance;
    private final EventHandler eventHandler = new EventHandler();
    private final CraftingHandler craftingHandler = new CraftingHandler();
    private final Nightmare nightmare = new Nightmare();
    private QuestListChecker questListChecker;
    // This will handle all personal quest
    private OnGoingQuest onGoingQuest;
    // This will handle all guild quest
    private QuestManager questManager;
    private OpenStore openStore;
    private DungeonEvents dungeonEvents;
    private Data dungeonSetting;
    private Data onGoingQuestData;
    private Data allQuestData;
    private OpenAI openai;
    private QuestSetting questSetting;

    @Override
    public void onEnable() {
        instance = this;
        getConfig().options().copyDefaults();
        saveDefaultConfig();

        questSetting = new QuestSetting(getConfig());
        questListChecker = new QuestListChecker();
        openai = new OpenAI();

        loadYMLFiles();
        loadAllWords();

        // Load all quest here
        onGoingQuest = new OnGoingQuest();
        questManager = new QuestManager();

        Objects.requireNonNull(getCommand("acreload")).setExecutor(new ReloadCommand());

        Objects.requireNonNull(getCommand("open")).setExecutor(new OpenInventory());
        Objects.requireNonNull(getCommand("scroll")).setExecutor(new Scroll());
        Objects.requireNonNull(getCommand("scroll")).setTabCompleter(new ScrollAutoComplete());
        Objects.requireNonNull(getCommand("ward")).setExecutor(new Wards());
        Objects.requireNonNull(getCommand("ward")).setTabCompleter(new WardsAutoComplete());
        Objects.requireNonNull(getCommand("arrow")).setExecutor(new Arrow());
        Objects.requireNonNull(getCommand("arrow")).setTabCompleter(new ArrowAutoComplete());
        Objects.requireNonNull(getCommand("books")).setExecutor(new Books());
        Objects.requireNonNull(getCommand("books")).setTabCompleter(new BookAutoComplete());

        Objects.requireNonNull(getCommand("quest")).setExecutor(new QuestCommand());

        dungeonEvents = new DungeonEvents();
        Objects.requireNonNull(getCommand("dungeon")).setExecutor(dungeonEvents);
        Bukkit.getPluginManager().registerEvents(dungeonEvents, this);

        eventHandler.registerEvents(this, Bukkit.getPluginManager());
        craftingHandler.registerCraftingEvents(Bukkit.getPluginManager(), this);

        Bukkit.getPluginManager().registerEvents(new WardenEvent(), this);

        Bukkit.getPluginManager().registerEvents(new AntiAFK(), this);

        // Register Quest Events
        Bukkit.getPluginManager().registerEvents(new QuestEvents(), this);
        Bukkit.getPluginManager().registerEvents(new QuestTracker(), this);

//        worldTime.runTaskTimer(this, 0, 20);
        nightmare.runTaskTimer(this, 0, 60);

        DungeonRunnable dungeonRunnable = new DungeonRunnable();
        dungeonRunnable.runTaskTimer(this, 0, 20);

        openStore = new OpenStore();
        Objects.requireNonNull(getCommand("margrave")).setExecutor(openStore);
        Bukkit.getPluginManager().registerEvents(openStore, this);

        setupEconomy();
    }

    @Override
    public void onLoad() {
        try {
            new WorldFlags();
        } catch (NoClassDefFoundError e) {
            getLogger().warning("WorldGuard not found! All WorldGuard flags will be disabled.");
        }
    }

    @Override
    public void onDisable() {
        onGoingQuest.saveData();
        if (dungeonEvents != null) {
            dungeonEvents.removeAllEntities();
        }
    }

    public void loadYMLFiles() {
        dungeonSetting = new Data(FileName.DUNGEON_SETTINGS);
        onGoingQuestData = new Data(FileName.ONGOING_QUEST);
        allQuestData = new Data(FileName.ALL_REGISTERED_QUEST);
    }

    public Data getDungeonSetting() {
        return dungeonSetting;
    }
    public Data getOnGoingQuestData() { return  onGoingQuestData; }
    public Data getAllQuestData() {
        return allQuestData;
    }

    public static AdventureCraftCore getInstance() {
        return instance;
    }

    private void loadAllWords() {
        List<String> getWorldList = AdventureCraftCore.getInstance().getDungeonSetting().getConfig().getStringList(DungeonSettings.DUNGEON_TYPE);
        for (String worldName : getWorldList) {
            if (Bukkit.getWorld(worldName) == null) {
               WorldCreator creator = new WorldCreator(worldName);
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

    private void setupEconomy() {
        if (getServer().getPluginManager().getPlugin("Vault") == null) {
            getLogger().warning("Vault not found! Economy will not be registered!");
            return;
        }
        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            return;
        }
        econ = rsp.getProvider();
    }

    public Economy getEconomy() {
        return econ;
    }
    public OpenAI getOpenai() { return openai; }
    public OnGoingQuest getOnGoingQuest() {
        return onGoingQuest;
    }
    public QuestManager getQuestManager() {
        return questManager;
    }
    public QuestListChecker getQuestListChecker() {
        return questListChecker;
    }
    public QuestSetting getQuestSetting() {
        return questSetting;
    }

    public void generalReload() {
        this.reloadConfig();
        onGoingQuest.saveData();
        onGoingQuest.restoreQuest();
    }
}