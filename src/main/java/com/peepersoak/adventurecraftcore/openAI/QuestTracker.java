package com.peepersoak.adventurecraftcore.openAI;

import com.peepersoak.adventurecraftcore.AdventureCraftCore;
import com.peepersoak.adventurecraftcore.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.enchantment.EnchantItemEvent;
import org.bukkit.event.entity.EntityBreedEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Random;

public class QuestTracker implements Listener {

    private final Random rand = new Random();

    public QuestTracker() {
        startTrackingSessionDuration();
    }

    // PLACING
    @EventHandler
    public void onPlace(BlockPlaceEvent e) {
        Player player = e.getPlayer();
        ItemStack item = e.getItemInHand();
        if (isImportantItem(item, player)) return;
        int count = track(player);

        // Check count if should trigger an event
        boolean createQuest = shouldCreateQuest(count);
        if (!createQuest) return;

        Block block = e.getBlock();
        String material = Utils.capitalizeFirstLetter(Utils.cleanString(block.getType().getKey().getKey()));

        String event = "The player is currently placing a " + material + " block. ";
        createPrompt(player, event);
    }
    // BREAKING || HARVESTING
    @EventHandler
    public void onBreak(BlockBreakEvent e) {
        Player player = e.getPlayer();
        int count = track(player);

        // Check count if should trigger an event
        boolean createQuest = shouldCreateQuest(count);
        if (!createQuest) return;

        Block block = e.getBlock();
        String material = Utils.capitalizeFirstLetter(Utils.cleanString(block.getType().getKey().getKey()));

        String event = "The player is currently breaking a " + material + " block. ";
        createPrompt(player, event);
    }
    // KILLING || DAMAGING || DYING
    @EventHandler
    public void damage(EntityDamageByEntityEvent e) {
        DamageCause cause = e.getCause();
        if (cause != DamageCause.ENTITY_ATTACK) return;

        Entity entity = e.getEntity();
        Entity damager = e.getDamager();

        Player eventPlayer = null;
        String eventTrigger = null;
        int count = 0;

        // Check count if should trigger an event

        // This is receiving damage
        if (entity instanceof Player player && damager instanceof LivingEntity enemy) {
            String enemyStr = Utils.capitalizeFirstLetter(Utils.cleanString(enemy.getType().getKey().getKey()));
            eventPlayer = player;
            if (e.getFinalDamage() >= player.getHealth()) {
                eventTrigger = "The player died while fighting/running against " + enemyStr;
            } else {
                eventTrigger = "The player received damage from " + enemyStr;
            }
            count = track(player);
        }

        // This is sending damage
        if (damager instanceof Player player && entity instanceof LivingEntity livingEntity) {
            String enemyStr = Utils.capitalizeFirstLetter(Utils.cleanString(livingEntity.getType().getKey().getKey()));
            eventPlayer = player;
            if (e.getFinalDamage() >= livingEntity.getHealth()) {
                eventTrigger = "The player killed " + enemyStr + ". ";
            } else {
                eventTrigger = "The player attacked " + enemyStr + ". ";
            }
            count = track(player);
        }

        boolean createQuest = shouldCreateQuest(count) ||
                shouldCreateQuest(count) ||
                shouldCreateQuest(count) ||
                shouldCreateQuest(count);
        if (!createQuest) return;

        if (eventTrigger != null) {
            createPrompt(eventPlayer, eventTrigger);
        }
    }
    // CONSUMING
    @EventHandler
    public void onConsume(PlayerItemConsumeEvent e) {
        ItemStack item = e.getItem();
        Player player = e.getPlayer();

        int count = track(player);
        // Check count if should trigger an event
        boolean createQuest = shouldCreateQuest(count);
        if (!createQuest) return;

        String eventTrigger = "The player has consumed a/an " + Utils.capitalizeFirstLetter(Utils.cleanString(item.getType().getKey().getKey()));
        createPrompt(player, eventTrigger);
    }
    // CRAFTING
    @EventHandler
    public void onCraft(CraftItemEvent e) {
        ItemStack item = e.getRecipe().getResult();
        if (!(e.getWhoClicked() instanceof Player player)) return;
        int count = track(player);
        // Check count if should trigger an event
        boolean createQuest = shouldCreateQuest(count);
        if (!createQuest) return;

        String material = Utils.capitalizeFirstLetter(Utils.cleanString(item.getType().getKey().getKey()));
        String eventTrigger = "The player craft a/an " + material + ". ";
        createPrompt(player, eventTrigger);
    }
    // SUMMONING
    @EventHandler
    public void onSummmon(EntityBreedEvent e) {
        LivingEntity entity = e.getEntity();
        if (!(e.getBreeder() instanceof Player player)) return;
        int count = track(player);
        // Check count if should trigger an event
        boolean createQuest = shouldCreateQuest(count);
        if (!createQuest) return;

        String entityStr = Utils.capitalizeFirstLetter(Utils.cleanString(entity.getType().getKey().getKey()));
        String eventTrigger = "The player breeds " + entityStr + ". ";
        createPrompt(player, eventTrigger);
    }
    // Enchanting
    @EventHandler
    public void onEnchant(EnchantItemEvent e) {
        Player player = e.getEnchanter();
        int count = track(player);
        ItemStack item = e.getItem();
        String material = Utils.capitalizeFirstLetter(Utils.cleanString(item.getType().getKey().getKey()));
        // Check count if should trigger an event
        boolean createQuest = shouldCreateQuest(count);
        if (!createQuest) return;

        String eventTrigger = "The player is currently enchanting a " + material + ". ";
        createPrompt(player, eventTrigger);
    }
    // Anvil
    @EventHandler
    public void onAnvilUse(InventoryClickEvent e) {
        Inventory inventory = e.getClickedInventory();
        if (inventory == null) return;
        if (!(e.getWhoClicked() instanceof Player player)) return;
        if (e.getClick() != ClickType.LEFT &&
                e.getClick() != ClickType.SHIFT_LEFT &&
                e.getClick() != ClickType.RIGHT &&
                e.getClick() != ClickType.SHIFT_RIGHT) return;

        String inventoryName = e.getView().getTitle();

        // First Item

        String firstIngredient = "nothing";
        ItemStack firstItem = e.getInventory().getItem(0);
        if (firstItem != null && firstItem.getType() != Material.AIR) {
            firstIngredient = Utils.capitalizeFirstLetter(Utils.cleanString(firstItem.getType().getKey().getKey()));
        }
        // Second Item
        boolean hasSecondItem = false;
        String secondIngredient = "nothing";
        if (e.getInventory().getSize() >= 2) {
            ItemStack secondItem = e.getInventory().getItem(1);
            if (secondItem != null && secondItem.getType() != Material.AIR) {
                hasSecondItem = true;
                secondIngredient = Utils.capitalizeFirstLetter(Utils.cleanString(secondItem.getType().getKey().getKey()));
            }
        }

        // Third Item
        boolean hasThirdItem = false;
        String thirdIngredient = "nothing";
        if (e.getInventory().getSize() >= 3) {
            ItemStack thirdItem = e.getInventory().getItem(2);
            if (thirdItem != null && thirdItem.getType() != Material.AIR) {
                hasThirdItem = true;
                thirdIngredient = Utils.capitalizeFirstLetter(Utils.cleanString(thirdItem.getType().getKey().getKey()));
            }
        }

        boolean hasFourthItem = false;
        String fourIngredient = "nothing";
        if (e.getInventory().getSize() >= 4) {
            ItemStack fourItem = e.getInventory().getItem(3);
            if (fourItem != null && fourItem.getType() != Material.AIR) {
                hasFourthItem = true;
                fourIngredient = Utils.capitalizeFirstLetter(Utils.cleanString(fourItem.getType().getKey().getKey()));
            }
        }

        int count = 0;
        String eventTrigger = "";

        if (inventory.getType() == InventoryType.ANVIL) {
            if (e.getSlot() != 2 || !hasThirdItem) return;
            // Check count if should trigger an event
            eventTrigger = "The player used an anvil to combine " + firstIngredient + " and " + secondIngredient + ". ";
        } else if (inventory.getType() == InventoryType.SMITHING) {
            if (e.getSlot() != 3 || !hasFourthItem) return;
            // Check count if should trigger an event
            eventTrigger = "The player used a smithing table and " + firstIngredient + " to upgrade " + secondIngredient + " using " + thirdIngredient + " to create " + fourIngredient + ". ";
        } else if (inventory.getType() == InventoryType.CARTOGRAPHY) {
            if (e.getSlot() != 2 || !hasThirdItem) return;
            // Check count if should trigger an event
            eventTrigger = "The player used a cartography. ";
        } else if (inventory.getType() == InventoryType.STONECUTTER) {
            if (e.getSlot() != 1 || !hasSecondItem) return;
            // Check count if should trigger an event
            eventTrigger = "The player used a stone cutter to cut a " + firstIngredient + " into " + secondIngredient + ". ";
        } else if (inventory.getType() == InventoryType.GRINDSTONE) {
            if (e.getSlot() != 2 || !hasThirdItem) return;
            // Check count if should trigger an event
            eventTrigger = "The player used a grindstone to remove an enchantment from " + thirdIngredient + ". ";
        } else if (inventory.getType() == InventoryType.LOOM) {
            if (e.getSlot() != 3 || !hasFourthItem) return;
            // Check count if should trigger an event
            eventTrigger = "The player used a loom to create a " + fourIngredient + ". ";
        } else if (inventory.getType() == InventoryType.FURNACE) {
            if (e.getSlot() != 2 || !hasThirdItem) return;
            // Check count if should trigger an event
            eventTrigger = "The player used a furnace to cook " + thirdIngredient + ". ";
        } else if (inventory.getType() == InventoryType.SMOKER) {
            if (e.getSlot() != 2 || !hasThirdItem) return;
            // Check count if should trigger an event
            eventTrigger = "The player used a smoker to cook " + thirdIngredient + ". ";
        } else if (inventory.getType() == InventoryType.BLAST_FURNACE) {
            if (e.getSlot() != 2 || !hasThirdItem) return;
            // Check count if should trigger an event
            eventTrigger = "The player used a blast furnace to smelt " + thirdIngredient + ". ";
        } else if (inventory.getType() == InventoryType.MERCHANT) {
            if (e.getSlot() != 2 || !hasThirdItem) return;
            // Check count if should trigger an event
            eventTrigger = "The player traded with a " + inventoryName +  " villager for " + thirdIngredient + ". ";
        } else {
            return;
        }

        count = track(player);
        boolean createQuest = shouldCreateQuest(count);
        if (!createQuest) return;

        System.out.println(count);
        createPrompt(player, eventTrigger);
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent e) {
        Player player = e.getPlayer();
        ItemStack item = e.getItem();
        if (e.getClickedBlock() != null && item != null) {
            if (isImportantItem(item, player)) {
                e.setCancelled(true);
            }
        }
    }

