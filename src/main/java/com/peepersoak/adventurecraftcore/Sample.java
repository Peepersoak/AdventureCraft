package com.peepersoak.adventurecraftcore;

import com.comphenix.protocol.wrappers.WrappedGameProfile;
import com.comphenix.protocol.wrappers.WrappedSignedProperty;
import com.mojang.authlib.properties.Property;
import me.libraryaddict.disguise.disguisetypes.PlayerDisguise;
import org.bukkit.entity.Zombie;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntitySpawnEvent;

import java.util.UUID;

public class Sample implements Listener {

    @EventHandler
    public void onSpawn(EntitySpawnEvent e) {
        if (e.getEntity() instanceof Zombie zombie) {
            WrappedGameProfile sukuna = new WrappedGameProfile(UUID.fromString("862f09f5-534f-4a89-87c1-9c84847fe675"), "skin4676d43a");
            sukuna.getProperties().put("textures", WrappedSignedProperty.fromHandle(new Property("textures", "ewogICJ0aW1lc3RhbXAiIDogMTYyMDk0MzUxMzM5MSwKICAicHJvZmlsZUlkIiA6ICI3NTE0NDQ4MTkxZTY0NTQ2OGM5NzM5YTZlMzk1N2JlYiIsCiAgInByb2ZpbGVOYW1lIiA6ICJUaGFua3NNb2phbmciLAogICJzaWduYXR1cmVSZXF1aXJlZCIgOiB0cnVlLAogICJ0ZXh0dXJlcyIgOiB7CiAgICAiU0tJTiIgOiB7CiAgICAgICJ1cmwiIDogImh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYmQyYmYwN2ExOGZmYTBiNjg2YzY1MjdhMmY0MDA5NGRiODE4NjY5NDUzOWI5ZDc5NzNhMWIzNjUyZDJhYjRmZSIKICAgIH0KICB9Cn0=", "YGS2Ra5zi49DLa2ZfO/IJ0NsMP7F0UI9hLnoOwl0kX+Iin7tsWRIEc2TmKbayS5+gr86xLzS/3JiR8sXkptWi3UNVr2VgpKFuj2g0a3cDHFtlV4nk2R24boIX46+KYXkj2SRiZaUvJQnb2QWB+ljl1BVtoNJR9ZKsd/iLvpwKPl4LA7ebifjvt5TO+VIScylg5XKibzC+zL6jJnKUgupoSxIsDbduXcSNup5pnOoysqG74yxJxqfsr3IMy1XJiieiowWX0FR7ANlC4pK8jy1obC2nFx2ZNm+/LF7hxB6wAVmRShXqL4drJtQirPUUcpPhQO/cTW5+LwaS3RzqDnBBzgNjJDFdBAE8iEPAcN4ElS4VWqFF9QFYVxS+D2CnBaBDR5xgB2cjGEhH1RMNP26uRcgPbZVh8VmulzWrCDCLfKegPhkVSEC/5SNWVsZoSFqjo+qvMwiUUbX8TTU2VWEv+Rh5ZyCnm7zEn8jzmZnQXuhxyP+KxyfIV3l5UJdk5i1Se28dWJe41LP18bMGRzhgrV0pRx7cZuNUjv+2ddDZfMUYc7zo+mbrSiD501UKF03+T6Owq0RiSYV6gMa6aV6korkGv8U/tzuYMKe3615TNEqT6FZZW7gh3UDODFju5VyoFwmHaMd3w/crJx0J+ZECZv2EHyG2eqahX+jHsPTe28=")));

            PlayerDisguise disguise = new PlayerDisguise(sukuna);

            disguise.setNameVisible(false);
            disguise.setEntity(zombie);
            disguise.startDisguise();
        }
    }
}
