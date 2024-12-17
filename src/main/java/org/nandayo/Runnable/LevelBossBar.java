package org.nandayo.Runnable;

import org.bukkit.Bukkit;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.nandayo.Cache;
import org.nandayo.Calculate;
import org.nandayo.PlayerData;
import org.nandayo.magicianAcademy;

import static org.nandayo.Utils.HexUtil.parse;

public class LevelBossBar extends BukkitRunnable {

    @Override
    public void run() {
        for(Player p : Bukkit.getOnlinePlayers()) {
            PlayerData pd = Cache.getPlayer(p);
            BossBar bb;
            if(Cache.getBossBar(p) != null) {
                bb = Cache.getBossBar(p);
            }else {
                bb = Bukkit.createBossBar("Loading", BarColor.GREEN, BarStyle.SOLID);
                Cache.setBossBar(p, bb);
            }
            if(!bb.getPlayers().contains(p)) bb.addPlayer(p);
            double levelRate = (double) pd.getXp() / Calculate.getLevelRequirements((short) (pd.getLevel()+1));
            bb.setTitle(parse("{SUBTITLE}Level Progress: {WHITE}&l" + pd.getLevel() + magicianAcademy.level +
                    "     {SUBTITLE}Rank: {WHITE}" + pd.getRank().getTitle()));
            bb.setProgress(fixDouble(levelRate));
        }
    }

    private double fixDouble(double d) {
        if(d < 0.0) {
            return 0.0;
        }else if(d > 1.0) {
            return 1.0;
        }
        return d;
    }
}
