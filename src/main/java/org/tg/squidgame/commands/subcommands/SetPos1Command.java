package org.tg.squidgame.commands.subcommands;

import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.tg.squidgame.TGSquidGame;
import org.tg.squidgame.data.ArenaData;

import java.util.HashMap;
import java.util.Map;

public class SetPos1Command implements SubCommand {

    private final TGSquidGame plugin;

    public SetPos1Command(TGSquidGame plugin) {
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
            Map<String, String> replacements = new HashMap<>();
            replacements.put("command", "setpos1");
            sender.sendMessage(plugin.getMessagesManager().getMessage("usage-setpos", replacements));
            return true;
        }

        String arenaName = args[0];
        ArenaData arena = plugin.getArenaManager().getArena(arenaName);

        if (arena == null) {
            Map<String, String> replacements = new HashMap<>();
            replacements.put("arena", arenaName);
            sender.sendMessage(plugin.getMessagesManager().getMessage("arena-not-found", replacements));
            return true;
        }

        if (!plugin.getPlayerManager().isInEditMode(arenaName, player)) {
            Map<String, String> replacements = new HashMap<>();
            replacements.put("arena", arenaName);
            sender.sendMessage(plugin.getMessagesManager().getMessage("edit-mode-required"));
            sender.sendMessage(plugin.getMessagesManager().getMessage("edit-mode-use-command", replacements));
            return true;
        }

        Location loc = player.getLocation();
        arena.setPos1(loc);

        player.sendMessage(plugin.getMessagesManager().getMessage("pos1-set"));
        Map<String, String> replacements = new HashMap<>();
        replacements.put("x", String.valueOf(loc.getBlockX()));
        replacements.put("y", String.valueOf(loc.getBlockY()));
        replacements.put("z", String.valueOf(loc.getBlockZ()));
        player.sendMessage(plugin.getMessagesManager().getMessage("location-info", replacements));

        if (arena.isComplete()) {
            player.sendMessage(org.bukkit.ChatColor.YELLOW + "Arena setup completed successfully! All positions are now set.");
        }

        return true;
    }
}
