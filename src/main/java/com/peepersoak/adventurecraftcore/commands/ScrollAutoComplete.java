package com.peepersoak.adventurecraftcore.commands;

import com.peepersoak.adventurecraftcore.items.scrolls.ScrollType;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class ScrollAutoComplete implements TabCompleter {
    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {

        if (!(sender instanceof Player player)) return null;
        if (!player.isOp()) return null;

        if (args.length == 1) {
            List<String> type = new ArrayList<>();
            for (ScrollType s : ScrollType.values()) {
                type.add(s.name());
            }
            return type;
        }

        return null;
    }
}
