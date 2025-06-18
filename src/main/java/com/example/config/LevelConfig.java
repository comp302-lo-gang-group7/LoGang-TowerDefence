package com.example.config;

import com.example.game.Wave;
import java.util.List;

/**
 * Represents the configuration for a campaign level. This includes the map
 * used for the level, starting resources and the waves that will be spawned.
 */
public class LevelConfig {
    private String mapName;
    private int startingGold;
    private int lives;
    private List<Wave> waves;

    public LevelConfig() {}

    /**
     * Represents the configuration for a level in the game, including map details,
     * starting resources, player lives, and wave information.
     */
    public LevelConfig(String mapName, int startingGold, int lives, List<Wave> waves) {
        /**
         * The name of the map associated with this level.
         */
        this.mapName = mapName;

        /**
         * The amount of gold the player starts with in this level.
         */
        this.startingGold = startingGold;

        /**
         * The number of lives the player has at the beginning of this level.
         */
        this.lives = lives;

        /**
         * The list of waves that define enemy spawns for this level.
         */
        this.waves = waves;
    }

    public String getMapName() {
        return mapName;
    }

    public int getStartingGold() {
        return startingGold;
    }

    public int getLives() {
        return lives;
    }

    public List<Wave> getWaves() {
        return waves;
    }
}