package io.ib67.manhunt.listener;

import io.ib67.manhunt.ManHunt;
import io.ib67.manhunt.game.Game;
import io.ib67.manhunt.game.GamePhase;
import io.ib67.manhunt.game.GamePlayer;
import io.ib67.manhunt.game.GameResult;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;

public class Death implements Listener {
    @EventHandler
    public void onEntityDeath(EntityDeathEvent e) {
        Game game = ManHunt.getInstance().getGame();

        if (game.getPhase() != GamePhase.STARTED)
            return;

        if (e.getEntityType() == EntityType.PLAYER) {
            //Player Died Event
            Player player = (Player) e.getEntity();
            game.isInGame(player).ifPresent(p -> {
                if (p.getRole() == GamePlayer.Role.RUNNER)
                    game.stop(GameResult.HUNTER_WIN);
            });
        } else if (e.getEntityType() == EntityType.ENDER_DRAGON)
            game.stop(GameResult.RUNNER_WIN);
    }
}
