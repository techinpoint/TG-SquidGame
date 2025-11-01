package org.tg.squidgame.commands.subcommands;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.tg.squidgame.TGSquidGame;
import org.tg.squidgame.data.ArenaData;

public class LobbyCommand implements SubCommand {

    private final TGSquidGame plugin;

    public LobbyCommand(TGSquidGame plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean execute(CommandSender sender, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "Only players can use this command.");
            return true;
        }

        Player player = (Player) sender;

        if (args.length < 1) {
            player.sendMessage(ChatColor.RED + "Usage: /tgsg <arenaName> lobby");
            return true;
        }

        String arenaName = args[0];
        ArenaData arena = plugin.getArenaManager().getArena(arenaName);

        if (arena == null) {
            player.sendMessage(ChatColor.RED + "Arena '" + arenaName + "' not found.");
            return true;
        }

        if (arena.getLobby() == null) {
            player.sendMessage(ChatColor.RED + "Lobby location not set for arena '" + arenaName + "'.");
            return true;
        }

        player.teleport(arena.getLobby());
        player.sendMessage(ChatColor.GREEN + "✅ Teleported to lobby of arena '" + arenaName + "'");

        return true;
    }
}
