package com.peepersoak.adventurecraftcore.items.wards;

import com.peepersoak.adventurecraftcore.items.witch.CustomWitch;
import com.peepersoak.adventurecraftcore.utils.StringPath;
import com.peepersoak.adventurecraftcore.utils.Utils;
import net.minecraft.server.level.ServerLevel;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_19_R2.CraftWorld;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.Objects;

public class WardEvents implements Listener {

    @EventHandler
    public void onInteract(PlayerInteractEvent e) {
        if (e.getHand() != EquipmentSlot.HAND) return;
        if (e.getItem() == null) return;
        ItemStack item = e.getItem();
        ItemMeta meta = item.getItemMeta();
        if (meta == null) return;
        if (!Utils.getPDC(meta).has(StringPath.CUSTOM_WARD, PersistentDataType.STRING)) return;
        WardType type = WardType.valueOf(Utils.getPDC(meta).get(StringPath.CUSTOM_WARD, PersistentDataType.STRING));
        if (item.getType() != Material.TOTEM_OF_UNDYING) return;

        e.getPlayer().getInventory().setItemInMainHand(new ItemStack(Material.AIR));
        Location location = e.getPlayer().getLocation();
        ServerLevel world = ((CraftWorld) Objects.requireNonNull(location.getWorld())).getHandle();
        world.addFreshEntityWithPassengers(new CustomWitch(location, type));
    }
}
