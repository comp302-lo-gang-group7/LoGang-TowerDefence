package com.example.storage_manager;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Utility class to persist user settings as JSON. Settings are stored under
 * {@code cot/data/settings.json} within the project.
 */
public class SettingsManager {

    private static final Path SETTINGS_FILE = Paths.get("cot", "data", "settings.json");

    /**
     * Data object representing user settings.
     */
    public static class Settings {
        public int musicVolume = 75;
        public int sfxVolume = 100;
        public String difficulty = "Normal";
        public String gameSpeed = "Normal";
        public boolean showHints = true;
        public boolean autoSave = true;
        public boolean fullscreen = false;
        public boolean showFps = false;
    }

    /**
     * Loads settings from disk. If no settings file exists, defaults are
     * returned.
     */
    public static Settings load() {
        ObjectMapper mapper = new ObjectMapper();
        if (Files.exists(SETTINGS_FILE)) {
            try {
                return mapper.readValue(SETTINGS_FILE.toFile(), Settings.class);
            } catch (IOException e) {
                throw new UncheckedIOException("Failed to read settings", e);
            }
        }
        return new Settings();
    }

    /**
     * Saves the provided settings to disk.
     */
    public static void save(Settings settings) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            Files.createDirectories(SETTINGS_FILE.getParent());
            mapper.writerWithDefaultPrettyPrinter().writeValue(SETTINGS_FILE.toFile(), settings);
        } catch (IOException e) {
            throw new UncheckedIOException("Failed to save settings", e);
        }
    }

    // prevent instantiation
    private SettingsManager() {}
}