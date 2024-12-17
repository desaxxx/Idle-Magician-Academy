package org.nandayo.Menus;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.nandayo.*;
import org.nandayo.GuiManager.Button;
import org.nandayo.GuiManager.Menu;
import org.nandayo.Utils.ItemCreator;

import java.util.Arrays;

import static org.nandayo.Utils.HexUtil.parse;

public class AdminMenu extends Menu {

    public enum Page {
        IDLE,
        MODIFYING,
        MODIFYING_MAIN,
        SELECTING_PLAYER;
    }
    public enum Stat {
        XP,
        LEVEL,
        KNOWLEDGE,
        MANA;
    }

    public AdminMenu(Player p, Player mp, Page page) {
        new AdminMenu(p, mp, page, Stat.XP);
    }
    public AdminMenu(Player p, Player mp, Page page, Stat stat) {
         this.setTitle("{TITLE}Academy | {RED}ADMINISTRATION");
         this.setSize(45);
         this.setFillers(Arrays.asList(11,12,13,14,15));

         //Load configurations button
         this.addButton(new Button(3) {
             @Override
             public ItemStack getItem() {
                 return ItemCreator.of(Material.BOOK)
                         .name("{TITLE}Load Configurations")
                         .lore("{STAR}&nClick to reload.",
                                 "",
                                 " {SUBTITLE}Spell YML",
                                 " {SUBTITLE}Rank YML",
                                 " {SUBTITLE}Task YML",
                                 "{RED}&o(Be aware that this will not affect the current",
                                 "{RED}&ocaches. So players need to rejoin.)"
                         )
                         .get();
             }

             @Override
             public void onClick(Player p, ClickType clickType) {
                 Spell.loadConfigurations();
                 Rank.loadConfigurations();
                 Task.loadConfigurations();
                 p.sendMessage(parse("{SUBTITLE}Reloaded configurations."));
             }
         });
         //Modify button
         this.addButton(new Button(5) {
             @Override
             public ItemStack getItem() {
                 return ItemCreator.of(Material.ANVIL)
                         .name("{TITLE}Modify Player Data")
                         .lore("{STAR}Click to process.")
                         .get();
             }

             @Override
             public void onClick(Player p, ClickType clickType) {
                 new AdminMenu(p, p, Page.MODIFYING_MAIN);
             }
         });
         //Selecting player
        if(page == Page.SELECTING_PLAYER) {
            int i = 18;
            for(Player selectableP : Bukkit.getOnlinePlayers()) {
                if(i > 44) {
                    break;
                }
                this.addButton(new Button(i++) {
                    @Override
                    public ItemStack getItem() {
                        return ItemCreator.of(Material.PLAYER_HEAD)
                                .name("{TITLE}Selectable: {WHITE}" + selectableP.getName())
                                .lore("{STAR}Click to select this player.")
                                .get();
                    }

                    @Override
                    public void onClick(Player p, ClickType clickType) {
                        new AdminMenu(p, selectableP, Page.MODIFYING_MAIN);
                    }
                });
            }
        }
        //Selected player
        if(page == Page.MODIFYING_MAIN || page == Page.MODIFYING) {
            this.addButton(new Button(8) {
                @Override
                public ItemStack getItem() {
                    return ItemCreator.of(Material.PLAYER_HEAD)
                            .name("{TITLE}Modifying: {WHITE}" + mp.getName())
                            .lore("{STAR}Click to change player.")
                            .get();
                }

                @Override
                public void onClick(Player p, ClickType clickType) {
                    new AdminMenu(p, p, Page.SELECTING_PLAYER);
                }
            });
        }
        //Modifying main page
        if(page == Page.MODIFYING_MAIN && mp != null) {
            this.addButton(new Button(28) {
                @Override
                public ItemStack getItem() {
                    return ItemCreator.of(Material.EXPERIENCE_BOTTLE)
                            .name("{TITLE}Modify XP")
                            .get();
                }

                @Override
                public void onClick(Player p, ClickType clickType) {
                    new AdminMenu(p, mp, Page.MODIFYING, Stat.XP);
                }
            });
            this.addButton(new Button(30) {
                @Override
                public ItemStack getItem() {
                    return ItemCreator.of(Material.ITEM_FRAME)
                            .name("{TITLE}Modify Level")
                            .get();
                }

                @Override
                public void onClick(Player p, ClickType clickType) {
                    new AdminMenu(p, mp, Page.MODIFYING, Stat.LEVEL);
                }
            });
            this.addButton(new Button(32) {
                @Override
                public ItemStack getItem() {
                    return ItemCreator.of(Material.WRITTEN_BOOK)
                            .name("{TITLE}Modify Knowledge")
                            .get();
                }

                @Override
                public void onClick(Player p, ClickType clickType) {
                    new AdminMenu(p, mp, Page.MODIFYING, Stat.KNOWLEDGE);
                }
            });
            this.addButton(new Button(34) {
                @Override
                public ItemStack getItem() {
                    return ItemCreator.of(Material.END_CRYSTAL)
                            .name("{TITLE}Modify Mana")
                            .get();
                }

                @Override
                public void onClick(Player p, ClickType clickType) {
                    new AdminMenu(p, mp, Page.MODIFYING, Stat.MANA);
                }
            });
        }
        //Modifying stat page
        if(page == Page.MODIFYING) {
            this.addButton(new Button(31) {
                @Override
                public ItemStack getItem() {
                    return getModifyingItem(p, stat);
                }

                @Override
                public void onClick(Player p, ClickType clickType) {
                }
            });
            this.addButton(new Button(32) {
                @Override
                public ItemStack getItem() {
                    return ItemCreator.of(Material.LIME_STAINED_GLASS_PANE)
                            .amount(1)
                            .name("{GREEN}Increase 1")
                            .get();
                }

                @Override
                public void onClick(Player p, ClickType clickType) {
                    addStat(p, mp, stat, 1);
                    new AdminMenu(p, mp, Page.MODIFYING, stat);
                }
            });
            this.addButton(new Button(33) {
                @Override
                public ItemStack getItem() {
                    return ItemCreator.of(Material.LIME_STAINED_GLASS_PANE)
                            .amount(10)
                            .name("{GREEN}Increase 10")
                            .get();
                }

                @Override
                public void onClick(Player p, ClickType clickType) {
                    addStat(p, mp, stat, 10);
                    new AdminMenu(p, mp, Page.MODIFYING, stat);
                }
            });
            this.addButton(new Button(30) {
                @Override
                public ItemStack getItem() {
                    return ItemCreator.of(Material.RED_STAINED_GLASS_PANE)
                            .amount(1)
                            .name("{RED}Decrease 1")
                            .get();
                }

                @Override
                public void onClick(Player p, ClickType clickType) {
                    addStat(p, mp, stat, -1);
                    new AdminMenu(p, mp, Page.MODIFYING, stat);
                }
            });
            this.addButton(new Button(29) {
                @Override
                public ItemStack getItem() {
                    return ItemCreator.of(Material.RED_STAINED_GLASS_PANE)
                            .amount(10)
                            .name("{RED}Decrease 10")
                            .get();
                }

                @Override
                public void onClick(Player p, ClickType clickType) {
                    addStat(p, mp, stat, -10);
                    new AdminMenu(p, mp, Page.MODIFYING, stat);
                }
            });
            //Back
            this.addButton(new Button(36) {
                @Override
                public ItemStack getItem() {
                    return ItemCreator.of(Material.ARROW).name("{RED}Back").get();
                }

                @Override
                public void onClick(Player p, ClickType clickType) {
                    new AdminMenu(p, mp, Page.MODIFYING_MAIN, Stat.XP);
                }
            });
        }
        this.displayTo(p);
    }

