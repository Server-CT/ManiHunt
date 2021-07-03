package io.ib67.manhunt.placeholder;

import io.ib67.manhunt.ManHunt;
import io.ib67.manhunt.game.GamePlayer;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.Optional;

public class placeholder extends PlaceholderExpansion {
    private final ManHunt plugin;
    public placeholder(ManHunt plugin) {
        this.plugin = plugin;
    }
    @Override
    public boolean persist() {
        return true;
    }
    @Override
    public boolean canRegister() {
        return true;
    }
    @Override
    public String getAuthor() {
        return plugin.getDescription().getAuthors().toString();
    }
    @Override
    public String getIdentifier() {
        return "Manhunt";
    }
    @Override
    public String getVersion() {
        return plugin.getDescription().getVersion();
    }

    public String onPlaceholderRequest(Player player, String identifier) {
        Optional<GamePlayer.Role> role = ManHunt.getInstance().getPlayerRole(player);
        if (identifier.equals("rule")) {
            if (role.get() == GamePlayer.Role.HUNTER) {
                return (ChatColor.RED + "HUNTER");
            }
            if (role.get() == GamePlayer.Role.RUNNER) {
                return (ChatColor.GREEN + "RUNNER");
            }
            return (ChatColor.GRAY + "OBSERVER");
        }
        return null;
    }
}



