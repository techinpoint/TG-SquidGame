package org.tg.squidgame.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.tg.squidgame.TGSquidGame;
import org.tg.squidgame.gui.ArenaGUI;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class GUIListener implements Listener {

    private final TGSquidGame plugin;
    private final Map<UUID, ArenaGUI> openGUIs = new HashMap<>();

    public GUIListener(TGSquidGame plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player)) {
            return;
        }

        Player player = (Player) event.getWhoClicked();
        UUID playerId = player.getUniqueId();
        
        // Check if player has an open ArenaGUI
        ArenaGUI gui = openGUIs.get(playerId);
        if (gui != null && event.getInventory().equals(gui.getInventory())) {
            event.setCancelled(true);
            
            if (event.getCurrentItem() == null) {
                return;
            }

            boolean isRightClick = event.getClick() == ClickType.RIGHT;
            boolean isShiftClick = event.getClick() == ClickType.SHIFT_LEFT || event.getClick() == ClickType.SHIFT_RIGHT;
            
            gui.handleClick(player, event.getSlot(), event.getCurrentItem(), isRightClick, isShiftClick);
        }
    }

    public void registerGUI(Player player, ArenaGUI gui) {
        openGUIs.put(player.getUniqueId(), gui);
    }

    public void unregisterGUI(Player player) {
        openGUIs.remove(player.getUniqueId());
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        if (event.getPlayer() instanceof Player) {
            Player player = (Player) event.getPlayer();
            ArenaGUI gui = openGUIs.get(player.getUniqueId());
            if (gui != null && event.getInventory().equals(gui.getInventory())) {
                unregisterGUI(player);
            }
        }
    }
}
