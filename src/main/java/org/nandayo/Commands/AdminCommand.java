package org.nandayo.Commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.nandayo.*;
import org.nandayo.Menus.AdminMenu;

import java.util.ArrayList;
import java.util.List;

import static org.nandayo.Utils.HexUtil.parse;

public class AdminCommand implements CommandExecutor, TabCompleter {

    AdminCommandMethods methods = new AdminCommandMethods();

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        if(sender instanceof Player p && !p.hasPermission("ma.admin")) {
            p.sendMessage(parse("{RED}No permission."));
            return true;
        }

        if(args.length >= 2 && args[0].equalsIgnoreCase("createBackup")) {
            Player p = Bukkit.getPlayer(args[1]);
            if(p == null) {
                sender.sendMessage(parse("{RED}This player is offline"));
                return true;
            }
            PlayerData pd = Cache.getPlayer(p);
            pd.createBackup();
            sender.sendMessage(parse("{GREEN}A backup was created for player " + p.getName() + "."));
        }
        else if(args.length >= 1 && args[0].equalsIgnoreCase("menu")) {
            if(!(sender instanceof Player)) {
                sender.sendMessage(parse("{RED}You cannot use this on console."));
                return true;
            }
            Player p = (Player) sender;
            new AdminMenu(p, p, AdminMenu.Page.IDLE);
        }
        else if(args.length >= 4 && args[0].equalsIgnoreCase("data") && args[1].equalsIgnoreCase("set")) {
            if(args.length >= 5) {
                methods.setData(sender, args[2], args[3], args[4]);
            }else if(sender instanceof Player p) {
                methods.setData(p, args[2], args[3], p.getName());
            }else {
                sender.sendMessage(parse("{RED}Wrong usage."));
                return true;
            }
        }
        else if(args.length >= 4 && args[0].equalsIgnoreCase("data") && args[1].equalsIgnoreCase("add")) {
            if(args.length >= 5) {
                methods.addData(sender, args[2], args[3], args[4]);
            }else if(sender instanceof Player p) {
                methods.addData(p, args[2], args[3], p.getName());
            }else {
                sender.sendMessage(parse("{RED}Wrong usage."));
                return true;
            }
        }
        else if(args.length >= 3 && args[0].equalsIgnoreCase("data") && args[1].equalsIgnoreCase("resetSpellCast")) {
            if(args.length >= 4) {
                methods.resetSpellCast(sender, args[2], args[3]);
            }else if(sender instanceof Player p) {
                methods.resetSpellCast(sender, args[2], p.getName());
            }else {
                sender.sendMessage(parse("{RED}Wrong usage."));
                return true;
            }
        }
        else if(args.length >= 3 && args[0].equalsIgnoreCase("data") && args[1].equalsIgnoreCase("removeSpell")) {
            if(args.length >= 4) {
                methods.removeSpell(sender, args[2], args[3]);
            }else if(sender instanceof Player p) {
                methods.removeSpell(sender, args[2], p.getName());
            }else {
                sender.sendMessage(parse("{RED}Wrong usage."));
                return true;
            }
        }
        else if(args.length >= 2 && args[0].equalsIgnoreCase("data") && args[1].equalsIgnoreCase("resetActiveTask")) {
            if(args.length >= 4) {
                methods.resetActiveTask(sender, args[2]);
            }else if(sender instanceof Player p) {
                methods.resetActiveTask(sender, p.getName());
            }else {
                sender.sendMessage(parse("{RED}Wrong usage."));
                return true;
            }
        }
        else if(args.length >= 3 && args[0].equalsIgnoreCase("data") && args[1].equalsIgnoreCase("removeCompletedTask")) {
            if(args.length >= 4) {
                methods.removeCompletedTask(sender, args[2], args[3]);
            }else if(sender instanceof Player p) {
                methods.removeCompletedTask(sender, args[2], p.getName());
            }else {
                sender.sendMessage(parse("{RED}Wrong usage."));
                return true;
            }
        }
        return true;
    }


    /* TAB COMPLETE
     * /ama data set mana 1 yoshii01
     * /ama data add mana 1 yoshii01
     * /ama data resetSpellCast FIREBALL yoshii01
     * /ama data removeSpell FIREBALL yoshii01
     * /ama data resetActiveTask yoshii01
     * /ama data removeCompletedTask MANA_COLLECT_50 yoshii01
     */
    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String s, String[] args) {
        if(args.length == 1) {
            return magicianAcademy.fixedTabComp(args[0], "createBackup","menu","data");
        }
        else if (args.length == 2 && args[0].equalsIgnoreCase("createBackup")) {
            return null;
        }
        else if(args.length >= 2 && args[0].equalsIgnoreCase("data")) {
            if (args.length == 2) {
                return magicianAcademy.fixedTabComp(args[1], "add","set","resetSpellCast","removeSpell","resetActiveTask","removeCompletedTask");
            }
            else if(args.length == 3 && args[1].equalsIgnoreCase("set")) {
                return magicianAcademy.fixedTabComp(args[2], "rank","role","xp","totalxp","level","knowledge","mana","accumulatedMana");
            }
            else if(args.length == 3 && args[1].equalsIgnoreCase("add")) {
                return magicianAcademy.fixedTabComp(args[2], "xp","totalxp","level","knowledge","mana","accumulatedMana");
            }
            else if(args.length == 4 && args[1].equalsIgnoreCase("set")) {
                return magicianAcademy.fixedTabComp(args[3], "<amount>");
            }
            else if(args.length == 4 && args[1].equalsIgnoreCase("add")) {
                return magicianAcademy.fixedTabComp(args[3], "<amount>");
            }
            else if(args.length == 5 && args[1].equalsIgnoreCase("set")) {
                return null;
            }
            else if(args.length == 5 && args[1].equalsIgnoreCase("add")) {
                return null;
            }
            else if(args.length == 3 && args[1].equalsIgnoreCase("resetSpellCast")) {
                return magicianAcademy.fixedTabComp(args[2], Spell.listID().toArray(new String[0]));
            }
            else if(args.length == 4 && args[1].equalsIgnoreCase("resetSpellCast")) {
                return null;
            }
            else if(args.length == 3 && args[1].equalsIgnoreCase("removeSpell")) {
                return magicianAcademy.fixedTabComp(args[2], Spell.listID().toArray(new String[0]));
            }
            else if(args.length == 4 && args[1].equalsIgnoreCase("removeSpell")) {
                return null;
            }
            else if(args.length == 3 && args[1].equalsIgnoreCase("resetActiveTask")) {
                return null;
            }
            else if(args.length == 3 && args[1].equalsIgnoreCase("removeCompletedTask")) {
                return magicianAcademy.fixedTabComp(args[2], Task.listID().toArray(new String[0]));
            }
            else if(args.length == 4 && args[1].equalsIgnoreCase("removeCompletedTask")) {
                return null;
            }
        }
        return new ArrayList<>();
    }
}
