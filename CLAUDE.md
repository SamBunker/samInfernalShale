# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Build Commands

```bash
# Build the project
./gradlew build

# Clean build artifacts
./gradlew clean

# Compile Java sources
./gradlew compileJava
```

## Architecture Overview

This is a **RuneScape automation script** for Infernal Shale mining built on the **PowerBot SDK**. The architecture follows a hierarchical task-based system with event-driven state management.

### Core Components

- **`samInfernalShale.java`**: Main script entry point with event handlers for inventory changes and game messages
- **`TaskManager.java`**: Orchestrates task execution across multiple task lists based on priority
- **Task System**: Modular tasks organized into priority-based lists (Utility → Movement → Inventory → Mining)

### Task List Hierarchy (Priority Order)
1. **UtilityTaskList**: High-priority tasks (timing reset, running, special attacks)
2. **MovementTaskList**: Movement and positioning 
3. **InventoryTaskList**: Gem bag management and cloth handling
4. **MiningTaskList**: Core mining operations (3T, regular, AFK mining)

### Configuration System

The script uses `@ScriptConfiguration` annotations for user settings:
- **Mining Method**: Supports "3T Mining", "Mining", and "AFK Mining"
- **SelectedRocks**: User-selectable rocks for interaction
- Dynamic UI visibility based on selected mining method

### State Management

- **Variables.java**: Runtime counters (rocks mined, failures, crushed shale obtained)
- **Constants.java**: Game object IDs, areas, and item constants
- **Event-driven tracking**: Uses `@Subscribe` methods for inventory and message events

### Key Features

- **Multi-method mining**: 3-tick manipulation, regular mining, and AFK modes
- **Real-time price integration**: Fetches current prices from RuneScape Wiki API
- **Performance tracking**: Success rates, profit calculations, and failure analysis
- **Smart gem management**: Automatic gem bag handling with overflow detection

## Development Notes

- **PowerBot SDK**: Uses version `[1.+,1.0.99-SNAPSHOT)` with client-sdk and client-sdk-loader
- **Java + Kotlin**: Hybrid project with Kotlin 1.7.10 support
- **Event System**: Relies heavily on PowerBot's event bus for game state changes
- **Paint System**: Custom UI overlay showing real-time statistics and task status

## Mining Method Specifics

- **3T Mining**: Advanced tick manipulation using wet cloth timing reset
- **Regular Mining**: Standard mining with cloth preparation for efficiency
- **AFK Mining**: Passive approach with minimal interaction requirements

## Special Attack System

The `SpecialAttack` task requires:
- Special attack percentage at 100%
- Dragon/Crystal/Infernal pickaxe in inventory or equipped
- Pickaxe actually equipped (not just in inventory)

## Crushed Shale Tracking

The script tracks crushed infernal shale obtained from crushing operations via inventory change events and displays the count in the paint overlay.