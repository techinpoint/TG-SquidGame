package org.tg.squidgame.managers;

import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.tg.squidgame.TGSquidGame;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

public class MessagesManager {

    private final TGSquidGame plugin;
    private FileConfiguration messagesConfig;
    private File messagesFile;

    public MessagesManager(TGSquidGame plugin) {
        this.plugin = plugin;
        loadMessages();
    }

    public void loadMessages() {
        messagesFile = new File(plugin.getDataFolder(), "messages.yml");

        if (!messagesFile.exists()) {
            plugin.saveResource("messages.yml", false);
        }

        messagesConfig = YamlConfiguration.loadConfiguration(messagesFile);

        InputStream defaultStream = plugin.getResource("messages.yml");
        if (defaultStream != null) {
            YamlConfiguration defaultConfig = YamlConfiguration.loadConfiguration(new InputStreamReader(defaultStream));
            messagesConfig.setDefaults(defaultConfig);
        }
    }

    public void reloadMessages() {
        loadMessages();
    }

    public String getMessage(String path) {
        return getMessage(path, new HashMap<>());
    }

    public String getMessage(String path, Map<String, String> replacements) {
        return getMessage(path, replacements, false);
    }

    public String getMessage(String path, Map<String, String> replacements, boolean skipPrefix) {
        String message = messagesConfig.getString(path, "&c[Message '" + path + "' not found]");
        message = ChatColor.translateAlternateColorCodes('&', message);

        for (Map.Entry<String, String> entry : replacements.entrySet()) {
            message = message.replace("{" + entry.getKey() + "}", entry.getValue());
        }

        if (skipPrefix) {
            return message;
        }

        return message;
    }

    public String getRawMessage(String path) {
        return getRawMessage(path, new HashMap<>());
    }

    public String getRawMessage(String path, Map<String, String> replacements) {
        return getMessage(path, replacements, true);
    }

    public String getMessageWithPrefix(String path) {
        return getMessageWithPrefix(path, new HashMap<>());
    }

    public String getMessageWithPrefix(String path, Map<String, String> replacements) {
        String prefix = ChatColor.translateAlternateColorCodes('&', messagesConfig.getString("prefix", "&6[TG SquidGame]&r "));
        return prefix + getMessage(path, replacements);
    }

    public String getPositionSuffix(int position) {
        switch (position) {
            case 1: return getMessage("position-1st");
            case 2: return getMessage("position-2nd");
            case 3: return getMessage("position-3rd");
            case 4: return getMessage("position-4th");
            case 5: return getMessage("position-5th");
            default:
                Map<String, String> replacements = new HashMap<>();
                replacements.put("n", String.valueOf(position));
                return getMessage("position-nth", replacements);
        }
    }
}