    private void createPrompt(Player player, String event) {
        StringBuilder prompt = new StringBuilder();

        String worldType;

        if (player.getWorld().getEnvironment() == World.Environment.NETHER) {
            worldType = "THE_NETHER";
        } else if (player.getWorld().getEnvironment() == World.Environment.THE_END) {
            worldType = "THE_END";
        } else {
            worldType = "OVERWORLD";
        }


        long playTime = Utils.getSessionDuration(player);
        long daysTime = playTime / (24 * 3600);;
        long hoursTime = (playTime % (24 * 3600)) / 3600;;
        long minutesTime = (playTime % 3600) / 60;;
        long secondsTime = playTime % 60;;

        String biome = player.getLocation().getBlock().getBiome().getKey().getKey();
        String mainItem = "nothing";
        String offItem = "nothing";
        if (player.getInventory().getItemInMainHand().getType() != Material.AIR) {
            mainItem = player.getInventory().getItemInMainHand().getType().getKey().getKey();
        }
        if (player.getInventory().getItemInOffHand().getType() != Material.AIR) {
            offItem = player.getInventory().getItemInOffHand().getType().getKey().getKey();
        }

        // Say what the player is doing when the event got trigger
        prompt.append(event);
        // How long they are playing
        prompt.append("They have been playing for ")
                .append(daysTime)
                .append(" days, ")
                .append(hoursTime)
                .append(" hour's, ")
                .append(minutesTime)
                .append(" minute's, ")
                .append("and ")
                .append(secondsTime)
                .append(" second's. ");

        boolean isStorming = player.getWorld().hasStorm();
        boolean isthunder =  player.getWorld().isThundering();

        // The world
        prompt.append("They are currently in ").append(worldType).append(" ");
        // The Biome
        prompt.append("in the biome of ").append(Utils.capitalizeFirstLetter(Utils.cleanString(biome))).append(". ");

        Location location = player.getLocation();
        Block block = location.add(0, -1, 0).getBlock();
        Material blockStandinMaterial = block.getType();
        String materialString = Utils.capitalizeFirstLetter(Utils.color(blockStandinMaterial.getKey().getKey()));
        prompt.append("They are currently standing on top of ").append(materialString).append(". ");

        if (isStorming) {
            prompt.append("It is currently raining hard right now. ");
        }
        if (isthunder) {
            prompt.append("Thunder can be heard in the distance. ");
        }

        // The Item in Main hand
        prompt.append("They are currently holding ").append(mainItem).append(" and ").append(offItem).append(" on the off hand. ");

        // Armors
        String helmet = "nothing";
        String chestplate = "nothing";
        String leggings = "nothing";
        String boots = "nothing";
        ItemStack helmetItem = player.getInventory().getHelmet();
        ItemStack chestplateItem = player.getInventory().getChestplate();
        ItemStack leggingsItem = player.getInventory().getLeggings();
        ItemStack bootsItem = player.getInventory().getBoots();

        if (helmetItem != null && helmetItem.getType() != Material.AIR) {
            helmet = helmetItem.getType().getKey().getKey();
        }
        if (chestplateItem != null && chestplateItem.getType() != Material.AIR) {
            chestplate = chestplateItem.getType().getKey().getKey();
        }
        if (leggingsItem != null && leggingsItem.getType() != Material.AIR) {
            leggings = leggingsItem.getType().getKey().getKey();
        }
        if (bootsItem != null && bootsItem.getType() != Material.AIR) {
            boots = bootsItem.getType().getKey().getKey();
        }

        prompt.append("They are wearing ").append(helmet).append(" in helmet, ");
        prompt.append(chestplate).append(" in chest plate, ");
        prompt.append(leggings).append(" in leggings, ");
        prompt.append(boots).append(" in boots. ");

        int level = player.getLevel();
        int expNeeded = player.getExpToLevel();
        prompt.append(" They have a level of ").append(level).append(" and needs at least ").append(expNeeded).append(" exp to level up. ");

        double hungryPoints = player.getFoodLevel();
        double healthPoints = player.getHealth();
        prompt.append("Their food level is ").append(hungryPoints).append(" points and their health is ").append(healthPoints).append(" points. ");

        StringBuilder importantItems = new StringBuilder();
        for (ItemStack item : player.getInventory().getContents()) {
            if (item == null || item.getType() == Material.AIR) continue;
            if (helmetItem == null || helmetItem.equals(item)) continue;
            if (chestplateItem == null || chestplateItem.equals(item)) continue;
            if (leggingsItem == null || leggingsItem.equals(item)) continue;
            if (bootsItem == null || bootsItem.equals(item)) continue;
            if (!AdventureCraftCore.getInstance().getQuestListChecker().itemIsRare(item)) continue;
            String material = Utils.capitalizeFirstLetter(Utils.cleanString(item.getType().getKey().getKey()));
            importantItems.append(material).append(", ");
        }

        if (importantItems.isEmpty()) {
            importantItems.append("nothing");
        }

        prompt.append("They currently carrying ").append(importantItems.toString().trim()).append(" in their inventory. ");



        prompt.append("Base on this information, create a %rank% rank quest and incorporate the %mythology% in the quest to infuse it with rich and intricate lore. The quest should have %objective_count% objectives.");

        AdventureCraftCore.getInstance().getOnGoingQuest().createANewQuest(player, prompt.toString().trim());
    }
    private int track(Player player) {
        PersistentDataContainer data = player.getPersistentDataContainer();
        Integer count = data.get(ObjectiveStrings.KEY_GENERAL_FREQUENCY, PersistentDataType.INTEGER);
        if (count == null) {
            count = 0;
        }
        count++;
        System.out.println(Utils.color("&b" + count + " " + ObjectiveStrings.KEY_GENERAL_FREQUENCY.getKey()));
        data.set(ObjectiveStrings.KEY_GENERAL_FREQUENCY, PersistentDataType.INTEGER, count);
        return count;
    }
    private boolean shouldCreateQuest(int count) {
        int frequency = AdventureCraftCore.getInstance().getQuestSetting().getQuestFrequency();
        if (count % frequency != 0) return false;
        return rand.nextBoolean();
    }

    private boolean isImportantItem(ItemStack item, Player player) {
        ItemMeta meta = item.getItemMeta();
        if (meta == null) return false;
        PersistentDataContainer pdc = meta.getPersistentDataContainer();
        boolean isImportant = pdc.has(ObjectiveStrings.PDC_CUSTOM_ITEM, PersistentDataType.STRING);

        if (isImportant) {
            player.sendMessage(Utils.color("&cYou can do that to this item!"));
        }

        return isImportant;
    }

    private void startTrackingSessionDuration() {
        new BukkitRunnable() {
            @Override
            public void run() {
                for (Player player : Bukkit.getOnlinePlayers()) {
                    Long duration = Utils.getPDC(player).get(ObjectiveStrings.KEY_SESSION_DURATION, PersistentDataType.LONG);
                    if (duration == null) duration = 0L;
                    duration += 5;
                    System.out.println("NEW DURATION = " + duration);
                    Utils.getPDC(player).set(ObjectiveStrings.KEY_SESSION_DURATION, PersistentDataType.LONG, duration);
                }
            }
        }.runTaskTimer(AdventureCraftCore.getInstance(), 0, 100);
    }
}
