package com.peepersoak.adventurecraftcore.newyear;

import com.peepersoak.adventurecraftcore.AdventureCraftCore;
import com.peepersoak.adventurecraftcore.utils.Flags;
import com.peepersoak.adventurecraftcore.utils.Utils;
import org.antlr.v4.runtime.misc.NotNull;
import org.bukkit.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class NewYearEvent implements CommandExecutor, Listener {

    private final NamespacedKey gunKey = new NamespacedKey(AdventureCraftCore.getInstance(), "GunKey");
    private final List<Location> locationList = new ArrayList<>();
    private final Random rand = new Random();
    private boolean startEvent = false;
    private boolean setLocation = false;
    private final String LOCATION = "Firework_Locations";


    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player player)) return false;
        if (!player.isOp()) return false;

        if (args.length == 1) {
            String cmd = args[0];

            if (cmd.equalsIgnoreCase("gun")) {
                ItemStack item = new ItemStack(Material.LEATHER_HORSE_ARMOR);
                ItemMeta meta = item.getItemMeta();
                if (meta == null) return false;
                Utils.getPDC(meta).set(gunKey, PersistentDataType.INTEGER, 1);
                meta.setDisplayName(Utils.color("&4Fireworks Gun"));
                item.setItemMeta(meta);

                player.getInventory().addItem(item);
            }
            else if (cmd.equalsIgnoreCase("start")) {
                mainEvent();
                startEvent = true;
                player.sendMessage(Utils.color("&6Event Started"));
            }
            else if (cmd.equalsIgnoreCase("stop")) {
                startEvent = false;
                player.sendMessage(Utils.color("&6Event Stop"));
            }
            else if (cmd.equalsIgnoreCase("set")) {
                if (setLocation) {
                    setLocation = false;
                    player.sendMessage(Utils.color("&cSet Location False"));
                    return false;
                }
                setLocation = true;
                player.sendMessage(Utils.color("&6Set Location"));
            }
            else if (cmd.equalsIgnoreCase("save")) {
                List<String> locRaw = new ArrayList<>();

                for (Location loc : locationList) {
                    locRaw.add(Utils.serialized(loc));
                }

                AdventureCraftCore.getInstance().getDungeonSetting().addNewList(LOCATION, locRaw);
                player.sendMessage(Utils.color("&6Location save"));
            }
        }

        return false;
    }

    @EventHandler
    public void onBreak(BlockBreakEvent e) {
        if (!setLocation) return;
        Player player = e.getPlayer();
        if (!player.isOp()) return;
        e.setCancelled(true);

        Location location = e.getBlock().getLocation().add(0.5,1,0.5);
        if (locationList.contains(location)) {
            locationList.remove(location);
            player.sendMessage(Utils.color("&cLocation removed"));
        } else {
            locationList.add(location);
            player.sendMessage(Utils.color("&6Location set"));
        }
    }

    @EventHandler
    public void onShoot(PlayerInteractEvent e) {
        if (e.getAction() != Action.RIGHT_CLICK_AIR) return;
        if (e.getHand() != EquipmentSlot.HAND) return;
        Player player = e.getPlayer();
        if (!Utils.checkWGState(player, Flags.ALLOW_FIREWORKS)) return;
        if (!e.hasItem() || e.getItem() == null) return;

        ItemStack item = e.getItem();
        ItemMeta meta = item.getItemMeta();
        if (meta == null) return;

        final Location loc = player.getEyeLocation();
        final Vector v = loc.getDirection();
        double speed = 0.5;
        v.normalize().multiply(speed);

        if (Utils.getPDC(meta).has(gunKey, PersistentDataType.INTEGER)) {
            Firework firework = loc.getWorld().spawn(loc, Firework.class);
            firework.setVelocity(v);
            firework.setGravity(false);
            firework.setShotAtAngle(true);
            firework.setFireworkMeta(getFireworkMeta(firework));
        }
    }

    private void mainEvent() {
        if (startEvent) return;

        List<String> locations = AdventureCraftCore.getInstance().getDungeonSetting().getConfig().getStringList(LOCATION);
        if (locations.isEmpty()) return;

        System.out.println("not empty");

        final List<Location> finalLocations = new ArrayList<>();

        for (String str : locations) {
            finalLocations.add((Location) Utils.deserialized(str));
        }
//        for (String loc : locations) {
//            String[] sp = loc.split("%");
//
//            if (sp.length != 4) continue;
//
//            World world = Bukkit.getWorld(sp[0]);
//            System.out.println("checkign world");
//            if (world == null) continue;
//            System.out.println("world is valid");
//
//            int x = Integer.parseInt(sp[1]);
//            int y = Integer.parseInt(sp[2]);
//            int z = Integer.parseInt(sp[3]);
//
//            finalLocations.add(new Location(world, x, y, z));
//
//            System.out.println("location added");
//        }

        new BukkitRunnable() {
            @Override
            public void run() {
                if (!startEvent) {
                    System.out.println("canceled");
                    this.cancel();
                    return;
                }

                Collections.shuffle(finalLocations);
                Location location = finalLocations.get(0);
                if (location.getWorld() == null) return;

                Firework firework = (Firework) location.getWorld().spawnEntity(location, EntityType.FIREWORK);
                firework.setFireworkMeta(getFireworkMeta(firework));
            }
        }.runTaskTimer(AdventureCraftCore.getInstance(), 0, 5);
    }

    private FireworkMeta getFireworkMeta(Firework firework) {
        FireworkMeta meta = firework.getFireworkMeta();
        meta.clearEffects();

        meta.setPower(Utils.getRandom(3, 1));

        int firstColor = Utils.getRandom(250);
        int secondColor = Utils.getRandom(250);
        int thirdColor = Utils.getRandom(250);

        FireworkEffect.Type[] type = FireworkEffect.Type.values();

        meta.addEffect(FireworkEffect.builder()
                .withColor(Color.fromBGR(firstColor, secondColor, thirdColor))
                .flicker(rand.nextBoolean())
                .trail(rand.nextBoolean())
                .with(type[Utils.getRandom(type.length - 1)])
                .build());

        return meta;
    }
}
