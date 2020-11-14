package io.ib67.manhunt.util;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.inventory.ItemStack;

public class LodestoneCompass {
    public static ItemStack allocate(Location loc) {
        NBTUtil.NBTValue x = new NBTUtil.NBTValue().set(loc.getBlockX());
        NBTUtil.NBTValue y = new NBTUtil.NBTValue().set(loc.getBlockY());
        NBTUtil.NBTValue z = new NBTUtil.NBTValue().set(loc.getBlockZ());
        Object compound = NBTUtil.setTagValue(NBTUtil.newNBTTagCompound(), "X", x);
        compound = NBTUtil.setTagValue(compound, "Y", y);
        compound = NBTUtil.setTagValue(compound, "Z", z);
        ItemStack modified = NBTUtil.setTagValue(new ItemStack(Material.COMPASS), "LodestoneTracked", new NBTUtil.NBTValue().set(false));
        modified = NBTUtil.setTagValue(modified, "LodestonePos", new NBTUtil.NBTValue(compound));
        modified = NBTUtil.setTagValue(modified, "LodestoneDimension", new NBTUtil.NBTValue().set(envAsName(loc.getWorld().getEnvironment())));
        return modified;
    }

    private static String envAsName(World.Environment env) {
        switch (env) {
            case NORMAL:
                return "minecraft:overworld";
            case THE_END:
                return "minecraft:the_end";
            case NETHER:
                return "minecraft:the_nether";
        }
        throw new IllegalArgumentException("Invaild env: " + env);
    }
}
