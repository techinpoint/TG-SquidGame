package org.tg.squidgame.commands.subcommands;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.tg.squidgame.TGSquidGame;

public class StopCommand implements SubCommand {

    private final TGSquidGame plugin;

    public StopCommand(TGSquidGame plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean execute(CommandSender sender, String[] args) {
        if (!sender.hasPermission("tgsg.admin")) {
            sender.sendMessage(ChatColor.RED + "You don't have permission to use this command.");
            return true;
        }

        if (args.length < 1) {
            sender.sendMessage(ChatColor.RED + "Usage: /tgsg <arenaName> stop");
            return true;
        }

        String arenaName = args[0];

        if (plugin.getArenaManager().getActiveGame(arenaName) == null) {
            sender.sendMessage(ChatColor.RED + "No game is running in arena '" + arenaName + "'");
            return true;
        }

        plugin.getArenaManager().stopGame(arenaName);
        sender.sendMessage(ChatColor.GREEN + "Game stopped in arena '" + arenaName + "'");

        return true;
    }
}
