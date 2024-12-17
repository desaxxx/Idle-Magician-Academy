package org.nandayo.CustomEvents;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.nandayo.Spell;

public class SpellLearnEvent extends Event {

    private static final HandlerList handlers = new HandlerList();
    private final Player player;
    private final Spell spellLearned;

    public SpellLearnEvent(Player player, Spell spellLearned) {
        this.player = player;
        this.spellLearned = spellLearned;
    }

    public Player getPlayer() {
        return player;
    }
    public Spell getSpellLearned() {
        return spellLearned;
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
