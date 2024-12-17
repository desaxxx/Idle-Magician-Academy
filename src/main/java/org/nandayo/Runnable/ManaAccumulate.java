package org.nandayo.Runnable;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.nandayo.Cache;
import org.nandayo.PlayerData;
import org.nandayo.magicianAcademy;

import java.util.UUID;

import static org.nandayo.Utils.HexUtil.parse;

public class ManaAccumulate extends BukkitRunnable {

    @Override
    public void run() {
        int i = 0;
        for(String id : PlayerData.list()) {
            i++;
            int amount = 10;

            UUID uuid = UUID.fromString(id);
            if(Bukkit.getPlayer(uuid) == null) {
                accumulate(Bukkit.getOfflinePlayer(uuid), amount);
            }else {
                accumulate(Bukkit.getPlayer(uuid), amount);
            }

            if(i % 10 == 0) {
                try {
                    Thread.sleep(5*1000L);
                }catch (InterruptedException e) {
                    magicianAcademy.inst().getLogger().warning("There was an interrupt in mana accumulating. Moving on...");
                }
            }
        }
    }

    private void accumulate(Player p, int amount) {
        PlayerData pd = Cache.getPlayer(p);
        if(pd.getMana() + pd.getAccumulatedMana() + amount > pd.getRank().getManaLimit()) {
            amount = pd.getRank().getManaLimit() - pd.getAccumulatedMana() - pd.getMana();
        }
        if(amount <= 0) return;
        pd.addAccumulatedMana(amount);
        p.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacy(parse("{SUBTITLE}+" + amount +
                magicianAcademy.mana + " accumulated.")));
    }
    private  void accumulate(OfflinePlayer p, int amount) {
        PlayerData pd = new PlayerData(p);
        amount = (int) Math.round(amount*0.1);
        if(pd.getMana() + pd.getAccumulatedMana() + amount > pd.getRank().getManaLimit()) {
            amount = pd.getRank().getManaLimit() - pd.getAccumulatedMana() - pd.getMana();
        }
        if(amount <= 0) return;
        pd.addAccumulatedMana(amount);
        pd.saveChanges();
    }
}
