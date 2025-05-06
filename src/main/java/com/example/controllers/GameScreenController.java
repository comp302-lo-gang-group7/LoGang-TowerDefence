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
import com.example.ui.ImageLoader;
import javafx.collections.ListChangeListener;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Popup;

import java.io.IOException;
import java.net.ContentHandler;
import java.util.HashMap;
import java.util.Map;

public class GameScreenController extends Controller implements GameEventListener {
	@FXML private Label debugText;
	@FXML private Pane  gameArea;

	private static final int TILE_SIZE = 64;
	private final String  MAP_NAME = "Forest Path";

	private TileView[][]                mapTiles;
	private GameModel                   gameModel;
	private Popup                       towerMenu;
	private int                         clickedTileX, clickedTileY;
	private final Map<Entity, ImageView> entityViews = new HashMap<>();

	@FXML
	public void initialize() {
		// 1) load the raw TileView[][] (each has its TileEnum)
		try {
			mapTiles = MapStorageManager.loadMap(MAP_NAME);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		int rows = mapTiles.length, cols = mapTiles[0].length;

		// 2) init game model
		gameModel = new GameModel(cols, rows);
		debugText.textProperty().bindBidirectional(gameModel.debugMessage);
		gameModel.addListener(this);

		// 3) render each TileView with grass-underlay + tile overlay
		renderTiles(rows, cols);

		// 4) size the window & pane exactly to the grid
		double w = cols * TILE_SIZE, h = (rows + 1) * TILE_SIZE; // TODO: Why does it need to be offset by one
		Main.getViewManager().resizeWindow((int)w, (int)h);
		gameArea.setPrefSize(w, h);

		// 5) hook up dynamic entities
		gameModel.map.getEntities().addListener((ListChangeListener<Entity>) ch -> {
			while (ch.next()) {
				if (ch.wasAdded())   ch.getAddedSubList().forEach(this::addEntityView);
				if (ch.wasRemoved()) ch.getRemoved().forEach(this::removeEntityView);
			}
		});

		// 6) build tower popup
		buildTowerMenu();
	}

	/**
	 * For each tile position, composite grass + the tile sprite
	 * and then place the TileView at the correct layoutX/Y.
	 */
	private void renderTiles(int rows, int cols) {
		Image tileset = ImageLoader.getImage("/com/example/assets/tiles/Tileset-64x64.png");
		PixelReader pr = tileset.getPixelReader();

		for (int y = 0; y < rows; y++) {
			for (int x = 0; x < cols; x++) {
				TileView tv = mapTiles[y][x];
				TileEnum t   = tv.getType();

				// 1) make a fresh 64×64 image
				WritableImage combo = new WritableImage(TILE_SIZE, TILE_SIZE);
				PixelWriter pw = combo.getPixelWriter();

				// 2) draw grass underlay
				int gx = TileEnum.GRASS.getCol() * TILE_SIZE;
				int gy = TileEnum.GRASS.getRow() * TILE_SIZE;
				for (int py = 0; py < TILE_SIZE; py++) {
					for (int px = 0; px < TILE_SIZE; px++) {
						pw.setArgb(px, py, pr.getArgb(gx + px, gy + py));
					}
				}

				// 3) overlay with proper alpha blending
				int tx = t.getCol() * TILE_SIZE;
				int ty = t.getRow() * TILE_SIZE;
				gx = TileEnum.GRASS.getCol() * TILE_SIZE;
				gy = TileEnum.GRASS.getRow() * TILE_SIZE;

				for (int py = 0; py < TILE_SIZE; py++) {
					for (int px = 0; px < TILE_SIZE; px++) {
						// read grass underlay directly from tileset
						int bgArgb = pr.getArgb(gx + px, gy + py);

						// read tile pixel
						int fgArgb = pr.getArgb(tx + px, ty + py);
						int fgA    = (fgArgb >>> 24) & 0xFF;

						if (fgA == 0) {
							// fully transparent: keep grass
							pw.setArgb(px, py, bgArgb);
						} else if (fgA == 255) {
							// fully opaque: use tile pixel
							pw.setArgb(px, py, fgArgb);
						} else {
							// blend fg over bg
							double alpha = fgA / 255.0;

							int fgR = (fgArgb >> 16) & 0xFF;
							int fgG = (fgArgb >>  8) & 0xFF;
							int fgB =  fgArgb        & 0xFF;

							int bgR = (bgArgb >> 16) & 0xFF;
							int bgG = (bgArgb >>  8) & 0xFF;
							int bgB =  bgArgb        & 0xFF;

							int outR = (int)(fgR * alpha + bgR * (1 - alpha));
							int outG = (int)(fgG * alpha + bgG * (1 - alpha));
							int outB = (int)(fgB * alpha + bgB * (1 - alpha));

							int outArgb = (0xFF << 24)
									| (outR << 16)
									| (outG <<  8)
									|  outB;
							pw.setArgb(px, py, outArgb);
						}
					}
				}


				// 4) set that composite as the TileView’s image
				tv.setImage(combo);

				// 5) size & position
				tv.setFitWidth(TILE_SIZE);
				tv.setFitHeight(TILE_SIZE);
				tv.setPreserveRatio(false);
				tv.setLayoutX(x * TILE_SIZE);
				tv.setLayoutY(y * TILE_SIZE);

				// 6) click‐handler for empty lots only
				if (t == TileEnum.EMPTY_TOWER_TILE) {
					final int xx = x, yy = y;
					tv.setOnMouseClicked(e -> onTileClicked(xx, yy, e));
				}

				// 7) add to pane
				gameArea.getChildren().add(tv);
			}
		}
	}

	private void onTileClicked(int x, int y, MouseEvent e) {
		if (!gameModel.isValidConstructionLot(x, y)) return;
		clickedTileX = x;
		clickedTileY = y;
		towerMenu.show(gameArea.getScene().getWindow(),
				e.getScreenX(), e.getScreenY());
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