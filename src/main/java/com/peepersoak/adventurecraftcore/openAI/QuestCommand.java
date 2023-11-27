package com.peepersoak.adventurecraftcore.openAI;

import com.peepersoak.adventurecraftcore.AdventureCraftCore;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;

public class QuestCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player player) {
            OnGoingQuest onGoingQuest = AdventureCraftCore.getInstance().getOnGoingQuest();
            List<QuestData> questList = onGoingQuest.getAllQuest(player.getUniqueId());
            if (questList == null) {
                questList = new ArrayList<>();
            }
            Inventory inv = Bukkit.createInventory(null, InventoryType.HOPPER, ObjectiveStrings.QUEST_PERSONAL_BOARD);
            keepUpdatingQuestWindow(player, questList, inv);
        } else {
            String player = args[0];
            Player target = Bukkit.getPlayer(player);
            if (target == null || !target.isOnline()) return false;
            QuestManager manager = AdventureCraftCore.getInstance().getQuestManager();
            List<QuestData> questDataList = new ArrayList<>(manager.getTodaysAvailableQuest().values());
            Inventory inv = Bukkit.createInventory(null, 54, ObjectiveStrings.QUEST_ADVENTURECRAFT_BOARD);
            keepUpdatingQuestWindow(target, questDataList, inv);
        }
        return false;
    }

    private void keepUpdatingQuestWindow(Player player, List<QuestData> questDataList, Inventory inv) {
        setQuestItems(inv, questDataList, inv.getSize());
        player.openInventory(inv);
        new BukkitRunnable() {
            @Override
            public void run() {
                List<HumanEntity> entities = inv.getViewers();
                if (entities.isEmpty()) {
                    this.cancel();
                    return;
                }
                setQuestItems(inv, questDataList, inv.getSize());
            }
        }.runTaskTimer(AdventureCraftCore.getInstance(), 0, 20);
    }

    private void setQuestItems(Inventory inventory, List<QuestData> questList, int total) {
        List<ItemStack> questPaper = new ArrayList<>();
        if (questList != null && !questList.isEmpty())  {
            for (int i = 0; i < questList.size() && i < total; i++) {
                QuestData data = questList.get(i);
                ItemStack paper = data.createPaperQuest();
                questPaper.add(paper);
            }
        }
        inventory.setContents(questPaper.toArray(new ItemStack[0]));
    }
}