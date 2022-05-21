package com.peepersoak.adventurecraftcore;

import com.peepersoak.adventurecraftcore.combat.levelmobs.Nightmare;
import com.peepersoak.adventurecraftcore.utils.Data;
import com.peepersoak.adventurecraftcore.utils.EventHandler;
import com.peepersoak.adventurecraftcore.utils.FileName;
import com.peepersoak.adventurecraftcore.world.WorldTime;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public final class AdventureCraftCore extends JavaPlugin {

    private static AdventureCraftCore instance;
    private final EventHandler eventHandler = new EventHandler();
    private final WorldTime worldTime = new WorldTime();
    private final Nightmare nightmare = new Nightmare();

    private Data scriptureSetting;

    @Override
    public void onEnable() {
        instance = this;
        getConfig().options().copyDefaults();
        saveDefaultConfig();

        eventHandler.registerEvents(this, Bukkit.getPluginManager());

        worldTime.runTaskTimer(this, 0, 20);
        nightmare.runTaskTimer(this, 0, 60);

        loadYMLFiles();
    }

    @Override
    public void onDisable() {
        //
    }

    public void loadYMLFiles() {
        scriptureSetting = new Data(FileName.SCRIPTURE_SETTINGS);
    }

    public static AdventureCraftCore getInstance() {
        return instance;
    }

    public Data getScriptureSetting() {
        return scriptureSetting;
    }
}