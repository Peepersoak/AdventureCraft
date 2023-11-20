package com.peepersoak.adventurecraftcore;

import com.peepersoak.adventurecraftcore.combat.levelmobs.Nightmare;
import com.peepersoak.adventurecraftcore.combat.levelmobs.warden.WardenEvent;
import com.peepersoak.adventurecraftcore.commands.*;
import com.peepersoak.adventurecraftcore.dungeon.DungeonEvents;
import com.peepersoak.adventurecraftcore.dungeon.DungeonRunnable;
import com.peepersoak.adventurecraftcore.dungeon.DungeonSettings;
import com.peepersoak.adventurecraftcore.enchantment.crafting.events.CraftingHandler;
import com.peepersoak.adventurecraftcore.enchantment.store.OpenStore;
import com.peepersoak.adventurecraftcore.openAI.OnGoingQuest;
import com.peepersoak.adventurecraftcore.openAI.OpenAI;
import com.peepersoak.adventurecraftcore.openAI.Quest;
import com.peepersoak.adventurecraftcore.openAI.QuestCommand;
import com.peepersoak.adventurecraftcore.utils.*;
import com.peepersoak.adventurecraftcore.world.AntiAFK;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public final class AdventureCraftCore extends JavaPlugin {
    private static Economy econ = null;
    private static AdventureCraftCore instance;
    private final EventHandler eventHandler = new EventHandler();
    private final CraftingHandler craftingHandler = new CraftingHandler();
    private final Nightmare nightmare = new Nightmare();
    private OnGoingQuest onGoingQuest;
    private OpenStore openStore;
    private DungeonEvents dungeonEvents;
    private Data dungeonSetting;
    private Data onGoingQuestData;
    private OpenAI openai;

    @Override
    public void onEnable() {
        instance = this;
        getConfig().options().copyDefaults();
        saveDefaultConfig();

        openai = new OpenAI();

        loadYMLFiles();
        loadAllWords();

        // Load all quest here
        onGoingQuest = new OnGoingQuest();

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

//        worldTime.runTaskTimer(this, 0, 20);
        nightmare.runTaskTimer(this, 0, 60);

        DungeonRunnable dungeonRunnable = new DungeonRunnable();
        dungeonRunnable.runTaskTimer(this, 0, 20);

        openStore = new OpenStore();
        Objects.requireNonNull(getCommand("margrave")).setExecutor(openStore);
        Bukkit.getPluginManager().registerEvents(openStore, this);

        if (!setupEconomy()) getServer().getPluginManager().disablePlugin(this);
    }

    @Override
    public void onLoad() {
        new WorldFlags();
    }

    @Override
    public void onDisable() {
        onGoingQuest.saveData();
        dungeonEvents.removeAllEntities();
    }

    public void loadYMLFiles() {
        dungeonSetting = new Data(FileName.DUNGEON_SETTINGS);
        onGoingQuestData = new Data(FileName.ONGOING_QUEST);
    }

    public Data getDungeonSetting() {
        return dungeonSetting;
    }
    public Data getOnGoingQuestData() { return  onGoingQuestData; }

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
    public OpenAI getOpenai() { return openai; }
    public OnGoingQuest getOnGoingQuest() {
        return onGoingQuest;
    }
}