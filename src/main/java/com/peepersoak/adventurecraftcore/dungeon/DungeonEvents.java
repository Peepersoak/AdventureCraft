package com.peepersoak.adventurecraftcore.dungeon;

import com.peepersoak.adventurecraftcore.AdventureCraftCore;
import com.peepersoak.adventurecraftcore.combat.levelmobs.MobFactory;
import com.peepersoak.adventurecraftcore.items.arrows.ArrowFactory;
import com.peepersoak.adventurecraftcore.items.scrolls.ScrollFactory;
import com.peepersoak.adventurecraftcore.items.wards.WardFactory;
import com.peepersoak.adventurecraftcore.utils.Utils;
import org.bukkit.*;
import org.bukkit.block.BlockState;
import org.bukkit.block.Container;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

public class DungeonEvents implements CommandExecutor, Listener {

    public DungeonEvents() {
        loadLootTables(DungeonSettings.MOB_LOOT_TABLE, mobsLootList);
        loadLootTables(DungeonSettings.BOSS_LOOT_TABLE, bossLootList);
        loadLootTables(DungeonSettings.CHEST_LOOT_TABLE, chestLootList);
        loadMobType();

        enabledDungeon = AdventureCraftCore.getInstance().getDungeonSetting().getConfig().getBoolean(DungeonSettings.ENABLE_DUNGEON);

        List<String> getWorldList = AdventureCraftCore.getInstance().getDungeonSetting().getConfig().getStringList(DungeonSettings.DUNGEON_TYPE);
        Collections.shuffle(getWorldList);
        dungeonWorld = Bukkit.getWorld(getWorldList.get(0));

        chestType.add(Material.CHEST);
        chestType.add(Material.BARREL);
        chestType.add(Material.SHULKER_BOX);

        runPlayerUpdater();

        dungeonCoin.addUnsafeEnchantment(Enchantment.DURABILITY, 0);
        ItemMeta meta = dungeonCoin.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(Utils.color("&4Dungeon Coins"));

            List<String> lore = new ArrayList<>();
            lore.add(Utils.color("&cYou can sell this"));
            lore.add(Utils.color("&cto the dungeon master"));
            meta.setLore(lore);
            meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
            dungeonCoin.setItemMeta(meta);
        }

