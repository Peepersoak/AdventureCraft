package com.peepersoak.adventurecraftcore.dungeon;

import com.peepersoak.adventurecraftcore.AdventureCraftCore;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Collections;
import java.util.List;

public class DungeonRunnable extends BukkitRunnable {

    public DungeonRunnable() {
        List<String> getWorldList = AdventureCraftCore.getInstance().getDungeonSetting().getConfig().getStringList(DungeonSettings.DUNGEON_TYPE);
        Collections.shuffle(getWorldList);

        if (getWorldList.size() == 0) {
            dungeonWorld = null;
        } else {
            dungeonWorld = Bukkit.getWorld(getWorldList.get(0));
        }
    }

    private final World dungeonWorld;

    public void removeInvisibility() {
        if (dungeonWorld == null) {
            this.cancel();
            return;
        }
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (player.getWorld() != dungeonWorld) continue;
            player.removePotionEffect(PotionEffectType.INVISIBILITY);
        }
    }

    @Override
    public void run() {
        removeInvisibility();
    }
}
