package org.nandayo.CustomEvents;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class ManaUseEvent extends Event {

    private static final HandlerList handlers = new HandlerList();
    private final Player player;
    private final int manaUsed;

    public ManaUseEvent(Player player, int manaUsed) {
        this.player = player;
        this.manaUsed = manaUsed;
    }

    public Player getPlayer() {
        return player;
    }
    public int getManaUsed() {
        return manaUsed;
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
