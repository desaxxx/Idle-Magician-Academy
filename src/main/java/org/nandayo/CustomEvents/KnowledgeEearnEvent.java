package org.nandayo.CustomEvents;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class KnowledgeEearnEvent extends Event {

    private static final HandlerList handlers = new HandlerList();
    private final Player player;
    private final int knowledgeEarned;

    public KnowledgeEearnEvent(Player player, int knowledgeEarned) {
        this.player = player;
        this.knowledgeEarned = knowledgeEarned;
    }

    public Player getPlayer() {
        return player;
    }
    public int getKnowledgeEarned() {
        return knowledgeEarned;
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
