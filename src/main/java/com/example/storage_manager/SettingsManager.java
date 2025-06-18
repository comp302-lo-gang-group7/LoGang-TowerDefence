package com.example.storage_manager;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * A utility class for managing user settings by persisting them as JSON files.
 */
public class SettingsManager {

    private static final Path SETTINGS_FILE = Paths.get("cot", "data", "settings.json");

    /**
     * Represents the user settings data structure.
     */
    public static class Settings {
        /**
         * The volume level for music, ranging from 0 to 100.
         */
        public int musicVolume = 75;

        /**
         * The volume level for sound effects, ranging from 0 to 100.
         */
        public int sfxVolume = 100;

        /**
         * The difficulty level of the game (e.g., "Easy", "Normal", "Hard").
         */
        public String difficulty = "Normal";

        /**
         * The speed of the game (e.g., "Slow", "Normal", "Fast").
         */
        public String gameSpeed = "Normal";

        /**
         * Indicates whether hints should be displayed during gameplay.
         */
        public boolean showHints = true;

        /**
         * Indicates whether the game should automatically save progress.
         */
        public boolean autoSave = true;

        /**
         * Indicates whether the game should run in fullscreen mode.
         */
        public boolean fullscreen = false;

        /**
         * Indicates whether the frames-per-second (FPS) counter should be displayed.
         */
        public boolean showFps = false;
    }

    /**
     * Loads the user settings from the disk. If the settings file does not exist,
     * default settings are returned.
     *
     * @return An instance of {@link Settings} containing the loaded or default settings.
     * @throws UncheckedIOException If an error occurs while reading the settings file.
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
     * Saves the provided user settings to the disk.
     *
     * @param settings An instance of {@link Settings} containing the settings to be saved.
     * @throws UncheckedIOException If an error occurs while writing the settings file.
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

    /**
     * Private constructor to prevent instantiation of this utility class.
     */
    private SettingsManager() {}
}