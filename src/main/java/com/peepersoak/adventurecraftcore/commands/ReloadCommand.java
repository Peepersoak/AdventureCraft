package com.peepersoak.adventurecraftcore.commands;

import com.peepersoak.adventurecraftcore.AdventureCraftCore;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ReloadCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (sender instanceof Player player) {
            if (!player.isOp()) return false;
        }

        AdventureCraftCore.getInstance().generalReload();
        AdventureCraftCore.getInstance().getLogger().info("All configs has been reloaded");

        return false;
    }
}
