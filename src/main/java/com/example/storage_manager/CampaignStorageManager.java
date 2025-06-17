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
 * Class CampaignStorageManager
 */
public class CampaignStorageManager {
    /**
     * TODO
     */
    private static final Path CAMPAIGN_FILE = Paths.get("cot", "data", "campaign.json");

    /**
     * TODO
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