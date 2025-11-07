package org.tg.squidgame.commands.subcommands;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.tg.squidgame.TGSquidGame;
import org.tg.squidgame.data.ArenaData;

public class SetPos2Command implements SubCommand {

    private final TGSquidGame plugin;

    public SetPos2Command(TGSquidGame plugin) {
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
            java.util.Map<String, String> replacements = new java.util.HashMap<>();
            replacements.put("command", "setpos2");
            sender.sendMessage(plugin.getMessagesManager().getMessage("usage-setpos", replacements));
            return true;
        }

        String arenaName = args[0];
        ArenaData arena = plugin.getArenaManager().getArena(arenaName);

        if (arena == null) {
            java.util.Map<String, String> replacements = new java.util.HashMap<>();
            replacements.put("arena", arenaName);
            sender.sendMessage(plugin.getMessagesManager().getMessage("arena-not-found", replacements));
            return true;
        }

        if (!plugin.getPlayerManager().isInEditMode(arenaName, player)) {
            java.util.Map<String, String> replacements = new java.util.HashMap<>();
            replacements.put("arena", arenaName);
            sender.sendMessage(plugin.getMessagesManager().getMessage("edit-mode-required"));
            sender.sendMessage(plugin.getMessagesManager().getMessage("edit-mode-use-command", replacements));
            return true;
        }

        org.bukkit.Location loc = player.getLocation();
        arena.setPos2(loc);

        player.sendMessage(plugin.getMessagesManager().getMessage("pos2-set"));
        java.util.Map<String, String> replacements = new java.util.HashMap<>();
        replacements.put("x", String.valueOf(loc.getBlockX()));
        replacements.put("y", String.valueOf(loc.getBlockY()));
        replacements.put("z", String.valueOf(loc.getBlockZ()));
        player.sendMessage(plugin.getMessagesManager().getMessage("location-info", replacements));

        if (arena.isComplete()) {
            player.sendMessage(ChatColor.YELLOW + "Arena setup completed successfully! All positions are now set.");
        }

        return true;
    }
}