    private ItemStack getModifyingItem(Player mp, Stat stat) {
        PlayerData pd = Cache.getPlayer(mp);
        return switch (stat) {
            case XP -> ItemCreator.of(Material.EXPERIENCE_BOTTLE)
                        .name("{TITLE}Modifying XP")
                        .lore("{SUBTITLE}Current XP: {WHITE}" + pd.getXp() + magicianAcademy.xp)
                        .get();
            case LEVEL -> ItemCreator.of(Material.ITEM_FRAME)
                        .name("{TITLE}Modifying Level")
                        .lore("{SUBTITLE}Current Level: {WHITE}" + pd.getLevel() + magicianAcademy.level)
                        .get();
            case KNOWLEDGE -> ItemCreator.of(Material.WRITABLE_BOOK)
                    .name("{TITLE}Modifying Knowledge")
                    .lore("{SUBTITLE}Current Knowledge: {WHITE}" + pd.getKnowledge() + magicianAcademy.knowledge)
                    .get();
            case MANA -> ItemCreator.of(Material.END_CRYSTAL)
                    .name("{TITLE}Modifying Mana")
                    .lore("{SUBTITLE}Current Mana: {WHITE}" + pd.getMana() + magicianAcademy.mana)
                    .get();
        };
    }

    private void addStat(Player p, Player mp, Stat stat, int amount) {
        PlayerData pd = Cache.getPlayer(mp);
        long oldD = 0, newD = 0;
        switch (stat) {
            case XP:
                oldD = pd.getXp();
                pd.addXp(amount);
                newD = pd.getXp();
                break;
            case LEVEL:
                oldD = pd.getLevel();
                pd.addLevel((short) amount);
                newD = pd.getLevel();
                break;
            case KNOWLEDGE:
                oldD = pd.getKnowledge();
                pd.addKnowledge(amount,true);
                newD = pd.getKnowledge();
                break;
            case MANA:
                oldD = pd.getMana();
                pd.addMana(amount, true);
                newD = pd.getMana();
                break;
        }
        p.sendMessage(parse("{SUBTITLE}Stat " + stat.name() + " of player " + mp.getName() + " set from {WHITE}" +
                oldD + "{SUBTITLE} to {WHITE}" + newD));
    }
}
