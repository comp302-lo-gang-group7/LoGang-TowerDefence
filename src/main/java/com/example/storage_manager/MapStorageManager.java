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
 * Provides functionality for saving, loading, deleting, and listing user-created maps.
 * Maps are stored as JSON files describing the tile grid.
 */
public class MapStorageManager {

    private static final int TILE_SIZE = 64;
    private static final Path MAP_DIRECTORY = Paths.get("cot", "data", "maps");
    private static final TileRenderer RENDERER =
            new TileRenderer("/com/example/assets/tiles/Tileset-64x64.png", TILE_SIZE);

    /**
     * Saves the given map as a JSON file to the specified directory.
     *
     * @param mapTiles a 2D array of TileView objects representing the map tiles
     * @param rows the number of rows in the map
     * @param cols the number of columns in the map
     * @param mapName the name of the map file to save (without extension)
     * @throws UncheckedIOException if an error occurs during saving
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
     * Loads a map from a JSON file and returns a 2D array of TileView objects.
     *
     * @param mapName the name of the map file to load (without extension)
     * @return a 2D array of TileView objects representing the map tiles
     * @throws IOException if the map file does not exist or cannot be read
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
     * Deletes the specified map file if it exists.
     *
     * @param mapName the name of the map file to delete (without extension)
     * @return true if the file was successfully deleted, false otherwise
     * @throws UncheckedIOException if an error occurs during deletion
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
     * Returns a list of all available map names in the maps directory.
     *
     * @return a list of map names (without the .json extension)
     * @throws UncheckedIOException if an error occurs while listing map files
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

    /**
     * Returns the path to the directory where map files are stored.
     *
     * @return the path to the map directory
     */
    public static Path getMapDirectory() {
        return MAP_DIRECTORY;
    }
}
