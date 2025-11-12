package org.tg.squidgame.commands.subcommands;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.tg.squidgame.TGSquidGame;

public class DeleteCommand implements SubCommand {

    private final TGSquidGame plugin;

    public DeleteCommand(TGSquidGame plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean execute(CommandSender sender, String[] args) {
        if (!sender.hasPermission("tgsg.admin")) {
            sender.sendMessage(ChatColor.RED + "You don't have permission to use this command.");
            return true;
        }

        if (args.length < 1) {
            sender.sendMessage(ChatColor.RED + "Usage: /tgsg delete <arenaName>");
            return true;
        }

        String arenaName = args[0];

        if (plugin.getArenaManager().deleteArena(arenaName)) {
            sender.sendMessage(ChatColor.GREEN + "Arena '" + arenaName + "' deleted successfully!");
        } else {
            sender.sendMessage(ChatColor.RED + "Failed to delete arena. It may not exist.");
        }

        return true;
    }
}
