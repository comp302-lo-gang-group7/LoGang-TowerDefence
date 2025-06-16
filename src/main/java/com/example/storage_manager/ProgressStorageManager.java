package com.example.storage_manager;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

/** Utility to persist star ratings for completed maps. */
public class ProgressStorageManager {
    private static final Path PROGRESS_FILE = Paths.get("cot", "data", "progress.json");

    /** Load progress file into a map of mapName -> stars. */
    public static Map<String, Integer> loadProgress() {
        ObjectMapper mapper = new ObjectMapper();
        if (Files.exists(PROGRESS_FILE)) {
            try {
                String content = Files.readString(PROGRESS_FILE).trim();
                if (content.isEmpty()) {
                    return new HashMap<>();
                }
                return mapper.readValue(content, new TypeReference<Map<String, Integer>>() {});
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return new HashMap<>();
    }



    /** Write the provided progress map to disk. */
    public static void saveProgress(Map<String, Integer> progress) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            // Remove null keys (Jackson can't serialize them)
            Map<String, Integer> cleaned = new HashMap<>();
            for (Map.Entry<String, Integer> entry : progress.entrySet()) {
                if (entry.getKey() != null) {
                    cleaned.put(entry.getKey(), entry.getValue());
                }
            }

            Files.createDirectories(PROGRESS_FILE.getParent());
            mapper.writerWithDefaultPrettyPrinter().writeValue(PROGRESS_FILE.toFile(), cleaned);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    /**
     * Update the rating for a map if the new stars exceed the existing value.
     */
    public static void recordRating(String mapName, int stars) {
        if (mapName == null) return;  // Defensive null check

        Map<String, Integer> data = loadProgress();
        int current = data.getOrDefault(mapName, 0);
        if (stars > current) {
            data.put(mapName, stars);
            saveProgress(data);
        }
    }

}