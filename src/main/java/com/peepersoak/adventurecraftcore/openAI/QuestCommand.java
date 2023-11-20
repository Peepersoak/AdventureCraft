package com.peepersoak.adventurecraftcore.openAI;

import com.peepersoak.adventurecraftcore.AdventureCraftCore;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class QuestCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        Player target;

        if (sender instanceof Player player) {
            if (!player.isOp()) return false;
            target = player;
        } else {
            target = null;
        }

        if (args.length > 0) {
            String cmd = args[0];
            if (cmd.equalsIgnoreCase("open")) {
                OnGoingQuest onGoingQuest = AdventureCraftCore.getInstance().getOnGoingQuest();
                if (target == null) {
                    System.out.println("Target is false");
                    return false;
                }
                List<Quest> questList = onGoingQuest.getQuest(target.getUniqueId());
                if (questList == null || questList.isEmpty()) {
                    System.out.println("Quest is empty");
                    return false;
                }

                List<ItemStack> questPaper = new ArrayList<>();

                for (Quest quest : questList) {
                    ItemStack paper = quest.getFormattedQuestPaper();
                    questPaper.add(paper);
                }

                Inventory inv = Bukkit.createInventory(null, 54, "Quest");
                inv.setContents(questPaper.toArray(new ItemStack[0]));

                target.openInventory(inv);
                return false;
            }
        }

        if (target != null) {
            String cmd = String.join(" ", args);
            Bukkit.getScheduler().runTaskAsynchronously(AdventureCraftCore.getInstance(), () -> {
                AdventureCraftCore.getInstance().getOnGoingQuest().createANewQuest(target, cmd);
            });
            target.sendMessage("Quest creating...");
        }
        return false;
    }
}
