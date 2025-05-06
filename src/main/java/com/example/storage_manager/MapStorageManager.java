package com.example.storage_manager;

import com.example.map.TileEnum;
import com.example.map.TileView;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;
import javafx.scene.image.WritableImage;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;

public class MapStorageManager {

    private static final int TILE_SIZE = 64;

    /**
     * Saves the given map as a JSON file using the provided name.
     *
     * @param mapTiles  The 2D array of TileView objects
     * @param rows      Number of rows in the map
     * @param cols      Number of columns in the map
     * @param mapName   The name of the map file (without .json)
     */
    public static void saveMap(TileView[][] mapTiles, int rows, int cols, String mapName) {
        ObjectMapper mapper = new ObjectMapper();

        // Create root JSON object
        ObjectNode root = mapper.createObjectNode();
        root.put("rows", rows);
        root.put("cols", cols);

        // Create a 2D array of flat indices
        ArrayNode tilesNode = mapper.createArrayNode();
        for (int r = 0; r < rows; r++) {
            ArrayNode rowNode = mapper.createArrayNode();
            for (int c = 0; c < cols; c++) {
                rowNode.add(mapTiles[r][c].getType().getFlatIndex());
            }
            tilesNode.add(rowNode);
        }

        root.set("tiles", tilesNode);

        // Write to file: src/main/resources/data/maps/<mapName>.json
        Path outputPath = Paths.get("src", "main", "resources", "data", "maps", mapName + ".json");
        try {
            Files.createDirectories(outputPath.getParent());
            mapper.writerWithDefaultPrettyPrinter().writeValue(outputPath.toFile(), root);
            System.out.println("Map saved to: " + outputPath);
        } catch (IOException e) {
            System.err.println("Error saving map: " + e.getMessage());
            e.printStackTrace();
        }
    }


    /**
     * Loads a saved map JSON file and rebuilds a 2D TileView[][] array.
     *
     * @param mapName the name of the map file (without .json)
     * @return the restored TileView 2D array
     * @throws IOException if the map cannot be loaded
     */
    public static TileView[][] loadMap(String mapName) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        String resourcePath = "/data/maps/" + mapName + ".json";
        InputStream stream = MapStorageManager.class.getResourceAsStream(resourcePath);
        if (stream == null) {
            throw new IOException("Map file not found: " + resourcePath);
        }

        JsonNode root = mapper.readTree(stream);
        int rows = root.get("rows").asInt();
        int cols = root.get("cols").asInt();
        JsonNode tiles = root.get("tiles");

        // Load tileset and prepare pixel reader
        Image tileset = new Image(Objects.requireNonNull(MapStorageManager.class.getResourceAsStream(
                "/com/example/assets/tiles/Tileset-64x64.png"
        )));
        PixelReader reader = tileset.getPixelReader();

        TileView[][] map = new TileView[rows][cols];

        for (int r = 0; r < rows; r++) {
            JsonNode rowNode = tiles.get(r);
            for (int c = 0; c < cols; c++) {
                int flatIndex = rowNode.get(c).asInt();
                TileEnum type = TileEnum.fromFlatIndex(flatIndex);

                int rowInSet = type.getRow();
                int colInSet = type.getCol();

                WritableImage tileImage = new WritableImage(
                        reader,
                        colInSet * TILE_SIZE,
                        rowInSet * TILE_SIZE,
                        TILE_SIZE,
                        TILE_SIZE
                );

                TileView tile = new TileView(tileImage, type);
                tile.setFitWidth(TILE_SIZE);
                tile.setFitHeight(TILE_SIZE);
                tile.setPreserveRatio(false);

                map[r][c] = tile;
            }
        }

        return map;
    }
}
