package org.tg.squidgame.games;

import org.bukkit.*;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.tg.squidgame.TGSquidGame;
import org.tg.squidgame.data.ArenaData;
import org.tg.squidgame.data.GameState;
import org.tg.squidgame.data.LightState;
import org.tg.squidgame.utils.BarrierManager;

import java.util.*;

public class RedLightGreenLight {

    private final TGSquidGame plugin;
    private final ArenaData arena;
    private GameState gameState;
    private LightState lightState;
    private BossBar bossBar;
    private final Map<UUID, Location> lastPositions;
    private BukkitTask gameTask;
    private int timeRemaining;

    public RedLightGreenLight(TGSquidGame plugin, ArenaData arena) {
        this.plugin = plugin;
        this.arena = arena;
        this.gameState = GameState.WAITING;
        this.lightState = LightState.GREEN;
        this.lastPositions = new HashMap<>();
    }

    public void start() {
        if (gameState != GameState.WAITING && gameState != GameState.STOPPED) {
            return;
        }

        gameState = GameState.STARTING;
        timeRemaining = arena.getTimeLimit();

        bossBar = Bukkit.createBossBar(
            "🟢 GREEN LIGHT - GO!",
            BarColor.GREEN,
            BarStyle.SOLID
        );

        Set<UUID> players = plugin.getPlayerManager().getArenaPl ayers(arena.getName());
        if (players.isEmpty()) {
            plugin.getLogger().warning("No players in arena " + arena.getName());
            stop();
            return;
        }

        for (UUID uuid : players) {
            Player player = Bukkit.getPlayer(uuid);
            if (player != null) {
                teleportToStart(player);
                bossBar.addPlayer(player);
                player.setGameMode(GameMode.ADVENTURE);
                lastPositions.put(uuid, player.getLocation().clone());
            }
        }

        if (arena.isBarrierEnabled()) {
            BarrierManager.createBarriers(arena);
        }

        gameState = GameState.RUNNING;
        startGameLoop();
    }

    public void stop() {
        if (gameTask != null) {
            gameTask.cancel();
            gameTask = null;
        }

        if (bossBar != null) {
            bossBar.removeAll();
            bossBar = null;
        }

        if (arena.isBarrierEnabled()) {
            BarrierManager.removeBarriers(arena);
        }

        Set<UUID> allPlayers = new HashSet<>(plugin.getPlayerManager().getArenaPl ayers(arena.getName()));
        allPlayers.addAll(plugin.getPlayerManager().getArenaSpectators(arena.getName()));

        for (UUID uuid : allPlayers) {
            Player player = Bukkit.getPlayer(uuid);
            if (player != null) {
                player.setGameMode(GameMode.ADVENTURE);
                if (arena.getLobby() != null) {
                    player.teleport(arena.getLobby());
                }
            }
        }

        plugin.getPlayerManager().clearArena(arena.getName());
        lastPositions.clear();
        gameState = GameState.STOPPED;
    }

    private void startGameLoop() {
        gameTask = new BukkitRunnable() {
            int phaseTicks = 0;
            int phaseLength = getRandomPhaseLength();

            @Override
            public void run() {
                if (gameState != GameState.RUNNING) {
                    cancel();
                    return;
                }

                timeRemaining--;
                if (timeRemaining <= 0) {
                    endGame(false);
                    cancel();
                    return;
                }

                Set<UUID> players = plugin.getPlayerManager().getArenaPl ayers(arena.getName());
                if (players.isEmpty()) {
                    endGame(false);
                    cancel();
                    return;
                }

                phaseTicks++;
                if (phaseTicks >= phaseLength) {
                    toggleLight();
                    phaseTicks = 0;
                    phaseLength = getRandomPhaseLength();
                }

                if (lightState == LightState.RED) {
                    checkPlayerMovement();
                }

                checkWinConditions();
            }
        }.runTaskTimer(plugin, 0L, 20L);
    }

    private void toggleLight() {
        if (lightState == LightState.GREEN) {
            lightState = LightState.RED;
            bossBar.setTitle("🔴 RED LIGHT - STOP!");
            bossBar.setColor(BarColor.RED);

            if (arena.isSoundEnabled()) {
                playSound(Sound.BLOCK_NOTE_BLOCK_BASS, 0.5f);
            }

            Set<UUID> players = plugin.getPlayerManager().getArenaPl ayers(arena.getName());
            for (UUID uuid : players) {
                Player player = Bukkit.getPlayer(uuid);
                if (player != null) {
                    lastPositions.put(uuid, player.getLocation().clone());
                }
            }
        } else {
            lightState = LightState.GREEN;
            bossBar.setTitle("🟢 GREEN LIGHT - GO!");
            bossBar.setColor(BarColor.GREEN);

            if (arena.isSoundEnabled()) {
                playSound(Sound.BLOCK_NOTE_BLOCK_CHIME, 1.0f);
            }
        }
    }

