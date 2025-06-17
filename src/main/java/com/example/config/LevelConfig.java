package com.example.config;

import com.example.game.Wave;
import java.util.List;


/**
 * Class LevelConfig
 */
public class LevelConfig {
    private String mapName;
    private int startingGold;
    private int lives;
    private List<Wave> waves;

    /**
     * TODO
     */
    public LevelConfig() {}

    /**
     * TODO
     */
    public LevelConfig(String mapName, int startingGold, int lives, List<Wave> waves) {
        this.mapName = mapName;
        this.startingGold = startingGold;
        this.lives = lives;
        this.waves = waves;
    }

    /**
     * TODO
     */
    public String getMapName() {
        return mapName;
    }

    /**
     * TODO
     */
    public int getStartingGold() {
        return startingGold;
    }

    /**
     * TODO
     */
    public int getLives() {
        return lives;
    }

    /**
     * TODO
     */
    public List<Wave> getWaves() {
        return waves;
    }
}