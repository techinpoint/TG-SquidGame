package org.tg.squidgame.managers;

import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.tg.squidgame.TGSquidGame;

import java.util.*;

public class PlayerManager {

    private final TGSquidGame plugin;
    private final Map<UUID, String> playerArenaMap;
    private final Map<String, Set<UUID>> arenaPlayersMap;
    private final Map<String, Set<UUID>> arenaSpectatorsMap;
    private final Map<String, Set<UUID>> arenaEditorsMap;
    private final Map<UUID, Location> originalLocations;
    private final Map<UUID, ItemStack[]> originalInventories;
    private final Map<UUID, ItemStack[]> originalArmor;

    public PlayerManager(TGSquidGame plugin) {
        this.plugin = plugin;
        this.playerArenaMap = new HashMap<>();
        this.arenaPlayersMap = new HashMap<>();
        this.arenaSpectatorsMap = new HashMap<>();
        this.arenaEditorsMap = new HashMap<>();
        this.originalLocations = new HashMap<>();
        this.originalInventories = new HashMap<>();
        this.originalArmor = new HashMap<>();
    }

    public void addPlayer(String arenaName, Player player) {
        UUID uuid = player.getUniqueId();
        
        // Store original location and inventory
        originalLocations.put(uuid, player.getLocation().clone());
        originalInventories.put(uuid, player.getInventory().getContents().clone());
        originalArmor.put(uuid, player.getInventory().getArmorContents().clone());
        
        // Clear inventory
        player.getInventory().clear();
        player.getInventory().setArmorContents(new ItemStack[4]);
        
        playerArenaMap.put(uuid, arenaName);
        arenaPlayersMap.computeIfAbsent(arenaName, k -> new HashSet<>()).add(uuid);
    }

    public void removePlayer(Player player) {
        UUID uuid = player.getUniqueId();
        String arenaName = playerArenaMap.remove(uuid);
        if (arenaName != null) {
            Set<UUID> players = arenaPlayersMap.get(arenaName);
            if (players != null) {
                players.remove(uuid);
            }
        }
        
        // Restore inventory and teleport to original location
        restorePlayerState(player);
    }

    public void addSpectator(String arenaName, Player player) {
        UUID uuid = player.getUniqueId();
        arenaSpectatorsMap.computeIfAbsent(arenaName, k -> new HashSet<>()).add(uuid);
        player.setGameMode(GameMode.SPECTATOR);
    }

    public void removeSpectator(String arenaName, Player player) {
        UUID uuid = player.getUniqueId();
        Set<UUID> spectators = arenaSpectatorsMap.get(arenaName);
        if (spectators != null) {
            spectators.remove(uuid);
        }
        
        // Restore player state when leaving spectator mode
        restorePlayerState(player);
    }

    public void clearArena(String arenaName) {
        Set<UUID> players = arenaPlayersMap.get(arenaName);
        if (players != null) {
            for (UUID uuid : new HashSet<>(players)) {
                Player player = plugin.getServer().getPlayer(uuid);
                if (player != null) {
                    restorePlayerState(player);
                }
                playerArenaMap.remove(uuid);
            }
            players.clear();
        }
        
        Set<UUID> spectators = arenaSpectatorsMap.get(arenaName);
        if (spectators != null) {
            for (UUID uuid : new HashSet<>(spectators)) {
                Player player = plugin.getServer().getPlayer(uuid);
                if (player != null) {
                    restorePlayerState(player);
                }
            }
            spectators.clear();
        }
    }

    private void restorePlayerState(Player player) {
        UUID uuid = player.getUniqueId();
        
        // Restore inventory
        ItemStack[] inventory = originalInventories.remove(uuid);
        if (inventory != null) {
            player.getInventory().setContents(inventory);
        }
        
        ItemStack[] armor = originalArmor.remove(uuid);
        if (armor != null) {
            player.getInventory().setArmorContents(armor);
        }
        
        // Restore location
        Location originalLocation = originalLocations.remove(uuid);
        if (originalLocation != null) {
            player.teleport(originalLocation);
        }
        
        // Reset game mode
        player.setGameMode(GameMode.SURVIVAL);
    }

    public String getPlayerArena(Player player) {
        return playerArenaMap.get(player.getUniqueId());
    }

    public Set<UUID> getArenaPlayers(String arenaName) {
        return arenaPlayersMap.getOrDefault(arenaName, new HashSet<>());
    }

    public Set<UUID> getArenaSpectators(String arenaName) {
        return arenaSpectatorsMap.getOrDefault(arenaName, new HashSet<>());
    }

    public boolean isPlayerInArena(Player player) {
        return playerArenaMap.containsKey(player.getUniqueId());
    }

    public boolean isPlayerSpectating(String arenaName, Player player) {
        Set<UUID> spectators = arenaSpectatorsMap.get(arenaName);
        return spectators != null && spectators.contains(player.getUniqueId());
    }

    public void enterEditMode(String arenaName, Player player) {
        UUID uuid = player.getUniqueId();
        arenaEditorsMap.computeIfAbsent(arenaName, k -> new HashSet<>()).add(uuid);
    }

    public void exitEditMode(String arenaName, Player player) {
        UUID uuid = player.getUniqueId();
        Set<UUID> editors = arenaEditorsMap.get(arenaName);
        if (editors != null) {
            editors.remove(uuid);
        }
    }

    public boolean isInEditMode(String arenaName, Player player) {
        Set<UUID> editors = arenaEditorsMap.get(arenaName);
        return editors != null && editors.contains(player.getUniqueId());
    }
}
