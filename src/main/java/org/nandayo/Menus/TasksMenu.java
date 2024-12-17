package org.nandayo.Menus;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.nandayo.Cache;
import org.nandayo.GuiManager.Button;
import org.nandayo.GuiManager.Menu;
import org.nandayo.Manager.ListManager;
import org.nandayo.PlayerData;
import org.nandayo.Task;
import org.nandayo.Utils.ItemCreator;
import org.nandayo.magicianAcademy;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TasksMenu extends Menu {

    public TasksMenu(Player p) {
        this.setTitle("{TITLE}Academy | Tasks");
        this.setSize(45);
        this.setFillers(Arrays.asList(0,1,2,3,4,5,6,7,8,9,17,18,26,27,35,36,37,38,39,40,41,42,43,44));

        List<Integer> freeSpace = Arrays.asList(10,11,12,13,14,15,16,19,20,21,22,23,24,25,28,29,30,31,32,33,34);
        int i = 0;
        for(String taskID : Task.listID()) {
            Task task = Cache.getTask(taskID);
            if(task == null) {
                magicianAcademy.inst().getLogger().warning("Could not find task: " + taskID);
                continue;
            }
            if(i+1 > freeSpace.size()) {
                break;
            }
            this.addButton(new Button(freeSpace.get(i++)) {
                @Override
                public ItemStack getItem() {
                    return getTaskItem(p, task);
                }

                @Override
                public void onClick(Player p, ClickType clickType) {
                    task.activate(p);
                    new TasksMenu(p);
                }
            });
        }
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

    private ItemStack getTaskItem(Player p, Task task) {
        if(task == null) {
            return null;
        }

        PlayerData pd = Cache.getPlayer(p);
        //COMPLETED
        if(!pd.canCompleteTask(task)) {
            long elapsed = magicianAcademy.timeSinceInMS(pd.getCompletedTaskDate(task),0) /1000;
            long remained = task.getRedoAfter()*60 > elapsed ? task.getRedoAfter()*60 - elapsed : 0;
            return ItemCreator.of(Material.PAPER)
                    .name("{TITLE}Task: {WHITE}" + task.getName())
                    .lore("{RED}This task was completed on &n" + magicianAcademy.formattedDate(pd.getCompletedTaskDate(task)) + "{RED}.",
                            "{RED}You can complete this task after " + magicianAcademy.formattedTime(remained*1000))
                    .get();
        }
        //AVAILABLE TO COMPLETE
        else {
            ListManager lm = new ListManager();
            if(pd.getActiveTask() != null && pd.getActiveTask().equals(task)) {
                long elapsed = magicianAcademy.timeSinceInMS(pd.getTaskStartDate(), 0) / 1000;
                long remained = task.getDuration() >= elapsed ? task.getDuration() - elapsed : 0;
                String c = pd.getTaskProgress() >= task.getRequired() ? "{GREEN}" : "{RED}";
                lm.add("{SUBTITLE}Progress: {WHITE}" + pd.getTaskProgress() + c + "/" + task.getRequired(),
                        "{SUBTITLE}Remained: {WHITE}" + magicianAcademy.formattedTime(remained*1000));
            }else {
                lm.add("{SUBTITLE}Duration: {WHITE}" + magicianAcademy.formattedTime(task.getDuration()*1000));
            }
            lm.add("",
                    "{SUBTITLE}&nRewards:",
                    "");
            for(String rewardData : task.getRewardsList()) {
                lm.add(" {SUBTITLE}" + magicianAcademy.titleCase(rewardData) + ": {WHITE}" +
                        task.getRewardAmount(rewardData) + magicianAcademy.getSymbol(rewardData));
            }
            return ItemCreator.of(Material.PAPER)
                    .name("{TITLE}Task: {WHITE}" + task.getName())
                    .lore(lm.result())
                    .get();
        }
    }
}
