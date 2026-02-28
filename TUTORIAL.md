# TG SquidGame - Complete Setup Tutorial

This tutorial will guide you through setting up and running both Red Light Green Light and Holi Festival game arenas.

---

## Table of Contents

1. [Installation](#installation)
2. [Choosing Your Game Type](#choosing-your-game-type)
3. [Creating Red Light Green Light Arena](#creating-red-light-green-light-arena)
4. [Creating Holi Festival Arena](#creating-holi-festival-arena)
5. [Using the GUI Editor](#using-the-gui-editor)
6. [Starting a Game](#starting-a-game)
7. [Player Experience](#player-experience)
8. [Advanced Configuration](#advanced-configuration)
9. [Troubleshooting](#troubleshooting)

---

## Installation

### Step 1: Install the Plugin

1. Download `TG-SquidGame-1.2.jar`
2. Place it in your server's `plugins` folder
3. Start or restart your Minecraft server (requires Paper 1.21.1+ or compatible)
4. Check the console for the message: `TG SquidGame v1.0 by Techinpoint Gamerz has been enabled!`

### Step 2: Verify Installation

Run the command:
```
/tgsg
```

You should see the help menu with all available commands.

---

## Choosing Your Game Type

The plugin supports two game types, each with different setup requirements:

### Red Light Green Light
- Classic Squid Game minigame
- Players race to finish line while freezing during red light
- Requires 8 position points (full setup)
- Best for: Competitive racing gameplay

### Holi Festival
- Indian festival-inspired color battle
- Players spray colors on each other to score points
- Requires only 4 position points (simplified setup)
- Best for: Casual fun and team events

---

## Creating Red Light Green Light Arena

### Step 1: Create the Arena File

Choose a location in your world where you want to build the arena. Then run:

```
/tgsg create myarena RedLightGreenLight
```

Replace `myarena` with your preferred arena name (no spaces).

**Expected Output:**
```
Arena 'myarena' created successfully!
Configure it with: /tgsg myarena setpos1, setpos2, etc.
```

### Step 2: Configuring Arena Positions

To set up a Red Light Green Light arena, you need to define 8 key locations. Here's the recommended order:

#### Define Arena Boundaries

Stand at one corner of where you want your arena to be:
```
/tgsg myarena setpos1
```

Walk to the opposite corner (diagonal):
```
/tgsg myarena setpos2
```

**Tip:** These two points define a 3D rectangular region. Everything inside is the arena.

#### Define Starting Area

Stand at one corner of where players should spawn:
```
/tgsg myarena setstart1
```

Walk to the opposite corner of the starting area:
```
/tgsg myarena setstart2
```

**Tip:** Players will be randomly spawned within this region when the game starts.

#### Define Win Zone

Stand at one corner of the finish line:
```
/tgsg myarena setwin1
```

Walk to the opposite corner:
```
/tgsg myarena setwin2
```

**Tip:** Players who reach this zone win the game.

#### Set Lobby Spawn

Stand where players should wait before the game starts:
```
/tgsg myarena setlobby
```

#### Set Spectator Spawn

Stand where eliminated/winning players should spectate from:
```
/tgsg myarena setspec
```

**Tip:** Choose a location with a good view of the arena.

#### Save Your Configuration

```
/tgsg myarena save
```

**Expected Output:**
```
Arena 'myarena' saved successfully!
```

---

## Creating Holi Festival Arena

Holi arenas are simpler to set up - no start zones or win zones needed!

### Step 1: Create the Arena File

```
/tgsg create holiarena Holi
```

Replace `holiarena` with your preferred arena name (no spaces).

**Expected Output:**
```
Arena 'holiarena' created successfully!
Configure it with: /tgsg holiarena setpos1, setpos2, etc.
```

### Step 2: Configuring Arena Positions

For Holi, you only need to define 4 key locations:

#### Define Arena Boundaries

Stand at one corner of your play area:
```
/tgsg holiarena setpos1
```

Walk to the opposite corner (diagonal):
```
/tgsg holiarena setpos2
```

**Tip:** Make the arena spacious (30x30 blocks or larger) so players have room to run and dodge.

#### Set Lobby Spawn

Stand where players should wait before the game starts:
```
/tgsg holiarena setlobby
```

#### Set Spectator Spawn

Stand where spectators should watch from (preferably elevated):
```
/tgsg holiarena setspec
```

**Tip:** Place spectator spawn above the arena for a bird's eye view.

#### Save Your Configuration

```
/tgsg holiarena save
```

**Expected Output:**
```
Arena 'holiarena' saved successfully!
```

---

## Using the GUI Editor

The GUI editor allows you to adjust arena settings without manually editing YAML files.

### Opening the Editor

```
/tgsg myarena edit
```

A graphical interface will open with the following options:

### Time Limit Setting

- **Item:** Clock
- **Action:** Click to cycle through time options (60s, 90s, 120s, 150s, 180s, etc.)
- **Purpose:** Sets how long the game runs before ending

### Barrier Toggle

- **Item:** Barrier block
- **Action:** Click to enable/disable barriers
- **Purpose:** When enabled, invisible barrier blocks surround the arena perimeter

### Sound Toggle

- **Item:** Note block
- **Action:** Click to enable/disable sounds
- **Purpose:** Controls whether players hear sound effects during the game

### Save Button

- **Item:** Emerald
- **Action:** Click to save all changes and exit
- **Purpose:** Commits your changes to the arena configuration file

### Canceling Changes

If you made changes you don't want to keep:
```
/tgsg myarena cancel
```

---

## Starting a Game

### Step 1: Verify Arena is Ready

```
/tgsg list
```

Your arena should show as "Ready" (green status). If it shows "Incomplete" (red), you need to set all positions.

### Step 2: Have Players Join

Players can join the arena with:
```
/tgsg myarena join
```

They will be teleported to the lobby spawn point.

### Step 3: Start the Game

When ready, an admin runs:
```
/tgsg myarena start
```

**What Happens:**
1. All players are teleported to random positions in the starting area
2. BossBar appears showing "GREEN LIGHT - GO!"
3. Barriers (if enabled) appear around the arena
4. Game timer begins

---

## Player Experience

### Red Light Green Light Experience

#### During Green Light

- **BossBar:** Shows "GREEN LIGHT - GO!" in green
- **Sound:** High-pitched chime sound plays
- **Action:** Players can move freely toward the win zone

#### During Red Light

- **BossBar:** Shows "RED LIGHT - STOP!" in red
- **Sound:** Low bass sound plays
- **Action:** Players must freeze completely
- **Penalty:** Moving even slightly results in instant elimination

#### Elimination

When eliminated:
- Player receives message: "You moved during RED LIGHT! You are eliminated."
- Instantly enters spectator mode
- Teleported to spectator spawn point
- Lightning sound effect plays
- Can watch the rest of the game

#### Winning

When a player reaches the win zone:
- Broadcast message: "Player has reached the finish line!"
- Player receives: "Congratulations! You won!"
- Victory sound plays
- Enters spectator mode to watch remaining players

#### Game End

The game ends when:
- Time runs out, OR
- All players are eliminated or have won

All players are returned to the lobby spawn point.

---

### Holi Festival Experience

#### Getting Started

When joining:
- Player receives Holi Pichkari (color spray gun)
- Item appears in inventory as a Blaze Rod
- Right-click to spray vibrant colors

#### During Gameplay

- **BossBar:** Shows "Holi Festival - Time remaining"
- **Objective:** Spray colors on other players
- **Scoring:** Each successful hit adds 1 point
- **Cooldown:** 3 seconds between sprays
- **Effects:** Hit players glow with color particles

#### Spraying Colors

How to use the Pichkari:
1. Aim at another player
2. Right-click to spray
3. Colorful particles shoot forward
4. Hit displays particle burst on target
5. Target glows and shows color effects

#### Getting Hit

When sprayed with color:
- Screen title shows "COLORED!"
- Your character glows
- Colorful particles surround you for 10 seconds
- Message shows who colored you

#### Game End

The game ends when:
- Time limit is reached (default 10 minutes)

At game end:
- Leaderboard displays top 5 players
- Champion gets victory title
- Fireworks celebration
- All players return to lobby

---

## Advanced Configuration

### Manual YAML Editing

Arena files are located in `plugins/TG-SquidGame/arenas/`

Example `myarena.yml`:
```yaml
arena:
  name: "myarena"
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
```

### Custom GUI Configuration

You can customize the GUI by editing the `gui` section:

```yaml
gui:
  name: "&6Custom Arena Settings"
  size: 27  # Must be multiple of 9
  items:
    timeLimit:
      slot: 11  # Inventory slot (0-26)
      name: "&a⏳ Custom Time Name"
      lore:
        - "&7Your custom description"
        - "&7Second line"
```

### Random Logic Options

- `complex`: Light phases last 4-10 seconds (unpredictable)
- `simple`: Light phases last 5-10 seconds (slightly more predictable)

Edit in the arena file:
```yaml
randomLogic: "complex"
```

### Global Settings

Edit `plugins/TG-SquidGame/config.yml`:

```yaml
settings:
  prefix: "&6[TG SquidGame]&r "
  defaultTimeLimit: 180
  enableJoinMessage: true
  soundEffects: true
  defaultBossBarColor: "GREEN"
  useComplexRandomLogic: true

holi:
  duration: 600  # 10 minutes
  pichkari:
    name: "&bHoli Pichkari"
    cooldown: 3  # seconds
    range: 10.0  # blocks
  colors:
    - RED
    - BLUE
    - GREEN
    - YELLOW
    - PURPLE
    - ORANGE
  fireworks: true
```

After editing, reload:
```
/tgsg reload
```

---

## Troubleshooting

### Problem: "Arena is not fully configured"

**Solution:** Check required positions based on game type:

For Red Light Green Light (8 positions required):
- pos1 and pos2 (arena boundaries)
- startPos1 and startPos2 (spawn area)
- winPos1 and winPos2 (finish line)
- lobby (waiting area)
- spectator (observer location)

For Holi (4 positions required):
- pos1 and pos2 (arena boundaries)
- lobby (waiting area)
- spectator (observer location)

Verify with:
```
/tgsg list
```

### Problem: Players can leave the arena during the game

**Solution:** Check that pos1 and pos2 properly enclose the entire play area.

### Problem: "World 'worldname' not found"

**Solution:** The arena file references a world that doesn't exist. Edit the arena file and change the `world:` value to match your world name (usually "world", "world_nether", or "world_the_end").

### Problem: Barriers don't appear

**Solution:**
1. Check if barriers are enabled: `/tgsg myarena edit`
2. Ensure pos1 and pos2 are set correctly
3. Try disabling and re-enabling:
   ```
   /tgsg myarena disablebarrier
   /tgsg myarena enablebarrier
   /tgsg myarena save
   ```

### Problem: Players aren't eliminated during red light

**Solution:** This is by design - only significant movement triggers elimination. The threshold is very small (0.15 blocks), but players must actually move, not just look around.

### Problem: Game won't start

**Solution:** Check these requirements:
1. Arena must be fully configured (all positions set)
2. At least one player must have joined with `/tgsg myarena join`
3. No game is currently running in that arena

### Problem: GUI items don't work

**Solution:**
1. Make sure you opened the GUI with `/tgsg myarena edit`
2. Verify you're in edit mode before clicking items
3. Try closing and reopening the GUI

---

## Building a Great Arena

### Red Light Green Light Design Tips

1. **Distance:** Make the start-to-finish distance 30-50 blocks for balanced gameplay
2. **Height:** Keep the arena relatively flat or use gentle slopes
3. **Visibility:** Ensure players can see the win zone from the starting area
4. **Spectator View:** Position the spectator spawn point above or to the side for a good view
5. **Lobby:** Place the lobby outside the arena boundaries
6. **Width:** Allow 10-20 blocks of width so players can spread out

#### Example Coordinates (Red Light Green Light)

For a basic 40-block straightaway arena:

```
/tgsg myarena setpos1        → Stand at 100, 64, 100
/tgsg myarena setpos2        → Stand at 100, 74, 200
/tgsg myarena setstart1      → Stand at 100, 64, 100
/tgsg myarena setstart2      → Stand at 110, 64, 110
/tgsg myarena setwin1        → Stand at 100, 64, 190
/tgsg myarena setwin2        → Stand at 110, 64, 200
/tgsg myarena setlobby       → Stand at 105, 64, 90
/tgsg myarena setspec        → Stand at 105, 80, 150
```

### Holi Festival Design Tips

1. **Size:** Make the arena 30x30 blocks or larger for movement space
2. **Obstacles:** Add pillars, walls, or structures for tactical gameplay
3. **Height:** Multiple levels add vertical gameplay dimension
4. **Open Areas:** Balance cover with open spaces for chasing
5. **Spectator View:** Elevated position for best view of action
6. **Theme:** Decorate with colorful blocks to match festival vibe

#### Example Coordinates (Holi)

For a 40x40 block arena:

```
/tgsg holiarena setpos1      → Stand at 200, 64, 200
/tgsg holiarena setpos2      → Stand at 240, 74, 240
/tgsg holiarena setlobby     → Stand at 220, 64, 190
/tgsg holiarena setspec      → Stand at 220, 85, 220
```

---

## Quick Reference Card

### Essential Commands

| Task | Command |
|------|---------|
| Create Red Light arena | `/tgsg create <name> RedLightGreenLight` |
| Create Holi arena | `/tgsg create <name> Holi` |
| Set boundaries | `/tgsg <name> setpos1` and `setpos2` |
| Set spawn area (RLGL only) | `/tgsg <name> setstart1` and `setstart2` |
| Set win zone (RLGL only) | `/tgsg <name> setwin1` and `setwin2` |
| Set lobby | `/tgsg <name> setlobby` |
| Set spectator | `/tgsg <name> setspec` |
| Save arena | `/tgsg <name> save` |
| Edit settings | `/tgsg <name> edit` |
| Join arena | `/tgsg <name> join` |
| Leave arena | `/tgsg leave` |
| Start game | `/tgsg <name> start` |
| Stop game | `/tgsg <name> stop` |
| List arenas | `/tgsg list` |
| Reload config | `/tgsg reload` |

---

## Next Steps

Now that you have your first arena set up:

1. Test both game modes with friends
2. Adjust time limits and settings based on feedback
3. Build multiple arenas of different types
4. Experiment with different arena layouts and themes
5. For Red Light Green Light: Create obstacle courses or themed environments
6. For Holi: Add strategic cover and multiple levels for tactical gameplay
7. Mix both game types for variety in events

Both game modes offer unique experiences - use Red Light Green Light for competitive tournaments and Holi for casual fun events!

---

**Developed by Techinpoint Gamerz (TG)**
**Version 1.2**
**Server Type:** Paper 1.21.1+

Enjoy your Squid Game experience!
