package com.peepersoak.adventurecraftcore.commands;

import com.peepersoak.adventurecraftcore.items.arrows.ArrowFactory;
import com.peepersoak.adventurecraftcore.items.arrows.ArrowType;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Arrow implements CommandExecutor {

    ArrowFactory factory = new ArrowFactory();

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) return false;
        if (!player.isOp()) return false;
        if (args.length != 1) return false;

        ArrowType type = ArrowType.valueOf(args[0]);
        player.getInventory().addItem(factory.createArrow(type));
        return false;
    }
}
