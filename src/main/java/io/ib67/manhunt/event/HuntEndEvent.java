package io.ib67.manhunt.event;

import io.ib67.manhunt.game.Game;
import lombok.Getter;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class HuntEndEvent extends Event {
    @Getter
    private final Game runningGame;

    public HuntEndEvent(Game g) {
        this.runningGame = g;
    }

    private final HandlerList handlerList = new HandlerList();

    @Override
    public HandlerList getHandlers() {
        return handlerList;
    }
}
