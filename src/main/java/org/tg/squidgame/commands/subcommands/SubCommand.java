package org.tg.squidgame.commands.subcommands;

import org.bukkit.command.CommandSender;

public interface SubCommand {
    boolean execute(CommandSender sender, String[] args);
}