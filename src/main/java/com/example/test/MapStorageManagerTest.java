package com.example.test;
import com.example.map.TileEnum;
import com.example.map.TileView;
import com.example.storage_manager.MapStorageManager;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Class MapStorageManagerTest
 */
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
}
