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

public class GameModel {
	private final GameMap map;
	private final HashSet<GameEventListener> listeners;
	public StringProperty debugMessage = new SimpleStringProperty();
	private final List<Entity> entities = new ArrayList<>();

	public GameModel(TileView[][] tileViews) {
		this.map = new GameMap(tileViews);
		this.listeners = new HashSet<>();
	}

	public void addListener(GameEventListener listener) {
		listeners.add(listener);
	}

	public GameMap getMap() {
		return map;
	}

	public List<Entity> getEntities() {
		return entities;
	}

	public void addAnimatedEntity(AnimatedEntity entity) {
		this.entities.add(entity);
	}
}
