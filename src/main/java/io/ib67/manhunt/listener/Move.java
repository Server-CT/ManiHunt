package io.ib67.manhunt.listener;

import io.ib67.manhunt.ManHunt;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

public class Move implements Listener {
    @EventHandler
    public void onMove(PlayerMoveEvent e) {
        if (!ManHunt.getInstance().getGame().isStarted()) {
            if (e.getPlayer().getLocation().distance(e.getPlayer().getWorld().getSpawnLocation()) > 30) {
                e.getPlayer().teleport(e.getPlayer().getWorld().getSpawnLocation());
                e.getPlayer().sendMessage(ManHunt.getInstance().getLanguage().GAMING.DONT_RUN_AWAY);
            }
            return;
        }
        ManHunt.getInstance().getGame().getRador().onMove(e.getPlayer());
    }
}
