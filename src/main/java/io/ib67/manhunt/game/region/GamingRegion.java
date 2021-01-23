package io.ib67.manhunt.game.region;

import io.ib67.manhunt.game.GamePhase;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public interface GamingRegion {
    boolean isInRegion(Location loc);

    Location getCenter(); //Center 相当于 Lobby，即玩家等待玩家进入/开局时的地方。

    boolean isLocked();

    void setLocked(boolean status); //解锁出生点限制。

    void setLockRadius(int radius); //出生点限制范围，为了上述例子中的第一条做的准备。

    int getLockRadius();

    boolean hasPlayer(Player p);

    void gameStatus(GamePhase phase);

    void joinPlayer(Player player, boolean isSpectator);

    void deletePlayer(Player player);
}
