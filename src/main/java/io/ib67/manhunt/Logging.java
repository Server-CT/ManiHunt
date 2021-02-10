package io.ib67.manhunt;


import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;

public class Logging {
    private static final String FORMAT = ChatColor.AQUA.toString() + ChatColor.BOLD + "ManHunt" + ChatColor.RED + "v2 " + ChatColor.RESET + ChatColor.WHITE + ">> %";

    public static void info(String mesg) {
        Bukkit.getServer().getConsoleSender().sendMessage(FORMAT.replaceAll("%", mesg));
    }

    public static void warn(String mesg) {
        Bukkit.getServer().getConsoleSender().sendMessage(FORMAT.replaceAll("%", ChatColor.RED + mesg));

    }

    public static void debug(String mesg) {
        if (!ManHunt.debug) return;
        Bukkit.getServer().getConsoleSender().sendMessage(FORMAT.replaceAll("%", "[D] " + ChatColor.UNDERLINE + mesg.replaceAll(org.bukkit.ChatColor.COLOR_CHAR + "", "&")));
    }
}
