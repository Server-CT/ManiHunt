package io.ib67.manhunt.game.region.impl;

import io.ib67.manhunt.ManHunt;
import io.ib67.manhunt.game.Game;
import io.ib67.manhunt.game.GamePhase;
import io.ib67.manhunt.game.region.GamingRegion;
import io.ib67.manhunt.util.TeleportUtil;
import org.bukkit.Bukkit;
import org.bukkit.Difficulty;
import org.bukkit.GameRule;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class SingleWorldRegion implements GamingRegion {
    private boolean locked;
    private int lockRadius = 30; //Defaults: 30
    private Game game;
    private List<String> players = new ArrayList<>();
    private List<String> spectators = new ArrayList<>();

    public SingleWorldRegion(Game game) {
        this.game = game;
    }

    @Override
    public boolean isInRegion(Location loc) {
        return true;
    }

    @Override
    public Location getCenter() {
        return Bukkit.getWorld(ManHunt.getInstance().getMainConfig().worldSettings.defaultWorldName).getSpawnLocation();
    }

    @Override
    public boolean isLocked() {
        return locked;
    }

    @Override
    public void setLocked(boolean status) {
        locked = status;
    }

    @Override
    public void setLockRadius(int radius) {
        lockRadius = radius;
    }

    @Override
    public int getLockRadius() {
        return lockRadius;
    }

    @Override
    public void gameStatus(GamePhase phase) {
        switch (phase) {
            case STARTING:
                getCenter().getWorld().setDifficulty(Difficulty.valueOf(ManHunt.getInstance().getMainConfig().worldSettings.difficulty));
                getCenter().getWorld().setGameRule(GameRule.DO_DAYLIGHT_CYCLE, true);
                break;
            case WAITING_FOR_PLAYER:
                setLocked(true);
                getCenter().getWorld().setDifficulty(Difficulty.PEACEFUL);
                getCenter().getWorld().setGameRule(GameRule.DO_DAYLIGHT_CYCLE, false);
                break;
            case STARTED:
                setLocked(false);
                TeleportUtil.airDrop(game.getRunner());
                break;
            case END:
                Location runnerDeathLoc = game.getRunner().getLocation().clone();
                game.getInGamePlayers().forEach(e -> e.getPlayer().teleport(runnerDeathLoc));
                break;
        }
    }

    @Override
    public void joinPlayer(Player player, boolean isSpectator) {
        player.teleport(getCenter());
        if (isSpectator) {
            spectators.add(player.getName());
        } else {
            players.add(player.getName());
        }
    }

    @Override
    public void deletePlayer(Player player) {
        if (spectators.contains(player.getName())) {
            spectators.remove(player.getName());
        } else {
            players.remove(player.getName());
        }
    }

}
