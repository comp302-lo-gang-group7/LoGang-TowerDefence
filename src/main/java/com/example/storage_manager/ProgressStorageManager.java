package com.example.storage_manager;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.JsonNode;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

/** Utility to persist star ratings for completed maps. */
public class ProgressStorageManager {
    private static final Path PROGRESS_FILE = Paths.get("cot", "data", "progress.json");
    public static class LevelProgress {
        public int stars;
        public long time;
    }

    /** Load progress file into a map of mapName -> stars. */
    public static Map<String, LevelProgress> loadProgress() {
        ObjectMapper mapper = new ObjectMapper();
        if (Files.exists(PROGRESS_FILE)) {
            try {
                String content = Files.readString(PROGRESS_FILE).trim();
                if (content.isEmpty()) {
                    return new HashMap<>();
                }
                // Support legacy format of Map<String,Integer>
                JsonNode node = mapper.readTree(content);
                Map<String, LevelProgress> result = new HashMap<>();
                if (node.isObject()) {
                    node.fields().forEachRemaining(entry -> {
                        LevelProgress lp = new LevelProgress();
                        JsonNode val = entry.getValue();
                        if (val.isInt()) {
                            lp.stars = val.asInt();
                            lp.time = 0;
                        } else {
                            lp.stars = val.has("stars") ? val.get("stars").asInt() : 0;
                            lp.time = val.has("time") ? val.get("time").asLong() : 0;
                        }
                        result.put(entry.getKey(), lp);
                    });
                }
                return result;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return new HashMap<>();
    }



    /** Write the provided progress map to disk. */
    public static void saveProgress(Map<String, LevelProgress> progress) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            Files.createDirectories(PROGRESS_FILE.getParent());
            mapper.writerWithDefaultPrettyPrinter().writeValue(PROGRESS_FILE.toFile(), progress);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    /**
     * Update the rating/time for a map if new values improve the existing ones.
     */
    public static void recordProgress(String mapName, int stars, long time) {
        if (mapName == null) return;

        Map<String, LevelProgress> data = loadProgress();
        LevelProgress existing = data.getOrDefault(mapName, new LevelProgress());
        if (stars > existing.stars) {
            existing.stars = stars;
        }
        if (existing.time == 0 || (time > 0 && time < existing.time)) {
            existing.time = time;
        }
        data.put(mapName, existing);
        saveProgress(data);
    }

}