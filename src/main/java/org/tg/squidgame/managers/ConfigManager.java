package org.tg.squidgame.managers;

import org.bukkit.configuration.file.FileConfiguration;
import org.tg.squidgame.TGSquidGame;

public class ConfigManager {

    private final TGSquidGame plugin;
    private String prefix;
    private int defaultTimeLimit;
    private boolean enableJoinMessage;
    private boolean soundEffects;
    private String defaultBossBarColor;
    private boolean useComplexRandomLogic;

    public ConfigManager(TGSquidGame plugin) {
        this.plugin = plugin;
    }

    public void loadMainConfig() {
        FileConfiguration config = plugin.getConfig();

        prefix = config.getString("settings.prefix", "&6[TG SquidGame]&r ");
        defaultTimeLimit = config.getInt("settings.defaultTimeLimit", 180);
        enableJoinMessage = config.getBoolean("settings.enableJoinMessage", true);
        soundEffects = config.getBoolean("settings.soundEffects", true);
        defaultBossBarColor = config.getString("settings.defaultBossBarColor", "GREEN");
        useComplexRandomLogic = config.getBoolean("settings.useComplexRandomLogic", true);
    }

    public String getPrefix() {
        return prefix;
    }

    public int getDefaultTimeLimit() {
        return defaultTimeLimit;
    }

    public boolean isEnableJoinMessage() {
        return enableJoinMessage;
    }

    public boolean isSoundEffects() {
        return soundEffects;
    }

    public String getDefaultBossBarColor() {
        return defaultBossBarColor;
    }

    public boolean isUseComplexRandomLogic() {
        return useComplexRandomLogic;
    }
}
