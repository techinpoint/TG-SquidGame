package org.tg.squidgame.commands.subcommands;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.tg.squidgame.TGSquidGame;

public class CancelCommand implements SubCommand {

    private final TGSquidGame plugin;

    public CancelCommand(TGSquidGame plugin) {
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
            player.sendMessage(ChatColor.RED + "Usage: /tgsg <arenaName> cancel");
            return true;
        }

        String arenaName = args[0];

        if (!plugin.getPlayerManager().isInEditMode(arenaName, player)) {
            player.sendMessage(ChatColor.RED + "You are not in edit mode for this arena.");
            return true;
        }

        plugin.getArenaManager().reloadArena(arenaName);
        plugin.getPlayerManager().exitEditMode(arenaName, player);
        player.closeInventory();
        player.sendMessage(ChatColor.YELLOW + "❌ Edit mode cancelled");
        player.sendMessage(ChatColor.GRAY + "Changes were not saved and arena data has been reloaded");

        return true;
    }
}
