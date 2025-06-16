package com.example.storage_manager;

import com.example.config.LevelConfig;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;

/** Utility methods for reading level configuration files. */
public class LevelStorageManager {
    private static final Path LEVEL_DIRECTORY = Paths.get("cot","data","levels");

    /**
     * Loads a level configuration from cot/data/levels.
     * @param levelName name of the level file without extension
     * @return parsed LevelConfig
     * @throws IOException if the file cannot be read
     */
    public static LevelConfig loadLevel(String levelName) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        Path filePath = LEVEL_DIRECTORY.resolve(levelName + ".json");
        if (!Files.exists(filePath)) {
            throw new IOException("Level file not found: " + filePath.toAbsolutePath());
        }
        return mapper.readValue(filePath.toFile(), LevelConfig.class);
    }

    /** Lists all available level configuration files without extension. */
    public static List<String> listAvailableLevels() {
        try {
            Files.createDirectories(LEVEL_DIRECTORY);
            List<String> levelNames = new ArrayList<>();
            try (DirectoryStream<Path> stream = Files.newDirectoryStream(LEVEL_DIRECTORY, "*.json")) {
                for (Path entry : stream) {
                    String fileName = entry.getFileName().toString();
                    levelNames.add(fileName.substring(0, fileName.length() - 5));
                }
            }
            return levelNames;
        } catch (IOException e) {
            throw new UncheckedIOException("Failed to list level files", e);
        }
    }
}