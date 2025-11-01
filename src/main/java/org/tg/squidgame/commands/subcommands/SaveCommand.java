package org.tg.squidgame.commands.subcommands;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.tg.squidgame.TGSquidGame;

public class SaveCommand implements SubCommand {

    private final TGSquidGame plugin;

    public SaveCommand(TGSquidGame plugin) {
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
            player.sendMessage(ChatColor.RED + "Usage: /tgsg <arenaName> save");
            return true;
        }

        String arenaName = args[0];

        if (!plugin.getPlayerManager().isInEditMode(arenaName, player)) {
            player.sendMessage(ChatColor.RED + "You must be in edit mode to save arena changes.");
            player.sendMessage(ChatColor.GRAY + "Use /tgsg " + arenaName + " edit to enter edit mode first.");
            return true;
        }

        // Save all arena data including positions, lobby, spectator, etc.
        plugin.getArenaManager().saveArena(arenaName);
        plugin.getPlayerManager().exitEditMode(arenaName, player);
        player.closeInventory();
        player.sendMessage(ChatColor.GREEN + "💾 Arena saved successfully!");
        player.sendMessage(ChatColor.GRAY + "All positions, lobby, spectator spawn, and settings have been saved.");
        player.sendMessage(ChatColor.GRAY + "Edit mode disabled for arena '" + arenaName + "'");

        return true;
    }
}
