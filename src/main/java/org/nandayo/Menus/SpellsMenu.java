package org.nandayo.Menus;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.nandayo.Cache;
import org.nandayo.GuiManager.Button;
import org.nandayo.GuiManager.Menu;
import org.nandayo.Manager.ListManager;
import org.nandayo.Manager.RequirementChecker;
import org.nandayo.PlayerData;
import org.nandayo.Spell;
import org.nandayo.Utils.ItemCreator;
import org.nandayo.magicianAcademy;

import java.util.Arrays;
import java.util.List;

public class SpellsMenu extends Menu {

    public SpellsMenu(Player p) {
        PlayerData pd = Cache.getPlayer(p);
        this.setTitle("{TITLE}Academy | Spells [Mana: " + pd.getMana() + magicianAcademy.mana + "]");
        this.setSize(45);
        this.setFillers(Arrays.asList(0,1,2,3,4,5,6,7,8,9,17,18,26,27,35,36,37,38,39,40,41,42,43,44));

        List<Integer> freeSpace = Arrays.asList(10,11,12,13,14,15,16,19,20,21,22,23,24,25,28,29,30,31,32,33,34);
        int i = 0;
        for(String spellID : Spell.listID()) {
            Spell spell = Cache.getSpell(spellID);
            if(spell == null) {
                magicianAcademy.inst().getLogger().warning("Could not find spell: " + spellID);
                continue;
            }
            if(i+1 > freeSpace.size()) {
                break;
            }
            this.addButton(new Button(freeSpace.get(i++)) {
                @Override
                public ItemStack getItem() {
                    return getSpellItem(p, spell);
                }

                @Override
                public void onClick(Player p, ClickType clickType) {
                    PlayerData pd = Cache.getPlayer(p);
                    if(pd.getSpells().contains(spell)) {
                        spell.cast(p);
                    }else {
                        spell.learn(p);
                    }
                    new SpellsMenu(p);
                }
            });
        }
        //Back
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

    private ItemStack getSpellItem(Player p, Spell spell) {
        PlayerData pd = Cache.getPlayer(p);
        if(pd.getSpells().contains(spell)) {
            long elapsed = magicianAcademy.timeSinceInMS(pd.getLastCasted(spell), 0) / 1000;
            long remained = spell.getCooldown() >= elapsed ? spell.getCooldown()-elapsed : 0;

            String mt = " " + magicianAcademy.getTickOrCross(pd.getMana() >= spell.getCost());
            String ct = remained > 0 ? " {RED}[" + magicianAcademy.formattedTime(remained*1000) + "]" : "";

            return ItemCreator.of(Material.AMETHYST_SHARD)
                    .name("{TITLE}Spell: {WHITE}" + spell.getName())
                    .lore("{SUBTITLE}&o" + spell.getDescription(),
                            "",
                            "{SUBTITLE}Mana cost: {WHITE}" + spell.getCost() + magicianAcademy.mana + mt,
                            "{SUBTITLE}Cooldown: {WHITE}" + spell.getCooldown() + "s",
                            "",
                            "{STAR}Click to cast spell." + ct)
                    .get();
        }else {
            ListManager lm = new ListManager();
            lm.add("{SUBTITLE}&o" + spell.getDescription(),
                    "",
                    "{SUBTITLE}&nLearn requirements",
                    "");
            for(String key : spell.getRequirements().keySet()) {
                int value = spell.getRequirements().get(key);
                RequirementChecker checker = new RequirementChecker(pd);
                String fulfill = " " + magicianAcademy.getTickOrCross(checker.fulfills(key, value).result());
                lm.add("{SUBTITLE} " + magicianAcademy.titleCase(key) + ": {WHITE}" + value + magicianAcademy.getSymbol(key) + fulfill);
            }
            lm.add("",
                    "{STAR}Click to learn spell.");
            return ItemCreator.of(Material.AMETHYST_SHARD)
                    .name("{TITLE}Spell: {RED}" + spell.getName())
                    .lore(lm.result())
                    .get();
        }
    }
}
