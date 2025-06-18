package com.example.game;

import com.example.entity.AnimatedEntity;
import com.example.entity.Entity;
import com.example.entity.Tower;
import com.example.map.GameMap;
import com.example.map.TileView;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

/**
 * Represents the model for the game, managing the game map, entities, and event listeners.
 */
public class GameModel {
	private final GameMap map;
	private final HashSet<GameEventListener> listeners;
	public StringProperty debugMessage = new SimpleStringProperty();
	private final List<Entity> entities = new ArrayList<>();

	/**
	 * Constructs a GameModel with the specified tile views.
	 *
	 * @param tileViews A 2D array of TileView objects representing the game map.
	 */
	public GameModel(TileView[][] tileViews) {
		this.map = new GameMap(tileViews);
		this.listeners = new HashSet<>();
	}

	/**
	 * Adds a listener to the game model to handle game events.
	 *
	 * @param listener The GameEventListener to be added.
	 */
	public void addListener(GameEventListener listener) {
		listeners.add(listener);
	}

	/**
	 * Retrieves the game map associated with this model.
	 *
	 * @return The GameMap object representing the game map.
	 */
	public GameMap getMap() {
		return map;
	}

	/**
	 * Retrieves the list of entities in the game.
	 *
	 * @return A list of Entity objects currently in the game.
	 */
	public List<Entity> getEntities() {
		return entities;
	}

	/**
	 * Adds an animated entity to the game.
	 *
	 * @param entity The AnimatedEntity to be added to the game.
	 */
	public void addAnimatedEntity(AnimatedEntity entity) {
		this.entities.add(entity);
	}
}
