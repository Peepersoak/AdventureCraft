package com.peepersoak.adventurecraftcore.achievement;

import com.peepersoak.adventurecraftcore.AdventureCraftCore;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class AchievementHandler {

	public AchievementHandler() {
		initiateConfig();
	}
	
	private File file;
	private FileConfiguration fileConfig;
	private List<String> marshmallow;
	private List<String> forbiddenLand;
	private List<String> exceedingLimit;
	private List<String> touchingTheUnknown;
	private List<String> godAmongMen;
	private List<String> theMonarch;
	private List<String> theLegendary;
	
	private final Achievement achievement = new Achievement();
	
	public void getMarshmallow() {
		marshmallow = fileConfig.getStringList("Marshmallow");
	}
	
	public void getForbiidenLand() {
		forbiddenLand = fileConfig.getStringList("Forbidden_Land");
	}
	
	public void getExceedingLimit() {
		exceedingLimit = fileConfig.getStringList("Exceeding_Limit");
	}
	
	public void getTouchingTheUnkown() {
		touchingTheUnknown = fileConfig.getStringList("Touching_The_Unkown");
	}
	
	public void getGodAmongMen() {
		godAmongMen = fileConfig.getStringList("God_Among_Men");
	}
	
	public void getTheMonarch() {
		theMonarch = fileConfig.getStringList("The_Ruler");
	}
	
	public void getTheLegendary() {
		theLegendary = fileConfig.getStringList("The_Legendary");
	}
	
	public void addToMarshmallow(String playerName) {
		getMarshmallow();
		if (marshmallow.contains(playerName)) return;
		marshmallow.add(playerName);
		fileConfig.set("Marshmallow", marshmallow);
		saveFileConfig();
		achievement.sendAchievement("Marshmallow", playerName);
	}
	
	public void addToForbiddenLan(String playerName) {
		getForbiidenLand();
		if (forbiddenLand.contains(playerName)) return;
		forbiddenLand.add(playerName);
		fileConfig.set("Forbidden_Land", forbiddenLand);
		saveFileConfig();
		achievement.sendAchievement("Forbidden_Land", playerName);
	}
	
	public void addExceedingLimits(String playerName) {
		getExceedingLimit();
		if (exceedingLimit.contains(playerName)) return;
		exceedingLimit.add(playerName);
		fileConfig.set("Exceeding_Limit", exceedingLimit);
		saveFileConfig();
		achievement.sendAchievement("Exceeding_Limit", playerName);
	}
	
	public void addTouchingTheUnkown(String playerName) {
		getTouchingTheUnkown();
		if (touchingTheUnknown.contains(playerName)) return;
		touchingTheUnknown.add(playerName);
		fileConfig.set("Touching_The_Unknown", touchingTheUnknown);
		saveFileConfig();
		achievement.sendAchievement("Touching_The_Unknown", playerName);
	}
	
	public void addGodAmongMen(String playerName) {
		getGodAmongMen();
		if (godAmongMen.contains(playerName)) return;
		godAmongMen.add(playerName);
		fileConfig.set("God_Among_Men", godAmongMen);
		saveFileConfig();
		achievement.sendAchievement("God_Among_Men", playerName);
	}
	
	public void addTheMonarch(String playerName) {
		getTheMonarch();
		if (theMonarch.contains(playerName)) return;
		theMonarch.add(playerName);
		fileConfig.set("The_Ruler", theMonarch);
		saveFileConfig();
		achievement.sendAchievement("The_Ruler", playerName);
	}
	
	public void addTheLegendary(String playerName) {
		getTheLegendary();
		if (theLegendary.contains(playerName)) return;
		theLegendary.add(playerName);
		fileConfig.set("The_Legendary", theLegendary);
		saveFileConfig();
		achievement.sendAchievement("The_Legendary", playerName);
	}
	
	public FileConfiguration getFileConfig() {
		return fileConfig;
	}
	
	public void saveFileConfig() {
		try {
			fileConfig.save(file);
			reload();
		} catch (IOException e) {
			System.out.println("Can't save the Custom Advancement file");
		}
	}
	
	public void reload() {
		fileConfig = YamlConfiguration.loadConfiguration(file);
	}
	
	public void initiateConfig() {
		file = new File(AdventureCraftCore.getInstance().getDataFolder(), "Custom_Advancement.yml");
		
		if (!file.exists()) {
			try {
				file.createNewFile();
			} catch (IOException e) {
				System.out.println("Custom advancement was not created!");
			}
		}
		
		fileConfig = YamlConfiguration.loadConfiguration(file);
		
		if (fileConfig.getString("Marshmallow") == null) {
			marshmallow = new ArrayList<>();
			marshmallow.add("Player_Name");
			fileConfig.addDefault("Marshmallow", marshmallow);
		}
		if (fileConfig.getString("Forbidden_Land") == null) {
			forbiddenLand = new ArrayList<>();
			forbiddenLand.add("Player_Name");
			fileConfig.addDefault("Forbidden_Land", forbiddenLand);
		}
		if (fileConfig.getString("Exceeding_Limit") == null) {
			exceedingLimit = new ArrayList<>();
			exceedingLimit.add("Player_Name");
			fileConfig.addDefault("Exceeding_Limit", exceedingLimit);
		}
		if (fileConfig.getString("Touching_The_Unkown") == null) {
			touchingTheUnknown = new ArrayList<>();
			touchingTheUnknown.add("Player_Name");
			fileConfig.addDefault("Touching_The_Unkown", touchingTheUnknown);
		}
		if (fileConfig.getString("God_Among_Men") == null) {
			godAmongMen = new ArrayList<>();
			godAmongMen.add("Player_Name");
			fileConfig.addDefault("God_Among_Men", godAmongMen);
		}
		if (fileConfig.getString("The_Ruler") == null) {
			theMonarch = new ArrayList<>();
			theMonarch.add("Player_Name");
			fileConfig.addDefault("The_Ruler", theMonarch);
		}
		if (fileConfig.getString("The_Legendary") == null) {
			theLegendary = new ArrayList<>();
			theLegendary.add("Player_Name");
			fileConfig.addDefault("The_Legendary", theLegendary);
		}
		
		fileConfig.options().copyDefaults(true);
		saveFileConfig();
	}
}
