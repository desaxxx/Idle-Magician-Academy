package org.nandayo.Manager;

import org.nandayo.Cache;
import org.nandayo.PlayerData;
import org.nandayo.Role;
import org.nandayo.magicianAcademy;

public class Rewarding {

    private final PlayerData pd;

    public Rewarding(PlayerData pd) {
        this.pd = pd;
    }

    public PlayerData getPlayerData() {
        return pd;
    }

    public Rewarding addStat(String rewardData, int value) {
        value = Math.max(0, value);
        switch (rewardData.toLowerCase()) {
            case "xp" -> pd.addXp(value);
            case "total_xp" -> pd.addTotalXp(value);
            case "level" -> pd.addLevel((short) value);
            case "knowledge" -> pd.addKnowledge(value, false);
            case "accumulated_mana" -> pd.addAccumulatedMana(value);
            case "mana" -> pd.addMana(value, false);
            default -> magicianAcademy.inst().getLogger().warning("Unknown data called at Rewarding: " + rewardData);
        }
        return this;
    }
    public Rewarding setRole(Role role) {
        pd.setRole(role);
        return this;
    }
    public Rewarding setRank(String rankID) {
        if(Cache.getRank(rankID) != null) {
            pd.setRank(Cache.getRank(rankID));
        }else {
            magicianAcademy.inst().getLogger().warning("Unknown rank called at Rewarding: " + rankID);
        }
        return this;
    }
}
