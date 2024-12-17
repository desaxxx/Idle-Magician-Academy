package org.nandayo.CustomEvents;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class ManaCollectEvent extends Event {

    private static final HandlerList handlers = new HandlerList();
    private final Player player;
    private final int manaCollected;

    public ManaCollectEvent(Player player, int manaCollected) {
        this.player = player;
        this.manaCollected = manaCollected;
    }

    public Player getPlayer() {
        return player;
    }
    public int getManaCollected() {
        return manaCollected;
    }

    //
    @Override
    public HandlerList getHandlers() {
        return handlers;
    }
    public static HandlerList getHandlerList() {
        return handlers;
    }
}
