package org.tg.squidgame.commands.subcommands;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.tg.squidgame.TGSquidGame;
import org.tg.squidgame.data.ArenaData;

public class SetLobbyCommand implements SubCommand {

    private final TGSquidGame plugin;

    public SetLobbyCommand(TGSquidGame plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean execute(CommandSender sender, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "Only players can use this command.");
            return true;
        }

        Player player = (Player) sender;

        if (!player.hasPermission("tgsg.admin")) {
            player.sendMessage(ChatColor.RED + "You don't have permission to use this command.");
            return true;
        }

        if (args.length < 1) {
            player.sendMessage(ChatColor.RED + "Usage: /tgsg <arenaName> setlobby");
            return true;
        }

        String arenaName = args[0];
        ArenaData arena = plugin.getArenaManager().getArena(arenaName);

        if (arena == null) {
            player.sendMessage(ChatColor.RED + "Arena '" + arenaName + "' not found.");
            return true;
        }

        arena.setLobby(player.getLocation());
        player.sendMessage(ChatColor.GREEN + "Lobby position set for arena '" + arenaName + "'");
        player.sendMessage(ChatColor.GRAY + "Don't forget to save with: /tgsg " + arenaName + " save");

        return true;
    }
}
