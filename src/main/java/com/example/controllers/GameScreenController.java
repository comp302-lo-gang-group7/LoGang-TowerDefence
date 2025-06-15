package com.example.controllers;

import com.example.entity.Entity;
import com.example.game.*;
import com.example.map.*;
import com.example.main.Main;
import com.example.storage_manager.MapStorageManager;
import com.example.utils.TileRenderer;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Point2D;
import javafx.scene.Parent;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.Popup;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class GameScreenController extends Controller {
	@FXML private Pane gameArea;
	@FXML private Pane mapLayer, entityLayer, towerLayer;
	@FXML public VBox hudOverlay;
	@FXML private Label goldLabel;
	@FXML private Label healthLabel;
	@FXML private Label waveLabel;
	@FXML private Button speedUp, pauseButton, exitButton;
	private GameManager gameManager;
	private PlayerState playerState;
	private javafx.animation.AnimationTimer hudTimer;
	private boolean isPaused = false;


	public static final int TILE_SIZE = 64;

    private Tile[][] tiles;
    private TileRenderer renderer;
	private final Popup contextMenu = new Popup();
	private boolean isFast;
	private Parent pauseOverlay;

	public void init( String mapName, int startingGold, List<int[]> waves) {
		contextMenu.setAutoHide(true);
		setupButtonIcons();

		playerState = new PlayerState(startingGold, 10);
		goldLabel.textProperty().bind(playerState.getGoldProperty().asString());
		healthLabel.textProperty().bind(
				playerState.getLivesProperty()
						.asString()
						.concat(String.format("/%d", playerState.getMaxLives()))
		);

		// load map data
        TileView[][] mapTiles;
        try {
			mapTiles = MapStorageManager.loadMap(mapName);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}

		int rows = mapTiles.length;
		int cols = mapTiles[0].length;
		tiles = new Tile[rows][cols];

		// init renderer & model
		renderer = new TileRenderer("/com/example/assets/tiles/Tileset-64x64.png", TILE_SIZE);
        GameModel gameModel = new GameModel(mapTiles);

		// render map tiles
		for (int y = 0; y < rows; y++) {
			for (int x = 0; x < cols; x++) {
				TileEnum type = mapTiles[y][x].getType();
				TileView tv = renderer.createTileView(type);
				tv.setLayoutX(x * TILE_SIZE);
				tv.setLayoutY(y * TILE_SIZE);
				tiles[y][x] = new Tile(tv, new TileModel(x, y));

				if (type == TileEnum.EMPTY_TOWER_TILE) {
					final int tx = x, ty = y;
					tv.setOnMouseClicked(e -> onTowerTileClicked(tv, tx, ty, e));
					towerLayer.getChildren().add(tv);
				} else {
					mapLayer.getChildren().add(tv);
				}
			}
		}

		// adjust window size
		double w = cols * TILE_SIZE;
		double h = rows * TILE_SIZE;
		Main.getViewManager().resizeWindow((int)w, (int)h);
		gameArea.setPrefSize(w, h);

		Canvas gameCanvas = new Canvas(cols * TILE_SIZE, rows * TILE_SIZE);
		entityLayer.getChildren().add(gameCanvas);

		// 2) Grab all of your Entities out of the model
		List<Entity> allEntities = gameModel.getEntities();

		// 3) Hook up & start the GameManager loop
		GameManager.initialize(gameCanvas, allEntities, gameModel, playerState);

		this.gameManager = GameManager.getInstance();

		waveLabel.textProperty().bind(
				gameManager.getCurrentWaveProperty()
						.asString()
						.concat(String.format("/%d", waves.size()))
		);

		gameManager.setWaves(waves);
		gameManager.start();
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
		opts.add(new Option("Archer", () -> constructTower(tileX, tileY, TileEnum.ARCHERY_TOWER), "/com/example/assets/buttons/Archer_Tower_Button.png"));
		opts.add(new Option("Mage", () -> constructTower(tileX, tileY, TileEnum.MAGE_TOWER), "/com/example/assets/buttons/Spell_Tower_Button.png"));
		opts.add(new Option("Artillery", () -> constructTower(tileX, tileY, TileEnum.ARTILLERY_TOWER), "/com/example/assets/buttons/Bomb_Tower_Button.png"));
		showRadialMenu(tileX, tileY, opts);
	}

	private void showSellMenu(int tileX, int tileY, double sx, double sy) {
		List<Option> opts = new ArrayList<>();
		opts.add(new Option("Sell", () -> sellTower(tileX, tileY), "/com/example/assets/buttons/Bin_Button.png"));
		showRadialMenu(tileX, tileY, opts);
	}

	private void showRadialMenu(int tileX, int tileY, List<Option> options) {
		contextMenu.getContent().clear();

		double baseRadius = TILE_SIZE * 0.5;
		double btnSize = 32;
		double halfDiag = Math.sqrt(2) * btnSize / 2;
		double pathRadius = baseRadius + halfDiag;
		double containerSize = pathRadius * 2 + btnSize;

		Pane container = new Pane();
		container.setPrefSize(containerSize, containerSize);

		double cx0 = containerSize / 2;
		double cy0 = containerSize / 2;

		Circle ring = new Circle(cx0, cy0, pathRadius);
		Circle outerBorder = new Circle(cx0, cy0, pathRadius + 1);
		Circle innerBorder = new Circle(cx0, cy0, pathRadius - 1);

		ring.setFill(Color.TRANSPARENT);
		outerBorder.setFill(Color.TRANSPARENT);
		innerBorder.setFill(Color.TRANSPARENT);

		ring.setStroke(Color.web("#87bfbe"));
		outerBorder.setStroke(Color.web("#000000"));
		innerBorder.setStroke(Color.web("#000000"));

		ring.setStrokeWidth(4);
		outerBorder.setStrokeWidth(1);
		innerBorder.setStrokeWidth(1);

		container.getChildren().add(ring);
		container.getChildren().add(outerBorder);
		container.getChildren().add(innerBorder);

		for (int i = 0; i < options.size(); i++) {
			Option opt = options.get(i);

			double angle = 2 * Math.PI * i / options.size() - Math.PI / 2;
			double bx = cx0 + Math.cos(angle) * pathRadius - btnSize / 2;
			double by = cy0 + Math.sin(angle) * pathRadius - btnSize / 2;

			Button btn = new Button();
			btn.setPrefSize(btnSize, btnSize);

			Image img = new Image(Objects.requireNonNull(getClass().getResourceAsStream(opt.iconPath)));
			ImageView view = new ImageView(img);
			view.setFitWidth(32);
			view.setFitHeight(32);
			btn.setGraphic(view);
			btn.setText(null);

			btn.setStyle(
					"-fx-background-color: transparent;" +
							"-fx-padding: 0;" +
							"-fx-border-color: transparent;"
			);

			btn.setLayoutX(bx);
			btn.setLayoutY(by);
			btn.setOnAction(evt -> {
				opt.action.run();
				contextMenu.hide();
			});

			container.getChildren().add(btn);
		}

		contextMenu.getContent().add(container);

		double localX = tileX * TILE_SIZE + TILE_SIZE / 2.0;
		double localY = tileY * TILE_SIZE + TILE_SIZE / 2.0;
		Point2D screenCenter = towerLayer.localToScreen(localX, localY);

		contextMenu.show(
				towerLayer.getScene().getWindow(),
				screenCenter.getX() - containerSize / 2,
				screenCenter.getY() - containerSize / 2
		);
	}


	private void constructTower(int x, int y, TileEnum towerType) {
		Tile tile = tiles[y][x];
		TileView newView = renderer.createTileView(towerType);
		newView.setLayoutX(x * TILE_SIZE);
		newView.setLayoutY(y * TILE_SIZE);

		towerLayer.getChildren().remove(tile.view);
		towerLayer.getChildren().add(newView);

		tile.view = newView;
		tile.model.setTower(towerType, 10, 5, 100);
		newView.setOnMouseClicked(e -> onTowerTileClicked(newView, x, y, e));
	}


	private void sellTower(int x, int y) {
		Tile tile = tiles[y][x];
		TileView newView = renderer.createTileView(TileEnum.EMPTY_TOWER_TILE);
		newView.setLayoutX(x * TILE_SIZE);
		newView.setLayoutY(y * TILE_SIZE);

		towerLayer.getChildren().remove(tile.view);
		towerLayer.getChildren().add(newView);

		tile.view = newView;
		tile.model.removeTower();
		newView.setOnMouseClicked(e -> onTowerTileClicked(newView, x, y, e));
	}


	@FXML
	public void pauseGame() {
		if (gameManager == null) return;

		if (!isPaused) {
			gameManager.pause();
			isPaused = true;

			try {
				pauseOverlay = FXMLLoader.load(
						getClass().getResource("/com/example/fxml/pause_menu.fxml")
				);
				gameArea.getChildren().add(pauseOverlay);
			} catch (IOException ex) {
				ex.printStackTrace();
			}

		} else {
			// resume
			gameManager.resume();
			isPaused = false;

			if (pauseOverlay != null) {
				gameArea.getChildren().remove(pauseOverlay);
				pauseOverlay = null;
			}
		}
	}


	@FXML
	public void speedUp(ActionEvent event) {
		if (gameManager == null) return;

		isFast = !isFast;
		double speed = isFast ? 2.0 : 1.0;
		gameManager.setGameSpeed(speed);

		// Optional: change the button’s icon or tooltip
		speedUp.setTooltip(new Tooltip((int)speed + "× Speed"));
		System.out.println("Game speed set to " + speed + "×");
	}
	
	// helper for radial menu
	private static class Option {
		final String label;
		final Runnable action;
		final String iconPath;

		Option(String label, Runnable action, String iconPath) {
			this.label = label;
			this.action = action;
			this.iconPath = iconPath;
		}
	}

	private void setupButtonIcons() {
		Image speedUpIcon = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/com/example/assets/buttons/Skip_Button.png")));
		Image optionsIcon = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/com/example/assets/buttons/Settings_Button.png")));
		Image exitIcon = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/com/example/assets/buttons/Cross_Button.png")));

		ImageView speedView = new ImageView(speedUpIcon);
		ImageView optionsView = new ImageView(optionsIcon);
		ImageView exitView = new ImageView(exitIcon);

		speedView.setFitWidth(32);
		speedView.setFitHeight(32);
		optionsView.setFitWidth(32);
		optionsView.setFitHeight(32);
		exitView.setFitWidth(32);
		exitView.setFitHeight(32);

		speedUp.setGraphic(speedView);
		pauseButton.setGraphic(optionsView);
		exitButton.setGraphic(exitView);
	}

}