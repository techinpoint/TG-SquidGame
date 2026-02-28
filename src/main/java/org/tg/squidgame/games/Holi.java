package org.tg.squidgame.games;

import org.bukkit.*;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.tg.squidgame.TGSquidGame;
import org.tg.squidgame.data.ArenaData;
import org.tg.squidgame.data.GameState;

import java.util.*;

public class Holi {

    private final TGSquidGame plugin;
    private final ArenaData arena;
    private GameState gameState;
    private BossBar bossBar;
    private final Map<UUID, Integer> holiScores;
    private final Map<UUID, Long> pichkariCooldowns;
    private BukkitTask gameTask;
    private int timeRemaining;
    private int countdownTimer;

    public Holi(TGSquidGame plugin, ArenaData arena) {
        this.plugin = plugin;
        this.arena = arena;
        this.gameState = GameState.WAITING;
        this.holiScores = new HashMap<>();
        this.pichkariCooldowns = new HashMap<>();
    }

    public void start() {
        if (gameState != GameState.WAITING && gameState != GameState.STOPPED) {
            return;
        }

        gameState = GameState.STARTING;
        timeRemaining = getHoliDuration();
        countdownTimer = arena.getStartCountdown();

        bossBar = Bukkit.createBossBar(
                ChatColor.translateAlternateColorCodes('&', getHoliBossBarText()),
                getHoliBossBarColor(),
                getHoliBossBarStyle()
        );

        Set<UUID> players = plugin.getPlayerManager().getArenaPlayers(arena.getName());
        if (players.isEmpty()) {
            stop();
            return;
        }

        for (UUID uuid : players) {
            Player player = Bukkit.getPlayer(uuid);
            if (player != null) {
                if (arena.getLobby() != null) {
                    player.teleport(arena.getLobby());
                }
                bossBar.addPlayer(player);
                player.setGameMode(GameMode.ADVENTURE);
                holiScores.put(uuid, 0);
            }
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
                    bossBar.setTitle(ChatColor.translateAlternateColorCodes('&', 
                        "&e&lStarting in &f&l" + countdownTimer + "&e&l..."));
                    broadcastMessage(ChatColor.translateAlternateColorCodes('&', 
                        "&d&lHOLI FESTIVAL &r&estarting in &f" + countdownTimer + "&e seconds!"));

                    if (countdownTimer <= 5) {
                        playSound(Sound.BLOCK_NOTE_BLOCK_PLING, 2.0f);
                    }

                    countdownTimer--;
                } else {
                    gameState = GameState.RUNNING;
                    bossBar.setTitle(ChatColor.translateAlternateColorCodes('&', getHoliBossBarText()));
                    broadcastMessage(ChatColor.translateAlternateColorCodes('&', getHoliStartMessage()));
                    playSound(Sound.ENTITY_PLAYER_LEVELUP, 1.0f);
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

        if (bossBar != null) {
            bossBar.removeAll();
            bossBar = null;
        }

        Set<UUID> allPlayers = new HashSet<>(plugin.getPlayerManager().getArenaPlayers(arena.getName()));
        allPlayers.addAll(plugin.getPlayerManager().getArenaSpectators(arena.getName()));

        for (UUID uuid : allPlayers) {
            Player player = Bukkit.getPlayer(uuid);
            if (player != null) {
                player.setGameMode(GameMode.SURVIVAL);
                player.getInventory().remove(Material.BLAZE_ROD);
                player.setGlowing(false);
            }
        }

        plugin.getPlayerManager().clearArena(arena.getName());
        holiScores.clear();
        pichkariCooldowns.clear();
        gameState = GameState.STOPPED;
    }

    public void removePlayer(Player player) {
        if (bossBar != null) {
            bossBar.removePlayer(player);
        }
        player.setGlowing(false);
        player.getInventory().remove(Material.BLAZE_ROD);
        holiScores.remove(player.getUniqueId());
        pichkariCooldowns.remove(player.getUniqueId());
    }

    private void startGameLoop() {
        gameTask = new BukkitRunnable() {
            private int tickCounter = 0;

            @Override
            public void run() {
                if (gameState != GameState.RUNNING) {
                    cancel();
                    return;
                }

                tickCounter++;
                if (tickCounter % 20 == 0) {
                    timeRemaining--;
                    updateBossBar();

                    if (timeRemaining <= 0) {
                        endGame();
                        cancel();
                        return;
                    }

                    if (timeRemaining <= 5) {
                        showCountdownTitle(timeRemaining);
                    }
                }
            }
        }.runTaskTimer(plugin, 0L, 1L);
    }

    private void updateBossBar() {
        int duration = getHoliDuration();
        double progress = (double) timeRemaining / duration;
        bossBar.setProgress(Math.max(0.0, Math.min(1.0, progress)));
        bossBar.setTitle(ChatColor.translateAlternateColorCodes('&', 
            getHoliBossBarText().replace("{time}", formatTime(timeRemaining))));
    }

    private void showCountdownTitle(int seconds) {
        Set<UUID> players = plugin.getPlayerManager().getArenaPlayers(arena.getName());
        for (UUID uuid : players) {
            Player player = Bukkit.getPlayer(uuid);
            if (player != null) {
                player.sendTitle(ChatColor.RED + String.valueOf(seconds), "", 0, 20, 0);
            }
        }
        playSound(Sound.BLOCK_NOTE_BLOCK_PLING, 2.0f);
    }

    private void endGame() {
        if (gameState == GameState.ENDING || gameState == GameState.STOPPED) {
            return;
        }

        gameState = GameState.ENDING;
        broadcastMessage(ChatColor.translateAlternateColorCodes('&', getHoliEndMessage()));
        showLeaderboard();

        if (isFireworksEnabled()) {
            spawnFireworks();
        }

        new BukkitRunnable() {
            @Override
            public void run() {
                stop();
                plugin.getArenaManager().stopGame(arena.getName());
            }
        }.runTaskLater(plugin, 100L);
    }

    private void showLeaderboard() {
        List<Map.Entry<UUID, Integer>> sortedScores = new ArrayList<>(holiScores.entrySet());
        sortedScores.sort((a, b) -> b.getValue().compareTo(a.getValue()));

        broadcastMessage(ChatColor.translateAlternateColorCodes('&', "&d&m                    &r &6&lHOLI LEADERBOARD &d&m                    "));
        for (int i = 0; i < Math.min(5, sortedScores.size()); i++) {
            UUID uuid = sortedScores.get(i).getKey();
            Player player = Bukkit.getPlayer(uuid);
            if (player != null) {
                String medal = getMedal(i + 1);
                String position = getPositionSuffix(i + 1);
                broadcastMessage(ChatColor.translateAlternateColorCodes('&', 
                    medal + " &e" + position + " &f" + player.getName() + " &7- &b" + sortedScores.get(i).getValue() + " hits"));
                
                if (i == 0) {
                    player.sendTitle(ChatColor.translateAlternateColorCodes('&', "&6&l✦ CHAMPION ✦"), 
                        ChatColor.translateAlternateColorCodes('&', "&dYou colored the most players!"), 10, 60, 10);
                }
            }
        }
        broadcastMessage(ChatColor.translateAlternateColorCodes('&', "&d&m                                                              "));
    }

    private String getMedal(int position) {
        switch (position) {
            case 1: return "🥇";
            case 2: return "🥈";
            case 3: return "🥉";
            default: return "  ";
        }
    }

    private void givePichkari(Player player) {
        ItemStack pichkari = new ItemStack(Material.BLAZE_ROD);
        ItemMeta meta = pichkari.getItemMeta();
        meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', getPichkariName()));
        meta.setLore(Arrays.asList(
            ChatColor.translateAlternateColorCodes('&', "&7▸ &bRight-click to spray vibrant colors"),
            ChatColor.translateAlternateColorCodes('&', "&7▸ &eHit players to score points"),
            "",
            ChatColor.translateAlternateColorCodes('&', "&d✦ Festival Edition ✦")
        ));
        meta.setUnbreakable(true);
        pichkari.setItemMeta(meta);
        player.getInventory().addItem(pichkari);
    }

    public void handlePichkariUse(Player player) {
        if (gameState != GameState.RUNNING && gameState != GameState.WAITING) {
            player.sendMessage(ChatColor.RED + "The Holi game has ended!");
            return;
        }

        UUID uuid = player.getUniqueId();
        long currentTime = System.currentTimeMillis();
        long cooldown = getPichkariCooldown() * 1000L;

        if (pichkariCooldowns.containsKey(uuid)) {
            long lastUse = pichkariCooldowns.get(uuid);
            if (currentTime - lastUse < cooldown) {
                long remaining = (cooldown - (currentTime - lastUse)) / 1000;
                player.sendMessage(ChatColor.YELLOW + "Cooldown: " + remaining + "s");
                return;
            }
        }

        pichkariCooldowns.put(uuid, currentTime);
        sprayColor(player);
    }

    private void sprayColor(Player shooter) {
        Location eyeLocation = shooter.getEyeLocation();
        double range = getPichkariRange();
        
        Color particleColor = getRandomColor();
        Particle.DustOptions dustOptions = new Particle.DustOptions(particleColor, 1.5f);

        for (int i = 0; i < range * 2; i++) {
            Location particleLoc = eyeLocation.clone().add(eyeLocation.getDirection().multiply(i * 0.5));
            shooter.getWorld().spawnParticle(Particle.DUST, particleLoc, 5, 0.2, 0.2, 0.2, dustOptions);
        }

        Set<UUID> players = plugin.getPlayerManager().getArenaPlayers(arena.getName());
        for (UUID uuid : players) {
            if (uuid.equals(shooter.getUniqueId())) continue;
            
            Player target = Bukkit.getPlayer(uuid);
            if (target != null && target.getLocation().distance(shooter.getLocation()) <= range) {
                if (isLookingAt(shooter, target, range)) {
                    hitPlayer(shooter, target, particleColor);
                }
            }
        }

        playSound(Sound.ENTITY_SPLASH_POTION_BREAK, 1.0f);
    }

    private boolean isLookingAt(Player shooter, Player target, double range) {
        Location eye = shooter.getEyeLocation();
        Location targetLoc = target.getLocation().add(0, 1, 0);
        
        if (eye.distance(targetLoc) > range) return false;
        
        double dot = eye.getDirection().normalize().dot(targetLoc.subtract(eye).toVector().normalize());
        return dot > 0.9;
    }

    private void hitPlayer(Player shooter, Player target, Color color) {
        if (gameState == GameState.RUNNING) {
            holiScores.put(shooter.getUniqueId(), holiScores.getOrDefault(shooter.getUniqueId(), 0) + 1);
            shooter.sendMessage(ChatColor.GREEN + "✓ Hit! Score: " + holiScores.get(shooter.getUniqueId()));
        }
        
        target.sendTitle(ChatColor.translateAlternateColorCodes('&', "&d&lCOLORED!"), 
            ChatColor.translateAlternateColorCodes('&', "&7by " + shooter.getName()), 5, 30, 5);
        target.sendMessage(ChatColor.translateAlternateColorCodes('&', 
            getHoliHitMessage().replace("{player}", shooter.getName())));
        
        // Make target glow
        target.setGlowing(true);
        
        // Spawn initial particle burst
        Particle.DustOptions dustOptions = new Particle.DustOptions(color, 2.0f);
        target.getWorld().spawnParticle(Particle.DUST, target.getLocation().add(0, 1, 0), 50, 0.5, 0.5, 0.5, dustOptions);
        
        // Continuous particles for 10 seconds
        new BukkitRunnable() {
            int ticks = 0;
            @Override
            public void run() {
                if (ticks >= 200 || !target.isOnline() || gameState == GameState.STOPPED) {
                    cancel();
                    return;
                }
                target.getWorld().spawnParticle(Particle.DUST, target.getLocation().add(0, 1, 0), 3, 0.3, 0.5, 0.3, dustOptions);
                ticks += 5;
            }
        }.runTaskTimer(plugin, 0L, 5L);
        
        target.playSound(target.getLocation(), Sound.ENTITY_PLAYER_SPLASH, 1.0f, 1.0f);
    }

    private Color getRandomColor() {
        List<String> colors = getHoliColors();
        String colorName = colors.get(new Random().nextInt(colors.size()));
        
        switch (colorName.toUpperCase()) {
            case "RED": return Color.RED;
            case "BLUE": return Color.BLUE;
            case "GREEN": return Color.GREEN;
            case "YELLOW": return Color.YELLOW;
            case "PURPLE": return Color.PURPLE;
            case "ORANGE": return Color.ORANGE;
            case "PINK": return Color.FUCHSIA;
            default: return Color.RED;
        }
    }

    private void spawnFireworks() {
        Set<UUID> players = plugin.getPlayerManager().getArenaPlayers(arena.getName());
        for (UUID uuid : players) {
            Player player = Bukkit.getPlayer(uuid);
            if (player != null) {
                Location loc = player.getLocation().clone().add(0, 2, 0);
                player.getWorld().spawnParticle(Particle.FIREWORK, loc, 50, 1, 1, 1, 0.1);
            }
        }
    }

    private String formatTime(int seconds) {
        int minutes = seconds / 60;
        int secs = seconds % 60;
        return String.format("%d:%02d", minutes, secs);
    }

    private String getPositionSuffix(int position) {
        switch (position) {
            case 1: return "1st";
            case 2: return "2nd";
            case 3: return "3rd";
            default: return position + "th";
        }
    }

    private void playSound(Sound sound, float pitch) {
        Set<UUID> allPlayers = new HashSet<>(plugin.getPlayerManager().getArenaPlayers(arena.getName()));
        for (UUID uuid : allPlayers) {
            Player player = Bukkit.getPlayer(uuid);
            if (player != null) {
                player.playSound(player.getLocation(), sound, 1.0f, pitch);
            }
        }
    }

    private void broadcastMessage(String message) {
        Set<UUID> allPlayers = new HashSet<>(plugin.getPlayerManager().getArenaPlayers(arena.getName()));
        for (UUID uuid : allPlayers) {
            Player player = Bukkit.getPlayer(uuid);
            if (player != null) {
                player.sendMessage(message);
            }
        }
    }

    // Config getters
    private int getHoliDuration() {
        return arena.getTimeLimit();
    }

    private int getPichkariCooldown() {
        return plugin.getConfig().getInt("holi.pichkari.cooldown", 3);
    }

    private double getPichkariRange() {
        return plugin.getConfig().getDouble("holi.pichkari.range", 10.0);
    }

    private String getPichkariName() {
        return plugin.getConfig().getString("holi.pichkari.name", "&d&lHoli Pichkari");
    }

    private List<String> getHoliColors() {
        return plugin.getConfig().getStringList("holi.colors");
    }

    private String getHoliBossBarText() {
        return plugin.getConfig().getString("holi.bossbar.text", "&d&lHOLI FESTIVAL &r&7| &b{time} remaining");
    }

    private BarColor getHoliBossBarColor() {
        String color = plugin.getConfig().getString("holi.bossbar.color", "BLUE");
        try {
            return BarColor.valueOf(color.toUpperCase());
        } catch (IllegalArgumentException e) {
            return BarColor.BLUE;
        }
    }

    private BarStyle getHoliBossBarStyle() {
        String style = plugin.getConfig().getString("holi.bossbar.style", "SOLID");
        try {
            return BarStyle.valueOf(style.toUpperCase());
        } catch (IllegalArgumentException e) {
            return BarStyle.SOLID;
        }
    }

    private String getHoliStartMessage() {
        return plugin.getConfig().getString("holi.messages.start", "&d&l🎨 HOLI FESTIVAL BEGINS! &r&bSpray colors and score points!");
    }

    private String getHoliEndMessage() {
        return plugin.getConfig().getString("holi.messages.end", "&e&l🏁 HOLI FESTIVAL ENDED! &r&7Thanks for playing!");
    }

    private String getHoliHitMessage() {
        return plugin.getConfig().getString("holi.messages.hit", "&d💦 {player} &bsplashed you with vibrant colors!");
    }

    private boolean isFireworksEnabled() {
        return plugin.getConfig().getBoolean("holi.fireworks", true);
    }

    public GameState getGameState() {
        return gameState;
    }

    public ArenaData getArena() {
        return arena;
    }

    public Map<UUID, Integer> getHoliScores() {
        return holiScores;
    }
}
