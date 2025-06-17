package com.example.storage_manager;

import com.example.config.LevelConfig;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;


/**
 * Class LevelStorageManager
 */
public class LevelStorageManager {
    /**
     * TODO
     */
    private static final Path LEVEL_DIRECTORY = Paths.get("cot","data","levels");


    /**
     * TODO
     */
    public static LevelConfig loadLevel(String levelName) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        Path filePath = LEVEL_DIRECTORY.resolve(levelName + ".json");
        if (!Files.exists(filePath)) {
            throw new IOException("Level file not found: " + filePath.toAbsolutePath());
        }
        return mapper.readValue(filePath.toFile(), LevelConfig.class);
    }


    /**
     * TODO
     */
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