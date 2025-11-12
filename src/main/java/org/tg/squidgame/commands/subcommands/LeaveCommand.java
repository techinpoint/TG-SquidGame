package org.tg.squidgame.commands.subcommands;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.tg.squidgame.TGSquidGame;

public class LeaveCommand implements SubCommand {

    private final TGSquidGame plugin;

    public LeaveCommand(TGSquidGame plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean execute(CommandSender sender, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "Only players can use this command.");
            return true;
        }

        Player player = (Player) sender;
        
        String arenaName = plugin.getPlayerManager().getPlayerArena(player);
        if (arenaName != null) {
            plugin.getPlayerManager().removePlayer(player);
            player.sendMessage(ChatColor.GREEN + "You have left the game.");
            return true;
        }
        
        String spectatingArena = plugin.getPlayerManager().getSpectatingArena(player);
        if (spectatingArena != null) {
            plugin.getPlayerManager().removeSpectator(spectatingArena, player);
            player.sendMessage(ChatColor.GREEN + "You have left the arena.");
            return true;
        }
        
        player.sendMessage(ChatColor.RED + "You are not in any game.");
        return true;
    }
}
