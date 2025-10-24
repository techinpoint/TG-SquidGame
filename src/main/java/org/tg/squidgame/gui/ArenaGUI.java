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
            arena.getGuiConfig().getInt("gui.size", 27) : 27;

        this.inventory = Bukkit.createInventory(null, size, title);
        setupItems();
    }

    private void setupItems() {
        if (arena.getGuiConfig() == null) {
            return;
        }

        int timeLimitSlot = arena.getGuiConfig().getInt("gui.items.timeLimit.slot", 11);
        String timeLimitName = ChatColor.translateAlternateColorCodes('&',
            arena.getGuiConfig().getString("gui.items.timeLimit.name", "&a⏳ Time Limit"));
        List<String> timeLimitLore = translateLore(
            arena.getGuiConfig().getStringList("gui.items.timeLimit.lore"));
        timeLimitLore.add(ChatColor.GRAY + "Current: " + ChatColor.YELLOW + arena.getTimeLimit() + "s");
        inventory.setItem(timeLimitSlot, createItem(Material.CLOCK, timeLimitName, timeLimitLore));

        int barrierSlot = arena.getGuiConfig().getInt("gui.items.barrier.slot", 13);
        String barrierName = ChatColor.translateAlternateColorCodes('&',
            arena.getGuiConfig().getString("gui.items.barrier.name", "&c🚫 Barriers"));
        List<String> barrierLore = translateLore(
            arena.getGuiConfig().getStringList("gui.items.barrier.lore"));
        barrierLore.add(ChatColor.GRAY + "Status: " +
            (arena.isBarrierEnabled() ? ChatColor.GREEN + "Enabled" : ChatColor.RED + "Disabled"));
        inventory.setItem(barrierSlot, createItem(Material.BARRIER, barrierName, barrierLore));

        int soundSlot = arena.getGuiConfig().getInt("gui.items.sound.slot", 15);
        String soundName = ChatColor.translateAlternateColorCodes('&',
            arena.getGuiConfig().getString("gui.items.sound.name", "&b🔊 Sounds"));
        List<String> soundLore = translateLore(
            arena.getGuiConfig().getStringList("gui.items.sound.lore"));
        soundLore.add(ChatColor.GRAY + "Status: " +
            (arena.isSoundEnabled() ? ChatColor.GREEN + "Enabled" : ChatColor.RED + "Disabled"));
        inventory.setItem(soundSlot, createItem(Material.NOTE_BLOCK, soundName, soundLore));

        int saveSlot = arena.getGuiConfig().getInt("gui.items.save.slot", 22);
        String saveName = ChatColor.translateAlternateColorCodes('&',
            arena.getGuiConfig().getString("gui.items.save.name", "&a💾 Save & Exit"));
        List<String> saveLore = translateLore(
            arena.getGuiConfig().getStringList("gui.items.save.lore"));
        inventory.setItem(saveSlot, createItem(Material.EMERALD, saveName, saveLore));
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
}
