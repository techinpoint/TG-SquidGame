package org.tg.squidgame.commands.subcommands;

import org.bukkit.command.CommandSender;
import org.tg.squidgame.TGSquidGame;

import java.util.HashMap;
import java.util.Map;

public class ListCommand implements SubCommand {

    private final TGSquidGame plugin;

    public ListCommand(TGSquidGame plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean execute(CommandSender sender, String[] args) {
        if (!sender.hasPermission("tgsg.user") && !sender.hasPermission("tgsg.admin")) {
            sender.sendMessage(plugin.getMessagesManager().getMessage("no-permission"));
            return true;
        }

        java.util.Set<String> arenas = plugin.getArenaManager().getArenaNames();

        if (arenas.isEmpty()) {
            sender.sendMessage(plugin.getMessagesManager().getMessage("list-empty"));
            return true;
        }

        sender.sendMessage(plugin.getMessagesManager().getMessage("list-header"));
        for (String arenaName : arenas) {
            org.tg.squidgame.data.ArenaData arena = plugin.getArenaManager().getArena(arenaName);
            String status = arena.isComplete() ?
                plugin.getMessagesManager().getRawMessage("list-status-ready") :
                plugin.getMessagesManager().getRawMessage("list-status-incomplete");

            Map<String, String> replacements = new HashMap<>();
            replacements.put("arena", arenaName);
            replacements.put("type", arena.getType());
            replacements.put("status", status);
            sender.sendMessage(plugin.getMessagesManager().getMessage("list-entry", replacements));
        }

        return true;
    }
}
