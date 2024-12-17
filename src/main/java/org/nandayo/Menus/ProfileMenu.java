package org.nandayo.Menus;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.Statistic;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.nandayo.*;
import org.nandayo.GuiManager.Button;
import org.nandayo.GuiManager.Menu;
import org.nandayo.Manager.ListManager;
import org.nandayo.Manager.RequirementChecker;
import org.nandayo.Utils.ItemCreator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class ProfileMenu extends Menu {

    public ProfileMenu(Player p) {
        PlayerData pd = Cache.getPlayer(p);
        this.setTitle("{TITLE}Academy | Profile");
        this.setSize(45);
        this.setFillers(Arrays.asList(0,1,2,3,4,5,6,7,8,9,17,18,26,27,35,36,37,38,39,40,41,42,43,44));

        //Personal Stats
        this.addButton(new Button(11) {
            @Override
            public ItemStack getItem() {
                byte levelRate = (byte) Math.round((double) pd.getXp() / Calculate.getLevelRequirements((short) (pd.getLevel()+1)) * 100);
                return ItemCreator.of(Material.PLAYER_HEAD)
                        .name("{TITLE}Personal Stats")
                        .lore("{SUBTITLE}Level: {WHITE}" + pd.getLevel() + magicianAcademy.level + "{STAR} (" + levelRate + "%)",
                                "{SUBTITLE}XP: {WHITE}" + pd.getXp() + magicianAcademy.xp + "{STAR} [" + pd.getTotalXp() + magicianAcademy.xp + "]",
                                "{SUBTITLE}Mana: {WHITE}" + pd.getMana() + magicianAcademy.mana,
                                "{SUBTITLE}Knowledge: {WHITE}" + pd.getKnowledge() + magicianAcademy.knowledge,
                                "{SUBTITLE}Spells learned: {WHITE}" + pd.getSpells().size(),
                                "{SUBTITLE}Tasks completed: {WHITE}" + pd.getCompletedTasks().size(),
                                "{SUBTITLE}Time spent: {WHITE}" + magicianAcademy.formattedTime(p.getStatistic(Statistic.PLAY_ONE_MINUTE) * 50L)
                                )
                        .get();
            }

            @Override
            public void onClick(Player p, ClickType clickType) {
            }
        });
        //Academy Progress
        this.addButton(new Button(13) {
            @Override
            public ItemStack getItem() {
                return ItemCreator.of(Material.ENCHANTED_BOOK)
                        .name("{TITLE}Academy Progress")
                        .lore("{SUBTITLE}Role: {WHITE}" + pd.getRole().getName(),
                                "{SUBTITLE}Rank: {WHITE}" + pd.getRank().getName(),
                                "{RED}SOON")
                        .get();
            }

            @Override
            public void onClick(Player p, ClickType clickType) {
            }
        });
        //Spell Mastery
        this.addButton(new Button(15) {
            @Override
            public ItemStack getItem() {
                return ItemCreator.of(Material.AMETHYST_SHARD)
                        .name("{TITLE}Spell Mastery")
                        .lore("{SUBTITLE}Learned: {WHITE}" + pd.getSpells().size(),
                                "{SUBTITLE}Mastered: {RED}SOON",
                                "{SUBTITLE}Most used: {RED}SOON"
                                )
                        .get();
            }

            @Override
            public void onClick(Player p, ClickType clickType) {
            }
        });
        //Achievements
        this.addButton(new Button(29) {
            @Override
            public ItemStack getItem() {
                return ItemCreator.of(Material.NETHER_STAR)
                        .name("{TITLE}Achievements {RED}SOON")
                        .get();
            }

            @Override
            public void onClick(Player p, ClickType clickType) {
            }
        });
        //Leaderboard
        this.addButton(new Button(31) {
            @Override
            public ItemStack getItem() {
                return getLeaderboardItem();
            }

            @Override
            public void onClick(Player p, ClickType clickType) {
            }
        });
        //Ranking up
        this.addButton(new Button(33) {
            @Override
            public ItemStack getItem() {
                return getRankUpItem(p);
            }

            @Override
            public void onClick(Player p, ClickType clickType) {
                PlayerData pd = Cache.getPlayer(p);
                pd.getRank().up(p);
            }
        });
        this.addButton(new Button(36) {
            @Override
            public ItemStack getItem() {
                return ItemCreator.of(Material.ARROW).name("{RED}Back").get();
            }

            @Override
            public void onClick(Player p, ClickType clickType) {
                new MainMenu(p);
            }
        });
        this.displayTo(p);
    }

    private ItemStack getRankUpItem(Player p) {
        PlayerData pd = Cache.getPlayer(p);
        Rank nextRank = pd.getRank().getNextRank();
        if(nextRank == null) {
            return ItemCreator.of(Material.BEACON)
                    .name("{TITLE}Ranking Up")
                    .lore("{SUBTITLE}Current rank: {WHITE}" + pd.getRank().getName(),
                            "{SUBTITLE}Next rank: {RED}-",
                            "",
                            "{SUBTITLE}&nBenefits:",
                            "",
                            "{SUBTITLE} Mana Limit: {WHITE}" + pd.getRank().getManaLimit() + magicianAcademy.mana,
                            "{SUBTITLE} Mana Accumulate Limit: {WHITE}" + pd.getRank().getManaAccumulateLimit() + magicianAcademy.mana
                    )
                    .get();
        }
        ListManager lm = new ListManager();
        lm.add("{SUBTITLE}Current rank: {WHITE}" + pd.getRank().getName(),
                "{SUBTITLE}Next rank: {WHITE}" + nextRank.getName(),
                "",
                "{SUBTITLE}&nRequirements:",
                "");
        for(String key : nextRank.getRequirements().keySet()) {
            int value = nextRank.getRequirements().get(key);
            RequirementChecker checker = new RequirementChecker(pd);
            String fulfill = " " + magicianAcademy.getTickOrCross(checker.fulfills(key, value).result());
            lm.add("{SUBTITLE} " + magicianAcademy.titleCase(key) + ": {WHITE}" + value + magicianAcademy.getSymbol(key) + fulfill);
        }
        lm.add("",
                "{SUBTITLE}&nBenefits:",
                "",
                "{SUBTITLE} Mana Limit: {WHITE}&m" + pd.getRank().getManaLimit() + magicianAcademy.mana
                        + "{WHITE} " + magicianAcademy.arrow + " {GREEN}" + nextRank.getManaLimit() + magicianAcademy.mana,
                "{SUBTITLE} Mana Accumulate Limit: {WHITE}&m" + pd.getRank().getManaAccumulateLimit() + magicianAcademy.mana
                        + "{WHITE} " + magicianAcademy.arrow + " {GREEN}" + nextRank.getManaAccumulateLimit() + magicianAcademy.mana,
                "",
                "{STAR}Click to rank up.");

        return ItemCreator.of(Material.BEACON)
                .name("{TITLE}Ranking Up")
                .lore(lm.result())
                .get();
    }

    private ItemStack getLeaderboardItem() {
        ListManager lm = new ListManager();
        lm.add("{SUBTITLE}&nTop Players",
                "");
        for(Byte i : magicianAcademy.leaderboardMap.keySet()) {
            UUID uuid = magicianAcademy.leaderboardMap.get(i);
            OfflinePlayer offP = Bukkit.getOfflinePlayer(uuid);
            PlayerData pd = new PlayerData(offP);
            lm.add("{STAR}" + (i+1) + ". {GREEN}" + offP.getName() + " {RED}: {WHITE}" + pd.getTotalXp() + magicianAcademy.xp);
        }
        lm.add("",
                "{STAR}&oList reloads every 5 minutes.");
        return ItemCreator.of(Material.EMERALD)
                .name("{TITLE}Leaderboards")
                .lore(lm.result())
                .get();
    }
}
