package com.example.game;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import com.example.entity.AnimatedEntity;
import com.example.entity.Entity;
import com.example.entity.Tower;
import com.example.map.GameMap;
import com.example.map.TileView;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class GameModel {
	private final GameMap map;
	private final HashSet<GameEventListener> listeners;
	public StringProperty debugMessage = new SimpleStringProperty();
	private final List<Entity> entities = new ArrayList<>();
	private final List<Tower> towers = new ArrayList<>();

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

	public void addTower(Tower tower) {
		this.towers.add(tower);
	}

	public void removeTower(Tower tower) {
		this.towers.remove(tower);
	}

	public List<Tower> getTowers() {
		return towers;
	}

	public List<Tower> getTowersInRange(double x, double y, double range) {
		List<Tower> nearbyTowers = new ArrayList<>();
		for (Tower tower : towers) {
			double dx = tower.getTileX() * 64 - x; // 64 is tile size
			double dy = tower.getTileY() * 64 - y;
			double distance = Math.sqrt(dx * dx + dy * dy);
			if (distance <= range) {
				nearbyTowers.add(tower);
			}
		}
		return nearbyTowers;
	}
}
