package org.nandayo.Commands;

import org.bukkit.command.CommandSender;
import org.nandayo.*;

import java.util.Date;

import static org.nandayo.Utils.HexUtil.parse;

public class AdminCommandMethods {

    public AdminCommandMethods() {
    }
    
    //Set Data
    public void setData(CommandSender admin, String dataType, String value, String target) {
        PlayerData pd = magicianAcademy.getData(target);
        if(pd == null) {
            admin.sendMessage(parse("{RED}Unknown player/uuid."));
            return;
        }
        String targetName = pd.getName();
        switch (dataType) {
            case "role":
                if(Role.listID().contains(value.toUpperCase())) {
                    pd.setRole(Cache.getRole(value.toUpperCase()));
                    admin.sendMessage(parse("{RED}Role of player " + targetName + " set to " + pd.getRole().getName()));
                }else {
                    admin.sendMessage(parse("{RED}Unknown role '" + value + "'"));
                }
                break;
            case "rank":
                if(Rank.listID().contains(value.toUpperCase())) {
                    pd.setRank(Cache.getRank(value.toUpperCase()));
                    admin.sendMessage(parse("{RED}Rank of player " + targetName + " set to " + pd.getRank().getName()));
                }else {
                    admin.sendMessage(parse("{RED}Unknown rank '" + value + "'"));
                }
                break;
            case "xp":
                int xp = magicianAcademy.parseInt(value, 0);
                pd.setXp(xp);
                admin.sendMessage(parse("{RED}Experience of player " + targetName + " set to " + xp + magicianAcademy.xp));
                break;
            case "totalxp":
                int totalxp = magicianAcademy.parseInt(value, 0);
                pd.setTotalXp(totalxp);
                admin.sendMessage(parse("{RED}Total experience of player " + targetName + " set to " + totalxp + magicianAcademy.xp));
                break;
            case "level":
                short level = (short) magicianAcademy.parseInt(value, 0);
                pd.setLevel(level);
                admin.sendMessage(parse("{RED}Level of player " + targetName + " set to " + level + magicianAcademy.level));
                break;
            case "knowledge":
                int knowledge = magicianAcademy.parseInt(value, 0);
                pd.setKnowledge(knowledge);
                admin.sendMessage(parse("{RED}Knowledge of player " + targetName + " set to " + knowledge + magicianAcademy.knowledge));
                break;
            case "mana":
                int mana = magicianAcademy.parseInt(value, 0);
                pd.setMana(mana);
                admin.sendMessage(parse("{RED}Mana of player " + targetName + " set to " + mana + magicianAcademy.mana));
                break;
            case "accumulatedMana":
                int accumulatedMana = magicianAcademy.parseInt(value, 0);
                pd.setAccumulatedMana(accumulatedMana);
                admin.sendMessage(parse("{RED}Accumulated mana of player " + targetName + " set to " + accumulatedMana + magicianAcademy.mana));
                break;
            default:
                admin.sendMessage(parse("{RED}Unknown dataType '" + dataType + "'"));
                break;
        }
    }
    //Add Date
    public void addData(CommandSender admin, String dataType, String value, String target) {
        PlayerData pd = magicianAcademy.getData(target);
        if(pd == null) {
            admin.sendMessage(parse("{RED}Unknown player/uuid."));
            return;
        }
        String targetName = pd.getName();
        switch (dataType) {
            case "xp":
                int xp = magicianAcademy.parseInt(value, 0);
                pd.addXp(xp);
                admin.sendMessage(parse("{RED}Experience of player " + targetName + " set to " + pd.getXp() + magicianAcademy.xp));
                break;
            case "totalxp":
                int totalxp = magicianAcademy.parseInt(value, 0);
                pd.addTotalXp(totalxp);
                admin.sendMessage(parse("{RED}Total experience of player " + targetName + " set to " + pd.getTotalXp() + magicianAcademy.xp));
                break;
            case "level":
                short level = (short) magicianAcademy.parseInt(value, 0);
                pd.addLevel(level);
                admin.sendMessage(parse("{RED}Level of player " + targetName + " set to " + pd.getLevel() + magicianAcademy.level));
                break;
            case "knowledge":
                int knowledge = magicianAcademy.parseInt(value, 0);
                pd.addKnowledge(knowledge, true);
                admin.sendMessage(parse("{RED}Knowledge of player " + targetName + " set to " + pd.getKnowledge() + magicianAcademy.knowledge));
                break;
            case "mana":
                int mana = magicianAcademy.parseInt(value, 0);
                pd.addMana(mana, true);
                admin.sendMessage(parse("{RED}Mana of player " + targetName + " set to " + pd.getMana() + magicianAcademy.mana));
                break;
            case "accumulatedMana":
                int accumulatedMana = magicianAcademy.parseInt(value, 0);
                pd.addAccumulatedMana(accumulatedMana);
                admin.sendMessage(parse("{RED}Accumulated mana of player " + targetName + " set to " + pd.getAccumulatedMana() + magicianAcademy.mana));
                break;
            default:
                admin.sendMessage(parse("{RED}Unknown dataType '" + dataType + "'"));
                break;
        }
    }

