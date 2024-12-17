package org.nandayo.GuiManager;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.nandayo.magicianAcademy;

public class MenuListener implements Listener {

    @EventHandler
    public void onGuiClick(InventoryClickEvent e) {
       Player p = (Player) e.getWhoClicked();
       if(p.hasMetadata("openGui")) {
           e.setCancelled(true);
           int slot = e.getSlot();
           Menu menu = (Menu) p.getMetadata("openGui").getFirst().value();
           if (menu != null) {
               for(Button button : menu.getButtons()) {
                   if(button.getSlot() == slot) {
                       button.onClick(p, e.getClick());
                   }
               }
           }
       }
    }

    @EventHandler
    public void onDrag (InventoryDragEvent e){
        Player p = (Player) e.getWhoClicked();
        if (p.hasMetadata("openGui")) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onClose (InventoryCloseEvent e){
        Player p = (Player) e.getPlayer();
        if (p.hasMetadata("openGui")) {
            p.removeMetadata("openGui", magicianAcademy.inst());
        }
    }

    @EventHandler
    public void onLeave (PlayerQuitEvent e) {
        Player p = e.getPlayer();
        if (p.hasMetadata("openGui")) {
            p.removeMetadata("openGui", magicianAcademy.inst());
        }
    }
}
