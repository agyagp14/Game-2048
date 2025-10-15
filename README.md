## 🎉 2048 Game - Java Implementation
A functional implementation of the popular 2048 game built with Java and Swing GUI, following functional programming principles.
https://img.shields.io/badge/Java-17-blue
https://img.shields.io/badge/Maven-3.6+-blue
https://img.shields.io/badge/GUI-Swing-green

## 🚀 How to Run
## Users just need:
-Java 17+ installed and Maven 3.6+, use git clone-> mvn clean compile -> run Main.java ,  Or
-Download the JAR from GitHub Releases And
Run: java -jar game-2048-1.0.0.jar

## 🎮 Features
- Classic 2048 gameplay
- Beautiful graphical interface
- Score tracking
- Win/lose detection
- Keyboard controls
- Restart functionality
- Configurable board size

## 🏗️ Implementation Details
**Architecture
The game follows a functional programming approach with:
->Immutable state: All game objects are immutable
->Pure functions: Operations return new instances instead of modifying state
->Side-effect free: No global state mutations

**Core Components
1. GameBoard.java
Responsibility: Main game logic and state management
Key Features:
-Immutable game state
-Tile movement and merging algorithms
-Win/lose condition detection
-Random tile generation
-Design Pattern: Functional state transitions

2. Tile.java
Responsibility: Represent individual game tiles
Key Features:
-Immutable tile properties (value, position, merge status)
-Functional updates with withPosition() and withMerged()
-Value-based equality

3. GameGUI.java
Responsibility: Graphical user interface
Key Features:
-Swing-based UI components
-Real-time board rendering
-Keyboard event handling
-Dynamic color schemes

4. Direction.java
Responsibility: Move direction enumeration
Key Features:
-Type-safe direction constants (UP, DOWN, LEFT, RIGHT)

## 🎯 Gameplay Instructions
Controls
↑ Arrow Key: Move tiles upward
↓ Arrow Key: Move tiles downward
← Arrow Key: Move tiles left
→ Arrow Key: Move tiles right
R Key: Restart the game
Close Window: Exit the game

## Rules
Each move shifts all tiles in the chosen direction
Tiles with the same value merge into one when they collide
After each move, a new tile (value 2 or 4) appears in a random empty space
Your score increases by the value of merged tiles
Game continues until you win (reach 2048) or lose (no moves left)


- <img width="741" height="827" alt="Screenshot (225)" src="https://github.com/user-attachments/assets/a024622d-6f23-4fca-a365-e922724cdc83" />




