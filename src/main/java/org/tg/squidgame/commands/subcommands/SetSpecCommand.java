package org.tg.squidgame.commands.subcommands;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.tg.squidgame.TGSquidGame;
import org.tg.squidgame.data.ArenaData;

public class SetSpecCommand implements SubCommand {

    private final TGSquidGame plugin;

    public SetSpecCommand(TGSquidGame plugin) {
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
            player.sendMessage(ChatColor.RED + "Usage: /tgsg <arenaName> setspec");
            return true;
        }

        String arenaName = args[0];
        ArenaData arena = plugin.getArenaManager().getArena(arenaName);

        if (arena == null) {
            player.sendMessage(ChatColor.RED + "Arena '" + arenaName + "' not found.");
            return true;
        }

        if (!plugin.getPlayerManager().isInEditMode(arenaName, player)) {
            player.sendMessage(ChatColor.RED + "⚠️ Edit mode required for this command");
            player.sendMessage(ChatColor.GRAY + "Use /tgsg " + arenaName + " edit to enable edit mode");
            return true;
        }

        arena.setSpectator(player.getLocation());
        player.sendMessage(ChatColor.GREEN + "Spectator position set for arena '" + arenaName + "'");

        if (arena.isComplete()) {
            player.sendMessage(ChatColor.YELLOW + "Arena setup completed successfully! All positions are now set.");
        }

        return true;
    }
}
