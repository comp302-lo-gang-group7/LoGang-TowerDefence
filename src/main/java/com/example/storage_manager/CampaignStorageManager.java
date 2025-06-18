package com.example.storage_manager;

import com.example.config.CampaignLevel;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;

/**
 * Manages the storage and retrieval of campaign configuration data.
 */
public class CampaignStorageManager {
    private static final Path CAMPAIGN_FILE = Paths.get("cot", "data", "campaign.json");

    /**
     * Loads the campaign configuration from a JSON file.
     *
     * @return a list of {@link CampaignLevel} objects representing the campaign configuration.
     *         Returns an empty list if the file does not exist or an error occurs during reading.
     */
    public static List<CampaignLevel> loadCampaign() {
        ObjectMapper mapper = new ObjectMapper();
        if (Files.exists(CAMPAIGN_FILE)) {
            try {
                return mapper.readValue(CAMPAIGN_FILE.toFile(), new TypeReference<List<CampaignLevel>>() {});
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return Collections.emptyList();
    }
}