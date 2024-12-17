package org.nandayo;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.nandayo.CustomEvents.KnowledgeEearnEvent;
import org.nandayo.CustomEvents.ManaUseEvent;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

import static org.nandayo.Utils.HexUtil.parse;

public class PlayerData {

    private final File file;
    private final FileConfiguration config;

    private Player player;
    private OfflinePlayer offlinePlayer;
    private Role role;
    private Rank rank;
    private int xp;
    private int totalXp;
    private short level;
    private int knowledge;
    private int accumulated_mana;
    private int mana;
    private List<Spell> spells;
    private final Map<Spell, Date> lastCasted = new HashMap<>();
    private final Map<String, Integer> statics = new HashMap<>();
    private Task activeTask;
    private short taskProgress;
    private Date taskStartDate;
    private final Map<Task, Date> completedTasks = new HashMap<>();

    public PlayerData(Player player) {
        this.file = new File(magicianAcademy.inst().getDataFolder(), "players/" + player.getUniqueId() + ".yml");
        this.config = YamlConfiguration.loadConfiguration(file);
        this.player = player;
        initializeData();
    }
    public PlayerData(OfflinePlayer offlinePlayer) {
        this.file = new File(magicianAcademy.inst().getDataFolder(), "players/" + offlinePlayer.getUniqueId() + ".yml");
        this.config = YamlConfiguration.loadConfiguration(file);
        this.offlinePlayer = offlinePlayer;
        initializeData();
    }
    private void initializeData() {
        this.role = Cache.getRole(config.getString("role", "STUDENT"));
        this.rank = Cache.getRank(config.getString("rank", "UNRANKED"));
        this.xp = config.getInt("xp", 0);
        this.totalXp = config.getInt("total_xp", 0);
        this.level = (short) config.getInt("level", 0);
        this.knowledge = config.getInt("knowledge", 0);
        this.accumulated_mana = config.getInt("accumulated_mana", 0);
        this.mana = config.getInt("mana", 0);

        //Spells
        this.spells = new ArrayList<>(magicianAcademy.parseSpells(config.getStringList("spells.learned")));
        for(Spell spell : this.spells) {
            String date = config.getString("spells.last_casted." + spell.getId());
            this.lastCasted.put(spell, magicianAcademy.parseDate(date));
        }

        //Tasks
        this.activeTask = Cache.getTask(config.getString("tasks.active"));
        this.taskProgress = (short) config.getInt("tasks.progress", 0);
        this.taskStartDate = magicianAcademy.parseDate(config.getString("tasks.start_date"));
        ConfigurationSection taskSection = config.getConfigurationSection("tasks.completed");
        if(taskSection != null) {
            for(String taskID : new ArrayList<>(taskSection.getKeys(false))) {
                Task task = Cache.getTask(taskID);
                if(task != null) {
                    this.completedTasks.put(task, magicianAcademy.parseDate(taskSection.getString(taskID)));
                }else {
                    this.completedTasks.put(task, new Date(0));
                }
            }
        }

        //Statics
        ConfigurationSection staticSection = config.getConfigurationSection("statics");
        if(staticSection != null) {
            for(String staticID : new ArrayList<>(staticSection.getKeys(false))) {
                this.statics.put(staticID, config.getInt("statics." + staticID, 0));
            }
        }
    }
    public void saveChanges() {
        try {
            config.set("role", getRole().getId());
            config.setComments("role", Arrays.asList("Named " + getName()));
            config.set("rank", getRank().getId());
            config.set("xp", getXp());
            config.set("total_xp", getTotalXp());
            config.set("level", getLevel());
            config.set("knowledge", getKnowledge());
            config.set("accumulated_mana", getAccumulatedMana());
            config.set("mana", getMana());
            config.set("spells.learned", getSpells().stream().map(Spell::getId).toList());
            for(Spell spell : getSpells()) {
                config.set("spells.last_casted." + spell.getId(), magicianAcademy.parseString(getLastCasted(spell)));
            }
            for (String staticID : statics.keySet()) {
                config.set("statics." + staticID, statics.getOrDefault(staticID, 0));
            }
            if(getActiveTask() != null) {
                config.set("tasks.active", getActiveTask().getId());
                config.set("tasks.progress", getTaskProgress());
                config.set("tasks.start_date", magicianAcademy.parseString(getTaskStartDate()));
            }else {
                config.set("tasks.active", null);
                config.set("tasks.progress", null);
                config.set("tasks.start_date", null);
            }
            for(Task task : completedTasks.keySet()) {
                if(task != null) {
                    String date = getCompletedTaskDate(task) != null ? magicianAcademy.parseString(getCompletedTaskDate(task)) : null;
                    config.set("tasks.completed." + task.getId(), date);
                }
            }
            config.save(file);
        }catch (IOException e) {
            magicianAcademy.inst().getLogger().warning("Could not save data of player " + getName());
        }
    }
    public void createBackup() {
        saveChanges();
        String date = formattedBackupDate(new Date());
        File backupDir = new File(magicianAcademy.inst().getDataFolder(), "backups/" + getUniqueId());
        if (!backupDir.exists()) backupDir.mkdirs();
        File backupFile = new File(backupDir, "backup_" + date + ".yml");
        try {
            Path sourcePath = file.toPath();
            Path backupPath = backupFile.toPath();
            Files.copy(sourcePath, backupPath);
        } catch (IOException e) {
            magicianAcademy.inst().getLogger().warning("Could not create backup file for player " + getName());
        }
        manageBackups();
    }
    private String formattedBackupDate(Date date) {
        if(date == null) return "NONE";
        return new SimpleDateFormat("yy-MM-dd_HH-mm-ss").format(date);
    }
    private void manageBackups() {
        final int maxBackups = 5;
        File[] backups = new File(magicianAcademy.inst().getDataFolder(), "backups/" + getUniqueId())
                .listFiles(((dir, name) -> name.endsWith(".yml")));
        if(backups == null || backups.length <= maxBackups) return;
        Arrays.sort(backups, Comparator.comparing(File::getName));

        int delete = backups.length - maxBackups;
        for(int i = 0; i < delete; i++) {
            backups[i].delete();
        }
    }