    //Reset Spell Cast
    public void resetSpellCast(CommandSender admin, String spellID, String target) {
        PlayerData pd = magicianAcademy.getData(target);
        if(pd == null) {
            admin.sendMessage(parse("{RED}Unknown player/uuid."));
            return;
        }
        String targetName = pd.getName();

        Spell spell = Cache.getSpell(spellID);
        if(spell == null) {
            admin.sendMessage(parse("{RED}Unknown spell '" + spellID + "'"));
            return;
        }
        if(!pd.getSpells().contains(spell)) {
            admin.sendMessage(parse("{RED}The player did not learn this spell."));
            return;
        }

        pd.setLastCasted(spell, new Date(0));
        admin.sendMessage(parse("Reset cast date of spell " + spell.getName() + " of player " + targetName));
    }

    //Remove spell
    public void removeSpell(CommandSender admin, String spellID, String target) {
        PlayerData pd = magicianAcademy.getData(target);
        if(pd == null) {
            admin.sendMessage(parse("{RED}Unknown player/uuid."));
            return;
        }
        String targetName = pd.getName();
        Spell spell = Cache.getSpell(spellID);
        if(spell == null) {
            admin.sendMessage(parse("{RED}Unknown spell '" + spellID + "'"));
            return;
        }
        if(!pd.getSpells().contains(spell)) {
            admin.sendMessage(parse("{RED}The player did not learn this spell."));
            return;
        }

        pd.removeSpell(spell);
        admin.sendMessage(parse("{RED}Removed spell " + spell.getName() + " from spells of player " + targetName));
    }

    //Reset active task
    public void resetActiveTask(CommandSender admin, String target) {
        PlayerData pd = magicianAcademy.getData(target);
        if(pd == null) {
            admin.sendMessage(parse("{RED}Unknown player/uuid."));
            return;
        }
        String targetName = pd.getName();
        if(pd.getActiveTask() == null) {
            admin.sendMessage(parse("{RED}The player has no active task"));
            return;
        }

        pd.setActiveTask(null);
        pd.setTaskProgress((short) 0);
        pd.setTaskStartDate(null);
        admin.sendMessage(parse("{RED}Reset active task of player " + targetName));
    }

    //Remove completed task
    public void removeCompletedTask(CommandSender admin, String taskID, String target) {
        PlayerData pd = magicianAcademy.getData(target);
        if(pd == null) {
            admin.sendMessage(parse("{RED}Unknown player/uuid."));
            return;
        }
        String targetName = pd.getName();
        Task task = Cache.getTask(taskID);
        if(task == null) {
            admin.sendMessage(parse("{RED}Unknown task '" + taskID + "'"));
            return;
        }
        if(!pd.isTaskCompleted(task)) {
            admin.sendMessage(parse("{RED}Player did not complete this task."));
            return;
        }

        pd.removeCompletedTask(task);
        admin.sendMessage(parse("{RED}Removed task " + task.getName() + " from completed tasks of player " + targetName));
    }
}
