package org.tg.squidgame.commands.subcommands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.tg.squidgame.TGSquidGame;

public class CreateCommand implements SubCommand {

    private final TGSquidGame plugin;

    public CreateCommand(TGSquidGame plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean execute(CommandSender sender, String[] args) {
        if (!sender.hasPermission("tgsg.admin")) {
            sender.sendMessage(ChatColor.RED + "You don't have permission to use this command.");
            return true;
        }

        if (args.length < 2) {
            sender.sendMessage(ChatColor.RED + "Usage: /tgsg create <name> <type>");
            sender.sendMessage(ChatColor.GRAY + "Types: RedLightGreenLight");
            return true;
        }

        String arenaName = args[0];
        String arenaType = args[1];

        if (!arenaType.equals("RedLightGreenLight")) {
            sender.sendMessage(ChatColor.RED + "Invalid type. Available: RedLightGreenLight");
            return true;
        }

        String worldName = sender instanceof Player ? ((Player) sender).getWorld().getName() : "world";

        if (plugin.getArenaManager().createArena(arenaName, arenaType, worldName)) {
            sender.sendMessage(ChatColor.GREEN + "Arena '" + arenaName + "' created successfully!");
            sender.sendMessage(ChatColor.YELLOW + "Configure it with: /tgsg " + arenaName + " setpos1, setpos2, etc.");
        } else {
            sender.sendMessage(ChatColor.RED + "Failed to create arena. It may already exist.");
        }

        return true;
    }
}
