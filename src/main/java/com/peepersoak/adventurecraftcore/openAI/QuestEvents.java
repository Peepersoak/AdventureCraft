package com.peepersoak.adventurecraftcore.openAI;

import com.peepersoak.adventurecraftcore.AdventureCraftCore;
import com.peepersoak.adventurecraftcore.utils.Utils;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Item;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.enchantment.EnchantItemEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class QuestEvents implements Listener {
    private final HashMap<UUID, Long> coolDown = new HashMap<>();
    private final HashMap<UUID, Location> lastLocation = new HashMap<>();

    //Handle the quest activation
    @EventHandler
    public void onQuestClick(InventoryClickEvent e) {
        if (!(e.getWhoClicked() instanceof Player player)) return;
        String inventoryName = e.getView().getTitle();

        boolean isPersonalBoard = inventoryName.equalsIgnoreCase(ObjectiveStrings.QUEST_PERSONAL_BOARD);
        boolean isAdventureCraftBoard = inventoryName.equalsIgnoreCase(ObjectiveStrings.QUEST_ADVENTURECRAFT_BOARD);

        if (!isPersonalBoard && !isAdventureCraftBoard) return;
        e.setCancelled(true);

        ItemStack questPaper = e.getCurrentItem();
        if (questPaper == null) return;
        ItemMeta meta = questPaper.getItemMeta();
        if (meta == null) return;
        PersistentDataContainer data = meta.getPersistentDataContainer();
        String questUUIDRaw = data.get(ObjectiveStrings.PDC_QUEST_UUID, PersistentDataType.STRING);
        if (questUUIDRaw == null) return;
        UUID questUUID = UUID.fromString(questUUIDRaw);
        UUID playerUUID = e.getWhoClicked().getUniqueId();

        if (isPersonalBoard) {
            // Check first if the quest is complete and give the rewards
            QuestData questData = AdventureCraftCore.getInstance().getOnGoingQuest().getQuest(playerUUID, questUUID);
            if (questData == null) return;
            if (questData.isDone()) {
                player.closeInventory();
                questData.collectRewards(player);
                return;
            }

            // Deactive all quest aside from the quest is being activated
            List<QuestData> getAllQuest = AdventureCraftCore.getInstance().getOnGoingQuest().getAllQuest(playerUUID);
            if (getAllQuest == null || getAllQuest.isEmpty()) return;
            getAllQuest.forEach(quest -> {
                if (quest.getQuestUUID().equals(questUUID)) {
                    quest.activateQuest(player);
                } else {
                    quest.deactiveQuest(player, false);
                }
            });
        }

        if (isAdventureCraftBoard) {
            AdventureCraftCore.getInstance().getQuestManager().getQuest(player, questUUID);
        }

        player.closeInventory();
    }

    // Assign the permission once the player join
    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        Player player = e.getPlayer();
        UUID playerUUID = player.getUniqueId();
        List<QuestData> questList = AdventureCraftCore.getInstance().getOnGoingQuest().getAllQuest(playerUUID);
        if (questList == null || questList.isEmpty()) return;
        QuestData quest = questList.stream().filter(QuestData::isActive).findFirst().orElse(null);
        if (quest == null) return;
        quest.assignPermission(player, true);
        player.sendMessage(Utils.color("&6You have an active quest! &b" + quest.getQuestName()));
    }

    // Listen to block break
    @EventHandler
    public void onBreak(BlockBreakEvent e) {
        Player player = e.getPlayer();
        Block block = e.getBlock();

        boolean hasPermission = checkPermission(player, ObjectiveStrings.BREAK_QUEST_PERMISSION) || checkPermission(player, ObjectiveStrings.HARVEST_QUEST_PERMISSION);
        if (!hasPermission) return;

        ItemStack item = e.getPlayer().getItemInUse();
        if (item != null) {
            if (item.containsEnchantment(Enchantment.SILK_TOUCH)) return;
        }

        QuestData quest = getQuestData(player);
        if (quest == null) return;
        quest.updateQuest(block);
    }

    @EventHandler
    public void onWalk(PlayerMoveEvent e) {
        Player player = e.getPlayer();
        UUID playerUUID = player.getUniqueId();
        long systemTime = System.currentTimeMillis();

        boolean hasWalkPerm = checkPermission(player, ObjectiveStrings.WALK_QUEST_PERMISSION);
        boolean hasFlyPerm = checkPermission(player, ObjectiveStrings.FLY_QUEST_PERMISSION);

        if (!hasWalkPerm && !hasFlyPerm) return;
        if (hasFlyPerm && !player.isGliding()) return;
        if (hasWalkPerm && player.isGliding()) return;
        // This will only proceed every seconds
        if (coolDown.containsKey(playerUUID)) {
            if (coolDown.get(playerUUID) > systemTime) return;
            coolDown.replace(playerUUID, systemTime + 1000);
        } else {
            coolDown.put(playerUUID, systemTime + 1000);
        }

        if (hasWalkPerm) {
            if (lastLocation.containsKey(playerUUID)) {
                World lastworld = lastLocation.get(playerUUID).getWorld();
                World currentWorld = player.getWorld();
                if (lastworld != null && lastworld.equals(currentWorld)) {
                    double distance = lastLocation.get(playerUUID).distance(player.getLocation());
                    if (distance <= 1.5) return;
                }
                lastLocation.replace(playerUUID, player.getLocation());
            } else {
                lastLocation.put(playerUUID, player.getLocation());
            }
        }

        QuestData quest = getQuestData(player);
        if (quest == null) return;
        quest.updateQuest();
    }

    @EventHandler
    public void onKill(EntityDeathEvent e) {
        if (e.getEntity().getKiller() == null) return;
        Player player = e.getEntity().getKiller();
        LivingEntity entity = e.getEntity();

        boolean hasPermission = checkPermission(player, ObjectiveStrings.KILL_QUEST_PERMISSION);
        if (!hasPermission) return;

        if (!(player.getItemInUse() instanceof Item item)) return;

        QuestData quest = getQuestData(player);
        if (quest == null) return;
        quest.updateQuest(entity, item.getItemStack());
    }

    @EventHandler
    public void onEnchant(EnchantItemEvent e) {
        Player player = e.getEnchanter();

        boolean hasPermission = checkPermission(player, ObjectiveStrings.ENCHANT_QUEST_PERMISSION);
        if (!hasPermission) return;

        ItemStack item = e.getItem();
        Map<Enchantment, Integer> allEnchants = e.getEnchantsToAdd();
        if (allEnchants.isEmpty()) return;

        QuestData quest = getQuestData(player);
        if (quest == null) return;
        quest.updateQuest(item, allEnchants);
    }

    @EventHandler
    public void onFish(PlayerFishEvent e) {
        Player player = e.getPlayer();
        boolean hasPermission = checkPermission(player, ObjectiveStrings.FISHIN_QUEST_PERMISSION);
        if (!hasPermission) return;

        if (!(e.getCaught() instanceof Item item)) return;
        ItemStack itemStack = item.getItemStack();

        QuestData quest = getQuestData(player);
        if (quest == null) return;
        quest.updateQuest(itemStack.getType());
    }

    @EventHandler
    public void onCraft(CraftItemEvent e) {
        if (!(e.getWhoClicked() instanceof Player player)) return;


        boolean hasPermission = checkPermission(player, ObjectiveStrings.CRAFT_QUEST_PERMISSION);
        if (!hasPermission) return;

        /**
         * TODO The formula below will not work properly on item that can't be stack
         * Since it will calculate base on the number of items present or the possible item that can be crafted
         * it should also consider the invotry space and how much item it can create
         * */
        int defaultAmount = e.getRecipe().getResult().getAmount();
        int totalAmount = defaultAmount;
        if (e.isShiftClick()) {
            int leastIngeredient = -1;
            for (ItemStack item : e.getInventory().getMatrix()) {
                if (item == null || item.getType() == Material.AIR) continue;
                int count = item.getAmount() * defaultAmount;
                if (leastIngeredient == -1 || count < leastIngeredient) {
                    leastIngeredient = count;
                }
            }
            totalAmount = leastIngeredient;
        }
        QuestData data = getQuestData(player);
        if (data == null) return;
        System.out.println(totalAmount +" " + e.getRecipe().getResult().getType());
        data.updateQuest(e.getRecipe().getResult(), totalAmount);
    }

    // For non craftable items using crafting table
    @EventHandler
    public void onUpgrade(InventoryClickEvent e) {
        Inventory inventory = e.getClickedInventory();
        if (inventory == null) return;
        if (!(e.getWhoClicked() instanceof Player player)) return;
        boolean hasPerms = checkPermission(player, ObjectiveStrings.CRAFT_QUEST_PERMISSION);
        if (!hasPerms) return;

        if (inventory.getType() == InventoryType.SMITHING) {
            if (e.getSlot() != 3) return;

            ItemStack firsItem = e.getInventory().getItem(0);
            ItemStack secondItem = e.getInventory().getItem(1);
            ItemStack thirdItem = e.getInventory().getItem(2);
            ItemStack fourtItem = e.getInventory().getItem(3);

            if (firsItem == null || secondItem == null || thirdItem == null || fourtItem == null) return;
            if (firsItem.getType() != Material.NETHERITE_UPGRADE_SMITHING_TEMPLATE) return;
            if (thirdItem.getType() != Material.NETHERITE_INGOT) return;

            QuestData data = getQuestData(player);
            if (data == null) return;
            data.updateQuest(fourtItem, 1);
        }
    }

    private boolean checkPermission(Player player, String type) {
        boolean isOP = player.isOp();
        boolean hasPerms = player.hasPermission(type);
        return !isOP && hasPerms;
    }

    private QuestData getQuestData(Player player) {
        PersistentDataContainer data = player.getPersistentDataContainer();
        String questUUIDRaw = data.get(ObjectiveStrings.PDC_QUEST_UUID, PersistentDataType.STRING);
        if (questUUIDRaw == null) return null;
        UUID questUUID = UUID.fromString(questUUIDRaw);
        UUID playerUUID = player.getUniqueId();
        return AdventureCraftCore.getInstance().getOnGoingQuest().getQuest(playerUUID, questUUID);
    }
}
