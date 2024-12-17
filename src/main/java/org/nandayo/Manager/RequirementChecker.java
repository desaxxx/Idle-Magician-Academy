package org.nandayo.Manager;

import org.bukkit.configuration.ConfigurationSection;
import org.nandayo.PlayerData;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class RequirementChecker {

    private final PlayerData pd;
    private final Map<String, Boolean> checkList = new HashMap<>();
    private final Map<String, Integer> dataList = new HashMap<>();

    public RequirementChecker(PlayerData pd) {
        this.pd = pd;
    }

    //SINGLE
    public RequirementChecker fulfills(String data, int value) {
        boolean b = switch (data.toLowerCase()) {
            case "xp" -> pd.getXp() >= value;
            case "total_xp" -> pd.getTotalXp() >= value;
            case "level" -> pd.getLevel() >= (short) value;
            case "knowledge" -> pd.getKnowledge() >= value;
            case "accumulated_mana" -> pd.getAccumulatedMana() >= value;
            case "mana" -> pd.getMana() >= value;
            default -> false;
        };
        checkList.put(data, b);
        dataList.put(data, value);
        return this;
    }

    //SECTION
    public RequirementChecker fulfills(ConfigurationSection requirementSection) {
        if(requirementSection == null) return this;

        for(String key : requirementSection.getKeys(false)) {
            int value = requirementSection.getInt(key);

            fulfills(key, value);
        }
        return this;
    }

    //CHARGE
    public RequirementChecker charge(String data, int value) {
        switch (data.toLowerCase()) {
            case "xp" -> pd.addXp(-value);
            case "total_xp" -> pd.addTotalXp(-value);
            case "level" -> pd.addLevel((short) -value);
            case "knowledge" -> pd.addKnowledge(-value, false);
            case "accumulated_mana" -> pd.addAccumulatedMana(-value);
            case "mana" -> pd.addMana(-value, false);
        }
        return this;
    }

    //RESULT
    public boolean result() {
        return !checkList.containsValue(false);
    }

    //UNFULFILLED
    public List<String> unfulfilled() {
        return checkList.keySet().stream()
                .filter(key -> !checkList.get(key))
                .collect(Collectors.toList());
    }

    //DATA REQUIRED
    public int getValue(String data) {
        return dataList.getOrDefault(data, 0);
    }
}
