package org.tg.squidgame;

import org.bukkit.plugin.java.JavaPlugin;
import org.tg.squidgame.commands.TGSGCommand;
import org.tg.squidgame.listeners.PlayerEventListener;
import org.tg.squidgame.managers.ArenaManager;
import org.tg.squidgame.managers.ConfigManager;
import org.tg.squidgame.managers.MessagesManager;
import org.tg.squidgame.managers.PlayerManager;

public class TGSquidGame extends JavaPlugin {

    private static TGSquidGame instance;
    private ConfigManager configManager;
    private MessagesManager messagesManager;
    private ArenaManager arenaManager;
    private PlayerManager playerManager;
    private org.tg.squidgame.listeners.GUIListener guiListener;

    @Override
    public void onEnable() {
        instance = this;

        saveDefaultConfig();

        configManager = new ConfigManager(this);
        messagesManager = new MessagesManager(this);
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
        guiListener = new org.tg.squidgame.listeners.GUIListener(this);
        getServer().getPluginManager().registerEvents(guiListener, this);
    }

    public void reload() {
        reloadConfig();
        configManager.loadMainConfig();
        messagesManager.reloadMessages();
        arenaManager.reloadAllArenas();
    }

    public static TGSquidGame getInstance() {
        return instance;
    }

    public ConfigManager getConfigManager() {
        return configManager;
    }

    public MessagesManager getMessagesManager() {
        return messagesManager;
    }

    public ArenaManager getArenaManager() {
        return arenaManager;
    }

    public PlayerManager getPlayerManager() {
        return playerManager;
    }

    public org.tg.squidgame.listeners.GUIListener getGuiListener() {
        return guiListener;
    }
}
