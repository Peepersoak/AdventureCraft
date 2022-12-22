package com.peepersoak.adventurecraftcore.commands;

import com.peepersoak.adventurecraftcore.enchantment.ItemFactory;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Books implements CommandExecutor {

    private final ItemFactory itemFactory = new ItemFactory();

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (!(sender instanceof Player player)) return false;
        if (!player.isOp()) return false;

        if (args.length == 3) {
            if (args[0].equalsIgnoreCase("paper")) {
                int level = Integer.parseInt(args[1]);
                String type = args[2];

                itemFactory.setPaper(level, type);
                player.getInventory().addItem(itemFactory.createPaper());
                return false;
            }
        }

        player.getInventory().addItem(itemFactory.createBook(Integer.parseInt(args[0])));

        return false;
    }
}
