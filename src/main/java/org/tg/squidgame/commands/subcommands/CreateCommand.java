package org.tg.squidgame.commands.subcommands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.tg.squidgame.TGSquidGame;

import java.util.HashMap;
import java.util.Map;

public class CreateCommand implements SubCommand {

    private final TGSquidGame plugin;

    public CreateCommand(TGSquidGame plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean execute(CommandSender sender, String[] args) {
        if (!sender.hasPermission("tgsg.admin")) {
            sender.sendMessage(plugin.getMessagesManager().getMessage("no-permission"));
            return true;
        }

        if (args.length < 2) {
            sender.sendMessage(plugin.getMessagesManager().getMessage("usage-create"));
            sender.sendMessage(plugin.getMessagesManager().getMessage("usage-create-types"));
            return true;
        }

        String arenaName = args[0];
        String arenaType = args[1];

        if (!arenaType.equals("RedLightGreenLight")) {
            sender.sendMessage(plugin.getMessagesManager().getMessage("create-invalid-type"));
            return true;
        }

        String worldName = sender instanceof Player ? ((Player) sender).getWorld().getName() : "world";

        if (plugin.getArenaManager().createArena(arenaName, arenaType, worldName)) {
            Map<String, String> replacements = new HashMap<>();
            replacements.put("arena", arenaName);
            sender.sendMessage(plugin.getMessagesManager().getMessage("arena-created", replacements));
            sender.sendMessage(plugin.getMessagesManager().getMessage("create-configure-hint", replacements));
        } else {
            sender.sendMessage(plugin.getMessagesManager().getMessage("create-failed"));
        }

        return true;
    }
}
