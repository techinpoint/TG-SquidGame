package org.tg.squidgame.commands.subcommands;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.tg.squidgame.TGSquidGame;
import org.tg.squidgame.data.ArenaData;

public class DisableBarrierCommand implements SubCommand {

    private final TGSquidGame plugin;

    public DisableBarrierCommand(TGSquidGame plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean execute(CommandSender sender, String[] args) {
        if (!sender.hasPermission("tgsg.admin")) {
            sender.sendMessage(ChatColor.RED + "You don't have permission to use this command.");
            return true;
        }

        if (args.length < 1) {
            sender.sendMessage(ChatColor.RED + "Usage: /tgsg <arenaName> disablebarrier");
            return true;
        }

        String arenaName = args[0];
        ArenaData arena = plugin.getArenaManager().getArena(arenaName);

        if (arena == null) {
            sender.sendMessage(ChatColor.RED + "Arena '" + arenaName + "' not found.");
            return true;
        }

        arena.setBarrierEnabled(false);
        sender.sendMessage(ChatColor.GREEN + "Barriers disabled for arena '" + arenaName + "'");
        sender.sendMessage(ChatColor.GRAY + "Don't forget to save with: /tgsg " + arenaName + " save");

        return true;
    }
}
