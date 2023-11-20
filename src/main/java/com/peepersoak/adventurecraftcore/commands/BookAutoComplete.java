package com.peepersoak.adventurecraftcore.commands;

import com.peepersoak.adventurecraftcore.enchantment.Enchantments;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.util.Arrays;
import java.util.List;

public class BookAutoComplete implements TabCompleter {

    private final Enchantments enchantments = new Enchantments();

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {

        if (args.length == 1) {
            String[] arr = {"normal", "custom", "skill"};
            return Arrays.asList(arr);
        } else if (args.length == 2) {
            enchantments.setAllEnchantment();

            if (args[0].equalsIgnoreCase("normal")) {
                return enchantments.getNormalEnchant().stream().map(el -> el.replace(" ", "_")).toList();
            } else if (args[0].equalsIgnoreCase("custom")) {
                return enchantments.getCustomEnchant().stream().map(el -> el.replace(" ", "_")).toList();
            } else if (args[0].equalsIgnoreCase("skill")) {
                return enchantments.getSkills().stream().map(el -> el.replace(" ", "_")).toList();
            }
        }

        return null;
    }
}