    private void checkPlayerMovement() {
        Set<UUID> players = new HashSet<>(plugin.getPlayerManager().getArenaPl ayers(arena.getName()));

        for (UUID uuid : players) {
            Player player = Bukkit.getPlayer(uuid);
            if (player == null) {
                continue;
            }

            Location lastPos = lastPositions.get(uuid);
            if (lastPos == null) {
                lastPos = player.getLocation();
                lastPositions.put(uuid, lastPos);
                continue;
            }

            double distance = player.getLocation().distance(lastPos);
            if (distance > 0.15) {
                eliminatePlayer(player);
            }
        }
    }

    private void checkWinConditions() {
        Set<UUID> players = new HashSet<>(plugin.getPlayerManager().getArenaPl ayers(arena.getName()));

        for (UUID uuid : players) {
            Player player = Bukkit.getPlayer(uuid);
            if (player != null && arena.isInWinZone(player.getLocation())) {
                declareWinner(player);
            }
        }
    }

    private void eliminatePlayer(Player player) {
        plugin.getPlayerManager().removePlayer(player);
        plugin.getPlayerManager().addSpectator(arena.getName(), player);

        if (arena.getSpectator() != null) {
            player.teleport(arena.getSpectator());
        }

        player.sendMessage(ChatColor.RED + "You moved during RED LIGHT! You are eliminated.");

        if (arena.isSoundEnabled()) {
            player.playSound(player.getLocation(), Sound.ENTITY_LIGHTNING_BOLT_THUNDER, 1.0f, 1.0f);
        }

        lastPositions.remove(player.getUniqueId());

        Set<UUID> remainingPlayers = plugin.getPlayerManager().getArenaPl ayers(arena.getName());
        if (remainingPlayers.isEmpty()) {
            endGame(false);
        }
    }

    private void declareWinner(Player player) {
        plugin.getPlayerManager().removePlayer(player);
        plugin.getPlayerManager().addSpectator(arena.getName(), player);

        if (arena.getSpectator() != null) {
            player.teleport(arena.getSpectator());
        }

        broadcastMessage(ChatColor.GOLD + "⭐ " + player.getName() + " has reached the finish line!");
        player.sendMessage(ChatColor.GREEN + "Congratulations! You won!");

        if (arena.isSoundEnabled()) {
            player.playSound(player.getLocation(), Sound.UI_TOAST_CHALLENGE_COMPLETE, 1.0f, 1.0f);
        }

        Set<UUID> remainingPlayers = plugin.getPlayerManager().getArenaPl ayers(arena.getName());
        if (remainingPlayers.isEmpty()) {
            endGame(true);
        }
    }

    private void endGame(boolean hadWinners) {
        gameState = GameState.ENDING;

        if (hadWinners) {
            broadcastMessage(ChatColor.GREEN + "Game Over! All winners have been crowned!");
        } else {
            broadcastMessage(ChatColor.RED + "Game Over! No winners this round.");
        }

        new BukkitRunnable() {
            @Override
            public void run() {
                stop();
            }
        }.runTaskLater(plugin, 100L);
    }

    private void teleportToStart(Player player) {
        if (arena.getStartPos1() == null || arena.getStartPos2() == null) {
            return;
        }

        Location start1 = arena.getStartPos1();
        Location start2 = arena.getStartPos2();

        double x = Math.min(start1.getX(), start2.getX()) + Math.random() * Math.abs(start1.getX() - start2.getX());
        double y = Math.min(start1.getY(), start2.getY());
        double z = Math.min(start1.getZ(), start2.getZ()) + Math.random() * Math.abs(start1.getZ() - start2.getZ());

        Location spawnLoc = new Location(start1.getWorld(), x, y, z);
        player.teleport(spawnLoc);
    }

    private int getRandomPhaseLength() {
        if ("complex".equals(arena.getRandomLogic())) {
            return 4 + new Random().nextInt(7);
        } else {
            return 5 + new Random().nextInt(6);
        }
    }

    private void playSound(Sound sound, float pitch) {
        Set<UUID> allPlayers = new HashSet<>(plugin.getPlayerManager().getArenaPl ayers(arena.getName()));
        allPlayers.addAll(plugin.getPlayerManager().getArenaSpectators(arena.getName()));

        for (UUID uuid : allPlayers) {
            Player player = Bukkit.getPlayer(uuid);
            if (player != null) {
                player.playSound(player.getLocation(), sound, 1.0f, pitch);
            }
        }
    }

    private void broadcastMessage(String message) {
        Set<UUID> allPlayers = new HashSet<>(plugin.getPlayerManager().getArenaPl ayers(arena.getName()));
        allPlayers.addAll(plugin.getPlayerManager().getArenaSpectators(arena.getName()));

        for (UUID uuid : allPlayers) {
            Player player = Bukkit.getPlayer(uuid);
            if (player != null) {
                player.sendMessage(message);
            }
        }
    }

    public GameState getGameState() {
        return gameState;
    }

    public LightState getLightState() {
        return lightState;
    }

    public ArenaData getArena() {
        return arena;
    }
}
