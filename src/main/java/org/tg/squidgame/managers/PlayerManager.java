package org.tg.squidgame.managers;

import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.tg.squidgame.TGSquidGame;

import java.util.*;

public class PlayerManager {

    private final TGSquidGame plugin;
    private final Map<UUID, String> playerArenaMap;
    private final Map<String, Set<UUID>> arenaPlayersMap;
    private final Map<String, Set<UUID>> arenaSpectatorsMap;
    private final Map<String, Set<UUID>> arenaEditorsMap;

    public PlayerManager(TGSquidGame plugin) {
        this.plugin = plugin;
        this.playerArenaMap = new HashMap<>();
        this.arenaPlayersMap = new HashMap<>();
        this.arenaSpectatorsMap = new HashMap<>();
        this.arenaEditorsMap = new HashMap<>();
    }

    public void addPlayer(String arenaName, Player player) {
        UUID uuid = player.getUniqueId();
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
    }

    public void clearArena(String arenaName) {
        Set<UUID> players = arenaPlayersMap.get(arenaName);
        if (players != null) {
            players.forEach(playerArenaMap::remove);
            players.clear();
        }
        Set<UUID> spectators = arenaSpectatorsMap.get(arenaName);
        if (spectators != null) {
            spectators.clear();
        }
    }

    public String getPlayerArena(Player player) {
        return playerArenaMap.get(player.getUniqueId());
    }

    public Set<UUID> getArenaPl ayers(String arenaName) {
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
