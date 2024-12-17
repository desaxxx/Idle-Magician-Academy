package org.nandayo.Runnable;

import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;
import org.nandayo.Cache;
import org.nandayo.PlayerData;
import org.nandayo.magicianAcademy;

import java.util.*;
import java.util.stream.Collectors;

public class Leaderboard extends BukkitRunnable {

    @Override
    public void run() {
        updateLeaderBoard();
    }

    private void updateLeaderBoard() {
        Map<UUID, Integer> totalXPMap = PlayerData.list().stream()
                .collect(Collectors.toMap(
                        UUID::fromString,
                        id -> {
                            PlayerData pd = magicianAcademy.getData(id);
                            return pd != null ? pd.getTotalXp() : 0;
                        }
                ));

        magicianAcademy.leaderboardMap.clear();
        List<UUID> topPlayers = totalXPMap.entrySet().stream()
                .sorted(Map.Entry.<UUID, Integer>comparingByValue().reversed())
                .limit(10)
                .map(Map.Entry::getKey)
                .toList();

        for (byte i = 0; i < topPlayers.size(); i++) {
            magicianAcademy.leaderboardMap.put(i, topPlayers.get(i));
        }
    }
}