        spawnBoss();
        updateEntityCount();
    }

    private final World dungeonWorld;
    private final List<ItemStack> mobsLootList = new ArrayList<>();
    private final List<ItemStack> bossLootList = new ArrayList<>();
    private final List<ItemStack> chestLootList = new ArrayList<>();
    private final String BOSSLOOT = "BossLoot";
    private final String MOBLOOT = "MobsLoot";
    private final String CHESTLOOT = "chestLoot";
    private final String MOBTYPE = "MobTypes";
    private final List<Location> openChest = new ArrayList<>();
    private final List<EntityType> mobType = new ArrayList<>();
    private final List<UUID> playerUUID = new ArrayList<>();
    private boolean setSpawner = false;
    private final ScrollFactory scrollFactory = new ScrollFactory();
    private final WardFactory wardFactory = new WardFactory();
    private final ArrowFactory arrowFactory = new ArrowFactory();
    private final List<Material> chestType = new ArrayList<>();
    private int maxEntityCount = 0;
    private final int MAX_ENTITY = 200;
    private final ItemStack dungeonCoin = new ItemStack(Material.SUNFLOWER);
    private Location bossSpawnLocation;
    private final NamespacedKey BOSS_PERSISTENT_DATA = new NamespacedKey(AdventureCraftCore.getInstance(), "BossData");
    private boolean enabledDungeon;

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (!(sender instanceof Player player)) {
            if (args.length == 2) {
                String cmd = args[0];
                String name = args[1];

                if (cmd.equalsIgnoreCase("enter")) {
                    Player target = Bukkit.getPlayer(name);
                    if (target == null) return false;
                    teleportPlayer(target);
                }

                else if (cmd.equalsIgnoreCase("exit")) {
                    Player target = Bukkit.getPlayer(name);
                    if (target == null) return false;
                    dungeonExit(target);
                }
            }
            return false;
        }

        if (!player.isOp()) return false;

        if (args.length == 1) {
            String cmd = args[0];
            if (cmd.equalsIgnoreCase("spawner")) {
                if (setSpawner) {
                    setSpawner = false;
                    player.sendMessage(Utils.color("&cSpawner set disabled"));
                } else {
                    setSpawner = true;
                    player.sendMessage(Utils.color("&6Break block to set the spawner"));
                }
            }

            if (cmd.equalsIgnoreCase("save")) {
               saveItemStack(mobsLootList, DungeonSettings.MOB_LOOT_TABLE);
               saveItemStack(bossLootList, DungeonSettings.BOSS_LOOT_TABLE);
               saveItemStack(chestLootList, DungeonSettings.CHEST_LOOT_TABLE);

               List<String> entityTypes = new ArrayList<>();
               for (EntityType type : mobType) {
                   entityTypes.add(type.name());
               }
               AdventureCraftCore.getInstance().getDungeonSetting().writeList(DungeonSettings.MOB_TYPES, entityTypes);
               AdventureCraftCore.getInstance().getLogger().info("Mob type has been save");
            }

            if (cmd.equalsIgnoreCase("bossloc")) {
                bossSpawnLocation = player.getLocation();
                AdventureCraftCore.getInstance().getDungeonSetting().writeString(DungeonSettings.BOSS_SPAWN, Utils.serialized(bossSpawnLocation));
                player.sendMessage(Utils.color("&6Boss spawn has been set"));
            }

            if (cmd.equalsIgnoreCase("true")) {
                maxEntityCount = 0;
                enabledDungeon = true;
                spawnBoss();
                AdventureCraftCore.getInstance().getDungeonSetting().writeBoolean(DungeonSettings.ENABLE_DUNGEON, enabledDungeon);
                player.sendMessage(Utils.color("&6Dungeon has been enabled"));
            }
            if (cmd.equalsIgnoreCase("false")) {
                maxEntityCount = 0;
                removeAllEntities();
                enabledDungeon = false;
                AdventureCraftCore.getInstance().getDungeonSetting().writeBoolean(DungeonSettings.ENABLE_DUNGEON, enabledDungeon);
                player.sendMessage(Utils.color("&cDungeon has been disabled"));
            }
        }

        if (args.length == 2) {
            String cmd = args[0];
            String type = args[1];

            if (cmd.equalsIgnoreCase("item")) {
                if (type.equalsIgnoreCase(BOSSLOOT)) {
                    Inventory inv = Bukkit.createInventory(null, 54, BOSSLOOT);
                    inv.setContents(bossLootList.toArray(new ItemStack[0]));
                    player.openInventory(inv);
                }
                else if (type.equalsIgnoreCase(MOBLOOT)) {
                    Inventory inv = Bukkit.createInventory(null, 54, MOBLOOT);
                    inv.setContents(mobsLootList.toArray(new ItemStack[0]));
                    player.openInventory(inv);
                }
                else if (type.equalsIgnoreCase(CHESTLOOT)) {
                    Inventory inv = Bukkit.createInventory(null, 54, CHESTLOOT);
                    inv.setContents(chestLootList.toArray(new ItemStack[0]));
                    player.openInventory(inv);
                }
                else if (type.equalsIgnoreCase(MOBTYPE)) {
                    Inventory inv = Bukkit.createInventory(null, 54, MOBTYPE);
                    List<ItemStack> eggs = new ArrayList<>();
                    for (EntityType t : mobType) {
                        eggs.add(new ItemStack(Material.valueOf((t.name() + "_Spawn_Egg").replace(" ", "_").toUpperCase())));
                    }
                    inv.setContents(eggs.toArray(new ItemStack[0]));
                    player.openInventory(inv);
                }
                else if (type.equalsIgnoreCase("coin")) {
                    player.getInventory().addItem(dungeonCoin);
                }
            }

            if (cmd.equalsIgnoreCase("tp")) {
                player.teleport(Objects.requireNonNull(Bukkit.getWorld(type)).getSpawnLocation());
            }
        }
        return false;
    }

    @EventHandler
    public void onInvClose(InventoryCloseEvent e) {
        String title = e.getView().getTitle();
        if (!title.equalsIgnoreCase(BOSSLOOT)
                && !title.equalsIgnoreCase(MOBLOOT)
                && !title.equalsIgnoreCase(CHESTLOOT)
                && !title.equalsIgnoreCase(MOBTYPE)) return;

        if (title.equalsIgnoreCase(BOSSLOOT)) {
            bossLootList.clear();
            Inventory inv = e.getInventory();
            for(ItemStack item : inv.getContents()) {
                if (item == null) continue;
                if (item.getType() == Material.AIR) continue;
                bossLootList.add(item);
            }
        } else if (title.equalsIgnoreCase(MOBLOOT)) {
            mobsLootList.clear();
            Inventory inv = e.getInventory();
            for(ItemStack item : inv.getContents()) {
                if (item == null) continue;
                if (item.getType() == Material.AIR) continue;
                mobsLootList.add(item);
            }
        } else if (title.equalsIgnoreCase(CHESTLOOT)) {
            chestLootList.clear();
            Inventory inv = e.getInventory();
            for(ItemStack item : inv.getContents()) {
                if (item == null) continue;
                if (item.getType() == Material.AIR) continue;
                chestLootList.add(item);
            }
        } else if (title.equalsIgnoreCase(MOBTYPE)) {
            mobType.clear();
            Inventory inv = e.getInventory();
            for (ItemStack item : inv.getContents()) {
                if (item == null) continue;
                if (item.getType() == Material.AIR) continue;
                String mobTypeName = item.getType().name().replace("_SPAWN_EGG", "").toUpperCase();
                System.out.println(mobTypeName);
                mobType.add(EntityType.valueOf(mobTypeName));
            }
        }

        System.out.println("Boss loot" + chestLootList.size());
        System.out.println("Mobs loot" + mobsLootList.size());
        System.out.println("Chest loot" + chestLootList.size());
    }

    @EventHandler
    public void onSpawn(CreatureSpawnEvent e) {
        final Location location = e.getLocation();
        final World world = location.getWorld();
        if (world == null || world != dungeonWorld) return;

        if (!enabledDungeon) {
            e.getEntity().remove();
            e.setCancelled(true);
            return;
        }

        if (e.getSpawnReason() == CreatureSpawnEvent.SpawnReason.SPAWNER) {
            int minLevel = 0;
            int maxLevel = playerUUID.size() * 5;
            int mobLevel = Utils.getRandom(maxLevel, minLevel);

            e.getEntity().remove();

            if (maxEntityCount > MAX_ENTITY) return;
            Collections.shuffle(mobType);
            LivingEntity entity = (LivingEntity) world.spawnEntity(location, mobType.get(0));
            new MobFactory(entity, mobLevel);
            maxEntityCount++;
        }
    }

    @EventHandler
    public void onDeath(EntityDeathEvent e) {
        if (e.getEntity().getWorld() != dungeonWorld) return;
        if (e.getEntity() instanceof Player) return;

        if (Utils.getPDC(e.getEntity()).has(BOSS_PERSISTENT_DATA, PersistentDataType.INTEGER)) {
            if (chestLootList.size() <= 0) return;
            int itemAmount = Utils.getRandom(10,5);

            int xpAmount = Utils.getRandom(2000, 1000);
            e.setDroppedExp(xpAmount);

            for (int i = 0; i < itemAmount; i++) {
                Collections.shuffle(chestLootList);
                ItemStack item = createLootItem(chestLootList.get(0), true);
                dungeonWorld.dropItemNaturally(e.getEntity().getLocation(), item);
            }

            return;
        }

        if (!mobType.contains(e.getEntityType())) return;
        if (maxEntityCount > 0) maxEntityCount--;

        int xpAmount = Utils.getRandom(1000, 100);
        e.setDroppedExp(xpAmount);

        if (Utils.getRandom(100) < 25) {
            dungeonWorld.dropItemNaturally(e.getEntity().getLocation(), dungeonCoin);
        }

        int itemAmount = Utils.getRandom(5, 1);
        if (mobsLootList.size() <= 0) return;
        for (int i = 0; i < itemAmount; i++) {
            Collections.shuffle(mobsLootList);
            ItemStack item = createLootItem(mobsLootList.get(0), false);
            dungeonWorld.dropItemNaturally(e.getEntity().getLocation(), item);
        }
    }

    @EventHandler
    public void onChunkLoad(ChunkLoadEvent e) {
        if (e.getWorld() != dungeonWorld) return;
        BlockState[] state = e.getChunk().getTileEntities();
        for (BlockState s : state) {
            if (s instanceof Container container) {
                Location location = s.getLocation();
                if (!chestType.contains(container.getType())) continue;
                if (openChest.contains(location)) continue;
                openChest.add(location);
                container.getInventory().clear();
                addChestContents(container.getInventory());
            }
            else if (s instanceof CreatureSpawner spawner) {
                setUpTheSpawner(spawner);
            }
        }
    }

    private void loadLootTables(String path, List<ItemStack> list) {
        List<String> loot = AdventureCraftCore.getInstance().getDungeonSetting().getConfig().getStringList(path);
        for (String str : loot) {
            ItemStack item = (ItemStack) Utils.deserialized(str);
            list.add(item);
        }
    }

    private void loadMobType() {
        List<String> type = AdventureCraftCore.getInstance().getDungeonSetting().getConfig().getStringList(DungeonSettings.MOB_TYPES);
        for (String str : type) {
            EntityType t = EntityType.valueOf(str);
            mobType.add(t);
        }
    }

    private void addChestContents(Inventory inv) {
        int itemCountMin = 1;
        int itemCountMax = 10;
        int itemCount = Utils.getRandom(itemCountMax, itemCountMin);
        for (int i = 0; i < itemCount; i++) {
            Collections.shuffle(chestLootList);
            int slot = Utils.getRandom(inv.getSize() - 1);
            inv.setItem(slot, createLootItem(chestLootList.get(0), false));
        }
    }

    private void runPlayerUpdater() {
        new BukkitRunnable() {
            @Override
            public void run() {
                for (Player player : Bukkit.getOnlinePlayers()) {
                    if (player.getWorld() == dungeonWorld) {
                        if (playerUUID.contains(player.getUniqueId())) continue;
                        playerUUID.add(player.getUniqueId());

                    } else {
                        playerUUID.remove(player.getUniqueId());
                    }
                }
            }
        }.runTaskTimer(AdventureCraftCore.getInstance(), 0, 100);
    }

    private void saveItemStack(List<ItemStack> list,String path) {
        List<String> serializedString = new ArrayList<>();
        for (ItemStack item : list) {
            serializedString.add(Utils.serialized(item));
        }
        AdventureCraftCore.getInstance().getDungeonSetting().writeList(path, serializedString);
        AdventureCraftCore.getInstance().getLogger().info(path + " hsa been saved");
    }

    private void teleportPlayer(Player player) {
        if (dungeonWorld == null) return;
        new BukkitRunnable() {
            int count = 10;
            @Override
            public void run() {
                if (count <= 0) {
                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "warp " + dungeonWorld.getName() + " " + player.getName());
                    this.cancel();
                    return;
                }
                if (count == 10) {
                    player.sendMessage(Utils.color("&6Entering the dungeon in 10 seconds!"));
                } else {
                    player.sendMessage(Utils.color("&c" + count));
                }
                count--;
            }
        }.runTaskTimer(AdventureCraftCore.getInstance(), 0, 20);
    }

    private void dungeonExit(Player player) {
        if (dungeonWorld == null) return;
        new BukkitRunnable() {
            int count = 10;
            @Override
            public void run() {
                if (count <= 0) {
                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "warp spawn " + player.getName());
                    this.cancel();
                    return;
                }
                if (count == 10) {
                    player.sendMessage(Utils.color("&Exiting the dungeon in 10 seconds!"));
                } else {
                    player.sendMessage(Utils.color("&c" + count));
                }
                count--;
            }
        }.runTaskTimer(AdventureCraftCore.getInstance(), 0, 20);
    }

    private ItemStack createLootItem(ItemStack itemStack, boolean isBoss) {
        ItemStack item = new ItemStack(itemStack.getType());

        if (item.getType() == Material.PAPER && Utils.getRandom(100) < 15) {
            item = scrollFactory.createScroll();
        }
        else if (item.getType() == Material.TOTEM_OF_UNDYING && Utils.getRandom(100) < 15) {
            item = wardFactory.createWard();
        }
        else if (item.getType() == Material.ARROW && Utils.getRandom(100) < 15) {
            item = arrowFactory.createArrow();
        }
        else if (item.getType() == Material.SUNFLOWER && Utils.getRandom(100) < 15) {
            item = dungeonCoin;
        }
        else {
            enchantItem(item);
        }

        int min = 1;
        int max = Math.min(item.getMaxStackSize(), (isBoss) ? Utils.getRandom(20, 10) : 5);
        int amount = (max < 2) ? 1 : Utils.getRandom(max, min);
        item.setAmount(amount);

        return item;
    }

    private void enchantItem(ItemStack item) {
        List<Enchantment> enchantments = Arrays.asList(Enchantment.values());
        Collections.shuffle(enchantments);
        int count = 0;
        int maxEnchantment = Utils.getRandom(5);

        for (Enchantment enchantment : enchantments) {
            if (count >= maxEnchantment) break;
            if (!enchantment.canEnchantItem(item)) continue;
            count++;
            int minLevel = 1;
            int maxLevel = enchantment.getMaxLevel();
            int level = (maxLevel < 2) ? 1 : Utils.getRandom(maxLevel, minLevel);
            item.addEnchantment(enchantment, level);
        }
    }

    private void setUpTheSpawner(CreatureSpawner spawner) {
        spawner.setRequiredPlayerRange(50);
        spawner.setSpawnCount(4);
        spawner.setDelay(100);
        spawner.setSpawnedType(EntityType.VILLAGER);
        spawner.setSpawnRange(10);
        spawner.update();
    }

    private void spawnBoss() {
        if (!enabledDungeon) return;
        String rawLocation = AdventureCraftCore.getInstance().getDungeonSetting().getConfig().getString(DungeonSettings.BOSS_SPAWN);
        if (rawLocation == null) return;
        bossSpawnLocation = (Location) Utils.deserialized(rawLocation);
        if (bossSpawnLocation == null) return;
        int level = Math.max((Math.max(playerUUID.size(), 1) * 5) * 2, 20);

        Ravager ravager = (Ravager) dungeonWorld.spawnEntity(bossSpawnLocation, EntityType.RAVAGER);
        Evoker evoker = (Evoker) dungeonWorld.spawnEntity(bossSpawnLocation, EntityType.EVOKER);

        Utils.getPDC(ravager).set(BOSS_PERSISTENT_DATA, PersistentDataType.INTEGER, 1);
        Utils.getPDC(evoker).set(BOSS_PERSISTENT_DATA, PersistentDataType.INTEGER, 2);

        ravager.setRemoveWhenFarAway(false);
        evoker.setRemoveWhenFarAway(false);

        new MobFactory(ravager, level);
        new MobFactory(evoker, level);

        ravager.setCustomName(Utils.color("&4Inuarashi"));
        evoker.setCustomName(Utils.color("&4Im"));

        ravager.addPassenger(evoker);

        updateBoss(ravager, evoker);
    }

    private void updateBoss(Ravager ravager, Evoker evoker) {
        new BukkitRunnable() {
            @Override
            public void run() {
                if (evoker.isDead() && ravager.isDead()) {
                    this.cancel();
                    return;
                }
                double distance = ravager.getLocation().distance(bossSpawnLocation);
                if (distance > 10) {
                    ravager.remove();
                    evoker.remove();
                    spawnBoss();
                }
            }
        }.runTaskTimer(AdventureCraftCore.getInstance(), 0, 20);
    }

    private void updateEntityCount() {
        new BukkitRunnable() {
            @Override
            public void run() {
                if (dungeonWorld == null) return;
                int count = 0;
                for (Entity entity : dungeonWorld.getEntities()) {
                    if (mobType.contains(entity.getType())) {
                        count++;
                    }
                }
                maxEntityCount = count;
            }
        }.runTaskTimer(AdventureCraftCore.getInstance(), 0, 20);
    }

    public void removeAllEntities() {
        List<Entity> list = dungeonWorld.getEntities();
        for (Entity ent : list) {
            if (ent instanceof Monster) ent.remove();
        }
    }
}
