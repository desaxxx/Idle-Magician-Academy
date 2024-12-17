package org.nandayo;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.nandayo.Manager.RequirementChecker;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.nandayo.Utils.HexUtil.parse;

public class Rank {

    private final String ns = "ranks.";
    private static File file;
    private static FileConfiguration config;

    private final String id;
    private String name;
    private String title;
    private final Map<String, Integer> requirements = new HashMap<>();
    private int mana_limit;
    private int mana_accumulate_limit;

    public Rank(String rankID) {
        this.id = rankID;
        if(!config.contains(ns + rankID)) {
            magicianAcademy.inst().getLogger().warning("Unknown rank call: " + rankID);
            return;
        }
        this.name = config.getString(ns + rankID + ".name", rankID);
        this.title = config.getString(ns + rankID + ".title", rankID);

        ConfigurationSection section = config.getConfigurationSection(ns + rankID + ".requirements");
        if(section != null) {
            for(String requirement : section.getKeys(false)) {
                this.requirements.put(requirement, section.getInt(requirement, 0));
            }
        }

        this.mana_limit = config.getInt(ns + rankID + ".mana_limit", 0);
        this.mana_accumulate_limit = config.getInt(ns + rankID + ".mana_accumulate_limit", 0);
    }
    public static void loadConfigurations() {
        file = new File(magicianAcademy.inst().getDataFolder(), "ranks.yml");
        if(!file.exists()) magicianAcademy.inst().saveResource("ranks.yml", false);
        config = YamlConfiguration.loadConfiguration(file);
    }

    public Rank getNextRank() {
        List<String> ranks = listID();
        int i = ranks.indexOf(this.getId());
        if(i == -1 || i+1 >= ranks.size()) return null;
        return Cache.getRank(ranks.get(i+1));
    }
    public void up(Player p) {
        PlayerData pd = Cache.getPlayer(p);
        Rank nextRank = getNextRank();
        if(nextRank == null) {
            p.sendMessage(parse("{RED}You already reached to the top rank."));
            return;
        }

        ConfigurationSection requirementSection = config.getConfigurationSection(ns + nextRank.getId() + ".requirements");
        RequirementChecker checker = new RequirementChecker(pd);
        boolean fulfillsRequirements = checker.fulfills(requirementSection).result();
        if(!fulfillsRequirements) {
            String unfulfilled = String.join(", ", checker.unfulfilled());
            p.sendMessage(parse("{RED}You do not fulfill requirements: " + unfulfilled));
            return;
        }

        //Charging
        checker.charge("mana", checker.getValue("mana"));

        pd.setRank(nextRank);
        p.sendMessage(parse("{SUBTITLE}You have ranked up to {GREEN}&l" + nextRank.getName() + "{SUBTITLE}. Congrats!"));
    }


    public String getId() {
        return id;
    }
    public String getName() {
        return name;
    }
    public String getTitle() {
        return title;
    }
    public Map<String, Integer> getRequirements() {
        return requirements;
    }
    public int getRequirement(String requirement) {
        return requirements.getOrDefault(requirement,0);
    }
    public int getManaLimit() {
        return mana_limit;
    }
    public int getManaAccumulateLimit() {
        return mana_accumulate_limit;
    }

    public static List<String> listID() {
        return config.getStringList("rank_order");
    }
}
