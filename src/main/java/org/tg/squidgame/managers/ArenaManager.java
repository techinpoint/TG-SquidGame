package org.tg.squidgame.managers;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.tg.squidgame.TGSquidGame;
import org.tg.squidgame.data.ArenaData;
import org.tg.squidgame.games.RedLightGreenLight;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class ArenaManager {

    private final TGSquidGame plugin;
    private final Map<String, ArenaData> arenas;
    private final Map<String, RedLightGreenLight> activeGames;
    private final Map<String, RedLightGreenLight> waitingGames;
    private final Map<String, FileConfiguration> gameTemplates;
    private final File arenasFolder;
    private final File gamesFolder;

    public ArenaManager(TGSquidGame plugin) {
        this.plugin = plugin;
        this.arenas = new HashMap<>();
        this.activeGames = new HashMap<>();
        this.waitingGames = new HashMap<>();
        this.gameTemplates = new HashMap<>();
        this.arenasFolder = new File(plugin.getDataFolder(), "arenas");
        this.gamesFolder = new File(plugin.getDataFolder(), "games");
        if (!arenasFolder.exists()) {
            arenasFolder.mkdirs();
        }
        if (!gamesFolder.exists()) {
            gamesFolder.mkdirs();
        }
        loadGameTemplates();
    }
    
    private void loadGameTemplates() {
        copyDefaultGameTemplates();
        
        File[] templateFiles = gamesFolder.listFiles((dir, name) -> name.endsWith(".yml"));
        if (templateFiles != null && templateFiles.length > 0) {
            for (File templateFile : templateFiles) {
                try {
                    String gameName = templateFile.getName().replace(".yml", "");
                    FileConfiguration templateConfig = YamlConfiguration.loadConfiguration(templateFile);
                    gameTemplates.put(gameName, templateConfig);
                    plugin.getLogger().info("Loaded game template: " + gameName);
                } catch (Exception e) {
                    plugin.getLogger().warning("Failed to load game template: " + templateFile.getName());
                    e.printStackTrace();
                }
            }
        } else {
            plugin.getLogger().warning("No game templates found in games folder.");
        }
    }
    
    private void copyDefaultGameTemplates() {
        String[] defaultTemplates = {"RedLightGreenLight.yml"};
        
        for (String templateName : defaultTemplates) {
            File templateFile = new File(gamesFolder, templateName);
            if (!templateFile.exists()) {
                try {
                    plugin.saveResource("games/" + templateName, false);
                    plugin.getLogger().info("Created default game template: " + templateName);
                } catch (Exception e) {
                    plugin.getLogger().warning("Failed to copy default game template: " + templateName);
                }
            }
        }
    }
    
    private FileConfiguration getGameTemplate(String gameType) {
        return gameTemplates.get(gameType);
    }

    public void loadAllArenas() {
        File[] files = arenasFolder.listFiles((dir, name) -> name.endsWith(".yml"));
        if (files == null || files.length == 0) {
            plugin.getLogger().info("No arena files found. Create arenas using /tgsg create");
            return;
        }

        for (File file : files) {
            loadArena(file);
        }

        plugin.getLogger().info("Loaded " + arenas.size() + " arena(s)");
    }

    private void loadArena(File file) {
        FileConfiguration config = YamlConfiguration.loadConfiguration(file);
        String name = config.getString("arena.name");
        String type = config.getString("arena.type");
        String worldName = config.getString("arena.world");

        if (name == null || type == null || worldName == null) {
            plugin.getLogger().warning("Invalid arena file: " + file.getName());
            return;
        }

        World world = Bukkit.getWorld(worldName);
        if (world == null) {
            plugin.getLogger().warning("World '" + worldName + "' not found for arena: " + name);
            return;
        }

        FileConfiguration gameTemplate = getGameTemplate(type);

        if (gameTemplate != null) {
            if (!config.contains("arena.barrierEnabled")) {
                config.set("arena.barrierEnabled", gameTemplate.getBoolean("defaults.barrierEnabled", true));
            }
            if (!config.contains("arena.timeLimit")) {
                config.set("arena.timeLimit", gameTemplate.getInt("defaults.timeLimit", 180));
            }
            if (!config.contains("arena.startCountdown")) {
                config.set("arena.startCountdown", gameTemplate.getInt("defaults.startCountdown", 5));
            }
            if (!config.contains("arena.randomLogic")) {
                config.set("arena.randomLogic", gameTemplate.getString("defaults.randomLogic", "complex"));
            }
            if (!config.contains("arena.soundEnabled")) {
                config.set("arena.soundEnabled", gameTemplate.getBoolean("defaults.soundEnabled", true));
            }
            if (!config.contains("arena.minPlayers")) {
                config.set("arena.minPlayers", gameTemplate.getInt("defaults.minPlayers", 1));
            }
            if (!config.contains("arena.autoStartDelay")) {
                config.set("arena.autoStartDelay", gameTemplate.getInt("defaults.autoStartDelay", 10));
            }
        }

        ArenaData arena = new ArenaData(name, type, worldName);

        arena.setPos1(parseLocation(world, config.getString("arena.pos1")));
        arena.setPos2(parseLocation(world, config.getString("arena.pos2")));
        arena.setStartPos1(parseLocation(world, config.getString("arena.startPos1")));
        arena.setStartPos2(parseLocation(world, config.getString("arena.startPos2")));
        arena.setWinPos1(parseLocation(world, config.getString("arena.winPos1")));
        arena.setWinPos2(parseLocation(world, config.getString("arena.winPos2")));
        arena.setLobby(parseLocation(world, config.getString("arena.lobby")));
        arena.setSpectator(parseLocation(world, config.getString("arena.spectator")));
        arena.setBarrierEnabled(config.getBoolean("arena.barrierEnabled", true));
        arena.setTimeLimit(config.getInt("arena.timeLimit", 180));
        arena.setStartCountdown(config.getInt("arena.startCountdown", 5));
        arena.setRandomLogic(config.getString("arena.randomLogic", "complex"));
        arena.setSoundEnabled(config.getBoolean("arena.soundEnabled", true));
        arena.setMinPlayers(config.getInt("arena.minPlayers", 1));
        arena.setAutoStartDelay(config.getInt("arena.autoStartDelay", 10));
        arena.setGuiConfig(config);
        arena.setSaved(true); // Arena loaded from disk is considered saved

        arenas.put(name, arena);
    }

    public void reloadAllArenas() {
        stopAllGames();
        arenas.clear();
        loadAllArenas();
    }

    public void reloadArena(String arenaName) {
        ArenaData arena = arenas.get(arenaName);
        if (arena == null) {
            return;
        }

        File file = new File(arenasFolder, arenaName + ".yml");
        if (!file.exists()) {
            return;
        }

        arenas.remove(arenaName);
        loadArena(file);
    }

    public boolean createArena(String name, String type, String worldName) {
        if (arenas.containsKey(name)) {
            return false;
        }

        File file = new File(arenasFolder, name + ".yml");
        if (file.exists()) {
            return false;
        }

        FileConfiguration gameTemplate = getGameTemplate(type);
        FileConfiguration config = new YamlConfiguration();

        config.set("arena.name", name);
        config.set("arena.type", type);
        config.set("arena.world", worldName);
        config.set("arena.pos1", "0,0,0");
        config.set("arena.pos2", "0,0,0");
        config.set("arena.startPos1", "0,0,0");
        config.set("arena.startPos2", "0,0,0");
        config.set("arena.winPos1", "0,0,0");
        config.set("arena.winPos2", "0,0,0");
        config.set("arena.lobby", "0,0,0");
        config.set("arena.spectator", "0,0,0");

        if (gameTemplate != null) {
            config.set("arena.barrierEnabled", gameTemplate.getBoolean("defaults.barrierEnabled", true));
            config.set("arena.timeLimit", gameTemplate.getInt("defaults.timeLimit", 180));
            config.set("arena.startCountdown", gameTemplate.getInt("defaults.startCountdown", 5));
            config.set("arena.randomLogic", gameTemplate.getString("defaults.randomLogic", "complex"));
            config.set("arena.soundEnabled", gameTemplate.getBoolean("defaults.soundEnabled", true));
            config.set("arena.minPlayers", gameTemplate.getInt("defaults.minPlayers", 1));
            config.set("arena.autoStartDelay", gameTemplate.getInt("defaults.autoStartDelay", 10));
        } else {
            config.set("arena.barrierEnabled", true);
            config.set("arena.timeLimit", 180);
            config.set("arena.startCountdown", 5);
            config.set("arena.randomLogic", "complex");
            config.set("arena.soundEnabled", true);
            config.set("arena.minPlayers", 1);
            config.set("arena.autoStartDelay", 10);
        }

        config.set("gui.name", "&6" + name + " Settings");
        config.set("gui.size", 45);
        config.set("gui.items.timeLimit.slot", 11);
        config.set("gui.items.timeLimit.name", "&a⏳ Time Limit");
        config.set("gui.items.timeLimit.lore", java.util.Arrays.asList("&7Change round time"));
        config.set("gui.items.barrier.slot", 13);
        config.set("gui.items.barrier.name", "&c🚫 Barriers");
        config.set("gui.items.barrier.lore", java.util.Arrays.asList("&7Toggle arena barriers"));
        config.set("gui.items.sound.slot", 15);
        config.set("gui.items.sound.name", "&b🔊 Sounds");
        config.set("gui.items.sound.lore", java.util.Arrays.asList("&7Enable or disable sound effects"));
        config.set("gui.items.minPlayers.slot", 19);
        config.set("gui.items.minPlayers.name", "&d Minimum Players");
        config.set("gui.items.minPlayers.lore", java.util.Arrays.asList("&7Minimum players to start"));
        config.set("gui.items.autoStart.slot", 20);
        config.set("gui.items.autoStart.name", "&e Auto-Start Timer");
        config.set("gui.items.autoStart.lore", java.util.Arrays.asList("&7Seconds before auto-start"));
        config.set("gui.items.save.slot", 40);
        config.set("gui.items.save.name", "&a💾 Save Settings");
        config.set("gui.items.save.lore", java.util.Arrays.asList("&7Save GUI configuration changes"));
        config.set("gui.items.close.slot", 44);
        config.set("gui.items.close.name", "&c❌ Close");
        config.set("gui.items.close.lore", java.util.Arrays.asList("&7Close without saving"));

        try {
            config.save(file);
            ArenaData arena = new ArenaData(name, type, worldName);
            arena.setGuiConfig(config);
            arena.setSaved(false); // Arena needs to be configured and saved properly
            arenas.put(name, arena);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean deleteArena(String name) {
        if (!arenas.containsKey(name)) {
            return false;
        }

        stopGame(name);
        arenas.remove(name);

        File file = new File(arenasFolder, name + ".yml");
        return file.delete();
    }

    public void saveArena(String arenaName) {
        ArenaData arena = arenas.get(arenaName);
        if (arena == null) {
            return;
        }

        File file = new File(arenasFolder, arenaName + ".yml");
        FileConfiguration config = YamlConfiguration.loadConfiguration(file);

        config.set("arena.name", arena.getName());
        config.set("arena.type", arena.getType());
        config.set("arena.world", arena.getWorldName());
        config.set("arena.pos1", locationToString(arena.getPos1()));
        config.set("arena.pos2", locationToString(arena.getPos2()));
        config.set("arena.startPos1", locationToString(arena.getStartPos1()));
        config.set("arena.startPos2", locationToString(arena.getStartPos2()));
        config.set("arena.winPos1", locationToString(arena.getWinPos1()));
        config.set("arena.winPos2", locationToString(arena.getWinPos2()));
        config.set("arena.lobby", locationToString(arena.getLobby()));
        config.set("arena.spectator", locationToString(arena.getSpectator()));
        config.set("arena.barrierEnabled", arena.isBarrierEnabled());
        config.set("arena.timeLimit", arena.getTimeLimit());
        config.set("arena.startCountdown", arena.getStartCountdown());
        config.set("arena.randomLogic", arena.getRandomLogic());
        config.set("arena.soundEnabled", arena.isSoundEnabled());
        config.set("arena.minPlayers", arena.getMinPlayers());
        config.set("arena.autoStartDelay", arena.getAutoStartDelay());

        if (arena.getGuiConfig() != null) {
            FileConfiguration guiConfig = arena.getGuiConfig();
            if (guiConfig.contains("gui")) {
                config.set("gui", guiConfig.get("gui"));
            }
        }

        try {
            config.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void saveArenaConfig(String arenaName) {
        ArenaData arena = arenas.get(arenaName);
        if (arena == null) {
            return;
        }

        File file = new File(arenasFolder, arenaName + ".yml");
        FileConfiguration config = YamlConfiguration.loadConfiguration(file);

        config.set("arena.barrierEnabled", arena.isBarrierEnabled());
        config.set("arena.timeLimit", arena.getTimeLimit());
        config.set("arena.soundEnabled", arena.isSoundEnabled());
        config.set("arena.minPlayers", arena.getMinPlayers());
        config.set("arena.autoStartDelay", arena.getAutoStartDelay());

        if (arena.getGuiConfig() != null) {
            FileConfiguration guiConfig = arena.getGuiConfig();
            if (guiConfig.contains("gui")) {
                config.set("gui", guiConfig.get("gui"));
            }
        }

        try {
            config.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void startGame(String arenaName) {
        ArenaData arena = arenas.get(arenaName);
        if (arena == null || !arena.isComplete()) {
            return;
        }

        if (activeGames.containsKey(arenaName)) {
            return;
        }

        RedLightGreenLight waitingGame = waitingGames.remove(arenaName);
        if (waitingGame != null) {
            waitingGame.cancelAutoStart();
        }

        if ("RedLightGreenLight".equals(arena.getType())) {
            RedLightGreenLight game = waitingGame != null ? waitingGame : new RedLightGreenLight(plugin, arena);
            activeGames.put(arenaName, game);
            game.start();
        }
    }

    public void stopGame(String arenaName) {
        RedLightGreenLight game = activeGames.remove(arenaName);
        if (game != null) {
            game.stop();
        }

        RedLightGreenLight waitingGame = waitingGames.remove(arenaName);
        if (waitingGame != null) {
            waitingGame.cancelAutoStart();
        }
    }

    public void stopAllGames() {
        for (RedLightGreenLight game : activeGames.values()) {
            game.stop();
        }
        activeGames.clear();

        for (RedLightGreenLight game : waitingGames.values()) {
            game.cancelAutoStart();
        }
        waitingGames.clear();
    }

    public ArenaData getArena(String name) {
        return arenas.get(name);
    }

    public RedLightGreenLight getActiveGame(String arenaName) {
        return activeGames.get(arenaName);
    }

    public Set<String> getArenaNames() {
        return arenas.keySet();
    }

    public boolean isGameRunning(String arenaName) {
        return activeGames.containsKey(arenaName);
    }

    public boolean arenaExists(String name) {
        return arenas.containsKey(name);
    }

    public RedLightGreenLight getWaitingGame(String arenaName) {
        return waitingGames.get(arenaName);
    }

    public void setWaitingGame(String arenaName, RedLightGreenLight game) {
        waitingGames.put(arenaName, game);
    }

    private Location parseLocation(World world, String locString) {
        if (locString == null || locString.isEmpty()) {
            return null;
        }

        String[] parts = locString.split(",");
        if (parts.length < 3) {
            return null;
        }

        try {
            double x = Double.parseDouble(parts[0].trim());
            double y = Double.parseDouble(parts[1].trim());
            double z = Double.parseDouble(parts[2].trim());
            return new Location(world, x, y, z);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private String locationToString(Location loc) {
        if (loc == null) {
            return "0,0,0";
        }
        return loc.getBlockX() + "," + loc.getBlockY() + "," + loc.getBlockZ();
    }
}