    //Leveling
    public void checkLevel() {
        int required = Calculate.getLevelRequirements((short) (getLevel()+1));
        if(getXp() >= required) {
            addXp(-required);
            addLevel((short) 1);
            getPlayer().sendMessage(parse("{GREEN}You have leveled up to &l" + getLevel() + magicianAcademy.level));
        }
    }

    public Player getPlayer() {
        return player;
    }
    public String getName() {
        return player != null ? player.getName() : offlinePlayer.getName();
    }
    public UUID getUniqueId() {
        return player != null ? player.getUniqueId() : offlinePlayer.getUniqueId();
    }
    public Role getRole() {
        return role;
    }
    public void setRole(Role role) {
        this.role = role;
    }
    public Rank getRank() {
        return rank;
    }
    public void setRank(Rank rank) {
        this.rank = rank;
    }

    //
    public int getXp() {
        return xp;
    }
    public void setXp(int xp) {
        this.xp = xp;
    }
    public void addXp(int xp) {
        this.xp += xp;
        if(xp > 0) {
            addTotalXp(xp);
            if(player != null) checkLevel();
        }
    }
    public int getTotalXp() {
        return totalXp;
    }
    public void setTotalXp(int totalXp) {
        this.totalXp = totalXp;
    }
    public void addTotalXp(int totalXp) {
        this.totalXp += totalXp;
    }
    public short getLevel() {
        return level;
    }
    public void setLevel(short level) {
        this.level = level;
    }
    public void addLevel(short level) {
        this.level += level;
    }
    public int getKnowledge() {
        return knowledge;
    }
    public void setKnowledge(int knowledge) {
        this.knowledge = knowledge;
    }
    public void addKnowledge(int knowledge, boolean isAdminAction) {
        this.knowledge += knowledge;
        if(!isAdminAction && knowledge > 0) {
            KnowledgeEearnEvent event = new KnowledgeEearnEvent(getPlayer(), knowledge);
            Bukkit.getServer().getPluginManager().callEvent(event);
        }
    }
    public int getAccumulatedMana() {
        return accumulated_mana;
    }
    public void setAccumulatedMana(int accumulated_mana) {
        this.accumulated_mana = accumulated_mana;
    }
    public void addAccumulatedMana(int accumulated_mana) {
        this.accumulated_mana += accumulated_mana;
    }
    public int getMana() {
        return mana;
    }
    public void setMana(int mana) {
        this.mana = mana;
    }
    public void addMana(int mana, boolean isAdminAction) {
        this.mana += mana;
        if(!isAdminAction) {
            if(mana < 0) {
                ManaUseEvent event = new ManaUseEvent(getPlayer(), -mana);
                Bukkit.getServer().getPluginManager().callEvent(event);
            }
        }
    }

