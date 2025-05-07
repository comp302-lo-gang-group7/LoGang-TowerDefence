package com.example.controllers;

import com.example.game.GameDataEvent;
import com.example.game.GameEvent;
import com.example.game.GameEventListener;
import com.example.game.GameModel;
import com.example.main.Main;
import com.example.map.*;
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

	private TileView[][] mapTiles; // These are primarily for setup and background creation

	private Tile[][] tiles;

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

		tiles = new Tile[rows][cols];

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
				TileModel model = new TileModel(x, y);

				tiles[y][x] = new Tile(tv, model);

				tv.setLayoutX(x * TILE_SIZE);
				tv.setLayoutY(y * TILE_SIZE);

				if (type == TileEnum.EMPTY_TOWER_TILE) {
					final int tx = x, ty = y;
					tv.setOnMouseClicked(e -> onTowerTileClicked(tv, tx, ty, e));
				}

				gameArea.getChildren().add(tv);
			}
		}

		// 4) adjust window & pane size
		double w = cols * TILE_SIZE;
		double h = (rows + 1) * TILE_SIZE;
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

	private void onTowerTileClicked(TileView tv, int x, int y, MouseEvent e) {
		if (tv.getType().equals(TileEnum.EMPTY_TOWER_TILE)) {
			clickedTileX = x;
			clickedTileY = y;
			towerMenu.show(gameArea.getScene().getWindow(), e.getScreenX(), e.getScreenY());
		}
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
		a.setOnAction(evt -> constructTower(TileEnum.ARCHERY_TOWER));
		m.setOnAction(evt -> constructTower(TileEnum.MAGE_TOWER));
		r.setOnAction(evt -> constructTower(TileEnum.ARTILLERY_TOWER));
		box.getChildren().addAll(a, m, r);
		towerMenu.getContent().add(box);
	}

	private void constructTower(TileEnum towerType) {
		Tile tile = tiles[clickedTileY][clickedTileX];

		// Replace visual with new tower image
		TileView newView = renderer.createTileView(towerType);
		newView.setLayoutX(clickedTileX * TILE_SIZE);
		newView.setLayoutY(clickedTileY * TILE_SIZE);

		// Swap in pane
		gameArea.getChildren().remove(tile.view);
		gameArea.getChildren().add(newView);

		// Update Tile (both view + model)
		tile.view = newView;
		tile.model.setTower(towerType, 10, 5, 100); // example values: HP=10, DMG=5, cost=100

		// Disable further clicks
		newView.setOnMouseClicked(null);

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
