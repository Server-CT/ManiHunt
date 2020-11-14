package io.ib67.manhunt.listener;

import io.ib67.manhunt.ManHunt;
import io.ib67.manhunt.game.Game;
import io.ib67.manhunt.game.GamePhase;
import io.ib67.manhunt.game.GamePlayer;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.Optional;

public class Chat implements Listener {
    @EventHandler
    public void onChat(AsyncPlayerChatEvent event) {
        if (ManHunt.get().getGame().getPhase() != GamePhase.STARTED)
            return;

        Player player = event.getPlayer();
        Optional<GamePlayer> og = ManHunt.get().getGame().isInGame(player);
        if (og.isPresent()) {
            GamePlayer.Role role = og.orElse(null).getRole();
            event.setFormat((role == GamePlayer.Role.HUNTER ?
                             ChatColor.RED + "[HUNTER] " :
                             ChatColor.GREEN + "[RUNNER] ") + ChatColor.RESET + event.getFormat());
            if (role == GamePlayer.Role.HUNTER && event.getMessage().startsWith("#")) {
                event.setCancelled(true);
                event.setFormat(ChatColor.WHITE + "[TEAM]" + event.getFormat());
                ManHunt.get()
                        .getGame()
                        .getInGamePlayers()
                        .stream()
                        .filter(g -> g.getRole() == GamePlayer.Role.HUNTER)
                        .map(GamePlayer::getPlayer)
                        .forEach(p -> p.sendMessage(event.getFormat()));
            }
        } else {
            event.setFormat(ChatColor.GRAY + "[SPECTATOR] " + ChatColor.RESET + event.getFormat());
            event.setCancelled(true);
            Game game = ManHunt.get().getGame();
            Bukkit.getOnlinePlayers().stream().filter(p -> !game.isInGame(p).isPresent()).forEach(p -> p.sendMessage(
                    event.getFormat()));
        }
    }
}
