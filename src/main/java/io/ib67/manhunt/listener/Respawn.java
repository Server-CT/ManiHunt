package io.ib67.manhunt.listener;

import io.ib67.manhunt.ManHunt;
import io.ib67.manhunt.game.Game;
import io.ib67.manhunt.game.GamePhase;
import io.ib67.manhunt.game.GamePlayer;
import io.ib67.manhunt.util.LodestoneCompass;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerRespawnEvent;

public class Respawn implements Listener {
    @EventHandler
    public void onRespawn(PlayerRespawnEvent event) {
        Game game = ManHunt.getInstance().getGame();

        if (game.getPhase() != GamePhase.STARTED || !game.isCompassEnabled())
            return;

        game.isInGame(event.getPlayer())
                .filter(g -> g.getRole() == GamePlayer.Role.HUNTER)
                .map(GamePlayer::getPlayer)
                .map(Player::getInventory)
                .ifPresent(i -> {
                    Bukkit.getScheduler().runTaskLater(ManHunt.getInstance(),()->
                    i.addItem(LodestoneCompass.allocate(game.getRunner().getLocation()))
                                                       ,10);
                });
    }
}
