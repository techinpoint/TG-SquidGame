package org.tg.squidgame.gui;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.tg.squidgame.TGSquidGame;
import org.tg.squidgame.data.ArenaData;

import java.util.ArrayList;
import java.util.List;

public class ArenaGUI {

    private final TGSquidGame plugin;
    private final ArenaData arena;
    private final Inventory inventory;

    public ArenaGUI(TGSquidGame plugin, ArenaData arena) {
        this.plugin = plugin;
        this.arena = arena;

        String title = arena.getGuiConfig() != null ?
            ChatColor.translateAlternateColorCodes('&', arena.getGuiConfig().getString("gui.name", "&6Arena Settings")) :
            ChatColor.GOLD + "Arena Settings";

        int size = arena.getGuiConfig() != null ?
            arena.getGuiConfig().getInt("gui.size", 45) : 45;

        this.inventory = Bukkit.createInventory(null, size, title);
        setupItems();
    }

    private void setupItems() {
        if (arena.getGuiConfig() == null) {
            setupDefaultItems();
            return;
        }

        int timeLimitSlot = arena.getGuiConfig().getInt("gui.items.timeLimit.slot", 11);
        String timeLimitName = ChatColor.translateAlternateColorCodes('&',
            arena.getGuiConfig().getString("gui.items.timeLimit.name", "&a⏳ Time Limit"));
        List<String> timeLimitLore = translateLore(
            arena.getGuiConfig().getStringList("gui.items.timeLimit.lore"));
        timeLimitLore.add(ChatColor.GRAY + "Current: " + ChatColor.YELLOW + arena.getTimeLimit() + "s");
        timeLimitLore.add(ChatColor.GRAY + "Click to change");
        inventory.setItem(timeLimitSlot, createItem(Material.CLOCK, timeLimitName, timeLimitLore));

        int barrierSlot = arena.getGuiConfig().getInt("gui.items.barrier.slot", 13);
        String barrierName = ChatColor.translateAlternateColorCodes('&',
            arena.getGuiConfig().getString("gui.items.barrier.name", "&c🚫 Barriers"));
        List<String> barrierLore = translateLore(
            arena.getGuiConfig().getStringList("gui.items.barrier.lore"));
        barrierLore.add(ChatColor.GRAY + "Status: " +
            (arena.isBarrierEnabled() ? ChatColor.GREEN + "Enabled" : ChatColor.RED + "Disabled"));
        barrierLore.add(ChatColor.GRAY + "Click to toggle");
        inventory.setItem(barrierSlot, createItem(Material.BARRIER, barrierName, barrierLore));

        int soundSlot = arena.getGuiConfig().getInt("gui.items.sound.slot", 15);
        String soundName = ChatColor.translateAlternateColorCodes('&',
            arena.getGuiConfig().getString("gui.items.sound.name", "&b🔊 Sounds"));
        List<String> soundLore = translateLore(
            arena.getGuiConfig().getStringList("gui.items.sound.lore"));
        soundLore.add(ChatColor.GRAY + "Status: " +
            (arena.isSoundEnabled() ? ChatColor.GREEN + "Enabled" : ChatColor.RED + "Disabled"));
        soundLore.add(ChatColor.GRAY + "Click to toggle");
        inventory.setItem(soundSlot, createItem(Material.NOTE_BLOCK, soundName, soundLore));

        int minPlayersSlot = arena.getGuiConfig().getInt("gui.items.minPlayers.slot", 19);
        String minPlayersName = ChatColor.translateAlternateColorCodes('&',
            arena.getGuiConfig().getString("gui.items.minPlayers.name", "&d Minimum Players"));
        List<String> minPlayersLore = new ArrayList<>();
        minPlayersLore.add(ChatColor.GRAY + "Minimum players to start");
        minPlayersLore.add(ChatColor.GRAY + "Current: " + ChatColor.YELLOW + arena.getMinPlayers());
        minPlayersLore.add(ChatColor.GRAY + "Click to change");
        inventory.setItem(minPlayersSlot, createItem(Material.PLAYER_HEAD, minPlayersName, minPlayersLore));

        int autoStartSlot = arena.getGuiConfig().getInt("gui.items.autoStart.slot", 20);
        String autoStartName = ChatColor.translateAlternateColorCodes('&',
            arena.getGuiConfig().getString("gui.items.autoStart.name", "&e Auto-Start Timer"));
        List<String> autoStartLore = new ArrayList<>();
        autoStartLore.add(ChatColor.GRAY + "Seconds before auto-start");
        autoStartLore.add(ChatColor.GRAY + "Current: " + ChatColor.YELLOW + arena.getAutoStartDelay() + "s");
        autoStartLore.add(ChatColor.GRAY + "Click to change");
        inventory.setItem(autoStartSlot, createItem(Material.REDSTONE_TORCH, autoStartName, autoStartLore));

        int saveSlot = arena.getGuiConfig().getInt("gui.items.save.slot", 40);
        String saveName = ChatColor.translateAlternateColorCodes('&',
            arena.getGuiConfig().getString("gui.items.save.name", "&a💾 Save Settings"));
        List<String> saveLore = translateLore(
            arena.getGuiConfig().getStringList("gui.items.save.lore"));
        saveLore.add(ChatColor.GRAY + "Save GUI configuration changes");
        inventory.setItem(saveSlot, createItem(Material.EMERALD, saveName, saveLore));

        int closeSlot = arena.getGuiConfig().getInt("gui.items.close.slot", 44);
        String closeName = ChatColor.translateAlternateColorCodes('&',
            arena.getGuiConfig().getString("gui.items.close.name", "&c❌ Close"));
        List<String> closeLore = new ArrayList<>();
        closeLore.add(ChatColor.GRAY + "Close without saving");
        inventory.setItem(closeSlot, createItem(Material.REDSTONE, closeName, closeLore));
    }

