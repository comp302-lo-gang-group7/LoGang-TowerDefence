package com.example.test;

import com.example.storage_manager.SettingsManager;
import org.junit.jupiter.api.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.*;

class SettingsManagerTest {
    private static final Path SETTINGS_FILE = Paths.get("cot", "data", "settings.json");
    private String originalContent;

    @BeforeEach
    void backup() throws IOException {
        if (Files.exists(SETTINGS_FILE)) {
            originalContent = Files.readString(SETTINGS_FILE);
        } else {
            originalContent = null;
        }
    }

    @AfterEach
    void restore() throws IOException {
        if (originalContent != null) {
            Files.writeString(SETTINGS_FILE, originalContent);
        } else {
            Files.deleteIfExists(SETTINGS_FILE);
        }
    }

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