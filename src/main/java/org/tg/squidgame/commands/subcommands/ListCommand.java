package org.tg.squidgame.commands.subcommands;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.tg.squidgame.TGSquidGame;

public class ListCommand implements SubCommand {

    private final TGSquidGame plugin;

    public ListCommand(TGSquidGame plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean execute(CommandSender sender, String[] args) {
        if (!sender.hasPermission("tgsg.admin")) {
            sender.sendMessage(ChatColor.RED + "You don't have permission to use this command.");
            return true;
        }

        var arenas = plugin.getArenaManager().getArenaNames();

        if (arenas.isEmpty()) {
            sender.sendMessage(ChatColor.YELLOW + "No arenas found. Create one with /tgsg create");
            return true;
        }

        sender.sendMessage(ChatColor.GOLD + "===== TG SquidGame Arenas =====");
        for (String arenaName : arenas) {
            var arena = plugin.getArenaManager().getArena(arenaName);
            String status = arena.isComplete() ? ChatColor.GREEN + "Ready" : ChatColor.RED + "Incomplete";
            sender.sendMessage(ChatColor.YELLOW + "- " + arenaName + ChatColor.GRAY + " (" + arena.getType() + ") " + status);
        }

        return true;
    }
}
