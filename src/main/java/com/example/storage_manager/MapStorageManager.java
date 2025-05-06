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

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.util.Objects;

public class MapStorageManager {

    private static final int TILE_SIZE = 64;
    private static final Path MAP_DIRECTORY = Paths.get("cot", "data", "maps");

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
            Files.createDirectories(MAP_DIRECTORY); // Ensure directories exist
            mapper.writerWithDefaultPrettyPrinter().writeValue(outputPath.toFile(), root);
            System.out.println("Map saved to: " + outputPath.toAbsolutePath());
        } catch (IOException e) {
            System.err.println("Error saving map: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Loads a map JSON file from cot/data/maps.
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
