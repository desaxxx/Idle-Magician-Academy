package org.nandayo;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.nandayo.CustomEvents.*;
import org.nandayo.Manager.Rewarding;

import java.util.ArrayList;
import java.util.List;

import static org.nandayo.Utils.HexUtil.parse;

public class EventListener implements Listener {

    @EventHandler
    public void manaUse(ManaUseEvent e) {
        Player p = e.getPlayer();
        PlayerData pd = Cache.getPlayer(p);
        pd.addStatic("mana_used", e.getManaUsed());

        //Task
        if(pd.getActiveTask() != null && pd.getActiveTask().getType().equals(Task.TaskType.MANA_USE)) {
            Task task = pd.getActiveTask();
            pd.addTaskProcess((short) e.getManaUsed());
            if(pd.getTaskProgress() >= pd.getActiveTask().getRequired()) {
                task.complete(p);
            }
        }
    }

    @EventHandler
    public void manaCollect(ManaCollectEvent e) {
        Player p = e.getPlayer();
        PlayerData pd = Cache.getPlayer(p);
        pd.addStatic("mana_collected", e.getManaCollected());

        final int collected = e.getManaCollected();
        int knowledge = (int) Math.round(collected*0.12);
        pd.addKnowledge(knowledge, false);
        p.sendMessage(parse("{SUBTITLE}You have collected {WHITE}" + collected + magicianAcademy.mana +
                "{SUBTITLE} from the {GREEN}Mana Nexus {SUBTITLE}and got {WHITE}" +
                knowledge + magicianAcademy.knowledge + "{SUBTITLE} along the way."));

        //Task
        if(pd.getActiveTask() != null && pd.getActiveTask().getType().equals(Task.TaskType.MANA_COLLECT)) {
            Task task = pd.getActiveTask();
            pd.addTaskProcess((short) e.getManaCollected());
            if(pd.getTaskProgress() >= pd.getActiveTask().getRequired()) {
                task.complete(p);
            }
        }
    }

    @EventHandler
    public void spellCast(SpellCastEvent e) {
        Player p = e.getPlayer();
        Spell spell = e.getSpellCasted();
        PlayerData pd = Cache.getPlayer(p);
        pd.addStatic("spell_used_" + spell.getId(), 1);

        pd.addKnowledge(spell.getEarnedKnowledge(),false);
        pd.addXp(spell.getEarnedXp());
        p.sendMessage(parse("{SUBTITLE}You casted spell {WHITE}" + spell.getName() +
                "{SUBTITLE} using {RED}" + spell.getCost() + magicianAcademy.mana + "{SUBTITLE} and got {WHITE}"
                + spell.getEarnedKnowledge() + magicianAcademy.knowledge + "{SUBTITLE}, {WHITE}" +
                spell.getEarnedXp() + magicianAcademy.xp + "{SUBTITLE} along the way."));

        //Task
        if(pd.getActiveTask() != null && pd.getActiveTask().getType().equals(Task.TaskType.SPELL_CAST)) {
            Task task = pd.getActiveTask();
            pd.addTaskProcess((short) 1);
            if(pd.getTaskProgress() >= pd.getActiveTask().getRequired()) {
                task.complete(p);
            }
        }
    }

    @EventHandler
    public void spellLearned(SpellLearnEvent e) {
        Player p = e.getPlayer();
        Spell spell = e.getSpellLearned();
        PlayerData pd = Cache.getPlayer(p);

        //Task
        if(pd.getActiveTask() != null && pd.getActiveTask().getType().equals(Task.TaskType.SPELL_LEARN)) {
            Task task = pd.getActiveTask();
            pd.addTaskProcess((short) 1);
            if(pd.getTaskProgress() >= pd.getActiveTask().getRequired()) {
                task.complete(p);
            }
        }
    }

    @EventHandler
    public void completeTask(CompleteTaskEvent e) {
        Player p = e.getPlayer();
        Task task = e.getTask();
        PlayerData pd = Cache.getPlayer(p);
        pd.addStatic("task_completed_" + task.getId(), 1);

        //Rewards
        List<String> rewardText = new ArrayList<>();
        for(String data : task.getRewardsList()) {
            int amount = task.getRewardAmount(data);
            new Rewarding(pd).addStat(data, amount);
            rewardText.add("{GREEN}+" + amount + magicianAcademy.getSymbol(data));
        }
        p.sendMessage(parse("{SUBTITLE}Collected task rewards: " + String.join("{SUBTITLE}, {GREEN}", rewardText)));
    }

    @EventHandler
    public void knowledgeEarn(KnowledgeEearnEvent e) {
        Player p = e.getPlayer();
        PlayerData pd = Cache.getPlayer(p);

        //Task
        if(pd.getActiveTask() != null && pd.getActiveTask().getType().equals(Task.TaskType.KNOWLEDGE_EARN)) {
            Task task = pd.getActiveTask();
            pd.addTaskProcess((short) e.getKnowledgeEarned());
            if(pd.getTaskProgress() >= pd.getActiveTask().getRequired()) {
                task.complete(p);
            }
        }
    }
}