    private void setupDefaultItems() {
        // Default GUI layout when no config is available
        List<String> timeLimitLore = new ArrayList<>();
        timeLimitLore.add(ChatColor.GRAY + "Configure game time limit");
        timeLimitLore.add(ChatColor.GRAY + "Current: " + ChatColor.YELLOW + arena.getTimeLimit() + "s");
        inventory.setItem(11, createItem(Material.CLOCK, ChatColor.GREEN + "⏳ Time Limit", timeLimitLore));

        List<String> barrierLore = new ArrayList<>();
        barrierLore.add(ChatColor.GRAY + "Toggle arena barriers");
        barrierLore.add(ChatColor.GRAY + "Status: " + 
            (arena.isBarrierEnabled() ? ChatColor.GREEN + "Enabled" : ChatColor.RED + "Disabled"));
        inventory.setItem(13, createItem(Material.BARRIER, ChatColor.RED + "🚫 Barriers", barrierLore));

        List<String> soundLore = new ArrayList<>();
        soundLore.add(ChatColor.GRAY + "Toggle game sounds");
        soundLore.add(ChatColor.GRAY + "Status: " + 
            (arena.isSoundEnabled() ? ChatColor.GREEN + "Enabled" : ChatColor.RED + "Disabled"));
        inventory.setItem(15, createItem(Material.NOTE_BLOCK, ChatColor.BLUE + "🔊 Sounds", soundLore));

        List<String> minPlayersLore = new ArrayList<>();
        minPlayersLore.add(ChatColor.GRAY + "Minimum players to start");
        minPlayersLore.add(ChatColor.GRAY + "Current: " + ChatColor.YELLOW + arena.getMinPlayers());
        minPlayersLore.add(ChatColor.GRAY + "Click to change");
        inventory.setItem(19, createItem(Material.PLAYER_HEAD, ChatColor.LIGHT_PURPLE + "Minimum Players", minPlayersLore));

        List<String> autoStartLore = new ArrayList<>();
        autoStartLore.add(ChatColor.GRAY + "Seconds before auto-start");
        autoStartLore.add(ChatColor.GRAY + "Current: " + ChatColor.YELLOW + arena.getAutoStartDelay() + "s");
        autoStartLore.add(ChatColor.GRAY + "Click to change");
        inventory.setItem(20, createItem(Material.REDSTONE_TORCH, ChatColor.YELLOW + "Auto-Start Timer", autoStartLore));

        List<String> saveLore = new ArrayList<>();
        saveLore.add(ChatColor.GRAY + "Save configuration changes");
        inventory.setItem(40, createItem(Material.EMERALD, ChatColor.GREEN + "Save Settings", saveLore));

        List<String> closeLore = new ArrayList<>();
        closeLore.add(ChatColor.GRAY + "Close without saving");
        inventory.setItem(44, createItem(Material.REDSTONE, ChatColor.RED + "❌ Close", closeLore));
    }

