package org.tg.squidgame.commands.subcommands;

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
            sender.sendMessage(plugin.getMessagesManager().getMessage("no-permission"));
            return true;
        }

        plugin.reload();
        sender.sendMessage(plugin.getMessagesManager().getMessage("config-reloaded"));

        return true;
    }
}
