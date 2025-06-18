package com.example.test;

import com.example.config.LevelConfig;
import com.example.entity.EntityGroup;
import com.example.game.Wave;
import com.example.storage_manager.LevelStorageManager;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for the {@link LevelStorageManager}.
 * This class contains unit tests to verify the functionality of loading level configurations.
 */
class LevelStorageManagerTest {

    /**
     * Tests the {@link LevelStorageManager#loadLevel(String)} method by loading a known level configuration.
     *
     * @throws Exception if an error occurs during level loading.
     */
    @Test
    void loadKnownLevel() throws Exception {
        LevelConfig cfg = LevelStorageManager.loadLevel("test_level");

        // Verify the map name of the loaded level configuration.
        assertEquals("Forest Path", cfg.getMapName());
        
        // Verify the starting gold of the loaded level configuration.
        assertEquals(1000, cfg.getStartingGold());
        
        // Verify the number of lives in the loaded level configuration.
        assertEquals(10, cfg.getLives());

        // Verify the waves in the loaded level configuration.
        List<Wave> waves = cfg.getWaves();
        assertEquals(2, waves.size());

        // Verify the first wave's entity group details.
        EntityGroup g0 = waves.get(0).group;
        assertEquals(5, g0.goblins);
        assertEquals(0, g0.warriors);
        assertEquals(5.0, g0.delayAfter);

        // Verify the second wave's entity group details.
        EntityGroup g1 = waves.get(1).group;
        assertEquals(0, g1.goblins);
        assertEquals(3, g1.warriors);
        assertEquals(6.0, g1.delayAfter);
    }
}