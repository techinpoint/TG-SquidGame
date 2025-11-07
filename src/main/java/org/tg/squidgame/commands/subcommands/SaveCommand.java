package org.tg.squidgame.commands.subcommands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.tg.squidgame.TGSquidGame;

import java.util.HashMap;
import java.util.Map;

public class SaveCommand implements SubCommand {

    private final TGSquidGame plugin;

    public SaveCommand(TGSquidGame plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean execute(CommandSender sender, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(plugin.getMessagesManager().getMessage("only-players"));
            return true;
        }

        Player player = (Player) sender;

        if (!player.hasPermission("tgsg.admin")) {
            sender.sendMessage(plugin.getMessagesManager().getMessage("no-permission"));
            return true;
        }

        if (args.length < 1) {
            sender.sendMessage(plugin.getMessagesManager().getMessage("usage-save"));
            return true;
        }

        String arenaName = args[0];

        if (!plugin.getArenaManager().arenaExists(arenaName)) {
            Map<String, String> replacements = new HashMap<>();
            replacements.put("arena", arenaName);
            sender.sendMessage(plugin.getMessagesManager().getMessage("arena-not-found", replacements));
            return true;
        }

        if (!plugin.getPlayerManager().isInEditMode(arenaName, player)) {
            Map<String, String> replacements = new HashMap<>();
            replacements.put("arena", arenaName);
            sender.sendMessage(plugin.getMessagesManager().getMessage("must-be-in-edit-mode"));
            sender.sendMessage(plugin.getMessagesManager().getMessage("use-edit-first", replacements));
            return true;
        }

        plugin.getArenaManager().saveArena(arenaName);
        plugin.getPlayerManager().exitEditMode(arenaName, player);
        player.closeInventory();
        
        Map<String, String> replacements = new HashMap<>();
        replacements.put("arena", arenaName);
        player.sendMessage(plugin.getMessagesManager().getMessage("arena-saved", replacements));

        return true;
    }
}
