package org.tg.squidgame.listeners;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.tg.squidgame.TGSquidGame;
import org.tg.squidgame.games.Holi;

public class HoliListener implements Listener {

    private final TGSquidGame plugin;

    public HoliListener(TGSquidGame plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPichkariUse(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        ItemStack item = event.getItem();

        if (item == null || item.getType() != Material.BLAZE_ROD) {
            return;
        }

        if (!item.hasItemMeta() || !item.getItemMeta().hasDisplayName()) {
            return;
        }

        String displayName = item.getItemMeta().getDisplayName();
        String pichkariName = ChatColor.translateAlternateColorCodes('&', 
            plugin.getConfig().getString("holi.pichkari.name", "&d&lHoli Pichkari"));

        if (!displayName.equals(pichkariName)) {
            return;
        }

        if (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            event.setCancelled(true);
            
            String arenaName = plugin.getPlayerManager().getPlayerArena(player);
            if (arenaName != null) {
                Holi holiGame = plugin.getArenaManager().getHoliGame(arenaName);
                if (holiGame != null) {
                    holiGame.handlePichkariUse(player);
                } else {
                    player.sendMessage(ChatColor.RED + "Wait for the game to start!");
                }
            }
        }
    }
}
