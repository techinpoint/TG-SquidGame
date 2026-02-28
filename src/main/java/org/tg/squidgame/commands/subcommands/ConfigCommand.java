package org.tg.squidgame.commands.subcommands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.tg.squidgame.TGSquidGame;
import org.tg.squidgame.data.ArenaData;
import org.tg.squidgame.gui.ArenaGUI;

import java.util.HashMap;
import java.util.Map;

public class ConfigCommand implements SubCommand {

    private final TGSquidGame plugin;

    public ConfigCommand(TGSquidGame plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean execute(CommandSender sender, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(plugin.getMessagesManager().getMessage("only-players"));
            return true;
        }

        Player player = (Player) sender;

        if (!player.hasPermission("tgsg.admin")) {
            player.sendMessage(plugin.getMessagesManager().getMessage("no-permission"));
            return true;
        }

        if (args.length < 1) {
            player.sendMessage(plugin.getMessagesManager().getMessage("usage-config"));
            return true;
        }

        String arenaName = args[0];
        ArenaData arena = plugin.getArenaManager().getArena(arenaName);

        if (arena == null) {
            Map<String, String> replacements = new HashMap<>();
            replacements.put("arena", arenaName);
            player.sendMessage(plugin.getMessagesManager().getMessage("arena-not-found", replacements));
            return true;
        }

        ArenaGUI gui = new ArenaGUI(plugin, arena);
        gui.open(player);
        
        Map<String, String> replacements = new HashMap<>();
        replacements.put("arena", arenaName);
        player.sendMessage(plugin.getMessagesManager().getMessage("gui-opened", replacements));

        return true;
    }
}
