package com.example.test;

import com.example.storage_manager.SettingsManager;
import org.junit.jupiter.api.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit test class for the {@link SettingsManager}.
 * Provides tests for loading and saving settings, as well as backup and restore functionality.
 */
class SettingsManagerTest {
    private static final Path SETTINGS_FILE = Paths.get("cot", "data", "settings.json");
    private String originalContent;

    /**
     * Backs up the original content of the settings file before each test.
     * 
     * @throws IOException if an I/O error occurs while reading the settings file.
     */
    @BeforeEach
    void backup() throws IOException {
        if (Files.exists(SETTINGS_FILE)) {
            originalContent = Files.readString(SETTINGS_FILE);
        } else {
            originalContent = null;
        }
    }

    /**
     * Restores the original content of the settings file after each test.
     * 
     * @throws IOException if an I/O error occurs while writing or deleting the settings file.
     */
    @AfterEach
    void restore() throws IOException {
        if (originalContent != null) {
            Files.writeString(SETTINGS_FILE, originalContent);
        } else {
            Files.deleteIfExists(SETTINGS_FILE);
        }
    }

    /**
     * Tests the round-trip functionality of saving and loading settings.
     * Ensures that the settings saved to the file are correctly loaded back.
     */
    @Test
    void loadAndSaveRoundTrip() {
        SettingsManager.Settings settings = new SettingsManager.Settings();
        settings.musicVolume = 10;
        settings.sfxVolume = 20;
        settings.difficulty = "Hard";
        settings.gameSpeed = "Fast";
        settings.showHints = false;
        settings.autoSave = false;
        settings.fullscreen = true;
        settings.showFps = true;

        SettingsManager.save(settings);
        SettingsManager.Settings loaded = SettingsManager.load();

        assertEquals(settings.musicVolume, loaded.musicVolume);
        assertEquals(settings.sfxVolume, loaded.sfxVolume);
        assertEquals(settings.difficulty, loaded.difficulty);
        assertEquals(settings.gameSpeed, loaded.gameSpeed);
        assertEquals(settings.showHints, loaded.showHints);
        assertEquals(settings.autoSave, loaded.autoSave);
        assertEquals(settings.fullscreen, loaded.fullscreen);
        assertEquals(settings.showFps, loaded.showFps);
    }
}