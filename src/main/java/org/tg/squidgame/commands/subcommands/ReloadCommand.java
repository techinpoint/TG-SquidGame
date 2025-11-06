package org.tg.squidgame.commands.subcommands;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.tg.squidgame.TGSquidGame;

public class ReloadCommand implements SubCommand {

    private final TGSquidGame plugin;

    public ReloadCommand(TGSquidGame plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean execute(CommandSender sender, String[] args) {
        if (!sender.hasPermission("tgsg.admin")) {
            sender.sendMessage(ChatColor.RED + "You don't have permission to use this command.");
            return true;
        }

        plugin.reload();
        sender.sendMessage(ChatColor.GREEN + "TG SquidGame configuration reloaded!");

        return true;
    }
}
