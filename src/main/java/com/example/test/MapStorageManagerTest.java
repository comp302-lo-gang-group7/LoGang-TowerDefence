package com.example.test;
import com.example.map.TileEnum;
import com.example.map.TileView;
import com.example.storage_manager.MapStorageManager;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import java.util.UUID;
import javafx.scene.image.Image;
import javafx.scene.image.WritableImage;

import static org.junit.jupiter.api.Assertions.*;

public class MapStorageManagerTest
{
	@TempDir
	private Path tempMap;

	private TileView[][] loadTestMap(String jsonMap) throws IOException
	{
		Path testMap = tempMap.resolve("testMap.json");
		Files.write(testMap, jsonMap.getBytes());
		return MapStorageManager.loadMap(testMap.toAbsolutePath().toString().replace(".json", ""));
	}

	@Test
	public void loadMap_validMap() throws IOException
	{
		TileView[][] loadedMap = loadTestMap("{\"rows\":2, \"cols\":2, \"tiles\": [ [0,1], [1,0] ]}");

		assertEquals(2, loadedMap.length);
		assertEquals(2, loadedMap[0].length);
		assertEquals(TileEnum.fromFlatIndex(0), loadedMap[0][0].getType());
		assertEquals(TileEnum.fromFlatIndex(1), loadedMap[0][1].getType());
		assertEquals(TileEnum.fromFlatIndex(1), loadedMap[1][0].getType());
		assertEquals(TileEnum.fromFlatIndex(0), loadedMap[1][1].getType());
	}

	@Test
	public void loadMap_mapNotExist()
	{
		assertThrows(
				IOException.class,
				() -> MapStorageManager.loadMap("this_map_does_not_exist"));
	}

	@Test
	public void loadMap_emptyMap() throws IOException
	{
		TileView[][] loadedMap = loadTestMap("{\"rows\":0, \"cols\":0, \"tiles\": []}");

		assertEquals(0, loadedMap.length);
	}

	@Test
	public void loadMap_invalidTileType()
	{
		assertThrows(IllegalArgumentException.class, () ->
				loadTestMap("{\"rows\":1, \"cols\":1, \"tiles\": [ [1984] ]}"));
	}

    private TileView stubTile(TileEnum type) {
        Image px = new WritableImage(1, 1);
        return new TileView(px, type);
    }

    @AfterEach
    void cleanUp() {
        for (Path p : createdFiles) {
            try {
                Files.deleteIfExists(p);
            } catch (IOException ignored) {
                // test artefacts – safe to ignore
            }
        }
        createdFiles.clear();
    }

	private final List<Path> createdFiles = new ArrayList<>();

	@Test
    void saveMap_validRoundTrip() throws Exception {
        String mapName = "saveMapValid_" + UUID.randomUUID();     // unique every run

        TileView[][] tiles = {
                { stubTile(TileEnum.GRASS), stubTile(TileEnum.HORIZONTAL_PATH) },
                { stubTile(TileEnum.VERTICAL_PATH), stubTile(TileEnum.GRASS) }
        };

        MapStorageManager.saveMap(tiles, 2, 2, mapName);

        Path out = MapStorageManager.getMapDirectory().resolve(mapName + ".json");
        createdFiles.add(out);                                    // register for cleanup

        assertAll("file written",
                () -> assertTrue(Files.exists(out)),
                () -> assertTrue(Files.size(out) > 0, "file should not be empty")
        );

        // quick JSON sanity-check
        ObjectMapper om = new ObjectMapper();
        JsonNode root = om.readTree(out.toFile());
        assertEquals(2, root.get("rows").asInt());
        assertEquals(2, root.get("cols").asInt());

        // full round-trip using loadMap
        TileView[][] roundTrip = MapStorageManager.loadMap(mapName);
        assertEquals(TileEnum.GRASS, roundTrip[0][0].getType());
        assertEquals(TileEnum.HORIZONTAL_PATH, roundTrip[0][1].getType());
        assertEquals(TileEnum.VERTICAL_PATH, roundTrip[1][0].getType());
        assertEquals(TileEnum.GRASS, roundTrip[1][1].getType());
    }

	 @Test
    void saveMap_overwriteReplacesContent() throws Exception {
        String mapName = "saveMapOverwrite_" + UUID.randomUUID();

        TileView[][] first = {
                { stubTile(TileEnum.GRASS), stubTile(TileEnum.GRASS) },
                { stubTile(TileEnum.GRASS), stubTile(TileEnum.GRASS) }
        };

        MapStorageManager.saveMap(first, 2, 2, mapName);

        TileView[][] second = {
                { stubTile(TileEnum.HORIZONTAL_PATH), stubTile(TileEnum.HORIZONTAL_PATH) },
                { stubTile(TileEnum.HORIZONTAL_PATH), stubTile(TileEnum.HORIZONTAL_PATH) }
        };

        MapStorageManager.saveMap(second, 2, 2, mapName);   // overwrite

        Path out = MapStorageManager.getMapDirectory().resolve(mapName + ".json");
        createdFiles.add(out);

        TileView[][] roundTrip = MapStorageManager.loadMap(mapName);
        // every tile should now be HORIZONTAL_PATH
        for (TileView[] row : roundTrip)
            for (TileView tv : row)
                assertEquals(TileEnum.HORIZONTAL_PATH, tv.getType(),
                        "old content still present → overwrite failed");
    }

	 @Test
    void saveMap_emptyMapProducesEmptyJson() throws Exception {
        String mapName = "saveMapEmpty_" + UUID.randomUUID();

        MapStorageManager.saveMap(new TileView[0][0], 0, 0, mapName);

        Path out = MapStorageManager.getMapDirectory().resolve(mapName + ".json");
        createdFiles.add(out);

        assertTrue(Files.exists(out));

        ObjectMapper om = new ObjectMapper();
        JsonNode root = om.readTree(out.toFile());
        assertEquals(0, root.get("rows").asInt());
        assertEquals(0, root.get("cols").asInt());
        assertTrue(root.get("tiles").isEmpty());
    }


}
