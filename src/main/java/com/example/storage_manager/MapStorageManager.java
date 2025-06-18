package com.example.storage_manager;

import com.example.map.TileEnum;
import com.example.map.TileView;
import com.example.utils.TileRenderer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import javafx.scene.layout.Pane;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Handles reading and writing of user created maps to disk. Maps are stored as
 * JSON describing the tile grid.
 */
public class MapStorageManager {

    private static final int TILE_SIZE = 64;
    private static final Path MAP_DIRECTORY = Paths.get("cot", "data", "maps");

    // reuse a single renderer so grass-stitching logic lives in one place
    private static final TileRenderer RENDERER =
            new TileRenderer("/com/example/assets/tiles/Tileset-64x64.png", TILE_SIZE);

    /**
     * Saves the given map as a JSON file to cot/data/maps.
     */
    public static void saveMap(TileView[][] mapTiles, int rows, int cols, String mapName) {
        ObjectMapper mapper = new ObjectMapper();

        ObjectNode root = mapper.createObjectNode();
        root.put("rows", rows);
        root.put("cols", cols);

        ArrayNode tilesNode = mapper.createArrayNode();
        for (int r = 0; r < rows; r++) {
            ArrayNode rowNode = mapper.createArrayNode();
            for (int c = 0; c < cols; c++) {
                rowNode.add(mapTiles[r][c].getType().getFlatIndex());
            }
            tilesNode.add(rowNode);
        }

        root.set("tiles", tilesNode);

        Path outputPath = MAP_DIRECTORY.resolve(mapName + ".json");
        try {
            Files.createDirectories(MAP_DIRECTORY);
            mapper.writerWithDefaultPrettyPrinter()
                    .writeValue(outputPath.toFile(), root);
        } catch (IOException e) {
            throw new UncheckedIOException("Error saving map", e);
        }
    }

    /**
     * Loads a map JSON file from cot/data/maps, returning a TileView[][]
     * where each TileView is produced by your TileRenderer (so you get
     * grass-underlay, seams-fixing, etc.).
     * @requires: mapName.json exists in the cot/data/maps folder
     * @effects: returns a TileVew[][] map, with each tile as specified in the .json
     */
    public static TileView[][] loadMap(String mapName) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        Path filePath = MAP_DIRECTORY.resolve(mapName + ".json");

        if (!Files.exists(filePath)) {
            throw new IOException("Map file not found: " + filePath.toAbsolutePath());
        }

        JsonNode root = mapper.readTree(filePath.toFile());
        int rows = root.get("rows").asInt();
        int cols = root.get("cols").asInt();
        JsonNode tiles = root.get("tiles");

        TileView[][] map = new TileView[rows][cols];

        for (int r = 0; r < rows; r++) {
            JsonNode rowNode = tiles.get(r);
            for (int c = 0; c < cols; c++) {
                int flatIndex = rowNode.get(c).asInt();
                TileEnum type = TileEnum.fromFlatIndex(flatIndex);

                // **use the renderer** instead of manual slicing
                TileView tv = RENDERER.createTileView(type);
                tv.setFitWidth(TILE_SIZE);
                tv.setFitHeight(TILE_SIZE);
                tv.setPreserveRatio(false);

                map[r][c] = tv;
            }
        }

        return map;
    }

    /**
     * Delete the specified map file if it exists.
     *
     * @param mapName name of the map without extension
     * @return true if the file was removed
     */
    public static boolean deleteMap(String mapName) {
        Path file = MAP_DIRECTORY.resolve(mapName + ".json");
        try {
            return Files.deleteIfExists(file);
        } catch (IOException e) {
            throw new UncheckedIOException("Could not delete map " + mapName, e);
        }
    }

    /**
     * Returns a list of all available map names (without the .json extension)
     * found in the maps directory.
     */
    public static List<String> listAvailableMaps() {
        try {
            Files.createDirectories(MAP_DIRECTORY);
            List<String> mapNames = new ArrayList<>();
            try (DirectoryStream<Path> stream = Files.newDirectoryStream(MAP_DIRECTORY, "*.json")) {
                for (Path entry : stream) {
                    String fileName = entry.getFileName().toString();
                    mapNames.add(fileName.substring(0, fileName.length() - 5));
                }
            }
            return mapNames;
        } catch (IOException e) {
            throw new UncheckedIOException("Failed to list map files", e);
        }
    }

    public static Path getMapDirectory()
    {
        return MAP_DIRECTORY;
    }
}
