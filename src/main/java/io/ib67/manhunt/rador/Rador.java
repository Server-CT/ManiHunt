package io.ib67.manhunt.rador;

import org.bukkit.entity.Player;

import java.util.Set;

public interface Rador {
    void start();

    void stop();

    void onMove(Player p);

    Set<Player> getNearbyList();

    void setWarnDistance(int i);
}
