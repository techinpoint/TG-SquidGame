package org.tg.squidgame.commands.subcommands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.tg.squidgame.TGSquidGame;
import org.tg.squidgame.data.ArenaData;

import java.util.HashMap;
import java.util.Map;

public class EditCommand implements SubCommand {

    private final TGSquidGame plugin;

    public EditCommand(TGSquidGame plugin) {
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
            player.sendMessage(plugin.getMessagesManager().getMessage("no-permission"));
            return true;
        }

        if (args.length < 1) {
            player.sendMessage(plugin.getMessagesManager().getMessage("usage-edit"));
            return true;
        }

        String arenaName = args[0];
        ArenaData arena = plugin.getArenaManager().getArena(arenaName);

        if (arena == null) {
            Map<String, String> replacements = new HashMap<>();
            replacements.put("arena", arenaName);
            player.sendMessage(plugin.getMessagesManager().getMessage("arena-not-found", replacements));
            return true;
        }

        Map<String, String> replacements = new HashMap<>();
        replacements.put("arena", arenaName);

        if (plugin.getPlayerManager().isInEditMode(arenaName, player)) {
            plugin.getPlayerManager().exitEditMode(arenaName, player);
            player.sendMessage(plugin.getMessagesManager().getMessage("edit-mode-disabled", replacements));
            player.sendMessage(plugin.getMessagesManager().getMessage("edit-mode-not-saved"));
        } else {
            plugin.getPlayerManager().enterEditMode(arenaName, player);
            player.sendMessage(plugin.getMessagesManager().getMessage("edit-mode-enabled", replacements));
            player.sendMessage(plugin.getMessagesManager().getMessage("edit-mode-enabled-info"));
            player.sendMessage(plugin.getMessagesManager().getMessage("edit-mode-save-reminder", replacements));
        }

        return true;
    }
}
