package com.peepersoak.adventurecraftcore.dungeon;

import com.peepersoak.adventurecraftcore.AdventureCraftCore;
import com.peepersoak.adventurecraftcore.combat.levelmobs.MobFactory;
import com.peepersoak.adventurecraftcore.items.arrows.ArrowFactory;
import com.peepersoak.adventurecraftcore.items.scrolls.ScrollFactory;
import com.peepersoak.adventurecraftcore.items.wards.WardFactory;
import com.peepersoak.adventurecraftcore.utils.Flags;
import com.peepersoak.adventurecraftcore.utils.StringPath;
import com.peepersoak.adventurecraftcore.utils.Utils;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.block.BlockState;
import org.bukkit.block.Container;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.block.EnderChest;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.*;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.inventory.*;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

public class DungeonEvents implements CommandExecutor, Listener {

    public DungeonEvents() {
        loadLootTables(DungeonSettings.MOB_LOOT_TABLE, mobsLootList);
        loadLootTables(DungeonSettings.BOSS_LOOT_TABLE, bossLootList);
        loadLootTables(DungeonSettings.CHEST_LOOT_TABLE, chestLootList);
        loadMobType();

        List<String> getWorldList = AdventureCraftCore.getInstance().getDungeonSetting().getConfig().getStringList(DungeonSettings.DUNGEON_TYPE);
        Collections.shuffle(getWorldList);
        if (getWorldList.size() == 0)  {
            dungeonWorld = null;
        } else {
            dungeonWorld = Bukkit.getWorld(getWorldList.get(0));
        }

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

        runEntityCounter();
        createDungeonLife();

        BOSS_TYPE.add(EntityType.RAVAGER);
        BOSS_TYPE.add(EntityType.ILLUSIONER);
        BOSS_TYPE.add(EntityType.ZOMBIE);
        BOSS_TYPE.add(EntityType.WARDEN);
        BOSS_TYPE.add(EntityType.WITHER);
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
    private final ScrollFactory scrollFactory = new ScrollFactory();
    private final WardFactory wardFactory = new WardFactory();
    private final ArrowFactory arrowFactory = new ArrowFactory();
    private final List<Material> chestType = new ArrayList<>();
    private int maxEntityCount = 0;
    private final ItemStack dungeonCoin = new ItemStack(Material.SUNFLOWER);
    private ItemStack dungeonLife;
    private final NamespacedKey BOSS_PERSISTENT_DATA = new NamespacedKey(AdventureCraftCore.getInstance(), "BossData");
    private final NamespacedKey DUNGEON_LIFE_COUNT = new NamespacedKey(AdventureCraftCore.getInstance(), "DungeonLifeCount");
    private final List<EntityType> BOSS_TYPE = new ArrayList<>();
    private boolean hasBoss = false;

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

            if (cmd.equalsIgnoreCase("reset")) {
                maxEntityCount = 0;
                removeAllEntities();
                hasBoss = false;
                player.sendMessage(Utils.color("&6Dungeon has been reset"));
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
                else if (type.equalsIgnoreCase("life")) {
                    player.getInventory().addItem(dungeonLife);
                }
            }

            if (cmd.equalsIgnoreCase("tp")) {
                World world = Bukkit.getWorld(type);
                if (world != null) {
                    player.teleport(world.getSpawnLocation());
                }
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
        if (!Utils.checkWGState(e.getEntity(), Flags.IS_DUNGEON_WORLD)) return;
        if (e.getSpawnReason() == CreatureSpawnEvent.SpawnReason.CUSTOM) return;
        if (e.getSpawnReason() != CreatureSpawnEvent.SpawnReason.NATURAL) return;
        if (!(e.getEntity() instanceof Monster)) return;

        final Location location = e.getLocation();
        final World world = location.getWorld();
        if (world == null || world != dungeonWorld) return;

        if (Utils.getPDC(e.getEntity()).has(StringPath.MOB_LEVEL_KEY, PersistentDataType.INTEGER)) return;

        int MAX_ENTITY = Utils.getWorldGuardValue(e.getEntity(), Flags.MAX_ENTITY_COUNT);
        MAX_ENTITY = MAX_ENTITY == -1 ? 200 : MAX_ENTITY;
        if (!Utils.checkWGState(e.getEntity(), Flags.ENABLE_DUNGEON)
                || maxEntityCount > MAX_ENTITY) {
            e.getEntity().remove();
            e.setCancelled(true);
            return;
        }

        if (location.getBlock().getLightFromSky() == 0 && !Utils.checkWGState(e.getEntity(), Flags.ALLOW_MOBS_ON_CAVES)) {
            e.getEntity().remove();
            e.setCancelled(true);
            return;
        }

        if (!Utils.checkWGState(e.getEntity(), Flags.LEVEL_MOBS)) return;

        if (e.getSpawnReason() == CreatureSpawnEvent.SpawnReason.SPAWNER && Utils.checkWGState(e.getEntity(), Flags.DUNGEON_SPAWNER_ONLY)) {
            int minLevel = 0;
            int maxLevel = playerUUID.size() * 5;
            int mobLevel = Utils.getRandom(maxLevel, minLevel);

            e.getEntity().remove();

            Collections.shuffle(mobType);
            LivingEntity entity = (LivingEntity) world.spawnEntity(location, mobType.get(0));
            new MobFactory(entity, mobLevel);
            maxEntityCount++;

            return;
        }

        int bossChance = Utils.getWorldGuardValue(e.getEntity(), Flags.DUNGEON_BOSS_SPAWN_CHANCE);
        bossChance = bossChance == -1 ? 10 : bossChance;

        if (Utils.getRandom(2000) < bossChance && !hasBoss) {
            spawnBoss(location, e.getEntity());
        } else {
            if (e.getEntity() instanceof Zombie) {
                Utils.spawnRandomZombie(location);
                e.getEntity().remove();
                e.setCancelled(true);
            } else {
                new MobFactory(e.getEntity());
            }
        }
    }

    @EventHandler
    public void onDeath(EntityDeathEvent e) {
        if (e.getEntity().getWorld() != dungeonWorld) return;
        if (e.getEntity() instanceof Player) return;
        if (!(e.getEntity() instanceof Monster)) return;
        if (!(e.getEntity().getLastDamageCause() instanceof EntityDamageByEntityEvent event)) return;
        if (event.getCause() != EntityDamageEvent.DamageCause.ENTITY_ATTACK) return;
        if (!(event.getDamager() instanceof Player player)) return;

        if (Utils.getPDC(e.getEntity()).has(BOSS_PERSISTENT_DATA, PersistentDataType.INTEGER)) {
            hasBoss = false;

            e.getDrops().clear();
            if (chestLootList.size() == 0) return;
            int itemAmount = Utils.getRandom(10,5);

            int xpAmount = Utils.getRandom(2000, 1000);
            e.setDroppedExp(xpAmount);

            for (int i = 0; i < itemAmount; i++) {
                Collections.shuffle(chestLootList);
                ItemStack item = createLootItem(chestLootList.get(0), true);
                e.getDrops().add(item);
            }

            return;
        }

        if (maxEntityCount > 0) maxEntityCount--;

        int xpAmount = Utils.getRandom(1000, 100);
        e.setDroppedExp(xpAmount);

        Integer level = Utils.getPDC(e.getEntity()).get(StringPath.MOB_LEVEL_KEY, PersistentDataType.INTEGER);
        if (level != null) {
            double maxAmount = Math.max(1, level) * 10;
            double minAmount = maxAmount / 2;

            double amount = Utils.getRandomDouble(maxAmount, minAmount);
            amount = Math.round(amount * 100.0) / 100.0;

            AdventureCraftCore.getEconomy().depositPlayer(player, amount);
            player.sendMessage(Utils.color("&8You earned &6" + amount));
        }

        double itemAmount = Utils.getRandom(5, 1);
        if (mobsLootList.size() == 0) return;
        for (int i = 0; i < itemAmount; i++) {
            Collections.shuffle(mobsLootList);
            ItemStack item = createLootItem(mobsLootList.get(0), false);
            e.getDrops().add(item);
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onDeath(EntityDamageEvent e) {
        if (!(e.getEntity() instanceof Player player)) return;
        if (player.getWorld() != dungeonWorld) return;
        if (!Utils.checkWGState(player, Flags.ALLOW_DUNGEON_LIFE)) return;

        ItemStack hand = player.getInventory().getItemInMainHand();
        ItemStack off = player.getInventory().getItemInOffHand();

        if (hand.getType() == Material.TOTEM_OF_UNDYING || off.getType() == Material.TOTEM_OF_UNDYING) return;

        if (e.getFinalDamage() >= player.getHealth()) {
            PersistentDataContainer pdc = player.getPersistentDataContainer();
            if (!pdc.has(DUNGEON_LIFE_COUNT, PersistentDataType.INTEGER)) return;
            Integer lifeCount = pdc.get(DUNGEON_LIFE_COUNT, PersistentDataType.INTEGER);
            if (lifeCount == null || lifeCount <= 0) return;
            e.setCancelled(true);
            lifeCount--;

            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "warp " + dungeonWorld.getName() + " " + player.getName());
            player.sendMessage(Utils.color("&4Your &6straw doll &4has been consumed! " + lifeCount + " doll remaining."));
            player.setHealth(Objects.requireNonNull(player.getAttribute(Attribute.GENERIC_MAX_HEALTH)).getBaseValue());
            pdc.set(DUNGEON_LIFE_COUNT, PersistentDataType.INTEGER, lifeCount);
        }
    }

    @EventHandler
    public void onDungeonLifeConsume(PlayerInteractEvent e) {
        if (e.getHand() != EquipmentSlot.HAND) return;
        if (e.getAction() != Action.RIGHT_CLICK_AIR) return;
        if (e.getItem() == null || !e.hasItem()) return;
        if (e.getItem().getType() != Material.TOTEM_OF_UNDYING) return;
        ItemStack item = e.getItem();
        ItemMeta meta = item.getItemMeta();
        if (meta == null) return;
        if (!Utils.getPDC(meta).has(StringPath.DUNGEON_LIFE, PersistentDataType.INTEGER)) return;
        item.setAmount(0);
        item.setType(Material.AIR);

        Player player = e.getPlayer();
        if (Utils.getPDC(player).has(DUNGEON_LIFE_COUNT, PersistentDataType.INTEGER)) {
            Integer count = Utils.getPDC(player).get(DUNGEON_LIFE_COUNT, PersistentDataType.INTEGER);
            if (count == null || count == 0) {
                count = 1;
            } else {
                count++;
            }
            Utils.getPDC(player).set(DUNGEON_LIFE_COUNT, PersistentDataType.INTEGER, count);
            player.sendMessage(Utils.color("&6Straw doll has been increased to &b" + count + " &edoll`s"));
        } else {
            Utils.getPDC(player).set(DUNGEON_LIFE_COUNT, PersistentDataType.INTEGER, 1);
            player.sendMessage(Utils.color("&6You now have &b1 &estraw doll!"));
        }

        player.getWorld().playSound(player.getLocation(), Sound.ITEM_TOTEM_USE, 5, 1);
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
            } else if (s instanceof EnderChest enderChest) {
                enderChest.setType(Material.CHEST);
                Location loc = s.getLocation();
                System.out.println("Ender Chest Found in " + loc.getBlockX() + " " + loc.getBlockY() + " " + loc.getBlockZ());
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
        if (chestLootList.size() == 0) return;
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

    private void runEntityCounter() {
        new BukkitRunnable() {
            @Override
            public void run() {
                if (dungeonWorld == null) return;
                int count = 0;
                for (Entity entity : dungeonWorld.getEntities()) {
                    if (entity instanceof Monster) count++;
                    if (entity instanceof LivingEntity e) {
                        if (Utils.getPDC(e).has(BOSS_PERSISTENT_DATA, PersistentDataType.INTEGER)) hasBoss = true;
                    }
                }
                maxEntityCount = count;
            }
        }.runTaskTimer(AdventureCraftCore.getInstance(), 0, 20);
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
                    player.setGameMode(GameMode.ADVENTURE);
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
                    player.setGameMode(GameMode.SURVIVAL);
                    this.cancel();
                    return;
                }
                if (count == 10) {
                    player.sendMessage(Utils.color("&4Exiting the dungeon in 10 seconds!"));
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
        } else {
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

    private void spawnBoss(Location location, LivingEntity entity) {
        int min = Utils.getWorldGuardValue(entity, Flags.BOSS_MINIMUM_LEVEL);
        int max = Utils.getWorldGuardValue(entity, Flags.BOSS_MAXIMUM_LEVEL);

        if (dungeonWorld.getSpawnLocation().distance(location) <= 1500) return;
        hasBoss = true;
        System.out.println(location.getX() + " " + location.getY() + " " + location.getZ());

        entity.remove();

        min = min == -1 ? 50 : min;
        max = max == -1 ? 100 : max;

        int bossLevel = Utils.getRandom(max, min);

        Collections.shuffle(BOSS_TYPE);
        switch (BOSS_TYPE.get(0)) {
            case RAVAGER -> spawnBossRavager(location, bossLevel);
            case ILLUSIONER -> spawnBossIllusioner(location, bossLevel);
            case ZOMBIE -> spawnBossZombie(location, bossLevel);
//            case WARDEN -> spawnBossWarden(location, bossLevel);
            case WITHER -> spawnBossWither(location, bossLevel);
        }
    }

    private void spawnBossRavager(Location location, int level) {
        Ravager ravager = (Ravager) dungeonWorld.spawnEntity(location, EntityType.RAVAGER);
        Evoker evoker = (Evoker) dungeonWorld.spawnEntity(location, EntityType.EVOKER);

        Utils.getPDC(ravager).set(BOSS_PERSISTENT_DATA, PersistentDataType.INTEGER, 1);
        Utils.getPDC(evoker).set(BOSS_PERSISTENT_DATA, PersistentDataType.INTEGER, 1);

        ravager.setRemoveWhenFarAway(false);
        evoker.setRemoveWhenFarAway(false);

        new MobFactory(ravager, level);
        new MobFactory(evoker, level);

        ravager.setCustomName(Utils.color("&4Inuarashi"));
        evoker.setCustomName(Utils.color("&4Im"));
        ravager.addPassenger(evoker);
    }

    private void spawnBossIllusioner(Location location, int level) {
        Illusioner illusioner = (Illusioner) dungeonWorld.spawnEntity(location, EntityType.ILLUSIONER);
        Evoker evoker = (Evoker) dungeonWorld.spawnEntity(location, EntityType.EVOKER);

        Utils.getPDC(illusioner).set(BOSS_PERSISTENT_DATA, PersistentDataType.INTEGER, 1);
        Utils.getPDC(evoker).set(BOSS_PERSISTENT_DATA, PersistentDataType.INTEGER, 1);

        illusioner.setRemoveWhenFarAway(false);
        evoker.setRemoveWhenFarAway(false);

        new MobFactory(illusioner, level);
        new MobFactory(evoker, level);

        illusioner.setCustomName(Utils.color("&4Magician"));
        evoker.setCustomName(Utils.color("&4Magician Assistant"));

        illusioner.addPassenger(evoker);
    }

    private void spawnBossZombie(Location location, int level) {
        Zombie zombie = (Zombie) dungeonWorld.spawnEntity(location, EntityType.ZOMBIE);

        Utils.getPDC(zombie).set(BOSS_PERSISTENT_DATA, PersistentDataType.INTEGER, 1);
        zombie.setBaby();
        zombie.setRemoveWhenFarAway(false);

        EntityEquipment equipment = zombie.getEquipment();

        if (equipment != null) {
            equipment.setHelmet(giveAllEnchant(new ItemStack(Material.NETHERITE_HELMET)));
            equipment.setChestplate(giveAllEnchant(new ItemStack(Material.NETHERITE_CHESTPLATE)));
            equipment.setLeggings(giveAllEnchant(new ItemStack(Material.NETHERITE_LEGGINGS)));
            equipment.setBoots(giveAllEnchant(new ItemStack(Material.NETHERITE_BOOTS)));
            equipment.setItemInMainHand(giveAllEnchant(new ItemStack(Material.NETHERITE_AXE)));
            equipment.setItemInOffHand(giveAllEnchant(new ItemStack(Material.NETHERITE_SWORD)));
        }

        new MobFactory(zombie, level);
        zombie.setCustomName(Utils.color("&4B4by Z0mb1e"));
    };

    private void spawnBossWarden(Location location, int level) {
        Warden warden = (Warden) dungeonWorld.spawnEntity(location, EntityType.WARDEN);
        Utils.getPDC(warden).set(BOSS_PERSISTENT_DATA, PersistentDataType.INTEGER, 1);
        warden.setRemoveWhenFarAway(false);
        new MobFactory(warden, level);
        warden.setCustomName(Utils.color("&4Hells Guard"));
    };

    private void spawnBossWither(Location location, int level) {
        Wither wither = (Wither) dungeonWorld.spawnEntity(location, EntityType.WITHER);
        Utils.getPDC(wither).set(BOSS_PERSISTENT_DATA, PersistentDataType.INTEGER, 1);
        wither.setRemoveWhenFarAway(false);
        new MobFactory(wither, level);
        wither.setCustomName(Utils.color("&4FALLEN ANGEL"));
    };

    private void createDungeonLife() {
        ItemStack item = new ItemStack(Material.TOTEM_OF_UNDYING);
        item.addUnsafeEnchantment(Enchantment.DURABILITY, 0);
        ItemMeta meta = item.getItemMeta();
        if (meta == null) return;
        meta.setDisplayName(Utils.color("&4STRAW DOLL"));
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);

        List<String> lore = new ArrayList<>();
        lore.add(Utils.color("&8Your fail safe item"));
        lore.add(Utils.color("&8Will automatically teleport"));
        lore.add(Utils.color("&8you back to spawn when you"));
        lore.add(Utils.color("&8take a lethal damage"));
        lore.add("");
        lore.add(Utils.color("&8Can be use as a regular totem"));
        lore.add("");
        lore.add(Utils.color("&bRight click to consume"));
        meta.setLore(lore);

        Utils.getPDC(meta).set(StringPath.DUNGEON_LIFE, PersistentDataType.INTEGER, 1);
        item.setItemMeta(meta);

        dungeonLife = item;
    }

    private ItemStack giveAllEnchant(ItemStack item) {
        for (Enchantment enchantment : Enchantment.values()) {
            if (enchantment.canEnchantItem(item)) item.addUnsafeEnchantment(enchantment, enchantment.getMaxLevel());
        }
        return item;
    }


    public void removeAllEntities() {
        if (dungeonWorld != null) {
            List<Entity> list = dungeonWorld.getEntities();
            for (Entity ent : list) {
                if (ent instanceof Monster) ent.remove();
            }
        }
    }
}
