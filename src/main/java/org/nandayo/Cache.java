package org.nandayo;

import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;

import java.util.HashMap;

import static org.nandayo.Utils.HexUtil.parse;

public class Cache {

    private static final magicianAcademy MA = magicianAcademy.inst();

    private final static HashMap<Player, PlayerData> pdCache = new HashMap<>();
    private final static HashMap<Player, BossBar> bossBarCache = new HashMap<>();
    private final static HashMap<String, Spell> spellCache = new HashMap<>();
    private final static HashMap<String, Rank> rankCache = new HashMap<>();
    private final static HashMap<String, Role> roleCache = new HashMap<>();
    private final static HashMap<String, Task> taskCache = new HashMap<>();

    //LOAD & UNLOAD
    public static void loadPlayer(Player player) {
        if(getPlayer(player) == null) {
            setPlayer(player, new PlayerData(player));
        }
        PlayerData pd = getPlayer(player);
        //First Time
        if(pd.getRole() == null) pd.setRole(getRole("STUDENT"));
        if(pd.getRank() == null) pd.setRank(getRank("UNKNOWN"));
    }
    public static void unloadPlayer(Player player) {
        if(getPlayer(player) != null) {
            if(getPlayer(player).getActiveTask() != null) {
                getPlayer(player).setActiveTask(null);
                getPlayer(player).setTaskProgress((short) 0);
                getPlayer(player).setTaskStartDate(null);
                player.sendMessage(parse("Reset active task due plugin reload."));
            }
            getPlayer(player).saveChanges();
            pdCache.remove(player);
        }
    }
    //no load for boss bar
    public static void unloadBossBar(Player player) {
        if(getBossBar(player) != null) {
            BossBar bb = getBossBar(player);
            bb.removeAll();
            bossBarCache.remove(player);
        }
    }
    public static void loadSpells() {
        spellCache.clear();
        for(String spellID : Spell.listID()) {
            Spell spell = new Spell(spellID);
            setSpell(spellID, spell);
        }
        MA.getLogger().info(parse("Loaded " + spellCache.size() + " spells"));
    }
    public static void loadRanks() {
        rankCache.clear();
        for(String rankID : Rank.listID()) {
            Rank rank = new Rank(rankID);
            setRank(rankID, rank);
        }
        MA.getLogger().info(parse("Loaded " + rankCache.size() + " ranks"));
    }
    public static void loadRoles() {
        roleCache.clear();
        for(String roleID : Role.listID()) {
            Role role = new Role(roleID);
            setRole(roleID, role);
        }
        MA.getLogger().info(parse("Loaded " + roleCache.size() + " roles"));
    }
    public static void loadTasks() {
        taskCache.clear();
        for (String taskID : Task.listID()) {
            Task task = new Task(taskID);
            setTask(taskID, task);
        }
        MA.getLogger().info(parse("Loaded " + taskCache.size() + " tasks"));
    }

    //GET
    public static PlayerData getPlayer(Player player) {
        return pdCache.get(player);
    }
    public static BossBar getBossBar(Player player) {
        return bossBarCache.get(player);
    }
    public static Spell getSpell(String spellID) {
        return spellCache.get(spellID);
    }
    public static Rank getRank(String rankID) {
        return rankCache.get(rankID);
    }
    public static Role getRole(String roleID) {
        return roleCache.get(roleID);
    }
    public static Task getTask(String taskID) {
        return taskCache.get(taskID);
    }

    //SET
    public static void setPlayer(Player player, PlayerData data) {
        pdCache.put(player, data);
    }
    public static void setBossBar(Player player, BossBar bossBar) {
        bossBarCache.put(player, bossBar);
    }
    public static void setSpell(String spellID, Spell spell) {
        spellCache.put(spellID, spell);
    }
    public static void setRank(String rankID, Rank rank) {
        rankCache.put(rankID, rank);
    }
    public static void setRole(String roleID, Role role) {
        roleCache.put(roleID, role);
    }
    public static void setTask(String taskID, Task task) {
        taskCache.put(taskID, task);
    }
}
