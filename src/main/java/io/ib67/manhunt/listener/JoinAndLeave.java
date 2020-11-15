package io.ib67.manhunt.listener;

import io.ib67.manhunt.ManHunt;
import io.ib67.manhunt.game.GamePhase;
import io.ib67.manhunt.gui.Vote;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class JoinAndLeave implements Listener {
    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        ManHunt.getInstance().getGame().joinPlayer(event.getPlayer());
        Vote vote = ManHunt.getInstance().getGame().vote;
        if (vote != null && vote.getShouldVote().contains(event.getPlayer().getUniqueId())) {
            Bukkit.getScheduler().runTaskLater(ManHunt.getInstance(),
                                               () -> event.getPlayer().openInventory(vote.getVoteInv()),
                                               10);
        }
    }

    @EventHandler
    public void onLeave(PlayerQuitEvent event) {
        if (ManHunt.getInstance().getGame().getPhase() == GamePhase.WAITING_FOR_PLAYER)
            ManHunt.getInstance().getGame().kickPlayer(event.getPlayer().getName());
    }
}
