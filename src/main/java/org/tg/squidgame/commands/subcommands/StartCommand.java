package org.tg.squidgame.commands.subcommands;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.tg.squidgame.TGSquidGame;
import org.tg.squidgame.data.ArenaData;

public class StartCommand implements SubCommand {

    private final TGSquidGame plugin;

    public StartCommand(TGSquidGame plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean execute(CommandSender sender, String[] args) {
        if (!sender.hasPermission("tgsg.admin")) {
            sender.sendMessage(ChatColor.RED + "You don't have permission to use this command.");
            return true;
        }

        if (args.length < 1) {
            sender.sendMessage(ChatColor.RED + "Usage: /tgsg <arenaName> start");
            return true;
        }

        String arenaName = args[0];
        ArenaData arena = plugin.getArenaManager().getArena(arenaName);

        if (arena == null) {
            sender.sendMessage(ChatColor.RED + "Arena '" + arenaName + "' not found.");
            return true;
        }

        if (!arena.isComplete()) {
            sender.sendMessage(ChatColor.RED + "❌ Arena '" + arenaName + "' is not fully configured.");
            sender.sendMessage(ChatColor.GRAY + "Missing positions. Required: pos1, pos2, start1, start2, win1, win2, lobby, spectator");
            sender.sendMessage(ChatColor.YELLOW + "Use /tgsg " + arenaName + " edit to configure the arena");
            return true;
        }

        if (plugin.getArenaManager().isGameRunning(arenaName)) {
            sender.sendMessage(ChatColor.RED + "Game is already running in arena '" + arenaName + "'");
            return true;
        }

        plugin.getArenaManager().startGame(arenaName);
        sender.sendMessage(ChatColor.GREEN + "Game started in arena '" + arenaName + "'");

        return true;
    }
}
