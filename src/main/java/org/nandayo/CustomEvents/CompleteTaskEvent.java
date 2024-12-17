package org.nandayo.CustomEvents;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.nandayo.Task;

public class CompleteTaskEvent extends Event {

    private static final HandlerList handlers = new HandlerList();
    private final Player player;
    private final Task task;

    public CompleteTaskEvent(Player player, Task task) {
        this.player = player;
        this.task = task;
    }

    public Player getPlayer() {
        return player;
    }
    public Task getTask() {
        return task;
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
