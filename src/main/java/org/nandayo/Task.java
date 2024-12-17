package org.nandayo;

import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.nandayo.CustomEvents.CompleteTaskEvent;

import java.io.File;
import java.util.*;

import static org.nandayo.Utils.HexUtil.parse;

public class Task {

    public enum TaskType {
        MANA_COLLECT,
        MANA_USE,
        SPELL_CAST,
        KNOWLEDGE_EARN,
        SPELL_LEARN;
    }

    private final String ns = "tasks.";
    private static File file;
    private static FileConfiguration config;

    private final String id;
    private String name;
    private TaskType type;
    private int required;
    private final Map<String, Integer> rewards = new HashMap<>();
    private long duration_s;
    private long redo_after;

    public Task(String taskID) {
        this.id = taskID;
        if (!config.contains(ns + taskID)) {
            magicianAcademy.inst().getLogger().warning("Unknown task call: " + ns + taskID);
            return;
        }
        this.name = config.getString(ns + taskID + ".name", taskID);

        try {
            this.type = TaskType.valueOf(config.getString(ns + taskID + ".task_type"));
        } catch (IllegalArgumentException e) {
            magicianAcademy.inst().getLogger().warning("Unknown TaskType call for task " + taskID);
        }
        this.required = config.getInt(ns + taskID + ".required", 0);

        ConfigurationSection rewardSection = config.getConfigurationSection(ns + taskID + ".rewards");
        if (rewardSection != null) {
            for (String s : new ArrayList<>(rewardSection.getKeys(false))) {
                this.rewards.put(s, rewardSection.getInt(s));
            }
        }
        this.duration_s = config.getLong(ns + taskID + ".duration_s", 0);
        this.redo_after = config.getLong(ns + taskID + ".redo_after", 0);
    }

    public static void loadConfigurations() {
        file = new File(magicianAcademy.inst().getDataFolder(), "tasks.yml");
        if(!file.exists()) magicianAcademy.inst().saveResource("tasks.yml", false);
        config = YamlConfiguration.loadConfiguration(file);
    }

    public void activate(Player p) {
        final PlayerData pd = Cache.getPlayer(p);
        if(pd.getActiveTask() != null) {
            p.sendMessage(parse("{RED}You already have an active task."));
            return;
        }
        if(!pd.canCompleteTask(this)) {
            p.sendMessage(parse("{RED}You already completed this task."));
            return;
        }

        pd.setCompletedTaskDate(this, null);
        pd.setActiveTask(this);
        pd.setTaskProgress((short) 0);
        Date start = new Date();
        pd.setTaskStartDate(start);

        p.sendMessage(parse("{SUBTITLE}Started a task. You have {GREEN}" + magicianAcademy.formattedTime(getDuration()*1000) +
                "{SUBTITLE} to complete it."));

        new BukkitRunnable() {
            @Override
            public void run() {
                if(pd.getActiveTask() == null || !pd.getActiveTask().equals(Task.this) || !pd.getTaskStartDate().equals(start)) {
                    cancel();
                }
                long elapsed = magicianAcademy.timeSinceInMS(start,0) / 1000;
                if(elapsed > getDuration()) {
                    pd.setActiveTask(null);
                    pd.setTaskProgress((short) 0);
                    pd.setTaskStartDate(null);
                    p.sendMessage(parse("{RED}Task duration expired!"));
                    cancel();
                }
            }
        }.runTaskTimer(magicianAcademy.inst(), 0,20L);
    }

    public void complete(Player p) {
        PlayerData pd = Cache.getPlayer(p);
        if(pd.getActiveTask() != null && !pd.getActiveTask().equals(this)) {
            p.sendMessage(parse("{RED}You cannot complete this."));
            return;
        }
        if(pd.getTaskProgress() < getRequired()) {
            p.sendMessage(parse("{RED}You did not fulfill the task requirement."));
            return;
        }
        long elapsed = magicianAcademy.timeSinceInMS(pd.getTaskStartDate(),0) / 1000;
        if(elapsed > getDuration()) {
            p.sendMessage(parse("{RED}You cannot complete an expired task."));
            return;
        }

        pd.setActiveTask(null);
        pd.setTaskProgress((short) 0);
        pd.setTaskStartDate(null);
        pd.setCompletedTaskDate(this, new Date());

        p.sendMessage(parse("{SUBTITLE}You have successfully completed task {WHITE}" + getName() + "{SUBTITLE} in " +
                magicianAcademy.formattedTime(elapsed*1000)));
        Bukkit.getServer().getPluginManager().callEvent(new CompleteTaskEvent(p, this));
    }

    public String getId() {
        return id;
    }
    public String getName() {
        return name;
    }
    public TaskType getType() {
        return type;
    }
    public int getRequired() {
        return required;
    }
    public List<String> getRewardsList() {
        return new ArrayList<>(rewards.keySet());
    }
    public int getRewardAmount(String stat) {
        return rewards.getOrDefault(stat, 0);
    }
    public long getDuration() {
        return duration_s;
    }
    public long getRedoAfter() {
        return redo_after;
    }

    //Static
    public static List<String> listID() {
        return config.getStringList("task_order");
    }
}
