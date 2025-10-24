package org.tg.squidgame.listeners;

import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.tg.squidgame.TGSquidGame;
import org.tg.squidgame.data.ArenaData;
import org.tg.squidgame.games.RedLightGreenLight;
import org.tg.squidgame.gui.ArenaGUI;

public class PlayerEventListener implements Listener {

    private final TGSquidGame plugin;

    public PlayerEventListener(TGSquidGame plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        String arenaName = plugin.getPlayerManager().getPlayerArena(player);

        if (arenaName != null) {
            RedLightGreenLight game = plugin.getArenaManager().getActiveGame(arenaName);
            if (game != null) {
                plugin.getPlayerManager().removePlayer(player);
                plugin.getPlayerManager().addSpectator(arenaName, player);

                if (plugin.getPlayerManager().getArenaPl ayers(arenaName).isEmpty()) {
                    plugin.getArenaManager().stopGame(arenaName);
                }
            } else {
                plugin.getPlayerManager().removePlayer(player);
            }
        }
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();

        if (player.getGameMode() == GameMode.CREATIVE && player.isOp()) {
            return;
        }

        String arenaName = plugin.getPlayerManager().getPlayerArena(player);
        if (arenaName == null) {
            return;
        }

        ArenaData arena = plugin.getArenaManager().getArena(arenaName);
        if (arena == null) {
            return;
        }

        if (plugin.getPlayerManager().isPlayerSpectating(arenaName, player)) {
            if (!arena.isInArenaBounds(player.getLocation())) {
                if (arena.getSpectator() != null) {
                    player.teleport(arena.getSpectator());
                    player.sendMessage(ChatColor.RED + "Spectators must stay within the arena!");
                }
            }
            return;
        }

        RedLightGreenLight game = plugin.getArenaManager().getActiveGame(arenaName);
        if (game != null) {
            if (!arena.isInArenaBounds(player.getLocation())) {
                event.setCancelled(true);
                plugin.getPlayerManager().removePlayer(player);
                plugin.getPlayerManager().addSpectator(arenaName, player);

                if (arena.getSpectator() != null) {
                    player.teleport(arena.getSpectator());
                }

                player.sendMessage(ChatColor.RED + "You left the arena! You are eliminated.");

                if (plugin.getPlayerManager().getArenaPl ayers(arenaName).isEmpty()) {
                    plugin.getArenaManager().stopGame(arenaName);
                }
            }
        }
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player)) {
            return;
        }

        Player player = (Player) event.getWhoClicked();

        if (event.getView().getTitle().contains("Settings")) {
            event.setCancelled(true);

            if (event.getCurrentItem() == null || !event.getCurrentItem().hasItemMeta()) {
                return;
            }

            String itemName = ChatColor.stripColor(event.getCurrentItem().getItemMeta().getDisplayName());

            for (String arenaName : plugin.getArenaManager().getArenaNames()) {
                if (plugin.getPlayerManager().isInEditMode(arenaName, player)) {
                    ArenaData arena = plugin.getArenaManager().getArena(arenaName);

                    if (itemName.contains("Time Limit")) {
                        int currentTime = arena.getTimeLimit();
                        int newTime = currentTime + 30;
                        if (newTime > 600) {
                            newTime = 60;
                        }
                        arena.setTimeLimit(newTime);
                        player.sendMessage(ChatColor.GREEN + "Time limit set to " + newTime + " seconds");
                        player.closeInventory();
                        new ArenaGUI(plugin, arena).open(player);
                    } else if (itemName.contains("Barriers")) {
                        arena.setBarrierEnabled(!arena.isBarrierEnabled());
                        player.sendMessage(ChatColor.GREEN + "Barriers " +
                            (arena.isBarrierEnabled() ? "enabled" : "disabled"));
                        player.closeInventory();
                        new ArenaGUI(plugin, arena).open(player);
                    } else if (itemName.contains("Sounds")) {
                        arena.setSoundEnabled(!arena.isSoundEnabled());
                        player.sendMessage(ChatColor.GREEN + "Sounds " +
                            (arena.isSoundEnabled() ? "enabled" : "disabled"));
                        player.closeInventory();
                        new ArenaGUI(plugin, arena).open(player);
                    } else if (itemName.contains("Save")) {
                        plugin.getArenaManager().saveArena(arenaName);
                        plugin.getPlayerManager().exitEditMode(arenaName, player);
                        player.closeInventory();
                        player.sendMessage(ChatColor.GREEN + "Arena '" + arenaName + "' saved successfully!");
                    }

                    return;
                }
            }
        }
    }
}
