package com.peepersoak.adventurecraftcore.commands;

import com.peepersoak.adventurecraftcore.items.arrows.ArrowType;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class ArrowAutoComplete implements TabCompleter {
    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {

        if (!(sender instanceof Player player)) return null;
        if (!player.isOp()) return null;

        if (args.length == 1) {
            List<String> list = new ArrayList<>();
            for (ArrowType t : ArrowType.values()) {
                list.add(t.name());
            }
            return list;
        }

        return null;
    }
}
