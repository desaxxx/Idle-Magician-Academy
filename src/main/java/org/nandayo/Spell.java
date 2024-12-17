package org.nandayo;

import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.nandayo.CustomEvents.SpellCastEvent;
import org.nandayo.CustomEvents.SpellLearnEvent;
import org.nandayo.Manager.RequirementChecker;

import java.io.File;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.nandayo.Utils.HexUtil.parse;

public class Spell {

    private final String ns = "spells.";
    private static File file;
    private static FileConfiguration config;

    private final String id;
    private String name;
    private String description;
    private int mana_cost;
    private int cooldown;
    private final Map<String, Integer> requirements = new HashMap<>();
    private int earned_min_xp;
    private int earned_max_xp;
    private int earned_min_knowledge;
    private int earned_max_knowledge;

    public Spell(String spellID) {
        this.id = spellID;
        if(!config.contains(ns + spellID)) {
            magicianAcademy.inst().getLogger().warning("Unknown spell call: " + spellID);
            return;
        }
        this.name = config.getString(ns + spellID + ".name", spellID);
        this.description = config.getString(ns + spellID + ".description", "");
        this.mana_cost = config.getInt(ns + spellID + ".mana_cost", 0);
        this.cooldown = config.getInt(ns + spellID + ".cooldown", 10^12);

        ConfigurationSection section = config.getConfigurationSection(ns + spellID + ".learn_requirements");
        if(section != null) {
            for(String requirement : section.getKeys(false)) {
                this.requirements.put(requirement, section.getInt(requirement, 0));
            }
        }

        int[] earnedXP = magicianAcademy.getSplitEarned(config.getString(ns + spellID + ".earn.xp"));
        this.earned_min_xp = earnedXP != null ? earnedXP[0] : 0;
        this.earned_max_xp = earnedXP != null ? earnedXP[1] : 0;
        int[] earnedKnowledge = magicianAcademy.getSplitEarned(config.getString(ns + spellID + ".earn.knowledge"));
        this.earned_min_knowledge = earnedKnowledge != null ? earnedKnowledge[0] : 0;
        this.earned_max_knowledge = earnedKnowledge != null ? earnedKnowledge[1] : 0;
    }
    public static void loadConfigurations() {
        file = new File(magicianAcademy.inst().getDataFolder(), "spells.yml");
        if(!file.exists()) magicianAcademy.inst().saveResource("spells.yml", false);
        config = YamlConfiguration.loadConfiguration(file);
    }

    //Events
    public void learn(Player p) {
        PlayerData pd = Cache.getPlayer(p);

        if(pd.getSpells().contains(this)) {
            p.sendMessage(parse("{RED}You already learned this spell."));
            return;
        }

        ConfigurationSection requirementSection = config.getConfigurationSection(ns + id + ".learn_requirements");
        RequirementChecker checker = new RequirementChecker(pd);
        boolean fulfillsRequirements = checker.fulfills(requirementSection).result();
        if(!fulfillsRequirements) {
            String unfulfilled = String.join(", ", checker.unfulfilled());
            p.sendMessage(parse("{RED}You do not fulfill requirements: " + unfulfilled));
            return;
        }

        pd.addSpell(this);
        p.sendMessage(parse("{SUBTITLE}You learned spell " + getName()));

        Bukkit.getServer().getPluginManager().callEvent(new SpellLearnEvent(p, this));
    }
    public void cast(Player p) {
        PlayerData pd = Cache.getPlayer(p);
        if(!pd.getSpells().contains(this)) {
            p.sendMessage(parse("{RED}You have not learned this spell."));
            return;
        }
        if(pd.getMana() < getCost()) {
            p.sendMessage(parse("{RED}Insufficient mana."));
            return;
        }
        long timeSince = magicianAcademy.timeSinceInMS(pd.getLastCasted(this), 0);
        if(timeSince/1000 < getCooldown()) {
            p.sendMessage(parse("{RED}This spell is on cooldown. " + magicianAcademy.formattedTime(getCooldown()*1000-timeSince)));
            return;
        }

        pd.setLastCasted(this, new Date());
        pd.addMana(-getCost(), false);

        Bukkit.getServer().getPluginManager().callEvent(new SpellCastEvent(p, this));
    }

    //Gets
    public String getId() {
        return id;
    }
    public String getName() {
        return name;
    }
    public String getDescription() {
        return description;
    }
    public int getCost() {
        return mana_cost;
    }
    public int getCooldown() {
        return cooldown;
    }

    public Map<String, Integer> getRequirements() {
        return requirements;
    }
    public int getRequirement(String requirement) {
        return requirements.getOrDefault(requirement,0);
    }

    public int getEarnedXp() {
        return magicianAcademy.getIntBetween(earned_min_xp, earned_max_xp);
    }
    public int getEarnedKnowledge() {
        return magicianAcademy.getIntBetween(earned_min_knowledge, earned_max_knowledge);
    }

    //Static
    public static List<String> listID() {
        return config.getStringList("spell_order");
    }
}
