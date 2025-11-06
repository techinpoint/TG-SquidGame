package org.tg.squidgame.data;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;

public class ArenaData {

    private final String name;
    private final String type;
    private final String worldName;
    private Location pos1;
    private Location pos2;
    private Location startPos1;
    private Location startPos2;
    private Location winPos1;
    private Location winPos2;
    private Location lobby;
    private Location spectator;
    private boolean barrierEnabled;
    private int timeLimit;
    private int startCountdown;
    private String randomLogic;
    private boolean soundEnabled;
    private int minPlayers;
    private int autoStartDelay;
    private FileConfiguration guiConfig;

    public ArenaData(String name, String type, String worldName) {
        this.name = name;
        this.type = type;
        this.worldName = worldName;
        this.barrierEnabled = true;
        this.timeLimit = 180;
        this.startCountdown = 5;
        this.randomLogic = "complex";
        this.soundEnabled = true;
        this.minPlayers = 1;
        this.autoStartDelay = 10;
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public String getWorldName() {
        return worldName;
    }

    public Location getPos1() {
        return pos1;
    }

    public void setPos1(Location pos1) {
        this.pos1 = pos1;
    }

    public Location getPos2() {
        return pos2;
    }

    public void setPos2(Location pos2) {
        this.pos2 = pos2;
    }

    public Location getStartPos1() {
        return startPos1;
    }

    public void setStartPos1(Location startPos1) {
        this.startPos1 = startPos1;
    }

    public Location getStartPos2() {
        return startPos2;
    }

    public void setStartPos2(Location startPos2) {
        this.startPos2 = startPos2;
    }

    public Location getWinPos1() {
        return winPos1;
    }

    public void setWinPos1(Location winPos1) {
        this.winPos1 = winPos1;
    }

    public Location getWinPos2() {
        return winPos2;
    }

    public void setWinPos2(Location winPos2) {
        this.winPos2 = winPos2;
    }

    public Location getLobby() {
        return lobby;
    }

    public void setLobby(Location lobby) {
        this.lobby = lobby;
    }

    public Location getSpectator() {
        return spectator;
    }

    public void setSpectator(Location spectator) {
        this.spectator = spectator;
    }

    public boolean isBarrierEnabled() {
        return barrierEnabled;
    }

    public void setBarrierEnabled(boolean barrierEnabled) {
        this.barrierEnabled = barrierEnabled;
    }

    public int getTimeLimit() {
        return timeLimit;
    }

    public void setTimeLimit(int timeLimit) {
        this.timeLimit = timeLimit;
    }

    public int getStartCountdown() {
        return startCountdown;
    }

    public void setStartCountdown(int startCountdown) {
        this.startCountdown = startCountdown;
    }

    public String getRandomLogic() {
        return randomLogic;
    }

    public void setRandomLogic(String randomLogic) {
        this.randomLogic = randomLogic;
    }

    public boolean isSoundEnabled() {
        return soundEnabled;
    }

    public void setSoundEnabled(boolean soundEnabled) {
        this.soundEnabled = soundEnabled;
    }

    public FileConfiguration getGuiConfig() {
        return guiConfig;
    }

    public void setGuiConfig(FileConfiguration guiConfig) {
        this.guiConfig = guiConfig;
    }

    public int getMinPlayers() {
        return minPlayers;
    }

    public void setMinPlayers(int minPlayers) {
        this.minPlayers = minPlayers;
    }

    public int getAutoStartDelay() {
        return autoStartDelay;
    }

    public void setAutoStartDelay(int autoStartDelay) {
        this.autoStartDelay = autoStartDelay;
    }

    public boolean isComplete() {
        return pos1 != null && pos2 != null && startPos1 != null &&
               startPos2 != null && winPos1 != null && winPos2 != null &&
               lobby != null && spectator != null;
    }

    public boolean isInArenaBounds(Location location) {
        if (pos1 == null || pos2 == null || location == null) {
            return false;
        }
        if (!location.getWorld().getName().equals(worldName)) {
            return false;
        }

        double minX = Math.min(pos1.getX(), pos2.getX());
        double maxX = Math.max(pos1.getX(), pos2.getX());
        double minY = Math.min(pos1.getY(), pos2.getY());
        double maxY = Math.max(pos1.getY(), pos2.getY());
        double minZ = Math.min(pos1.getZ(), pos2.getZ());
        double maxZ = Math.max(pos1.getZ(), pos2.getZ());

        return location.getX() >= minX && location.getX() <= maxX &&
               location.getY() >= minY && location.getY() <= maxY &&
               location.getZ() >= minZ && location.getZ() <= maxZ;
    }

    public boolean isInWinZone(Location location) {
        if (winPos1 == null || winPos2 == null || location == null) {
            return false;
        }
        if (!location.getWorld().getName().equals(worldName)) {
            return false;
        }

        double minX = Math.min(winPos1.getX(), winPos2.getX());
        double maxX = Math.max(winPos1.getX(), winPos2.getX());
        double minY = Math.min(winPos1.getY(), winPos2.getY());
        double maxY = Math.max(winPos1.getY(), winPos2.getY());
        double minZ = Math.min(winPos1.getZ(), winPos2.getZ());
        double maxZ = Math.max(winPos1.getZ(), winPos2.getZ());

        return location.getX() >= minX && location.getX() <= maxX &&
               location.getY() >= minY && location.getY() <= maxY &&
               location.getZ() >= minZ && location.getZ() <= maxZ;
    }

    public boolean isInStartZone(Location location) {
        if (startPos1 == null || startPos2 == null || location == null) {
            return false;
        }
        if (!location.getWorld().getName().equals(worldName)) {
            return false;
        }

        double minX = Math.min(startPos1.getX(), startPos2.getX());
        double maxX = Math.max(startPos1.getX(), startPos2.getX());
        double minY = Math.min(startPos1.getY(), startPos2.getY());
        double maxY = Math.max(startPos1.getY(), startPos2.getY());
        double minZ = Math.min(startPos1.getZ(), startPos2.getZ());
        double maxZ = Math.max(startPos1.getZ(), startPos2.getZ());

        return location.getX() >= minX && location.getX() <= maxX &&
               location.getY() >= minY && location.getY() <= maxY &&
               location.getZ() >= minZ && location.getZ() <= maxZ;
    }
}
