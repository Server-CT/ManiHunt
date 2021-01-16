package io.ib67.manhunt.game.region;

import org.bukkit.Location;

public interface GamingRegion {
    boolean isInRegion(Location loc);

    Location getCenter(); //Center 相当于 Lobby，即玩家等待玩家进入/开局时的地方。

    void setProperty(RegionProperty prop, Object property); //例如 freezeTime Difficulty

    //todo 修改Property储存 @czm23333
    Object getProperty(RegionProperty prop);

    boolean isLocked();

    void setLocked(boolean status); //解锁出生点限制。

    void setLockRadius(int radius); //出生点限制范围，为了上述例子中的第一条做的准备。

    int getLockRadius();
}
