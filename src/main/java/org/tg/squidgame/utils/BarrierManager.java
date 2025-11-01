package org.tg.squidgame.utils;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.tg.squidgame.data.ArenaData;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BarrierManager {

    private static final Map<String, List<Location>> arenaBarriers = new HashMap<>();

    public static void createBarriers(ArenaData arena) {
        if (arena.getPos1() == null || arena.getPos2() == null) {
            return;
        }

        removeBarriers(arena);

        List<Location> barriers = new ArrayList<>();
        Location pos1 = arena.getPos1();
        Location pos2 = arena.getPos2();
        World world = pos1.getWorld();

        int minX = Math.min(pos1.getBlockX(), pos2.getBlockX());
        int maxX = Math.max(pos1.getBlockX(), pos2.getBlockX());
        int minY = Math.min(pos1.getBlockY(), pos2.getBlockY());
        int maxY = Math.max(pos1.getBlockY(), pos2.getBlockY());
        int minZ = Math.min(pos1.getBlockZ(), pos2.getBlockZ());
        int maxZ = Math.max(pos1.getBlockZ(), pos2.getBlockZ());

        for (int y = minY; y <= maxY; y++) {
            for (int x = minX; x <= maxX; x++) {
                Location loc1 = new Location(world, x, y, minZ);
                if (world.getBlockAt(loc1).getType() == Material.AIR) {
                    world.getBlockAt(loc1).setType(Material.BARRIER);
                    barriers.add(loc1);
                }

                Location loc2 = new Location(world, x, y, maxZ);
                if (world.getBlockAt(loc2).getType() == Material.AIR) {
                    world.getBlockAt(loc2).setType(Material.BARRIER);
                    barriers.add(loc2);
                }
            }

            for (int z = minZ; z <= maxZ; z++) {
                Location loc1 = new Location(world, minX, y, z);
                if (world.getBlockAt(loc1).getType() == Material.AIR) {
                    world.getBlockAt(loc1).setType(Material.BARRIER);
                    barriers.add(loc1);
                }

                Location loc2 = new Location(world, maxX, y, z);
                if (world.getBlockAt(loc2).getType() == Material.AIR) {
                    world.getBlockAt(loc2).setType(Material.BARRIER);
                    barriers.add(loc2);
                }
            }
        }

        arenaBarriers.put(arena.getName(), barriers);
    }

    public static void removeBarriers(ArenaData arena) {
        List<Location> barriers = arenaBarriers.remove(arena.getName());
        if (barriers != null) {
            for (Location loc : barriers) {
                if (loc.getWorld().getBlockAt(loc).getType() == Material.BARRIER) {
                    loc.getWorld().getBlockAt(loc).setType(Material.AIR);
                }
            }
        }
    }
}
