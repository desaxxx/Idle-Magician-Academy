package org.nandayo;

import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.nandayo.Commands.AdminCommand;
import org.nandayo.Commands.MainCommand;
import org.nandayo.GuiManager.MenuListener;
import org.nandayo.Runnable.Leaderboard;
import org.nandayo.Runnable.LevelBossBar;
import org.nandayo.Runnable.ManaAccumulate;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

public final class magicianAcademy extends JavaPlugin implements Listener {

    private static magicianAcademy instance;
    public static magicianAcademy inst() {
        return instance;
    }
    ManaAccumulate MARun = new ManaAccumulate();
    LevelBossBar LBBRun = new LevelBossBar();
    Leaderboard LRun = new Leaderboard();

    @Override
    public void onEnable() {
        instance = this;
        if(!getDataFolder().exists()) getDataFolder().mkdirs();
        PluginManager pm = Bukkit.getPluginManager();
        pm.registerEvents(this, this);
        pm.registerEvents(new MenuListener(), this);
        pm.registerEvents(new EventListener(), this);
        if(pm.getPlugin("PlaceholderAPI") != null) {
            new PlaceholderManager().register();
            getLogger().info("PlaceholderAPI registered.");
        }
        getCommand("magicianacademy").setExecutor(new MainCommand());
        getCommand("amagicianacademy").setExecutor(new AdminCommand());

        //Load configurations.
        Spell.loadConfigurations();
        Rank.loadConfigurations();
        Role.loadConfigurations();
        Task.loadConfigurations();

        //Load caches
        Cache.loadSpells();
        Cache.loadRanks();
        Cache.loadRoles();
        Cache.loadTasks();
        Bukkit.getOnlinePlayers().forEach(Cache::loadPlayer);

        //Runnable
        MARun.runTaskTimer(this, 0, 10*20);
        LBBRun.runTaskTimer(this, 0, 10*20);
        LRun.runTaskTimer(this, 0, 5*60*20);
    }
    @Override
    public void onDisable() {
        MARun.cancel();
        LBBRun.cancel();
        LRun.cancel();
        //Unload cache for online
        Bukkit.getOnlinePlayers().forEach(Cache::unloadPlayer);
        Bukkit.getOnlinePlayers().forEach(Cache::unloadBossBar);
    }

    //Public symbols
    public final static String mana = "₪";
    public final static String knowledge = "❉";
    public final static String xp = "☼";
    public final static String level = "☁";

    public final static String arrow = "➔";

    public static HashMap<Byte, UUID> leaderboardMap = new HashMap<>();

    @EventHandler
    public void join(PlayerJoinEvent e) {
        Cache.loadPlayer(e.getPlayer());
    }
    @EventHandler
    public void quit(PlayerQuitEvent e) {
        Cache.unloadPlayer(e.getPlayer());
        Cache.unloadBossBar(e.getPlayer());
    }

    //Caches
    public static PlayerData getData(String target) {
        if(Bukkit.getPlayer(target) != null) {
            return Cache.getPlayer(Bukkit.getPlayer(target));
        }else {
            return new PlayerData(Bukkit.getOfflinePlayer(UUID.fromString(target)));
        }
    }

    //Spells
    public static List<Spell> parseSpells(List<String> list) {
        return list.stream()
                .map(Cache::getSpell)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }
    public static List<Spell> parseSpells(ConfigurationSection section) {
        if(section == null) return new ArrayList<>();
        return parseSpells(new ArrayList<>(section.getKeys(false)));
    }

    //Date and String parsing
    public static Date parseDate(String str) {
        if(str == null || str.isEmpty()) return null;
        try {
            return new SimpleDateFormat("MM/dd/yy HH:mm:ss").parse(str);
        } catch (ParseException e) {
            return null;
        }
    }
    public static String parseString(Date date) {
        if(date == null) return "NONE";
        return new SimpleDateFormat("MM/dd/yy HH:mm:ss").format(date);
    }
    public static String formattedDate(String str) {
        if(str == null || str.isEmpty()) return null;
        Date date = parseDate(str);
        return date != null ? new SimpleDateFormat("MM/dd/yy HH:mm").format(date) : "NONE";
    }
    public static String formattedDate(Date date) {
        if(date == null) return "NONE";
        return new SimpleDateFormat("MM/dd/yy HH:mm").format(date);
    }

    //Timings
    public static long timeSinceInMS(Date date, long def) {
        return date == null ? def : System.currentTimeMillis() - date.getTime();
    }
    public static String formattedTime(long millisecond) {
        long day = 24 * 60 * 60 * 1000;
        long hour = 60 * 60 * 1000;
        long minute = 60 * 1000;
        long second = 1000;

        long days = millisecond / day;
        millisecond %= day;

        long hours = millisecond / hour;
        millisecond %= hour;

        long minutes = millisecond / minute;
        millisecond %= minute;

        long seconds = millisecond / second;

        if(days == 0 && hours == 0 && minutes == 0) {
            return String.format("%d s", seconds);
        }else if(days == 0 && hours == 0) {
            return String.format("%d m, %d s", minutes, seconds);
        }else if(days == 0) {
            return String.format("%d h, %d m", hours, minutes);
        }
        return String.format("%d d, %d h", days, hours);
    }

    //Tab Complete
    public static List<String> fixedTabComp(String arg, String options) {
        return Arrays.asList(options);
    }
    public static List<String> fixedTabComp(String arg, String... options) {
        return Arrays.stream(options)
                .filter(x -> x.startsWith(arg))
                .collect(Collectors.toList());
    }

    //Tick & Cross
    public static String getTickOrCross(boolean b) {
        String tick = "{GREEN}[✔]";
        String cross = "{RED}[✗]";
        //make sure function is within a color function.
        return b ? tick : cross;
    }

    //Get data symbol
    public static String getSymbol(String data) {
        return switch (data.toLowerCase()) {
            case "xp","totalxp" -> xp;
            case "level" -> level;
            case "knowledge" -> knowledge;
            case "accumulated_mana","mana" -> mana;
            default -> "";
        };
    }

    //Integer between
    public static int getIntBetween(int min, int max) {
        double random = new Random().nextDouble();
        return (int) (random * (max - min) + min);
    }
    public static int[] getSplitEarned(String s) {
        if (s == null || s.isEmpty()) {
            return null;
        }

        String[] parts = s.split("-");
        int[] earnedValues = new int[2];
        if(parts.length == 1) {
            int value = magicianAcademy.parseInt(parts[0].trim(), 0);
            earnedValues[0] = value;
            earnedValues[1] = value;
        }else if(parts.length == 2) {
            earnedValues[0] = magicianAcademy.parseInt(parts[0].trim(), 0);
            earnedValues[1] = magicianAcademy.parseInt(parts[1].trim(), 0);
        }else {
            return null;
        }
        return earnedValues;
    }

    //Parsing integer
    public static int parseInt(String s, int def) {
        if(s == null || s.isEmpty()) return def;
        try {
            return Integer.parseInt(s);
        } catch (NumberFormatException e) {
            return def;
        }
    }

    //Title Case
    public static String titleCase(String s) {
        if(s == null || s.isEmpty()) return s;

        return Arrays.stream(s.split("\\s+"))
                .map(w -> w.substring(0,1).toUpperCase() + w.substring(1).toLowerCase())
                .collect(Collectors.joining(" "));
    }

}
