package com.peepersoak.adventurecraftcore.commands;

import com.peepersoak.adventurecraftcore.items.wards.WardFactory;
import com.peepersoak.adventurecraftcore.items.wards.WardType;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Wards implements CommandExecutor {

    private final WardFactory wardFactory = new WardFactory();

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (!(sender instanceof Player player)) return false;
        if (!player.isOp()) return false;
        if (args.length != 1) return false;

        WardType type = WardType.valueOf(args[0]);
        player.getInventory().addItem(wardFactory.createWard(type));

        return false;
    }
}
