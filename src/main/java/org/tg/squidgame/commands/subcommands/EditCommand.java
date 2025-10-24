package org.tg.squidgame.commands.subcommands;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.tg.squidgame.TGSquidGame;
import org.tg.squidgame.data.ArenaData;
import org.tg.squidgame.gui.ArenaGUI;

public class EditCommand implements SubCommand {

    private final TGSquidGame plugin;

    public EditCommand(TGSquidGame plugin) {
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
            player.sendMessage(ChatColor.RED + "Usage: /tgsg <arenaName> edit");
            return true;
        }

        String arenaName = args[0];
        ArenaData arena = plugin.getArenaManager().getArena(arenaName);

        if (arena == null) {
            player.sendMessage(ChatColor.RED + "Arena '" + arenaName + "' not found.");
            return true;
        }

        plugin.getPlayerManager().enterEditMode(arenaName, player);
        ArenaGUI gui = new ArenaGUI(plugin, arena);
        gui.open(player);
        player.sendMessage(ChatColor.GREEN + "Opened edit GUI for arena '" + arenaName + "'");

        return true;
    }
}
