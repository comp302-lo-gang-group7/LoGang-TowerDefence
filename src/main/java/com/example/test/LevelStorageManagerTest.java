package com.example.test;

import com.example.config.LevelConfig;
import com.example.entity.EntityGroup;
import com.example.game.Wave;
import com.example.storage_manager.LevelStorageManager;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Class LevelStorageManagerTest
 */
class LevelStorageManagerTest {
    @Test
    void loadKnownLevel() throws Exception {
        LevelConfig cfg = LevelStorageManager.loadLevel("test_level");

        assertEquals("Forest Path", cfg.getMapName());
        assertEquals(1000, cfg.getStartingGold());
        assertEquals(10, cfg.getLives());

        List<Wave> waves = cfg.getWaves();
        assertEquals(2, waves.size());

        EntityGroup g0 = waves.get(0).group;
        assertEquals(5, g0.goblins);
        assertEquals(0, g0.warriors);
        assertEquals(5.0, g0.delayAfter);

        EntityGroup g1 = waves.get(1).group;
        assertEquals(0, g1.goblins);
        assertEquals(3, g1.warriors);
        assertEquals(6.0, g1.delayAfter);
    }
}