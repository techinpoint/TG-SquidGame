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
    private final Map<UUID, Location> joinPositions;
    private final List<String> leaderboard;
    private BukkitTask gameTask;
    private BukkitTask autoStartTask;
    private int timeRemaining;
    private int countdownTimer;
    private long redLightStartTime;
    private boolean redLightWarning;
    private static final int RED_LIGHT_GRACE_PERIOD_TICKS = 10;

    public RedLightGreenLight(TGSquidGame plugin, ArenaData arena) {
        this.plugin = plugin;
        this.arena = arena;
        this.gameState = GameState.WAITING;
        this.lightState = LightState.GREEN;
        this.lastPositions = new HashMap<>();
        this.joinPositions = new HashMap<>();
        this.leaderboard = new ArrayList<>();
        this.redLightWarning = false;
    }

    public void start() {
        if (gameState != GameState.WAITING && gameState != GameState.STOPPED) {
            return;
        }

        gameState = GameState.STARTING;
        timeRemaining = arena.getTimeLimit();
        countdownTimer = arena.getStartCountdown();

        bossBar = Bukkit.createBossBar(
            "⏰ Game starting in " + countdownTimer + " seconds...",
            BarColor.YELLOW,
            BarStyle.SOLID
        );

        Set<UUID> players = plugin.getPlayerManager().getArenaPlayers(arena.getName());
        if (players.isEmpty()) {
            plugin.getLogger().warning("No players in arena " + arena.getName());
            stop();
            return;
        }

        // Store join positions and teleport to lobby first
        for (UUID uuid : players) {
            Player player = Bukkit.getPlayer(uuid);
            if (player != null) {
                joinPositions.put(uuid, player.getLocation().clone());
                if (arena.getLobby() != null) {
                    player.teleport(arena.getLobby());
                }
                bossBar.addPlayer(player);
                player.setGameMode(GameMode.ADVENTURE);
            }
        }

        if (arena.isBarrierEnabled()) {
            BarrierManager.createBarriers(arena);
        }

        startCountdown();
    }

    private void startCountdown() {
        new BukkitRunnable() {
            @Override
            public void run() {
                if (gameState != GameState.STARTING) {
                    cancel();
                    return;
                }

                Set<UUID> players = plugin.getPlayerManager().getArenaPlayers(arena.getName());
                if (players.isEmpty()) {
                    stop();
                    cancel();
                    return;
                }

                if (countdownTimer > 0) {
                    bossBar.setTitle("⏰ Game starting in " + countdownTimer + " seconds...");
                    broadcastMessage(ChatColor.YELLOW + "Game starting in " + countdownTimer + " seconds!");

                    if (arena.isSoundEnabled()) {
                        if (countdownTimer <= 5) {
                            playSound(Sound.BLOCK_NOTE_BLOCK_PLING, 2.0f);
                        } else {
                            playSound(Sound.BLOCK_NOTE_BLOCK_PLING, 1.0f);
                        }
                    }

                    countdownTimer--;
                } else {
                    gameState = GameState.RUNNING;
                    bossBar.setTitle("🟢 GREEN LIGHT - GO!");
                    bossBar.setColor(BarColor.GREEN);

                    for (UUID uuid : players) {
                        Player player = Bukkit.getPlayer(uuid);
                        if (player != null) {
                            lastPositions.put(uuid, player.getLocation().clone());
                        }
                    }

                    broadcastMessage(ChatColor.GREEN + "Game Started! Reach the finish line!");
                    startGameLoop();
                    cancel();
                }
            }
        }.runTaskTimer(plugin, 0L, 20L);
    }

    public void stop() {
        if (gameTask != null) {
            gameTask.cancel();
            gameTask = null;
        }

        if (autoStartTask != null) {
            autoStartTask.cancel();
            autoStartTask = null;
        }

        if (bossBar != null) {
            bossBar.removeAll();
            bossBar = null;
        }

        if (arena.isBarrierEnabled()) {
            BarrierManager.removeBarriers(arena);
        }

        Set<UUID> allPlayers = new HashSet<>(plugin.getPlayerManager().getArenaPlayers(arena.getName()));
        allPlayers.addAll(plugin.getPlayerManager().getArenaSpectators(arena.getName()));

        for (UUID uuid : allPlayers) {
            Player player = Bukkit.getPlayer(uuid);
            if (player != null) {
                player.setGameMode(GameMode.SURVIVAL);
            }
        }

        plugin.getPlayerManager().clearArena(arena.getName());
        lastPositions.clear();
        joinPositions.clear();
        leaderboard.clear();
        gameState = GameState.STOPPED;
        countdownTimer = arena.getStartCountdown(); // Reset to arena-specific countdown
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

                Set<UUID> players = plugin.getPlayerManager().getArenaPlayers(arena.getName());
                if (players.isEmpty()) {
                    endGame(false);
                    cancel();
                    return;
                }

                phaseTicks++;
                
                // Check for red light warning (1 second before red light)
                if (lightState == LightState.GREEN && phaseTicks == phaseLength - 20 && !redLightWarning) {
                    redLightWarning = true;
                    bossBar.setTitle("⚠️ RED LIGHT COMING - GET READY TO STOP!");
                    bossBar.setColor(BarColor.RED);
                    if (arena.isSoundEnabled()) {
                        playSound(Sound.BLOCK_NOTE_BLOCK_BASS, 0.8f);
                    }
                }
                
                if (phaseTicks >= phaseLength) {
                    toggleLight();
                    phaseTicks = 0;
                    phaseLength = getRandomPhaseLength();
                    redLightWarning = false;
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
            redLightStartTime = System.currentTimeMillis();
            bossBar.setTitle("🔴 RED LIGHT - STOP!");
            bossBar.setColor(BarColor.RED);

            if (arena.isSoundEnabled()) {
                playSound(Sound.BLOCK_NOTE_BLOCK_BASS, 0.5f);
            }

            Set<UUID> players = plugin.getPlayerManager().getArenaPlayers(arena.getName());
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
        long currentTime = System.currentTimeMillis();
        long timeSinceRedLight = currentTime - redLightStartTime;
        
        if (timeSinceRedLight < 500) {
            return;
        }

        Set<UUID> players = new HashSet<>(plugin.getPlayerManager().getArenaPlayers(arena.getName()));

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

            // Only check movement if player is in the game area (not in start/win zones during red light)
            if (isInGameArea(player.getLocation())) {
                double distance = player.getLocation().distance(lastPos);
                if (distance > 0.15) {
                    eliminatePlayer(player);
                }
            }
        }
    }

    private boolean isInGameArea(Location location) {
        // Player is in game area if they're not in start zone or win zone
        return !arena.isInStartZone(location) && !arena.isInWinZone(location);
    }

    private void checkWinConditions() {
        Set<UUID> players = new HashSet<>(plugin.getPlayerManager().getArenaPlayers(arena.getName()));

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
        } else if (arena.getLobby() != null) {
            player.teleport(arena.getLobby());
        }

        player.sendMessage(ChatColor.RED + "💀 You moved during RED LIGHT! You are eliminated.");

        if (arena.isSoundEnabled()) {
            player.playSound(player.getLocation(), Sound.ENTITY_LIGHTNING_BOLT_THUNDER, 1.0f, 1.0f);
        }

        lastPositions.remove(player.getUniqueId());

        Set<UUID> remainingPlayers = plugin.getPlayerManager().getArenaPlayers(arena.getName());
        if (remainingPlayers.isEmpty()) {
            endGame(false);
        }
    }

    private void declareWinner(Player player) {
        leaderboard.add(player.getName());
        plugin.getPlayerManager().removePlayer(player);
        plugin.getPlayerManager().addSpectator(arena.getName(), player);

        if (arena.getSpectator() != null) {
            player.teleport(arena.getSpectator());
        } else if (arena.getLobby() != null) {
            player.teleport(arena.getLobby());
        }

        String position = getPositionSuffix(leaderboard.size());
        broadcastMessage(ChatColor.GOLD + "⭐ " + player.getName() + " finished " + position + "!");
        player.sendMessage(ChatColor.GREEN + "🎉 Congratulations! You finished " + position + "!");

        if (arena.isSoundEnabled()) {
            player.playSound(player.getLocation(), Sound.UI_TOAST_CHALLENGE_COMPLETE, 1.0f, 1.0f);
        }

        Set<UUID> remainingPlayers = plugin.getPlayerManager().getArenaPlayers(arena.getName());
        if (remainingPlayers.isEmpty()) {
            endGame(true);
        }
    }

    private void endGame(boolean hadWinners) {
        gameState = GameState.ENDING;

        if (hadWinners) {
            broadcastMessage(ChatColor.GREEN + "🏁 Game Over!");
            showLeaderboard();
        } else {
            broadcastMessage(ChatColor.RED + "⏰ Game Over! Time's up or no players remaining.");
        }

        new BukkitRunnable() {
            @Override
            public void run() {
                stop();
            }
        }.runTaskLater(plugin, 100L);
    }

    private void showLeaderboard() {
        if (leaderboard.isEmpty()) {
            return;
        }

        broadcastMessage(ChatColor.GOLD + "🏆 === LEADERBOARD ===");
        for (int i = 0; i < Math.min(5, leaderboard.size()); i++) {
            String position = getPositionSuffix(i + 1);
            broadcastMessage(ChatColor.YELLOW + position + " " + leaderboard.get(i));
        }
        broadcastMessage(ChatColor.GOLD + "==================");
    }

    private String getPositionSuffix(int position) {
        switch (position) {
            case 1: return "1st";
            case 2: return "2nd";
            case 3: return "3rd";
            case 4: return "4th";
            case 5: return "5th";
            default: return position + "th";
        }
    }

    private void teleportToStart(Player player) {
        if (arena.getStartPos1() != null) {
            player.teleport(arena.getStartPos1());
        }
    }

    private int getRandomPhaseLength() {
        if ("complex".equals(arena.getRandomLogic())) {
            return 4 + new Random().nextInt(7);
        } else {
            return 5 + new Random().nextInt(6);
        }
    }

    private void playSound(Sound sound, float pitch) {
        Set<UUID> allPlayers = new HashSet<>(plugin.getPlayerManager().getArenaPlayers(arena.getName()));
        allPlayers.addAll(plugin.getPlayerManager().getArenaSpectators(arena.getName()));

        for (UUID uuid : allPlayers) {
            Player player = Bukkit.getPlayer(uuid);
            if (player != null) {
                player.playSound(player.getLocation(), sound, 1.0f, pitch);
            }
        }
    }

    private void broadcastMessage(String message) {
        Set<UUID> allPlayers = new HashSet<>(plugin.getPlayerManager().getArenaPlayers(arena.getName()));
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

    public void startAutoStartTimer() {
        if (autoStartTask != null) {
            autoStartTask.cancel();
        }

        int delay = arena.getAutoStartDelay();
        autoStartTask = new BukkitRunnable() {
            int timeLeft = delay;

            @Override
            public void run() {
                Set<UUID> players = plugin.getPlayerManager().getArenaPlayers(arena.getName());

                if (players.size() < arena.getMinPlayers()) {
                    broadcastMessage(ChatColor.YELLOW + "Waiting for " + arena.getMinPlayers() + " player(s) to start...");
                    timeLeft = delay;
                    return;
                }

                if (timeLeft > 0) {
                    if (timeLeft <= 5 || timeLeft % 5 == 0) {
                        broadcastMessage(ChatColor.YELLOW + "Game starting in " + timeLeft + " seconds...");
                    }
                    timeLeft--;
                } else {
                    cancel();
                    autoStartTask = null;
                    start();
                }
            }
        }.runTaskTimer(plugin, 0L, 20L);
    }

    public void cancelAutoStart() {
        if (autoStartTask != null) {
            autoStartTask.cancel();
            autoStartTask = null;
        }
    }
}
