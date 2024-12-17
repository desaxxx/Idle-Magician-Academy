package org.nandayo.Menus;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.nandayo.*;
import org.nandayo.CustomEvents.ManaCollectEvent;
import org.nandayo.GuiManager.Button;
import org.nandayo.GuiManager.Menu;
import org.nandayo.Utils.ItemCreator;

import java.util.Arrays;

import static org.nandayo.Utils.HexUtil.parse;

public class MainMenu extends Menu {

    public MainMenu(Player p) {
        PlayerData pd = Cache.getPlayer(p);
        this.setTitle("{TITLE}Academy");
        this.setSize(45);
        this.setFillers(Arrays.asList(0,1,2,3,4,5,6,7,8,9,17,18,26,27,35,36,37,38,39,40,41,42,43,44));

        this.addButton(new Button(19) {
            @Override
            public ItemStack getItem() {
                return ItemCreator.of(Material.END_CRYSTAL)
                        .name("{TITLE}Mana Nexus")
                        .lore("{SUBTITLE}&oThe Mana Nexus is a powerful and ancient location where",
                                "{SUBTITLE}&omana gathers and pulses, steadily accumulating over time,",
                                "{SUBTITLE}&oawaiting those who seek to harness its energy.",
                                "",
                                "{SUBTITLE}Accumulated {WHITE}" + pd.getAccumulatedMana() + magicianAcademy.mana,
                                "",
                                "{STAR}Click to collect them.")
                        .get();
            }

            @Override
            public void onClick(Player p, ClickType clickType) {
                collectManaCrystals(p);
                new MainMenu(p);
            }
        });
        this.addButton(new Button(21) {
            @Override
            public ItemStack getItem() {
                return ItemCreator.of(Material.AMETHYST_SHARD)
                        .name("{TITLE}Spells")
                        .amount(pd.getSpells().isEmpty() ? 1 : pd.getSpells().size())
                        .lore("{SUBTITLE}&oSpells channel magical energy to perform extraordinary",
                                "{SUBTITLE}&ofeats, each with unique effects and power.",
                                "",
                                "{STAR}Click to list spells.")
                        .get();
            }

            @Override
            public void onClick(Player p, ClickType clickType) {
                new SpellsMenu(p);
            }
        });
        this.addButton(new Button(23) {
            @Override
            public ItemStack getItem() {
                return ItemCreator.of(Material.PAPER)
                        .name("{TITLE}Tasks")
                        .amount(Task.listID().isEmpty() ? 1 : Task.listID().size())
                        .lore("{SUBTITLE}&oTasks are magical challenges that test your skills and",
                                "{SUBTITLE}&oreward you with valuable resources upon completion.",
                                "",
                                "{STAR}Click to list tasks.")
                        .get();
            }

            @Override
            public void onClick(Player p, ClickType clickType) {
                new TasksMenu(p);
            }
        });
        this.addButton(new Button(25) {
            @Override
            public ItemStack getItem() {
                byte levelRate = (byte) Math.round((double) pd.getXp() / Calculate.getLevelRequirements((short) (pd.getLevel()+1)) * 100);
                return ItemCreator.of(Material.PLAYER_HEAD)
                        .name("{TITLE}Profile")
                        .lore("{SUBTITLE}Role: {WHITE}" + pd.getRole().getName(),
                                "{SUBTITLE}Rank: {WHITE}" + pd.getRank().getName(),
                                "{SUBTITLE}Level: {WHITE}" + pd.getLevel() + magicianAcademy.level + "{STAR} (" + levelRate + "%)",
                                "{SUBTITLE}Xp: {WHITE}" + pd.getXp() + magicianAcademy.xp + " {STAR}[" + pd.getTotalXp() + magicianAcademy.xp + "]",
                                "{SUBTITLE}Knowledge: {WHITE}" + pd.getKnowledge() + magicianAcademy.knowledge,
                                "{SUBTITLE}Mana: {WHITE}" + pd.getMana() + magicianAcademy.mana,
                                "",
                                "{STAR}Click to get detailed profile."
                        )
                        .get();
            }

            @Override
            public void onClick(Player p, ClickType clickType) {
                new ProfileMenu(p);
            }
        });
        this.addButton(new Button(40) {
            @Override
            public ItemStack getItem() {
                return ItemCreator.of(Material.LILY_PAD)
                        .name("{GREEN}Refresh")
                        .get();
            }

            @Override
            public void onClick(Player p, ClickType clickType) {
                new MainMenu(p);
            }
        });
        this.displayTo(p);
    }

    private void collectManaCrystals(Player p) {
        PlayerData pd = Cache.getPlayer(p);
        int accumulated = pd.getAccumulatedMana();
        int toCollect = accumulated;
        if(pd.getMana() + toCollect > pd.getRank().getManaLimit()) {
            toCollect = pd.getRank().getManaLimit() - pd.getMana();
        }
        if(toCollect > 0) {
            pd.addAccumulatedMana(-toCollect);
            pd.addMana(toCollect, false);

            ManaCollectEvent event = new ManaCollectEvent(p, toCollect);
            Bukkit.getServer().getPluginManager().callEvent(event);
        }else if (accumulated > 0){
            p.sendMessage(parse("{RED}You cannot gain anymore mana."));
        }else {
            p.sendMessage(parse("{RED}You have no accumulated mana crystals."));
        }
    }
}
