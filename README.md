# Clash of Towers

**Clash of Towers** is a tower defense game where players strategically place different types of towers to defend against waves of enemies.  
The game includes a custom map editor, configurable difficulty settings, gold and tower management, and a wave spawning system.  
Players must survive all enemy waves without letting their hit points drop to zero.

---

## Project Setup

We highly recommend using **IntelliJ IDEA** as your main development environment for this project.

- IntelliJ offers excellent Java and JavaFX support out of the box.
- Scene Builder can be directly integrated inside IntelliJ IDEA to design `.fxml` files visually.
- Maven is used for project management and dependency handling.

---

## Tools and Technologies

- **Java 23+**
- **JavaFX 25**
- **Maven** (for build and dependency management)
- **IntelliJ IDEA** (IDE recommendation)
- **Gluon Scene Builder** (for JavaFX UI design)

---

## How to Use Scene Builder in IntelliJ

1. Download and install **Gluon Scene Builder** from [gluonhq.com](https://gluonhq.com/products/scene-builder/).
2. In IntelliJ IDEA:
   - Right-click any `.fxml` file → **Open In SceneBuilder**.
   - If not configured automatically, you can set the Scene Builder path under **Settings → Languages & Frameworks → JavaFX**.
3. This allows you to visually edit your game's UI screens directly from IntelliJ!

---

## How to Build and Run

1. Open the project in **IntelliJ IDEA**.
2. Make sure you have **Maven** installed or let IntelliJ handle it automatically.
3. Execute `mvn clean javafx:run` from the project root. The Maven plugin launches the
   `com.example.game` module with `com.example.main.Main` as the main class.
4. The game window should appear. From there, you can:
   - Start a new game
   - Create and edit maps
   - Save, load, and play through waves of enemies

---

## Basic Game Idea

- Place towers during the **grace period** before waves.
- Each wave spawns enemies that try to reach your base.
- Towers attack automatically when enemies are in range.
- Earn **gold** for defeating enemies and spend it to build or upgrade towers.
- Survive all waves without letting your **hit points** reach zero to win the game.
- Create your own **custom maps** with the Map Editor!

---
