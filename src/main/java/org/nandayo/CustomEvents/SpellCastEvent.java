package org.nandayo.CustomEvents;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.nandayo.Spell;

public class SpellCastEvent extends Event {

    private static final HandlerList handlers = new HandlerList();
    private final Player player;
    private final Spell spellCasted;

    public SpellCastEvent(Player player, Spell spellCasted) {
        this.player = player;
        this.spellCasted = spellCasted;
    }

    public Player getPlayer() {
        return player;
    }
    public Spell getSpellCasted() {
        return spellCasted;
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
