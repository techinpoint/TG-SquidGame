package org.tg.squidgame.commands.subcommands;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.tg.squidgame.TGSquidGame;
import org.tg.squidgame.data.ArenaData;
import org.tg.squidgame.games.RedLightGreenLight;

public class JoinCommand implements SubCommand {

    private final TGSquidGame plugin;

    public JoinCommand(TGSquidGame plugin) {
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
            player.sendMessage(ChatColor.RED + "Usage: /tgsg <arenaName> join");
            return true;
        }

        String arenaName = args[0];
        ArenaData arena = plugin.getArenaManager().getArena(arenaName);

        if (arena == null) {
            player.sendMessage(ChatColor.RED + "Arena '" + arenaName + "' not found.");
            return true;
        }

        if (!arena.isComplete()) {
            player.sendMessage(ChatColor.RED + "Arena '" + arenaName + "' is not ready yet.");
            return true;
        }

        if (plugin.getPlayerManager().isPlayerInArena(player)) {
            player.sendMessage(ChatColor.RED + "You are already in an arena!");
            return true;
        }

        RedLightGreenLight game = plugin.getArenaManager().getActiveGame(arenaName);
        if (game != null) {
            player.sendMessage(ChatColor.RED + "Game is already running in this arena!");
            return true;
        }

        plugin.getPlayerManager().addPlayer(arenaName, player);

        if (arena.getLobby() != null) {
            player.teleport(arena.getLobby());
        }

        player.sendMessage(ChatColor.GREEN + "You joined arena '" + arenaName + "'");
        player.sendMessage(ChatColor.GRAY + "Waiting for game to start...");

        RedLightGreenLight waitingGame = plugin.getArenaManager().getWaitingGame(arenaName);
        if (waitingGame == null) {
            waitingGame = new RedLightGreenLight(plugin, arena);
            plugin.getArenaManager().setWaitingGame(arenaName, waitingGame);
            waitingGame.startAutoStartTimer();
        }

        return true;
    }
}
