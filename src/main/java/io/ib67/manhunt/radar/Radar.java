package io.ib67.manhunt.radar;

import org.bukkit.entity.Player;

import java.util.Set;

public interface Radar {
    void start();

    void stop();

    void onMove(Player p);

    Set<Player> getNearbyList();

    void setWarnDistance(int i);
}
