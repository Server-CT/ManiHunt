package io.ib67.manhunt.util;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.CompassMeta;

public class LodestoneCompass {
    @SuppressWarnings("all")
    public static ItemStack allocate(Player p, Location loc) {
        if (Material.valueOf("LODESTONE") == null) {
            //Old version.
            p.setCompassTarget(loc);
            return new ItemStack(Material.COMPASS);
        }

        // AmemiyaShigure: Do NOT use NMS.
        ItemStack compass = new ItemStack(Material.COMPASS);
        CompassMeta meta = (CompassMeta) compass.getItemMeta();
        meta.setLodestone(loc);
        meta.setLodestoneTracked(true);
        compass.setItemMeta(meta);
        return compass;
    }
}
