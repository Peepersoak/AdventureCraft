package com.peepersoak.adventurecraftcore.utils;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class OpenInventory implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) return false;

        if (args.length != 2) return false;

        String cmd = args[0];
        String targetName = args[1];

        Player target = Bukkit.getPlayer(targetName);
        if (target == null || !player.isOnline()) return false;
        if (!cmd.equalsIgnoreCase("inv")) return false;

        Inventory inv = Bukkit.createInventory(target, 54, Utils.color("&d" + targetName + " [Inventory]"));

        Inventory pInv = target.getInventory();

        for (int i = 0; i < inv.getSize(); i++) {
            switch (i) {
                case 1 -> inv.setItem(i, target.getInventory().getHelmet());
                case 2 -> inv.setItem(i, target.getInventory().getChestplate());
                case 3 -> inv.setItem(i, target.getInventory().getLeggings());
                case 4 -> inv.setItem(i, target.getInventory().getBoots());
                case 6 -> inv.setItem(i, target.getInventory().getItemInMainHand());
                case 7 -> inv.setItem(i, target.getInventory().getItemInOffHand());
                default -> {
                    if (i > 17) {
                        int slot = i - 18;
                        if (slot < 9) {
                            inv.setItem(i + 27, pInv.getItem(slot));
                        } else {
                            inv.setItem(i - 9, pInv.getItem(slot));
                        }
                    } else {
                        inv.setItem(i, getBlackPane());
                    }
                }
            }
        }

        player.openInventory(inv);
        return false;
    }

    private ItemStack getBlackPane() {
        ItemStack item = new ItemStack(Material.BLACK_STAINED_GLASS_PANE);
        ItemMeta meta = item.getItemMeta();
        assert meta != null;
        meta.setDisplayName(" ");
        item.setItemMeta(meta);
        return item;
    }
}
