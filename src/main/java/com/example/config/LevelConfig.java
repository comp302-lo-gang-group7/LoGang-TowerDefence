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

    public LevelConfig(String mapName, int startingGold, int lives, List<Wave> waves) {
        this.mapName = mapName;
        this.startingGold = startingGold;
        this.lives = lives;
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