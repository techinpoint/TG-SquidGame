# TG SquidGame Plugin

**Version:** 1.0
**Author:** Techinpoint Gamerz (TG)
**Minecraft Version:** 1.21+
**Server Type:** Spigot/Paper

---

## Overview

TG SquidGame is a modular Minecraft minigame plugin inspired by the popular Squid Game series. This plugin supports multiple arenas in a single world and is designed for easy expansion. In version 1.0, only the **Red Light Green Light** minigame is implemented.

### Key Features

- **Multi-Arena Support**: Run multiple game arenas simultaneously in one world
- **Modular Design**: Easy to extend with new minigames
- **GUI Configuration**: Intuitive in-game editor for arena settings
- **YAML-Based Setup**: Each arena has its own configuration file
- **Barrier System**: Automatic arena boundaries using barrier blocks
- **Spectator Mode**: Eliminated players can spectate the ongoing game
- **Disconnect Handling**: Players who disconnect are automatically eliminated
- **BossBar Integration**: Real-time game state display
- **Sound Effects**: Immersive audio cues for game events

---

## Installation

1. Download the `TG-SquidGame-1.0.jar` file
2. Place it in your server's `plugins` folder
3. Restart your server
4. The plugin will create necessary directories and files:
   - `plugins/TG-SquidGame/config.yml` - Main configuration
   - `plugins/TG-SquidGame/arenas/` - Arena configuration files

---

## Configuration

### Main Config (`config.yml`)

```yaml
settings:
  prefix: "&6[TG SquidGame]&r "
  defaultTimeLimit: 180
  enableJoinMessage: true
  soundEffects: true
  defaultBossBarColor: "GREEN"
  useComplexRandomLogic: true
```

**Configuration Options:**

- `prefix`: Chat message prefix
- `defaultTimeLimit`: Default game duration in seconds
- `enableJoinMessage`: Show messages when players join arenas
- `soundEffects`: Enable/disable sound effects globally
- `defaultBossBarColor`: Default BossBar color (GREEN, RED, BLUE, etc.)
- `useComplexRandomLogic`: Use advanced randomization for light changes

### Arena Configuration

Each arena has its own `.yml` file in the `arenas` folder. Example: `redlight1.yml`

```yaml
arena:
  name: "redlight1"
  type: "RedLightGreenLight"
  world: "world"
  pos1: "100,65,100"
  pos2: "150,75,150"
  startPos1: "110,65,110"
  startPos2: "120,65,120"
  winPos1: "145,65,145"
  winPos2: "150,65,150"
  lobby: "90,65,90"
  spectator: "160,70,160"
  barrierEnabled: true
  timeLimit: 180
  randomLogic: "complex"
  soundEnabled: true

gui:
  name: "&6Red Light Settings"
  size: 27
  items:
    # GUI item configurations
```

---

## Commands

### Basic Commands

| Command | Description | Permission |
|---------|-------------|------------|
| `/tgsg` or `/sg` | Show help menu | - |
| `/tgsg reload` | Reload plugin configuration | `tgsg.admin` |
| `/tgsg list` | List all arenas | `tgsg.admin` |
| `/tgsg create <name> <type>` | Create a new arena | `tgsg.admin` |
| `/tgsg delete <name>` | Delete an arena | `tgsg.admin` |

### Arena Setup Commands

| Command | Description | Permission |
|---------|-------------|------------|
| `/tgsg <arena> setpos1` | Set arena corner 1 | `tgsg.admin` |
| `/tgsg <arena> setpos2` | Set arena corner 2 | `tgsg.admin` |
| `/tgsg <arena> setstart1` | Set start area corner 1 | `tgsg.admin` |
| `/tgsg <arena> setstart2` | Set start area corner 2 | `tgsg.admin` |
| `/tgsg <arena> setwin1` | Set win zone corner 1 | `tgsg.admin` |
| `/tgsg <arena> setwin2` | Set win zone corner 2 | `tgsg.admin` |
| `/tgsg <arena> setlobby` | Set lobby spawn point | `tgsg.admin` |
| `/tgsg <arena> setspec` | Set spectator spawn point | `tgsg.admin` |

### Arena Management Commands

| Command | Description | Permission |
|---------|-------------|------------|
| `/tgsg <arena> edit` | Open arena settings GUI | `tgsg.admin` |
| `/tgsg <arena> save` | Save and exit edit mode | `tgsg.admin` |
| `/tgsg <arena> cancel` | Exit edit mode without saving | `tgsg.admin` |
| `/tgsg <arena> enablebarrier` | Enable barrier blocks | `tgsg.admin` |
| `/tgsg <arena> disablebarrier` | Disable barrier blocks | `tgsg.admin` |

### Game Control Commands

| Command | Description | Permission |
|---------|-------------|------------|
| `/tgsg <arena> start` | Start the game | `tgsg.admin` |
| `/tgsg <arena> stop` | Stop the game | `tgsg.admin` |
| `/tgsg <arena> join` | Join an arena | - |

---

## Permissions

- `tgsg.admin` - Access to all admin commands and arena setup
- No special permission required for players to join games

---

## Red Light Green Light Game

### How It Works

1. **Game Start**: All players are teleported to the starting area
2. **Green Light Phase**: Players can move freely (4-10 seconds)
3. **Red Light Phase**: Players must freeze (4-10 seconds)
4. **Movement Detection**: Players who move during red light are eliminated
5. **Win Condition**: First players to reach the win zone are declared winners
6. **Time Limit**: Game ends after the configured time limit

### Elimination Rules

- Moving during red light = Instant elimination
- Leaving arena bounds = Instant elimination
- Disconnecting = Automatic elimination
- Eliminated players become spectators

### Spectator Mode

- Eliminated and winning players enter spectator mode
- Can freely fly within arena bounds
- Cannot interfere with active players
- Must stay within arena boundaries

---

## Building from Source

### Requirements

- Java 17 or higher
- Maven 3.6+

### Build Instructions

```bash
git clone <repository-url>
cd TG-SquidGame
mvn clean package
```

The compiled JAR will be in `target/TG-SquidGame-1.0.jar`

---

## Future Minigames (Planned)

- Glass Bridge
- Tug of War
- Marbles
- Honeycomb/Dalgona Candy
- And more!

---

## Support & Credits

**Developed by:** Techinpoint Gamerz (TG)
**Version:** 1.0
**Minecraft Version:** 1.21+

For support, please contact Techinpoint Gamerz.

---

## License

This plugin is proprietary software. All rights reserved by Techinpoint Gamerz.

---

## Changelog

### Version 1.0 (Initial Release)
- Red Light Green Light minigame implementation
- Multi-arena support
- GUI configuration system
- Barrier management
- Spectator mode
- Disconnect handling
- BossBar integration
- Sound effects
