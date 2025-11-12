package org.tg.squidgame.listeners;

import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.tg.squidgame.TGSquidGame;
import org.tg.squidgame.data.GameState;
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

                if (plugin.getPlayerManager().getArenaPlayers(arenaName).isEmpty()) {
                    plugin.getArenaManager().stopGame(arenaName);
                }
            } else {
                plugin.getPlayerManager().removePlayer(player);
            }
        }
    }

    @EventHandler
    public void onEntityDamage(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof Player)) {
            return;
        }

        Player player = (Player) event.getEntity();
        String arenaName = plugin.getPlayerManager().getPlayerArena(player);

        if (arenaName != null) {
            event.setCancelled(true);
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
                    player.sendMessage(plugin.getMessagesManager().getMessage("spectator-bounds"));
                }
            }
            return;
        }

        RedLightGreenLight game = plugin.getArenaManager().getActiveGame(arenaName);
        if (game != null) {
            // Only check bounds for spectators and players not in active game
            GameState state = game.getGameState();
            if (state == GameState.WAITING || state == GameState.STARTING) {
                if (!arena.isInArenaBounds(player.getLocation())) {
                    event.setCancelled(true);
                    
                    if (arena.getLobby() != null) {
                        player.teleport(arena.getLobby());
                    }
                    
                    player.sendMessage(plugin.getMessagesManager().getMessage("left-arena"));
                    player.sendMessage(plugin.getMessagesManager().getMessage("teleported-back"));
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
                        cycleArenaSetting(arena, "timeLimit");
                        player.sendMessage(ChatColor.GREEN + "Time limit set to " + arena.getTimeLimit() + " seconds");
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
                    } else if (itemName.contains("Minimum Players")) {
                        cycleArenaSetting(arena, "minPlayers");
                        player.sendMessage(ChatColor.GREEN + "Minimum players set to " + arena.getMinPlayers());
                        player.closeInventory();
                        new ArenaGUI(plugin, arena).open(player);
                    } else if (itemName.contains("Auto-Start") || itemName.contains("Timer")) {
                        cycleArenaSetting(arena, "autoStart");
                        player.sendMessage(ChatColor.GREEN + "Auto-start delay set to " + arena.getAutoStartDelay() + " seconds");
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

    private void cycleArenaSetting(ArenaData arena, String setting) {
        switch (setting) {
            case "timeLimit":
                int[] timeLimits = {60, 120, 180, 240, 300, 600};
                int currentTime = arena.getTimeLimit();
                int nextTimeIndex = 0;
                for (int i = 0; i < timeLimits.length; i++) {
                    if (timeLimits[i] == currentTime) {
                        nextTimeIndex = (i + 1) % timeLimits.length;
                        break;
                    }
                }
                arena.setTimeLimit(timeLimits[nextTimeIndex]);
                break;

            case "minPlayers":
                int[] minPlayerValues = {1, 2, 3, 4, 5, 6, 8, 10};
                int currentMin = arena.getMinPlayers();
                int nextMinIndex = 0;
                for (int i = 0; i < minPlayerValues.length; i++) {
                    if (minPlayerValues[i] == currentMin) {
                        nextMinIndex = (i + 1) % minPlayerValues.length;
                        break;
                    }
                }
                arena.setMinPlayers(minPlayerValues[nextMinIndex]);
                break;

            case "autoStart":
                int[] autoStartValues = {5, 10, 15, 20, 30, 45, 60};
                int currentAutoStart = arena.getAutoStartDelay();
                int nextAutoStartIndex = 0;
                for (int i = 0; i < autoStartValues.length; i++) {
                    if (autoStartValues[i] == currentAutoStart) {
                        nextAutoStartIndex = (i + 1) % autoStartValues.length;
                        break;
                    }
                }
                arena.setAutoStartDelay(autoStartValues[nextAutoStartIndex]);
                break;
        }
    }
}
