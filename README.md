# 🌀 Maze Crawler

A fast-paced 2D maze navigation mini-game built in **Java Swing** for Programming 2 (14-day sprint project). Guide your character through hand-crafted mazes using only your arrow keys — but watch out: touching a wall is instant death. Race the clock, climb the stages, and earn your spot on the persistent leaderboard.

---

## 🎮 Game Description

**Maze Crawler** challenges players to navigate increasingly difficult maze layouts without colliding with any walls. The game features:

- **Username-based play** — enter your name on the main menu
- **3 Difficulty Levels** — Easy, Medium, and Hard, each with more stages and tighter mazes
- **Fixed Hand-Crafted Maps** — every stage is a designed challenge, not random
- **Points-Based Scoring** — earn points per stage cleared, plus a **time bonus** for fast runs
- **Persistent Leaderboard** — top scores are saved to disk and survive between sessions
- **Animated Main Menu** — soft gradient effects and smooth transitions
- **In-Game Sound** — synthesized step, bump, and death tones (toggleable)
- **Eye-Friendly Palette** — WCAG-AA contrast tuned colors (soft slate, teal, amber)

The project demonstrates **OOP** (encapsulated `Player`, `Maze`, `SoundManager`, `LeaderboardManager` classes), **data structures** (2D grid maze, sorted leaderboard list, `CardLayout` panel stack), and **game-loop logic** (Swing `Timer`, key-event collision detection).

---

## 🛠️ Setup Instructions

### Requirements
- **Java JDK 17 or higher** (tested on JDK 21)
- Any OS with a desktop environment (Windows / macOS / Linux)
- Optional: **VS Code** with the *Extension Pack for Java*, or any IDE / plain terminal

### Run from the terminal

1. Place `MazeCrawler.java` in any folder (no `src/` subfolder needed — it's a single-file build).
2. Open a terminal in that folder and run:

```bash
javac MazeCrawler.java
java MazeCrawler
```

### Run from VS Code

1. Open the folder containing `MazeCrawler.java` in VS Code.
2. Make sure the *Extension Pack for Java* is installed.
3. Open `MazeCrawler.java` and click **Run** above the `public class MazeCrawler` line — or press **F5**.

### Persistent data location

The leaderboard is saved automatically to:

```
~/.mazecrawler/leaderboard.dat
```

(Windows: `C:\Users\<you>\.mazecrawler\leaderboard.dat`)

Delete this file to reset your high scores.

---

## 🎯 Controls

| Action            | Key                         |
| ----------------- | --------------------------- |
| Move Up           | `↑` Arrow / `W`             |
| Move Down         | `↓` Arrow / `S`             |
| Move Left         | `←` Arrow / `A`             |
| Move Right        | `→` Arrow / `D`             |
| Pause / Menu      | `Esc`                       |
| Confirm / Select  | `Enter`                     |
| Toggle Sound      | From the **Settings** menu  |
| Quit Game         | From the **Main Menu**      |

**⚠️ Rule:** If your character touches a wall, you die and the current run resets. Reach the green **Exit** tile to clear the stage.

---

## 🏆 Scoring

```
Stage Score = (Base Stage Points × Difficulty Multiplier) + Time Bonus
```

| Difficulty | Stages | Multiplier |
| ---------- | ------ | ---------- |
| Easy       | 5      | ×1.0       |
| Medium     | 8      | ×1.5       |
| Hard       | 12     | ×2.0       |

The faster you clear a stage, the bigger the time bonus. Final scores are submitted to the leaderboard when you finish all stages of a chosen difficulty.

---

## 📁 Project Structure

```
MazeCrawler/
├── MazeCrawler.java     # Single-file source (all classes consolidated)
├── README.md            # This file
└── screenshots/         # Final build screenshots
    ├── main-menu.png
    ├── difficulty.png
    ├── gameplay.png
    ├── game-over.png
    └── leaderboard.png
```

### Internal classes (inside `MazeCrawler.java`)

| Class                | Responsibility                                         |
| -------------------- | ------------------------------------------------------ |
| `MazeCrawler`        | Entry point — boots the Swing UI on the EDT            |
| `GameFrame`          | Main window + `CardLayout` panel router                |
| `MainMenuPanel`      | Username input, animated background, navigation        |
| `SettingsPanel`      | Sound toggle and other preferences                     |
| `GamePanel`          | Renders the maze, handles input and the game loop      |
| `GameOverPanel`      | End-of-run summary + score submission                  |
| `LeaderboardPanel`   | Displays top scores                                    |
| `Maze`               | 2D grid model, walkable/wall/exit cell logic           |
| `Player`             | Position state and movement                            |
| `Difficulty`         | Enum: stage counts and score multipliers               |
| `MazeLibrary`        | Hand-crafted fixed layouts per difficulty              |
| `SoundManager`       | Synthesized tones via `javax.sound.sampled`            |
| `LeaderboardManager` | Disk persistence to `~/.mazecrawler/leaderboard.dat`   |

---

## 👥 Team & Course

- **Course:** Programming 2
- **Project:** 14-day Mini-Game Sprint
- **Language:** Java (Swing)
- **Team Members:** _Add your names here_
- **Repository:** _Add your GitHub link here_

---

## 📜 License

Educational project — free to study, fork, and remix for learning purposes.
