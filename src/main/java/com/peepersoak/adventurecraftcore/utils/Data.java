package com.peepersoak.adventurecraftcore.utils;

import com.peepersoak.adventurecraftcore.AdventureCraftCore;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;

public class Data {

    public Data(String fileName) {
        this.fileName = fileName;
        init();
    }

    private final String fileName;
    private YamlConfiguration config;

    private void init() {
        File file = new File(AdventureCraftCore.getInstance().getDataFolder(), fileName);

        if (!file.exists()) {
            if (AdventureCraftCore.getInstance().getResource(fileName) != null) {
                AdventureCraftCore.getInstance().saveResource(fileName, false);
                file = new File(AdventureCraftCore.getInstance().getDataFolder(), fileName);
            } else {
                AdventureCraftCore.getInstance().getLogger().warning("Failed To Create File: " + fileName);
            }
        }

        config = YamlConfiguration.loadConfiguration(file);
        config.options().copyDefaults(true);
    }

    public YamlConfiguration getConfig() {
        return config;
    }
}
