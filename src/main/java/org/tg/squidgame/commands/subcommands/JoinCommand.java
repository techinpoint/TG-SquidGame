package org.tg.squidgame.commands.subcommands;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.tg.squidgame.TGSquidGame;
import org.tg.squidgame.data.ArenaData;
import org.tg.squidgame.games.Holi;
import org.tg.squidgame.games.RedLightGreenLight;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class JoinCommand implements SubCommand {

    private final TGSquidGame plugin;

    public JoinCommand(TGSquidGame plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean execute(CommandSender sender, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(plugin.getMessagesManager().getMessage("only-players"));
            return true;
        }

        Player player = (Player) sender;

        if (!player.hasPermission("tgsg.user")) {
            sender.sendMessage(plugin.getMessagesManager().getMessage("no-permission"));
            return true;
        }

        if (args.length < 1) {
            sender.sendMessage(plugin.getMessagesManager().getMessage("usage-join"));
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

        if (!arena.isComplete()) {
            Map<String, String> replacements = new HashMap<>();
            replacements.put("arena", arenaName);
            sender.sendMessage(plugin.getMessagesManager().getMessage("arena-incomplete", replacements));
            return true;
        }

        if (plugin.getPlayerManager().isPlayerInArena(arenaName, player)) {
            sender.sendMessage(ChatColor.RED + "You are already in arena '" + arenaName + "'");
            return true;
        }

        plugin.getPlayerManager().addPlayer(arenaName, player);

        if (arena.getLobby() != null) {
            player.teleport(arena.getLobby());
        }

        Map<String, String> replacements = new HashMap<>();
        replacements.put("arena", arenaName);
        player.sendMessage(plugin.getMessagesManager().getMessage("join-success", replacements));

        // Handle different game types
        if ("Holi".equalsIgnoreCase(arena.getType())) {
            giveHoliItems(player);
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', 
                "&d&l✦ &r&bYou received your &d&lHoli Pichkari&b! Get ready to spray colors!"));
            
            // Auto-start logic for Holi
            if (!plugin.getArenaManager().isGameRunning(arenaName)) {
                int playerCount = plugin.getPlayerManager().getArenaPlayers(arenaName).size();
                if (playerCount >= arena.getMinPlayers()) {
                    player.sendMessage(ChatColor.GREEN + "Minimum players reached! Game starting soon...");
                    new org.bukkit.scheduler.BukkitRunnable() {
                        @Override
                        public void run() {
                            if (!plugin.getArenaManager().isGameRunning(arenaName)) {
                                plugin.getArenaManager().startGame(arenaName);
                            }
                        }
                    }.runTaskLater(plugin, arena.getAutoStartDelay() * 20L);
                }
            }
        } else if ("RedLightGreenLight".equalsIgnoreCase(arena.getType())) {
            RedLightGreenLight game = plugin.getArenaManager().getActiveGame(arenaName);

            if (game != null && game.getGameState() != org.tg.squidgame.data.GameState.WAITING) {
                sender.sendMessage(ChatColor.RED + "Game is already running in arena '" + arenaName + "'");
                sender.sendMessage(ChatColor.YELLOW + "Wait for the current game to finish.");
                plugin.getPlayerManager().removePlayer(arenaName, player);
                return true;
            }

            player.sendMessage(plugin.getMessagesManager().getMessage("join-waiting"));

            RedLightGreenLight waitingGame = plugin.getArenaManager().getWaitingGame(arenaName);
            if (waitingGame == null) {
                waitingGame = new RedLightGreenLight(plugin, arena);
                plugin.getArenaManager().setWaitingGame(arenaName, waitingGame);
                waitingGame.startAutoStartTimer();
            } else {
                waitingGame.resetWaitingMessage();
            }
        }

        return true;
    }

    private void giveHoliItems(Player player) {
        ItemStack pichkari = new ItemStack(Material.BLAZE_ROD);
        ItemMeta meta = pichkari.getItemMeta();
        meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', 
            plugin.getConfig().getString("holi.pichkari.name", "&d&lHoli Pichkari")));
        meta.setLore(Arrays.asList(
            ChatColor.translateAlternateColorCodes('&', "&7▸ &bRight-click to spray vibrant colors"),
            ChatColor.translateAlternateColorCodes('&', "&7▸ &eHit players to score points"),
            "",
            ChatColor.translateAlternateColorCodes('&', "&d✦ Festival Edition ✦")
        ));
        meta.setUnbreakable(true);
        pichkari.setItemMeta(meta);
        player.getInventory().addItem(pichkari);
    }
}
