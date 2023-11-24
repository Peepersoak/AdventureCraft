package com.peepersoak.adventurecraftcore.openAI;

import com.peepersoak.adventurecraftcore.AdventureCraftCore;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;

public class QuestCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        Player target;

        if (sender instanceof Player player) {
            target = player;
        } else {
            target = null;
        }

        if (args.length > 0) {
            String cmd = args[0];
            if (cmd.equalsIgnoreCase("open")) {
                if (target == null) {
                    System.out.println("Target is false");
                    return false;
                }
                keepUpdatingQuestWindow(target);
                return false;
            }
        }

        if (target != null) {
            String cmd = String.join(" ", args);
            Bukkit.getScheduler().runTaskAsynchronously(AdventureCraftCore.getInstance(), () -> {
                AdventureCraftCore.getInstance().getOnGoingQuest().createANewQuest(target, cmd);
            });
        }
        return false;
    }

    private void keepUpdatingQuestWindow(Player player) {
        OnGoingQuest onGoingQuest = AdventureCraftCore.getInstance().getOnGoingQuest();

        List<QuestData> questList = onGoingQuest.getAllQuest(player.getUniqueId());
        if (questList == null || questList.isEmpty()) {
            System.out.println("Quest is empty");
            return;
        }
        final Inventory inv = Bukkit.createInventory(null, 54, ObjectiveStrings.QUEST_INVENTORY_NAME);
        setQuestItems(inv, questList);
        player.openInventory(inv);
        new BukkitRunnable() {
            @Override
            public void run() {
                List<HumanEntity> entities = inv.getViewers();
                if (entities.isEmpty()) {
                    this.cancel();
                    return;
                }
                setQuestItems(inv, questList);
            }
        }.runTaskTimer(AdventureCraftCore.getInstance(), 0, 20);
    }

    private void setQuestItems(Inventory inventory, List<QuestData> questList) {
        List<ItemStack> questPaper = new ArrayList<>();
        for (QuestData quest : questList) {
            ItemStack paper = quest.createPaperQuest();
            questPaper.add(paper);
            if (quest.getRewardCustomItem() != null && quest.getRewardCustomItem().getRewards() != null) {
                ItemStack item = quest.getRewardCustomItem().getRewards();
                questPaper.add(item);
            }
        }
        inventory.setContents(questPaper.toArray(new ItemStack[0]));
    }
}
