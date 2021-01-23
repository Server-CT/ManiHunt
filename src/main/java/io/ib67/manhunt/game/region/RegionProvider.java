package io.ib67.manhunt.game.region;

import io.ib67.manhunt.game.Game;

public interface RegionProvider<T extends GamingRegion> {
    T claim(Game game);

    boolean isSingletonGameRequired();
}
