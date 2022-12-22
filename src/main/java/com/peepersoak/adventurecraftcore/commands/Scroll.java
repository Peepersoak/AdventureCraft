package com.peepersoak.adventurecraftcore.commands;

import com.peepersoak.adventurecraftcore.items.scrolls.ScrollFactory;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Scroll implements CommandExecutor {

    private final ScrollFactory scrollFactory = new ScrollFactory();

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (!(sender instanceof Player player)) return false;
        if (!player.isOp()) return false;
        if (args.length != 1) return false;

        String scroll = args[0];

        player.getInventory().addItem(scrollFactory.createScroll(scroll));

        return false;
    }
}
