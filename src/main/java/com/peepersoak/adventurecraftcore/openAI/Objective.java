package com.peepersoak.adventurecraftcore.openAI;

import com.peepersoak.adventurecraftcore.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;

import java.util.UUID;

public class Objective {
    private final String objective;
    private final String title;
    private final String material;
    private final String entityType;
    private final String enchantment;
    private final int level;
    private final String world;
    private final String biome;
    private final int startY;
    private final int endY;
    private final int timeStart;
    private final int timeEnd;
    private final int totalCount;
    private final UUID objectiveUUID;
    private final BossBar bossBar;

    public Objective(String objective,
                     String title,
                     String material,
                     String entityType,
                     String enchantment,
                     int level,
                     int count,
                     int totalCount,
                     String world,
                     String biome,
                     int startY,
                     int endY,
                     int timeStart,
                     int timeEnd,
                     UUID objectiveUUID) {
        this.objective = objective;
        this.title = title;
        this.material = material;
        this.entityType = entityType;
        this.enchantment = enchantment;
        this.level = level;
        this.count = count;
        this.totalCount = totalCount;
        this.world = world;
        this.biome = biome;
        this.startY = startY;
        this.endY = endY;
        this.timeStart = timeStart;
        this.timeEnd = timeEnd;
        this.objectiveUUID = objectiveUUID;

        bossBar = Bukkit.createBossBar(Utils.color(title), BarColor.GREEN, BarStyle.SEGMENTED_10);
        updateBossBar();
    }

    private int count;

    public void assignObjective(Player player) {
        updateBossBar();
        bossBar.addPlayer(player);
    }
    public void removeObjective() {
        bossBar.removeAll();
    }
    // TODO Bug on update progress
    public boolean updateProgress(Player player) {
        count++;
        updateBossBar();
        if (count >= totalCount) {
            count = totalCount;
            announceObjectiveCompletion(player);
            return true;
        }
        return false;
    }
    public boolean updateProgress(Player player, int progress) {
        count += progress;
        updateBossBar();
        if (count >= totalCount) {
            count = totalCount;
            announceObjectiveCompletion(player);
            return true;
        }
        return false;
    }

    public UUID getObjectiveUUID() {
        return objectiveUUID;
    }

    public String getObjective() {
        return objective;
    }

    public String getTitle() {
        return title;
    }

    public String getMaterial() {
        return material;
    }

    public String getEntityType() {
        return entityType;
    }

    public String getEnchantment() {
        return enchantment;
    }

    public int getLevel() {
        return level;
    }

    public int getCount() {
        return count;
    }

    public int getTotalCount() {
        return totalCount;
    }

    public String getWorld() {
        return world;
    }

    public String getBiome() {
        return biome;
    }

    public int getStartY() {
        return startY;
    }

    public int getEndY() {
        return endY;
    }

    public int getTimeStart() {
        return timeStart;
    }

    public int getTimeEnd() {
        return timeEnd;
    }
    public void updateBossBarName(int duration) {
        if (bossBar != null) {
            bossBar.setTitle(Utils.color(title) + ": " + Utils.color("&c" + Utils.convertSecondsToTime(duration)));
        }
    }

    private void announceObjectiveCompletion(Player player) {
        player.sendMessage(Utils.color( "&7Objective &6" + title + " &7has been completed!"));
    }
    private void updateBossBar() {
        double progress = (totalCount - count) / (double) totalCount;
        if (progress <= 0.5) {
            bossBar.setColor(BarColor.YELLOW);
        }
        if (progress <= 0.2) {
            bossBar.setColor(BarColor.RED);
        }
        if (progress < 0) {
            progress = 0;
        }
        bossBar.setProgress(progress);
        if (progress <= 0) {
            bossBar.removeAll();
        }
    }
}
