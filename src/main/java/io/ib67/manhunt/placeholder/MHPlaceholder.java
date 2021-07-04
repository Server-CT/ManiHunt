package io.ib67.manhunt.placeholder;

import io.ib67.manhunt.ManHunt;
import io.ib67.manhunt.game.GamePlayer;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.Optional;

public class MHPlaceholder extends PlaceholderExpansion {
    private final ManHunt plugin;
    public MHPlaceholder(ManHunt plugin) {
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
        return Arrays.toString(plugin.getDescription().getAuthors().toArray());
    }
    @Override
    public String getIdentifier() {
        return "ManiHunt";
    }
    @Override
    public String getVersion() {
        return plugin.getDescription().getVersion();
    }

    public String onPlaceholderRequest(Player player, String identifier) {
        Optional<GamePlayer> role = ManHunt.getInstance().getGame().isInGame(player);
        if(identifier.equals("rule")){
            if(role.isPresent()){
                switch (role.get().getRole()){
                    case HUNTER:
                        return (ChatColor.RED + "HUNTER");
                    case RUNNER:
                        return (ChatColor.GREEN + "RUNNER");
                }
            }else{
                return ChatColor.GRAY + "SPECTATOR"; // todo I18N required
            }
        }
        return null;
    }
}



