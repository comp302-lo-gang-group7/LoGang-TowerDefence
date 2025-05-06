package com.example.controllers;

import com.example.game.GameDataEvent;
import com.example.game.GameEvent;
import com.example.game.GameEventListener;
import com.example.game.GameModel;
import com.example.main.Main;
import com.example.map.Entity;
import com.example.map.TileEnum;
import com.example.map.TileView;
import com.example.storage_manager.MapStorageManager;
import com.example.utils.TileRenderer;
import javafx.collections.ListChangeListener;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Popup;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class GameScreenController extends Controller implements GameEventListener {
	@FXML private Label debugText;
	@FXML private Pane gameArea;

	private static final int TILE_SIZE = 64;
	private static final String MAP_NAME = "Forest Path";

	private TileView[][] mapTiles;
	private GameModel gameModel;
	private Popup towerMenu;
	private TileRenderer renderer;
	private int clickedTileX, clickedTileY;
	private final Map<Entity, ImageView> entityViews = new HashMap<>();

	@FXML
	public void initialize() {
		// 1) load map data
		try {
			mapTiles = MapStorageManager.loadMap(MAP_NAME);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		int rows = mapTiles.length;
		int cols = mapTiles[0].length;

		// 2) init renderer & model
		renderer = new TileRenderer("/com/example/assets/tiles/Tileset-64x64.png", TILE_SIZE);
		gameModel = new GameModel(cols, rows);
		debugText.textProperty().bindBidirectional(gameModel.debugMessage);
		gameModel.addListener(this);

		// 3) render tiles via renderer
		for (int y = 0; y < rows; y++) {
			for (int x = 0; x < cols; x++) {
				TileEnum type = mapTiles[y][x].getType();
				TileView tv = renderer.createTileView(type);
				tv.setLayoutX(x * TILE_SIZE);
				tv.setLayoutY(y * TILE_SIZE);

				if (type == TileEnum.EMPTY_TOWER_TILE) {
					final int tx = x, ty = y;
					tv.setOnMouseClicked(e -> onTileClicked(tx, ty, e));
				}

				gameArea.getChildren().add(tv);
			}
		}

		// 4) adjust window & pane size
		double w = cols * TILE_SIZE;
		double h = rows * TILE_SIZE;
		Main.getViewManager().resizeWindow((int) w, (int) h);
		gameArea.setPrefSize(w, h);

		// 5) entity views
		gameModel.map.getEntities().addListener((ListChangeListener<Entity>) ch -> {
			while (ch.next()) {
				if (ch.wasAdded())   ch.getAddedSubList().forEach(this::addEntityView);
				if (ch.wasRemoved()) ch.getRemoved().forEach(this::removeEntityView);
			}
		});

		// 6) build tower menu
		buildTowerMenu();
	}

	private void onTileClicked(int x, int y, MouseEvent e) {
		if (!gameModel.isValidConstructionLot(x, y)) return;
		clickedTileX = x;
		clickedTileY = y;
		towerMenu.show(gameArea.getScene().getWindow(), e.getScreenX(), e.getScreenY());
	}

	private void addEntityView(Entity e) {
		ImageView iv = new ImageView(e.getSprite().getImage());
		iv.setFitWidth(TILE_SIZE);
		iv.setFitHeight(TILE_SIZE);
		iv.setLayoutX(e.getTileX() * TILE_SIZE);
		iv.setLayoutY(e.getTileY() * TILE_SIZE);
		entityViews.put(e, iv);
		gameArea.getChildren().add(iv);
	}

	private void removeEntityView(Entity e) {
		ImageView iv = entityViews.remove(e);
		if (iv != null) gameArea.getChildren().remove(iv);
	}

	private void buildTowerMenu() {
		towerMenu = new Popup();
		VBox box = new VBox(5);
		box.setStyle("-fx-background-color:white; -fx-border-color:gray; -fx-padding:8;");
		Button a = new Button("Archer"), m = new Button("Mage"), r = new Button("Artillery");
		a.setOnAction(evt -> constructTower(GameModel.TowerType.ARCHER));
		m.setOnAction(evt -> constructTower(GameModel.TowerType.MAGE));
		r.setOnAction(evt -> constructTower(GameModel.TowerType.ARTILLERY));
		box.getChildren().addAll(a, m, r);
		towerMenu.getContent().add(box);
	}

	private void constructTower(GameModel.TowerType type) {
		gameModel.createTower(clickedTileX, clickedTileY, type);
		towerMenu.hide();
	}

	@Override
	public void handle(GameEvent event) {
		if (event.type == GameEvent.GameEventType.MESSAGE) {
			debugText.setText(((GameDataEvent<String>) event).data);
		}
	}

	@FXML
	public void goToSettings() {
		Main.getViewManager().switchTo("/com/example/fxml/settings.fxml");
		Main.getViewManager().resizeWindowDefault();
	}
}
