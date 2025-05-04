package com.example.game;

import com.example.entity.ArcherTower;
import com.example.entity.ArtilleryTower;
import com.example.entity.MageTower;
import com.example.entity.Tower;
import com.example.map.BlankTile;
import com.example.map.EmptyLotTile;
import com.example.map.Entity;
import com.example.map.GameMap;
import com.example.ui.ImageLoader;
import com.example.ui.SpriteProvider;
import com.example.utils.Point;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.image.Image;
import java.security.SecureRandom;


import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashSet;

public class GameModel
{
	private final ArrayList<Tower> towers;

	public final GameMap map;

	private final HashSet<GameEventListener> listeners;

	public StringProperty debugMessage = new SimpleStringProperty();

	public enum TowerType
	{
		ARCHER,
		MAGE,
		ARTILLERY
	}

	public GameModel()
	{
		map = new GameMap(10, 10);

		for ( int y = 0; y < map.getHeight(); y++ )
		{
			for ( int x = 0; x < map.getWidth(); x++ )
			{
				map.setTile(x, y, new EmptyLotTile(x, y, ImageLoader.getImage("/com/example/assets/towers/TowerSlotwithoutbackground128.png")));
			}
		}
		towers = new ArrayList<>();
		listeners = new HashSet<>();
	}

	public Tower createTower( int x, int y, TowerType type )
	{
		Tower tower;
		switch (type)
		{
			case ARCHER:
			{
				tower = new ArcherTower(x, y, 10, 10, ImageLoader.getImage("/com/example/assets/towers/Tower_archer256.png"));
				break;
			}
			case MAGE:
			{
				tower = new MageTower(x, y, 5, 30, ImageLoader.getImage("/com/example/assets/towers/Tower_spell256.png"));
				break;
			}
			case ARTILLERY:
			{
				tower = new ArtilleryTower(x, y, 7, 50, ImageLoader.getImage("/com/example/assets/towers/tower_bomb256.png"));
				break;
			}
			default:
			{
				tower = null;
				System.err.println("Unhandled Tower type: " + type);
				break;
			}
		}
		towers.add(tower);

		map.setTile(x, y, tower);

		debugMessage.set(String.format("%s tower placed at (%d, %d)", type.name(), x, y));

		return tower;
	}

	public boolean isValidConstructionLot( int x, int y )
	{
		return map.getTile(x, y) instanceof EmptyLotTile;
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
