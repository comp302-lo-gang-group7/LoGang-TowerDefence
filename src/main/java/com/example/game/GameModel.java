package com.example.game;

import javafx.scene.image.Image;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashSet;

public class GameModel
{
	private final ArrayList<Tower> towers;

	public final GameMap map;

	private final HashSet<GameEventListener> listeners;

	public enum TowerType
	{
		ARCHER,
		MAGE,
		ARTILLERY
	}

	private final Image archerSprite, blankTileSprite, emptyLotSprite;

	public GameModel() throws FileNotFoundException
	{
		archerSprite = new Image(new FileInputStream("src/main/resources/tower_archer.png"));
		blankTileSprite = new Image(new FileInputStream("src/main/resources/blank_tile.png"));
		emptyLotSprite = new Image(new FileInputStream("src/main/resources/empty_lot.png"));

		map = new GameMap(3, 3);

		for ( int y = 0; y < map.getHeight(); y++ )
		{
			for ( int x = 0; x < map.getWidth(); x++ )
			{
				map.setTile(x, y, new BlankTile(x, y, blankTileSprite));
			}
		}
		map.setTile(0, 0, new EmptyLotTile(0, 0, emptyLotSprite));
		towers = new ArrayList<>();
		listeners = new HashSet<>();
	}

	private void createTower(int x, int y, TowerType type)
	{
		Tower tower;
		switch (type)
		{
			case ARCHER:
			{
				tower = new ArcherTower(x, y, archerSprite, 10, 10);
				break;
			}
			case MAGE:
			{
				tower = new MageTower(x, y, archerSprite, 5, 30);
				break;
			}
			case ARTILLERY:
			{
				tower = new ArtilleryTower(x, y, archerSprite, 7, 50);
				break;
			}
			default:
				throw new IllegalArgumentException("Invalid tower type");
		}
		towers.add(tower);
		map.setTile(x, y, tower);

		postEvent(new GameDataEvent<>(GameEvent.GameEventType.MESSAGE, "Tower created"));
		postEvent(new GameDataEvent<>(GameEvent.GameEventType.REPAINT, tower));
	}

	public void addListener(GameEventListener listener)
	{
		listeners.add(listener);
	}

	public void removeListener(GameEventListener listener)
	{
		listeners.remove(listener);
	}

	private void postEvent(GameEvent event)
	{
		for ( GameEventListener listener : listeners )
		{
			listener.handle(event);
		}
	}

	public void onTileClicked(int x, int y)
	{
		if ( map.getTile(x, y) instanceof EmptyLotTile )
		{
			createTower(x, y, TowerType.ARCHER);
		}
	}
}
