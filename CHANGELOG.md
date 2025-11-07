# TG SquidGame Plugin - Version 1.2 Changelog

## Major Improvements

### 1. Fixed Teleportation Bug During Countdown
**Issue**: Players were being teleported to `pos1` during the 5-second countdown before game start.

**Solution**: Removed automatic teleportation during countdown. Players now stay at their current location throughout the countdown phase and game starts where they are standing.

**Files Modified**:
- `src/main/java/org/tg/squidgame/games/RedLightGreenLight.java`
  - Modified `startCountdown()` method to remove `teleportToStart()` call
  - Players' positions are now only tracked, not changed

### 2. Enhanced Minimum Players GUI
**Issue**: GUI lore was basic and not visually appealing.

**Solution**: Completely redesigned the lore with:
- Attractive formatting with Unicode symbols
- Color-coded information (Aqua for labels, White for values)
- Clear separation with empty lines
- Explicit click actions:
  - Left Click: +1 player (Green)
  - Right Click: -1 player (Red)
  - Shift + Left: +5 players (Green)
  - Shift + Right: -5 players (Red)

**Files Modified**:
- `src/main/java/org/tg/squidgame/gui/ArenaGUI.java`
  - Updated both config-based and default GUI item creation
  - Enhanced lore for Minimum Players item
  - Enhanced lore for Auto-Start Timer item

### 3. Arena Setup Completion Notification
**Issue**: No feedback when arena setup was complete.

**Solution**: Added yellow success message "Arena setup completed successfully! All positions are now set." when the last required position is set.

**Files Modified**:
- `src/main/java/org/tg/squidgame/commands/subcommands/SetPos1Command.java`
- `src/main/java/org/tg/squidgame/commands/subcommands/SetPos2Command.java`
- `src/main/java/org/tg/squidgame/commands/subcommands/SetStart1Command.java`
- `src/main/java/org/tg/squidgame/commands/subcommands/SetStart2Command.java`
- `src/main/java/org/tg/squidgame/commands/subcommands/SetWin1Command.java`
- `src/main/java/org/tg/squidgame/commands/subcommands/SetWin2Command.java`
- `src/main/java/org/tg/squidgame/commands/subcommands/SetLobbyCommand.java`
- `src/main/java/org/tg/squidgame/commands/subcommands/SetSpecCommand.java`

Each command now checks `arena.isComplete()` after setting the position and displays the success message if all 8 positions are configured.

### 4. Migrated to Paper API with Java 21
**Issue**: Plugin was using Spigot API with Java 17.

**Solution**: Upgraded to modern Paper API and Java 21 for better performance and features.

**Files Modified**:
- `pom.xml`
  - Changed repository from Spigot to PaperMC
  - Updated dependency from `spigot-api` to `paper-api` (1.21.1-R0.1-SNAPSHOT)
  - Updated Java version from 17 to 21
  - Updated plugin version to 1.2
  - Updated description to reflect Paper compatibility

- `src/main/resources/plugin.yml`
  - Updated version to 1.2
  - Updated description to indicate Paper 1.21.1+ compatibility
  - Quoted api-version for proper YAML parsing

### 5. Professional Polish and Improvements
**General Enhancements**:
- Removed unnecessary emojis from code messages (keeping GUI clean)
- Improved code consistency across all position-setting commands
- Better user feedback throughout the plugin
- More professional appearance for GUI tooltips
- Clearer visual hierarchy in GUI items

## Technical Details

### API Changes
- **From**: Spigot API 1.21.1-R0.1-SNAPSHOT
- **To**: Paper API 1.21.1-R0.1-SNAPSHOT
- **Java Version**: 17 → 21
- **Maven Repository**: hub.spigotmc.org → repo.papermc.io

### Compatibility
- **Minecraft Version**: 1.21.1+
- **Server Software**: Paper (recommended), Spigot (compatible)
- **Java Requirement**: Java 21 or higher

## Benefits

1. **Better User Experience**: Players no longer experience unexpected teleportation during countdown
2. **Clearer GUI**: More intuitive and visually appealing configuration interface
3. **Better Feedback**: Arena setup completion is now clearly communicated
4. **Modern Platform**: Paper API provides better performance and features
5. **Future-Proof**: Java 21 support ensures long-term compatibility

## Breaking Changes

**None** - This update is fully backward compatible with existing arena configurations.

## Migration Notes

To use this version:
1. Ensure your server is running Java 21 or higher
2. Use Paper 1.21.1+ or compatible Spigot version
3. Replace the old plugin JAR with the new one
4. No configuration changes required

## Build Information

- **Plugin Version**: 1.2
- **API Version**: 1.21
- **Build Target**: TG-SquidGame-1.2.jar
