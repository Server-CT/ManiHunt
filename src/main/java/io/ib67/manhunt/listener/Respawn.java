package io.ib67.manhunt.listener;

import io.ib67.manhunt.ManHunt;
import io.ib67.manhunt.game.Game;
import io.ib67.manhunt.util.LodestoneCompass;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerRespawnEvent;

public class Respawn implements Listener {
    @EventHandler
    public void onRespawn(PlayerRespawnEvent event) {
        Game game = ManHunt.getInstance().getGame();

        if (!game.isStarted() || !game.isCompassEnabled())
            return;

       event.getPlayer().getInventory().addItem(LodestoneCompass.allocate(game.getRunner().getLocation()));
    }
}
