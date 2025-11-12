package org.tg.squidgame.commands.subcommands;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.tg.squidgame.TGSquidGame;
import org.tg.squidgame.data.ArenaData;
import org.tg.squidgame.games.RedLightGreenLight;

import java.util.HashMap;
import java.util.Map;

public class JoinCommand implements SubCommand {

    private final TGSquidGame plugin;

    public JoinCommand(TGSquidGame plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean execute(CommandSender sender, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(plugin.getMessagesManager().getMessage("only-players"));
            return true;
        }

        Player player = (Player) sender;

        if (!player.hasPermission("tgsg.user")) {
            sender.sendMessage(plugin.getMessagesManager().getMessage("no-permission"));
            return true;
        }

        if (args.length < 1) {
            sender.sendMessage(plugin.getMessagesManager().getMessage("usage-join"));
            return true;
        }

        String arenaName = args[0];
        ArenaData arena = plugin.getArenaManager().getArena(arenaName);

        if (arena == null) {
            Map<String, String> replacements = new HashMap<>();
            replacements.put("arena", arenaName);
            sender.sendMessage(plugin.getMessagesManager().getMessage("arena-not-found", replacements));
            return true;
        }

        if (!arena.isComplete()) {
            Map<String, String> replacements = new HashMap<>();
            replacements.put("arena", arenaName);
            sender.sendMessage(plugin.getMessagesManager().getMessage("arena-incomplete", replacements));
            return true;
        }

        String playerCurrentArena = plugin.getPlayerManager().getPlayerArena(player);
        if (playerCurrentArena != null) {
            Map<String, String> replacements = new HashMap<>();
            replacements.put("arena", playerCurrentArena);
            sender.sendMessage(plugin.getMessagesManager().getMessage("already-in-arena"));
            sender.sendMessage(ChatColor.YELLOW + "Finish or leave that game first using /tgsg leave");
            return true;
        }

        String spectatingArena = plugin.getPlayerManager().getSpectatingArena(player);
        if (spectatingArena != null) {
            Map<String, String> replacements = new HashMap<>();
            replacements.put("arena", spectatingArena);
            sender.sendMessage(plugin.getMessagesManager().getMessage("already-in-arena"));
            sender.sendMessage(ChatColor.YELLOW + "Leave that arena first using /tgsg leave");
            return true;
        }

        RedLightGreenLight game = plugin.getArenaManager().getActiveGame(arenaName);
        RedLightGreenLight waitingGame = plugin.getArenaManager().getWaitingGame(arenaName);

        if (game != null && game.getGameState() != org.tg.squidgame.data.GameState.WAITING) {
            sender.sendMessage(ChatColor.RED + "Game is already running in arena '" + arenaName + "'");
            sender.sendMessage(ChatColor.YELLOW + "Wait for the current game to finish.");
            return true;
        }

        plugin.getPlayerManager().addPlayer(arenaName, player);

        if (arena.getLobby() != null) {
            player.teleport(arena.getLobby());
        }

        Map<String, String> replacements = new HashMap<>();
        replacements.put("arena", arenaName);
        player.sendMessage(plugin.getMessagesManager().getMessage("join-success", replacements));
        player.sendMessage(plugin.getMessagesManager().getMessage("join-waiting"));

        RedLightGreenLight waitingGame = plugin.getArenaManager().getWaitingGame(arenaName);
        if (waitingGame == null) {
            waitingGame = new RedLightGreenLight(plugin, arena);
            plugin.getArenaManager().setWaitingGame(arenaName, waitingGame);
            waitingGame.startAutoStartTimer();
        } else {
            waitingGame.resetWaitingMessage();
        }

        return true;
    }
}
