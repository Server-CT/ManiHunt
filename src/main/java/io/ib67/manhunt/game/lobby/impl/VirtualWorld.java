package io.ib67.manhunt.game.lobby.impl;

import io.ib67.manhunt.ManHunt;
import io.ib67.manhunt.game.GameManager;
import io.ib67.manhunt.game.lobby.Lobby;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class VirtualWorld implements Lobby {
    private final GameManager manager = ManHunt.getInstance().getGameManager();

    public VirtualWorld() {
        Bukkit.getScheduler().runTaskTimer(ManHunt.getInstance(),
                                           () -> Bukkit.getOnlinePlayers()
                                                   .stream()
                                                   .filter(p -> !manager.isInGame(p))
                                                   .forEach(p -> {
                                                       p.setGameMode(GameMode.SPECTATOR);
                                                       p.teleport(new Location(p.getWorld(), 0, 32767, 0));
                                                   }),
                                           0,
                                           20);
    }

    @Override
    public void joinPlayer(Player player) {
        ManHunt.getInstance().getGameSelector().openInventory(player);
    }
}
