package com.peepersoak.adventurecraftcore.combat.levelmobs.zombie;

import com.peepersoak.adventurecraftcore.AdventureCraftCore;
import com.peepersoak.adventurecraftcore.utils.Utils;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.extent.Extent;
import com.sk89q.worldedit.util.Location;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import com.sk89q.worldguard.protection.regions.RegionQuery;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Zombie;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;

public class DestroyBlock extends BukkitRunnable {

    public DestroyBlock(Zombie entity) {
        this.entity = entity;
    }
    private final Zombie entity;

    private void getBlock() {
        if (!hasPlayerAround()) return;
        if (Utils.getRandom(100) > 15) return;

        Block block = entity.getTargetBlockExact(1);
        if (block != null) {
            Block bottomBlock = block.getRelative(BlockFace.DOWN);
            if (removeRandom(block)) return;
            if (removeRandom(bottomBlock)) return;
        }

        if (!removeRandom(entity.getEyeLocation().getBlock())) {
            removeRandom(entity.getEyeLocation().getBlock().getRelative(BlockFace.DOWN));
        }
    }

    private boolean hasPlayerAround() {
        for (Entity entity : entity.getNearbyEntities(5, 5, 5)) {
            if (entity instanceof Player) return true;
        }
        return false;
    }

    private boolean removeBlock(Block block) {
        if (block.getType() != Material.AIR && block.getType() != Material.OBSIDIAN && block.getType() != Material.BEDROCK && block.getType().isSolid()) {
            block.breakNaturally();
            return true;
        }
        return false;
    }

    private boolean removeRandom(Block b) {
        for (BlockFace face : getFace()) {
            Block block = b.getRelative(face);
            if (removeBlock(block)) return true;
        }
        return false;
    }

    private List<BlockFace> getFace() {
        List<BlockFace> list = new ArrayList<>();
        list.add(BlockFace.NORTH);
        list.add(BlockFace.EAST);
        list.add(BlockFace.SOUTH);
        list.add(BlockFace.WEST);
        return list;
    }

    @Override
    public void run() {
        Location location = new Location(BukkitAdapter.adapt(entity.getWorld()), entity.getLocation().getBlockX(), entity.getLocation().getBlockY(), entity.getLocation().getBlockZ());
        RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
        RegionQuery query = container.createQuery();
        ApplicableRegionSet set = query.getApplicableRegions(location);
        if (!set.testState(null, AdventureCraftCore.ZOMBIE_BREAK)) return;

        getBlock();
        if (this.entity.isDead()) this.cancel();
    }
}
