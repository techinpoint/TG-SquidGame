package org.tg.squidgame.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.tg.squidgame.TGSquidGame;
import org.tg.squidgame.gui.ArenaGUI;

public class GUIListener implements Listener {

    private final TGSquidGame plugin;

    public GUIListener(TGSquidGame plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player)) {
            return;
        }

        Player player = (Player) event.getWhoClicked();
        Inventory inventory = event.getInventory();

        // Check if this is an ArenaGUI
        if (inventory.getHolder() == null && 
            event.getView().getTitle().contains("Arena Settings")) {
            
            event.setCancelled(true);
            
            if (event.getCurrentItem() == null) {
                return;
            }

            // Find the ArenaGUI instance - this is a simplified approach
            // In a real implementation, you might want to track GUI instances
            // For now, we'll handle the basic functionality here
            
            // This is a placeholder - you would need to implement proper GUI tracking
            // to get the actual ArenaGUI instance and call its handleClick method
        }
    }
}
