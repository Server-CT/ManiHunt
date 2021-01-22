package io.ib67.manhunt.util;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.Random;

public class TeleportUtil {
    public static void airDrop(Player runner) {
        Location loc = runner.getLocation();
        loc = new Location(loc.getWorld(), loc.getBlockX(), 0, loc.getBlockZ());
        Random random = new Random();
        loc.add(random.nextInt(200) + 100, 0, random.nextInt(200) + 100);
        loc = loc.getWorld().getHighestBlockAt(loc).getLocation();
        loc.getBlock().setType(Material.GLASS);
        loc.setY(loc.getY() + 1);
        runner.teleport(loc);
    }
}
