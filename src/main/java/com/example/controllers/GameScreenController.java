package com.example.controllers;

import com.example.map.*;
import com.example.game.GameDataEvent;
import com.example.game.GameEvent;
import com.example.game.GameEventListener;
import com.example.game.GameModel;
import com.example.main.Main;
import com.example.storage_manager.MapStorageManager;
import com.example.utils.PathFinder;
import com.example.utils.Point;
import com.example.utils.TileRenderer;
import javafx.fxml.FXML;
import javafx.geometry.Point2D;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;
import javafx.stage.Popup;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class GameScreenController extends Controller implements GameEventListener {
	@FXML private Label debugText;
	@FXML private Pane gameArea;

	private static final int TILE_SIZE = 64;
	private static final String MAP_NAME = "Forest Path";

	private TileView[][] mapTiles;
	private Tile[][] tiles;
	private GameModel gameModel;
	private TileRenderer renderer;
	private final Popup contextMenu = new Popup();

	@FXML
	public void initialize() {
		contextMenu.setAutoHide(true);

		// load map data
		try {
			mapTiles = MapStorageManager.loadMap(MAP_NAME);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}

		int rows = mapTiles.length;
		int cols = mapTiles[0].length;
		tiles = new Tile[rows][cols];

		// init renderer & model
		renderer = new TileRenderer("/com/example/assets/tiles/Tileset-64x64.png", TILE_SIZE);
		gameModel = new GameModel(mapTiles);
		debugText.textProperty().bindBidirectional(gameModel.debugMessage);
		gameModel.addListener(this);

		// render map tiles
		for (int y = 0; y < rows; y++) {
			for (int x = 0; x < cols; x++) {
				TileEnum type = mapTiles[y][x].getType();
				TileView tv = renderer.createTileView(type);
				tiles[y][x] = new Tile(tv, new TileModel(x, y));
				tv.setLayoutX(x * TILE_SIZE);
				tv.setLayoutY(y * TILE_SIZE);
				if (type == TileEnum.EMPTY_TOWER_TILE) {
					final int tx = x, ty = y;
					tv.setOnMouseClicked(e -> onTowerTileClicked(tv, tx, ty, e));
				}
				gameArea.getChildren().add(tv);
			}
		}

		// adjust window size
		double w = cols * TILE_SIZE;
		double h = (rows + 1) * TILE_SIZE;
		Main.getViewManager().resizeWindow((int)w, (int)h);
		gameArea.setPrefSize(w, h);

		// compute path and animate orb
		int[][] grid = gameModel.getMap().getExpandedGrid();
		Point start = PathFinder.findSpawnPoint(grid);
		Point goal  = PathFinder.findCastlePoint(grid);
		animateOrbAlongPath(PathFinder.findPath(grid, start, goal));
	}

	private void onTowerTileClicked(TileView tv, int x, int y, MouseEvent e) {
		if (tv.getType() == TileEnum.EMPTY_TOWER_TILE) {
			showBuildMenu(x, y, e.getScreenX(), e.getScreenY());
		} else {
			showSellMenu(x, y, e.getScreenX(), e.getScreenY());
		}
	}

	private void showBuildMenu(int tileX, int tileY, double sx, double sy) {
		List<Option> opts = new ArrayList<>();
		opts.add(new Option("Archer",   () -> constructTower(tileX, tileY, TileEnum.ARCHERY_TOWER)));
		opts.add(new Option("Mage",     () -> constructTower(tileX, tileY, TileEnum.MAGE_TOWER)));
		opts.add(new Option("Artillery",() -> constructTower(tileX, tileY, TileEnum.ARTILLERY_TOWER)));
		showRadialMenu(tileX, tileY, sx, sy, opts);
	}

	private void showSellMenu(int tileX, int tileY, double sx, double sy) {
		List<Option> opts = new ArrayList<>();
		opts.add(new Option("Sell", () -> sellTower(tileX, tileY)));
		showRadialMenu(tileX, tileY, sx, sy, opts);
	}

	private void showRadialMenu(int tileX, int tileY,
								double unusedScreenX, double unusedScreenY,
								List<Option> options) {
		contextMenu.getContent().clear();

		double baseRadius = TILE_SIZE * 0.5;
		double btnSize = 48;
		double halfDiag = Math.sqrt(2) * btnSize / 2;
		double pathRadius = baseRadius + halfDiag;
		double containerSize = pathRadius * 2 + btnSize;

		Pane container = new Pane();
		container.setPrefSize(containerSize, containerSize);

		double cx0 = containerSize / 2;
		double cy0 = containerSize / 2;

		// Outer outline ring in dark-teal
		Circle outerRing = new Circle(cx0, cy0, pathRadius);
		outerRing.setFill(Color.TRANSPARENT);
		outerRing.setStroke(Color.web("#004d40"));
		outerRing.setStrokeWidth(2);
		container.getChildren().add(outerRing);

		int count = options.size();
		for (int i = 0; i < count; i++) {
			double angle = 2 * Math.PI * i / count - Math.PI / 2;
			double bx = cx0 + Math.cos(angle) * pathRadius - btnSize / 2;
			double by = cy0 + Math.sin(angle) * pathRadius - btnSize / 2;

			Button btn = new Button(options.get(i).label);
			btn.setPrefSize(btnSize, btnSize);
			btn.setWrapText(true);
			btn.setTextAlignment(TextAlignment.CENTER);
			btn.setFont(Font.font(10));

			// To use an icon instead of text:
			// Image img = new Image(getClass().getResourceAsStream("/com/example/assets/icons/archer.png"));
			// btn.setGraphic(new ImageView(img));
			// btn.setText(null);

			btn.setLayoutX(bx);
			btn.setLayoutY(by);

			int idx = i;
			btn.setOnAction(evt -> {
				options.get(idx).action.run();
				contextMenu.hide();
			});
			container.getChildren().add(btn);
		}

		contextMenu.getContent().add(container);

		// 1) figure out the tile’s center in local coords
		double localX = tileX * TILE_SIZE + TILE_SIZE / 2.0;
		double localY = tileY * TILE_SIZE + TILE_SIZE / 2.0;
		// 2) convert to screen
		Point2D screenCenter = gameArea.localToScreen(localX, localY);

		// 3) show the popup so it’s perfectly centered on that point
		contextMenu.show(
				gameArea.getScene().getWindow(),
				screenCenter.getX() - containerSize / 2,
				screenCenter.getY() - containerSize / 2
		);
	}


	private void constructTower(int x, int y, TileEnum towerType) {
		Tile tile = tiles[y][x];
		TileView nv = renderer.createTileView(towerType);
		nv.setLayoutX(x * TILE_SIZE);
		nv.setLayoutY(y * TILE_SIZE);
		gameArea.getChildren().remove(tile.view);
		gameArea.getChildren().add(nv);
		tile.view = nv;
		tile.model.setTower(towerType, 10, 5, 100);
		nv.setOnMouseClicked(e -> onTowerTileClicked(nv, x, y, e));
	}

	private void sellTower(int x, int y) {
		Tile tile = tiles[y][x];
		TileView nv = renderer.createTileView(TileEnum.EMPTY_TOWER_TILE);
		nv.setLayoutX(x * TILE_SIZE);
		nv.setLayoutY(y * TILE_SIZE);
		gameArea.getChildren().remove(tile.view);
		gameArea.getChildren().add(nv);
		tile.view = nv;
		tile.model.removeTower();
		nv.setOnMouseClicked(e -> onTowerTileClicked(nv, x, y, e));
	}

	private void animateOrbAlongPath(List<Point> path) {
		if (path.isEmpty()) return;
		var orb = new javafx.scene.shape.Circle(8);
		orb.setFill(Color.RED);
		gameArea.getChildren().add(orb);
		final int[] idx = {0};
		var tl = new javafx.animation.Timeline();
		tl.setCycleCount(path.size());
		tl.getKeyFrames().add(new javafx.animation.KeyFrame(
						javafx.util.Duration.millis(30), e -> {
					if (idx[0] >= path.size()) { tl.stop(); return; }
					Point p = path.get(idx[0]++);
					orb.setLayoutX(p.x()); orb.setLayoutY(p.y());
				})
		);
		tl.play();
	}

	@Override
	public void handle(GameEvent event) {
		if (event.type == GameEvent.GameEventType.MESSAGE) {
			debugText.setText(((GameDataEvent<String>)event).data);
		}
	}

	@FXML
	public void goToSettings() {
		Main.getViewManager().switchTo("/com/example/fxml/settings.fxml");
		Main.getViewManager().resizeWindowDefault();
	}

	// helper for radial menu
	private static class Option {
		final String label; final Runnable action;
		Option(String label, Runnable action) {
			this.label = label; this.action = action;
		}
	}
}