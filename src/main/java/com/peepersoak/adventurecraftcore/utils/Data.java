package com.peepersoak.adventurecraftcore.utils;

import com.peepersoak.adventurecraftcore.AdventureCraftCore;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.List;


public class Data {

    public Data(String fileName) {
        this.fileName = fileName;
        init();
    }

    private final String fileName;
    private File file;
    private YamlConfiguration config;

    private void init() {
        file = new File(AdventureCraftCore.getInstance().getDataFolder(), fileName);

        if (!file.exists()) {
            if (AdventureCraftCore.getInstance().getResource(fileName) != null) {
                AdventureCraftCore.getInstance().saveResource(fileName, false);
                file = new File(AdventureCraftCore.getInstance().getDataFolder(), fileName);
            } else {
                try {
                    if (!file.getParentFile().exists()) {
                        file.getParentFile().mkdirs();
                    }
                    file.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                    AdventureCraftCore.getInstance().getLogger().warning(Utils.color("&cFailed To Create File: &b" + fileName));
                    return;
                }
            }
        }

        config = YamlConfiguration.loadConfiguration(file);
        config.options().copyDefaults(true);
    }

    private void saveConfig() {
        try {
            config.save(file);
            config = YamlConfiguration.loadConfiguration(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void writeString(String path, String value) {
        config.set(path, value);
        saveConfig();
    }

    public void writeInt(String path, int value) {
        config.set(path, value);
        saveConfig();
    }

    public void writeBoolean(String path, boolean value) {
        config.set(path, value);
        saveConfig();
    }

    public void writeList(String path, List<String> value) {
        config.set(path, value);
        saveConfig();
    }

    public boolean addToList(String path, String value) {
        List<String> list = config.getStringList(path);
        if (list.contains(value)) return false;
        list.add(value);
        config.set(path, list);
        saveConfig();
        return true;
    }

    public boolean addListToList(String path, List<String> listToAdd) {
        List<String> list = config.getStringList(path);
        for (String str : listToAdd) {
            if (list.contains(str)) continue;
            list.add(str);
        }
        config.set(path, list);
        saveConfig();
        return true;
    }

    public void removeToList(String path, String value) {
        List<String> list = config.getStringList(path);
        list.remove(value);
        config.set(path, list);
        saveConfig();
    }

    public YamlConfiguration getConfig() {
        return config;
    }
}
