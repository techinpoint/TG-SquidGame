package org.tg.squidgame;

import org.bukkit.plugin.java.JavaPlugin;
import org.tg.squidgame.commands.TGSGCommand;
import org.tg.squidgame.listeners.PlayerEventListener;
import org.tg.squidgame.managers.ArenaManager;
import org.tg.squidgame.managers.ConfigManager;
import org.tg.squidgame.managers.PlayerManager;

public class TGSquidGame extends JavaPlugin {

    private static TGSquidGame instance;
    private ConfigManager configManager;
    private ArenaManager arenaManager;
    private PlayerManager playerManager;

    @Override
    public void onEnable() {
        instance = this;

        saveDefaultConfig();

        configManager = new ConfigManager(this);
        playerManager = new PlayerManager(this);
        arenaManager = new ArenaManager(this);

        configManager.loadMainConfig();
        arenaManager.loadAllArenas();

        registerCommands();
        registerListeners();

        getLogger().info("TG SquidGame v1.0 by Techinpoint Gamerz has been enabled!");
    }

    @Override
    public void onDisable() {
        if (arenaManager != null) {
            arenaManager.stopAllGames();
        }
        getLogger().info("TG SquidGame has been disabled!");
    }

    private void registerCommands() {
        TGSGCommand commandExecutor = new TGSGCommand(this);
        getCommand("tgsg").setExecutor(commandExecutor);
        getCommand("tgsg").setTabCompleter(commandExecutor);
    }

    private void registerListeners() {
        getServer().getPluginManager().registerEvents(new PlayerEventListener(this), this);
    }

    public void reload() {
        reloadConfig();
        configManager.loadMainConfig();
        arenaManager.reloadAllArenas();
    }

    public static TGSquidGame getInstance() {
        return instance;
    }

    public ConfigManager getConfigManager() {
        return configManager;
    }

    public ArenaManager getArenaManager() {
        return arenaManager;
    }

    public PlayerManager getPlayerManager() {
        return playerManager;
    }
}
