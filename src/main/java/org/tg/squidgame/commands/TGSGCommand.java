package org.tg.squidgame.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.tg.squidgame.TGSquidGame;
import org.tg.squidgame.commands.subcommands.*;

import java.util.*;

public class TGSGCommand implements CommandExecutor, TabCompleter {

    private final TGSquidGame plugin;
    private final Map<String, SubCommand> subCommands;

    public TGSGCommand(TGSquidGame plugin) {
        this.plugin = plugin;
        this.subCommands = new HashMap<>();

        registerSubCommands();
    }

    private void registerSubCommands() {
        subCommands.put("create", new CreateCommand(plugin));
        subCommands.put("delete", new DeleteCommand(plugin));
        subCommands.put("reload", new ReloadCommand(plugin));
        subCommands.put("list", new ListCommand(plugin));
        subCommands.put("config", new ConfigCommand(plugin));
        subCommands.put("edit", new EditCommand(plugin));
        subCommands.put("save", new SaveCommand(plugin));
        subCommands.put("cancel", new CancelCommand(plugin));
        subCommands.put("lobby", new LobbyCommand(plugin));
        subCommands.put("setpos1", new SetPos1Command(plugin));
        subCommands.put("setpos2", new SetPos2Command(plugin));
        subCommands.put("setstart1", new SetStart1Command(plugin));
        subCommands.put("setstart2", new SetStart2Command(plugin));
        subCommands.put("setwin1", new SetWin1Command(plugin));
        subCommands.put("setwin2", new SetWin2Command(plugin));
        subCommands.put("setlobby", new SetLobbyCommand(plugin));
        subCommands.put("setspec", new SetSpecCommand(plugin));
        subCommands.put("enablebarrier", new EnableBarrierCommand(plugin));
        subCommands.put("disablebarrier", new DisableBarrierCommand(plugin));
        subCommands.put("start", new StartCommand(plugin));
        subCommands.put("stop", new StopCommand(plugin));
        subCommands.put("join", new JoinCommand(plugin));
        subCommands.put("leave", new LeaveCommand(plugin));
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            sendHelp(sender);
            return true;
        }

        String subCommandName = args[0].toLowerCase();

        // Check if first argument is a direct subcommand
        if (subCommands.containsKey(subCommandName)) {
            String[] newArgs = Arrays.copyOfRange(args, 1, args.length);
            return subCommands.get(subCommandName).execute(sender, newArgs);
        }

        // Check if first argument is an arena name with subcommand
        if (args.length >= 2 && plugin.getArenaManager().arenaExists(args[0])) {
            String arenaName = args[0];
            String arenaSubCommand = args[1].toLowerCase();

            if (subCommands.containsKey(arenaSubCommand)) {
                String[] newArgs = new String[args.length - 1];
                newArgs[0] = arenaName;
                System.arraycopy(args, 2, newArgs, 1, args.length - 2);

                return subCommands.get(arenaSubCommand).execute(sender, newArgs);
            }
        }

        sender.sendMessage(ChatColor.RED + "Unknown command. Type /tgsg for help.");
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> completions = new ArrayList<>();

        if (args.length == 1) {
            // First argument: global commands or arena names
            completions.addAll(Arrays.asList("create", "delete", "reload", "list"));
            completions.addAll(plugin.getArenaManager().getArenaNames());
            return filterCompletions(completions, args[0]);
        }

        if (args.length == 2) {
            // Second argument depends on first
            if (plugin.getArenaManager().arenaExists(args[0])) {
                // Arena-specific commands
                completions.addAll(Arrays.asList("config", "edit", "save", "cancel", "lobby", 
                    "setlobby", "setspec", "setstart1", "setstart2", "setwin1", "setwin2",
                    "setpos1", "setpos2", "enablebarrier", "disablebarrier", "start", "stop", "join", "leave"));
                return filterCompletions(completions, args[1]);
            }

            if ("create".equals(args[0])) {
                return Collections.emptyList(); // Arena name input
            }

            if ("delete".equals(args[0])) {
                completions.addAll(plugin.getArenaManager().getArenaNames());
                return filterCompletions(completions, args[1]);
            }
        }

        if (args.length == 3 && "create".equals(args[0])) {
            completions.add("RedLightGreenLight");
            return filterCompletions(completions, args[2]);
        }

        return completions;
    }

    private List<String> filterCompletions(List<String> completions, String arg) {
        List<String> filtered = new ArrayList<>();
        String lowerArg = arg.toLowerCase();
        for (String completion : completions) {
            if (completion.toLowerCase().startsWith(lowerArg)) {
                filtered.add(completion);
            }
        }
        return filtered;
    }

    private void sendHelp(CommandSender sender) {
        sender.sendMessage(ChatColor.GOLD + "===== TG SquidGame Commands =====");
        sender.sendMessage(ChatColor.YELLOW + "/tgsg create <name> <type>" + ChatColor.GRAY + " - Create new arena");
        sender.sendMessage(ChatColor.YELLOW + "/tgsg delete <name>" + ChatColor.GRAY + " - Delete arena");
        sender.sendMessage(ChatColor.YELLOW + "/tgsg list" + ChatColor.GRAY + " - List all arenas");
        sender.sendMessage(ChatColor.YELLOW + "/tgsg reload" + ChatColor.GRAY + " - Reload plugin");
        sender.sendMessage(ChatColor.YELLOW + "/tgsg <arena> config" + ChatColor.GRAY + " - Open settings GUI");
        sender.sendMessage(ChatColor.YELLOW + "/tgsg <arena> edit" + ChatColor.GRAY + " - Toggle edit mode");
        sender.sendMessage(ChatColor.YELLOW + "/tgsg <arena> save" + ChatColor.GRAY + " - Save and exit edit mode");
        sender.sendMessage(ChatColor.YELLOW + "/tgsg <arena> cancel" + ChatColor.GRAY + " - Exit without saving");
        sender.sendMessage(ChatColor.YELLOW + "/tgsg <arena> lobby" + ChatColor.GRAY + " - Teleport to lobby");
        sender.sendMessage(ChatColor.YELLOW + "/tgsg <arena> setpos1/2" + ChatColor.GRAY + " - Set arena bounds");
        sender.sendMessage(ChatColor.YELLOW + "/tgsg <arena> setstart1/2" + ChatColor.GRAY + " - Set start area");
        sender.sendMessage(ChatColor.YELLOW + "/tgsg <arena> setwin1/2" + ChatColor.GRAY + " - Set win zone");
        sender.sendMessage(ChatColor.YELLOW + "/tgsg <arena> setlobby" + ChatColor.GRAY + " - Set lobby spawn");
        sender.sendMessage(ChatColor.YELLOW + "/tgsg <arena> setspec" + ChatColor.GRAY + " - Set spectator spawn");
        sender.sendMessage(ChatColor.YELLOW + "/tgsg <arena> start" + ChatColor.GRAY + " - Start game");
        sender.sendMessage(ChatColor.YELLOW + "/tgsg <arena> stop" + ChatColor.GRAY + " - Stop game");
        sender.sendMessage(ChatColor.YELLOW + "/tgsg <arena> join" + ChatColor.GRAY + " - Join arena");
    }
}
