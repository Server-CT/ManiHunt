package io.ib67.manhunt.game.region.impl.listener;

import io.ib67.manhunt.game.region.impl.SingleWorldRegion;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

public class SingleWorldRegionPlayerListener implements Listener {
    private SingleWorldRegion region;

    public SingleWorldRegionPlayerListener(SingleWorldRegion region) {
        this.region = region;
    }

    @EventHandler
    public void onMove(PlayerMoveEvent event) {
        if (event.getTo() == null || !region.isLocked()) return;
        if (region.getCenter().distance(event.getTo()) > region.getLockRadius()) {
            event.getPlayer().teleport(region.getCenter());
        }
    }
}