    private ItemStack createItem(Material material, String name, List<String> lore) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(name);
            meta.setLore(lore);
            item.setItemMeta(meta);
        }
        return item;
    }

    private List<String> translateLore(List<String> lore) {
        List<String> translated = new ArrayList<>();
        for (String line : lore) {
            translated.add(ChatColor.translateAlternateColorCodes('&', line));
        }
        return translated;
    }

    public void open(Player player) {
        player.openInventory(inventory);
    }

    public Inventory getInventory() {
        return inventory;
    }

    public ArenaData getArena() {
        return arena;
    }

    public void handleClick(Player player, int slot, ItemStack clickedItem) {
        if (clickedItem == null || clickedItem.getType() == Material.AIR) {
            return;
        }

        switch (clickedItem.getType()) {
            case CLOCK:
                // Handle time limit change
                cycleTimeLimit();
                setupItems(); // Refresh GUI
                player.sendMessage(ChatColor.GREEN + "Time limit changed to " + arena.getTimeLimit() + " seconds");
                break;
            case BARRIER:
                // Handle barrier toggle
                arena.setBarrierEnabled(!arena.isBarrierEnabled());
                setupItems(); // Refresh GUI
                player.sendMessage(ChatColor.GREEN + "Barriers " + 
                    (arena.isBarrierEnabled() ? "enabled" : "disabled"));
                break;
            case NOTE_BLOCK:
                // Handle sound toggle
                arena.setSoundEnabled(!arena.isSoundEnabled());
                setupItems(); // Refresh GUI
                player.sendMessage(ChatColor.GREEN + "Sounds " + 
                    (arena.isSoundEnabled() ? "enabled" : "disabled"));
                break;
            case PLAYER_HEAD:
                cycleMinPlayers();
                setupItems();
                player.sendMessage(ChatColor.GREEN + "Minimum players set to " + arena.getMinPlayers());
                break;
            case REDSTONE_TORCH:
                cycleAutoStartDelay();
                setupItems();
                player.sendMessage(ChatColor.GREEN + "Auto-start delay set to " + arena.getAutoStartDelay() + " seconds");
                break;
            case EMERALD:
                plugin.getArenaManager().saveArenaConfig(arena.getName());
                player.sendMessage(ChatColor.GREEN + "GUI settings saved!");
                break;
            case REDSTONE:
                player.closeInventory();
                break;
        }
    }

    private void cycleTimeLimit() {
        int currentLimit = arena.getTimeLimit();
        int[] timeLimits = {60, 120, 180, 240, 300, 600};

        int nextIndex = 0;
        for (int i = 0; i < timeLimits.length; i++) {
            if (timeLimits[i] == currentLimit) {
                nextIndex = (i + 1) % timeLimits.length;
                break;
            }
        }

        arena.setTimeLimit(timeLimits[nextIndex]);
    }

    private void cycleMinPlayers() {
        int current = arena.getMinPlayers();
        int[] values = {1, 2, 3, 4, 5, 6, 8, 10};

        int nextIndex = 0;
        for (int i = 0; i < values.length; i++) {
            if (values[i] == current) {
                nextIndex = (i + 1) % values.length;
                break;
            }
        }

        arena.setMinPlayers(values[nextIndex]);
    }

    private void cycleAutoStartDelay() {
        int current = arena.getAutoStartDelay();
        int[] values = {5, 10, 15, 20, 30, 45, 60};

        int nextIndex = 0;
        for (int i = 0; i < values.length; i++) {
            if (values[i] == current) {
                nextIndex = (i + 1) % values.length;
                break;
            }
        }

        arena.setAutoStartDelay(values[nextIndex]);
    }
}
