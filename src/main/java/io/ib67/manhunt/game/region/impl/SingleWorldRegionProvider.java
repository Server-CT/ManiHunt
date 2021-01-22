package io.ib67.manhunt.game.region.impl;

import io.ib67.manhunt.ManHunt;
import io.ib67.manhunt.game.Game;
import io.ib67.manhunt.game.region.RegionProvider;
import io.ib67.manhunt.game.region.impl.listener.SingleWorldRegionPlayerListener;
import org.bukkit.Bukkit;

public class SingleWorldRegionProvider implements RegionProvider<SingleWorldRegion> {
    public static final SingleWorldRegionProvider INSTANCE = new SingleWorldRegionProvider();
    public static final String NAME = "SingleWorldGroup";

    private SingleWorldRegionProvider() {

    }

    @Override
    public SingleWorldRegion claim(Game game) {
        SingleWorldRegion r = new SingleWorldRegion(game);
        Bukkit.getPluginManager().registerEvents(new SingleWorldRegionPlayerListener(r), ManHunt.getInstance());
        return r;
    }
}
