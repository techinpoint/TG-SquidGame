# TG SquidGame Plugin

**Version:** 1.2
**Author:** Techinpoint Gamerz (TG)
**Minecraft Version:** 1.21+
**Server Type:** Paper 1.21.1+

---

## Overview

TG SquidGame is a modular Minecraft minigame plugin inspired by the popular Squid Game series and Indian festivals. This plugin supports multiple arenas in a single world and is designed for easy expansion. Current version includes two exciting minigames: **Red Light Green Light** and **Holi Festival**.

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
| `/tgsg create <name> <type>` | Create a new arena (RedLightGreenLight or Holi) | `tgsg.admin` |
| `/tgsg delete <name>` | Delete an arena | `tgsg.admin` |

### Arena Setup Commands

| Command | Description | Permission |
|---------|-------------|------------|
| `/tgsg <arena> setpos1` | Set arena corner 1 (Required for all games) | `tgsg.admin` |
| `/tgsg <arena> setpos2` | Set arena corner 2 (Required for all games) | `tgsg.admin` |
| `/tgsg <arena> setstart1` | Set start area corner 1 (Red Light Green Light only) | `tgsg.admin` |
| `/tgsg <arena> setstart2` | Set start area corner 2 (Red Light Green Light only) | `tgsg.admin` |
| `/tgsg <arena> setwin1` | Set win zone corner 1 (Red Light Green Light only) | `tgsg.admin` |
| `/tgsg <arena> setwin2` | Set win zone corner 2 (Red Light Green Light only) | `tgsg.admin` |
| `/tgsg <arena> setlobby` | Set lobby spawn point (Required for all games) | `tgsg.admin` |
| `/tgsg <arena> setspec` | Set spectator spawn point (Required for all games) | `tgsg.admin` |

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
| `/tgsg leave` | Leave all arenas | - |

---

## Permissions

- `tgsg.admin` - Access to all admin commands and arena setup
- No special permission required for players to join games

---

## Game Modes

### Red Light Green Light Game

Classic Squid Game minigame where players must reach the finish line while following light signals.

#### How It Works

1. **Game Start**: All players are teleported to the starting area
2. **Green Light Phase**: Players can move freely (4-10 seconds)
3. **Red Light Phase**: Players must freeze (4-10 seconds)
4. **Movement Detection**: Players who move during red light are eliminated
5. **Win Condition**: First players to reach the win zone are declared winners
6. **Time Limit**: Game ends after the configured time limit

#### Setup Requirements

For Red Light Green Light arenas, you must configure:
- Arena boundaries (pos1, pos2)
- Starting area (startPos1, startPos2)
- Win zone (winPos1, winPos2)
- Lobby spawn point
- Spectator spawn point

#### Elimination Rules

- Moving during red light = Instant elimination
- Leaving arena bounds = Instant elimination
- Disconnecting = Automatic elimination
- Eliminated players become spectators

---

### Holi Festival Game

Indian festival-inspired minigame where players spray colorful particles at each other to score points.

#### How It Works

1. **Game Start**: All players receive Holi Pichkari (color spray gun)
2. **Objective**: Spray colors on other players to score points
3. **Scoring**: Each successful hit adds 1 point to your score
4. **Cooldown**: 3-second cooldown between spray uses
5. **Win Condition**: Player with most hits wins at the end
6. **Time Limit**: Default 10 minutes (configurable)

#### Setup Requirements

For Holi arenas, you only need to configure:
- Arena boundaries (pos1, pos2)
- Lobby spawn point
- Spectator spawn point

Note: Start zones and win zones are not required for Holi.

#### Gameplay Features

- **Pichkari (Spray Gun)**: Right-click to spray vibrant colors
- **Color Effects**: Hit players glow and display particle effects
- **Leaderboard**: Top 5 players displayed at game end
- **Fireworks**: Victory celebration for the champion
- **No Damage**: PvP damage is disabled, only color scoring

#### Configuration

Customize Holi settings in `config.yml`:

```yaml
holi:
  duration: 600  # Game duration in seconds
  pichkari:
    name: "&bHoli Pichkari"
    cooldown: 3  # Cooldown in seconds
    range: 10.0  # Spray range in blocks
  colors:
    - RED
    - BLUE
    - GREEN
    - YELLOW
    - PURPLE
    - ORANGE
  bossbar:
    text: "&bHoli Festival - {time} remaining"
    color: "BLUE"
    style: "SOLID"
  fireworks: true
```

---

### Spectator Mode

Both game modes support spectator functionality:
- Eliminated and winning players enter spectator mode
- Can freely fly within arena bounds
- Cannot interfere with active players
- Must stay within arena boundaries

---

## Building from Source

### Requirements

- Java 21 or higher
- Maven 3.6+
- Paper 1.21.1+ (Spigot compatible)

### Build Instructions

```bash
git clone <repository-url>
cd TG-SquidGame
mvn clean package
```

The compiled JAR will be in `target/TG-SquidGame-1.2.jar`

---

## Available Minigames

- Red Light Green Light
- Holi Festival

## Future Minigames (Planned)

- Glass Bridge
- Tug of War
- Marbles
- Honeycomb/Dalgona Candy
- And more!

---

## Support & Credits

**Developed by:** Techinpoint Gamerz (TG)
**Version:** 1.2
**Minecraft Version:** 1.21.1+
**Server Type:** Paper (Spigot compatible)

For support, please contact Techinpoint Gamerz.

---

## License

This plugin is proprietary software. All rights reserved by Techinpoint Gamerz.

---

## Changelog

### Version 1.2 (Current)
- Added Holi Festival minigame
- Migrated to Paper API with Java 21
- Enhanced GUI with improved player controls
- Multiple arena support improvements
- Auto-start system with configurable timers
- Player can join multiple arenas simultaneously
- Enhanced particle effects and visual feedback
- Improved game state management

### Version 1.0 (Initial Release)
- Red Light Green Light minigame implementation
- Multi-arena support
- GUI configuration system
- Barrier management
- Spectator mode
- Disconnect handling
- BossBar integration
- Sound effects