    //
    public List<Spell> getSpells() {
        return new ArrayList<>(spells);
    }
    public void setSpells(List<Spell> spells) {
        this.spells = new ArrayList<>(spells);
    }
    public void addSpell(Spell spell) {
        List<Spell> list = getSpells();
        if(!list.contains(spell)) list.add(spell);
        setSpells(list);
    }
    public void removeSpell(Spell spell) {
        List<Spell> list = getSpells();
        list.remove(spell);
        setSpells(list);
    }
    public Date getLastCasted(Spell spell) {
        if(lastCasted.get(spell) == null) setLastCasted(spell, new Date(0));
        return lastCasted.get(spell);
    }
    public void setLastCasted(Spell spell, Date lastCasted) {
        this.lastCasted.put(spell, lastCasted);
    }

    //
    public Task getActiveTask() {
        return activeTask;
    }

    public void setActiveTask(Task activeTask) {
        this.activeTask = activeTask;
    }
    public short getTaskProgress() {
        return taskProgress;
    }
    public void setTaskProgress(short taskProgress) {
        this.taskProgress = taskProgress;
    }
    public void addTaskProcess(short amount) {
        this.taskProgress += amount;
    }
    public Date getTaskStartDate() {
        return taskStartDate;
    }
    public void setTaskStartDate(Date taskStartDate) {
        this.taskStartDate = taskStartDate;
    }
    public boolean isTaskCompleted(Task task) {
        return getCompletedTasks().contains(task) && getCompletedTaskDate(task) != null;
    }
    public boolean canCompleteTask(Task task) {
        if(isTaskCompleted(task)) {
            return System.currentTimeMillis() - getCompletedTaskDate(task).getTime() >= task.getRedoAfter()*60*1000;
        }
        return true;
    }
    public List<Task> getCompletedTasks() {
        return new ArrayList<>(completedTasks.keySet());
    }
    public void removeCompletedTask(Task task) {
        completedTasks.remove(task);
    }
    public Date getCompletedTaskDate(Task task) {
        return completedTasks.get(task);
    }
    public void setCompletedTaskDate(Task task, Date date) {
        this.completedTasks.put(task, date);
    }

    //
    public int getStatic(String staticID) {
        return statics.getOrDefault(staticID, 0);
    }
    public void setStatic(String staticID, int value) {
        statics.put(staticID, value);
    }
    public void addStatic(String staticID, int value) {
        int updated = statics.getOrDefault(staticID, 0) + value;
        statics.put(staticID, updated);
    }

    //
    public static List<String> list() {
        File folder = new File(magicianAcademy.inst().getDataFolder(), "players");
        File[] files = folder.listFiles(((dir, name) -> name.endsWith(".yml")));
        if(files == null) return new ArrayList<>();
        return Arrays.stream(files)
                .map(file -> file.getName().replaceFirst("\\.yml$", ""))
                .collect(Collectors.toList());
    }
}
