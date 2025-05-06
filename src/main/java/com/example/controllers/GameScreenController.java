package com.example.controllers;

import com.example.game.GameDataEvent;
import com.example.game.GameEvent;
import com.example.game.GameEventListener;
import com.example.game.GameModel;
import com.example.map.EmptyLotTile;
import com.example.map.Entity;
import com.example.map.Tile;
import com.example.map.TileEnum;
import com.example.ui.ImageLoader;
import com.example.ui.SpriteProvider;
import com.example.ui.SpriteView;
import com.example.utils.Point;
import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.collections.ListChangeListener;
import javafx.collections.MapChangeListener;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
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

public class GameScreenController implements GameEventListener
{
	public static final double TILE_SIZE = 64.0;

	private GameModel gameModel;

	@FXML
	private Label debugText;

	@FXML
	private Pane gameArea;

	private Popup towerConstructionMenu;

	private enum GUIState
	{
		NONE,
		TOWER_CONSTRUCTION
	}
	private GUIState state;
	private int clickedTileX, clickedTileY;

	private WritableImage staticTiles;

	public static class SavedMapData
	{
		public int rows;
		public int cols;
		public int[][] tiles;
	}
	private SavedMapData savedMapData;

	private SavedMapData loadSavedMap( String jsonPath ) throws IOException
	{
		ObjectMapper mapper = new ObjectMapper();
		return mapper.readValue(getClass().getResourceAsStream(jsonPath), SavedMapData.class);
	}

	@FXML
	public void initialize()
	{
		state = GUIState.NONE;

		try
		{
			savedMapData = loadSavedMap("/data/maps/Forest Path.json");
		}
		catch ( IOException e )
		{
			throw new RuntimeException( e );
		}

		gameModel = new GameModel(savedMapData.cols, savedMapData.rows);

		gameModel.map.getTiles().addListener(new MapChangeListener<>()
		{
			@Override
			public void onChanged( Change<? extends Point, ? extends Tile> change )
			{
				if ( change.wasAdded() )
				{
					// Create a SpriteView that will display the Tile entity's sprite
					Tile tile = change.getValueAdded();
					createSpriteView( tile )
							.getImageView()
							.setOnMouseClicked(mouseEvent -> onTileClicked(tile.getTileX(), tile.getTileY(), mouseEvent));
					System.out.printf("added %s at %s\n", tile, change.getKey());
				}
				if ( change.wasRemoved() )
				{
					// Unbind to delete all references and mark SpriteView for GC.
					change.getValueAdded().getSprite().unbind();
					System.out.printf("removed %s from %s\n", change.getValueRemoved(), change.getKey());
				}
			}
		});

		loadMapTiles();

		// The stitched image of static, non-interactable Tiles is set as backgound.
		ImageView staticTilesView = new ImageView(staticTiles);
		gameArea.getChildren().add(staticTilesView);

		debugText.textProperty().bindBidirectional(gameModel.debugMessage);
		gameModel.addListener(this);

		// Dynamically create construction popup menu
		// TODO: Try to put this into .fxml instead of dynamic creation.
		towerConstructionMenu = new Popup();

		// Create menu content
		VBox menuContent = new VBox();
		menuContent.setStyle("-fx-background-color: white; -fx-border-color: gray; -fx-padding: 5;");

		// Add tower type buttons
		Button btn1 = new Button("Archer");
		btn1.setOnAction(_ -> constructTower( GameModel.TowerType.ARCHER ));

		Button btn2 = new Button("Mage");
		btn2.setOnAction(_ -> constructTower( GameModel.TowerType.MAGE ));

		Button btn3 = new Button("Artillery");
		btn3.setOnAction(_ -> constructTower( GameModel.TowerType.ARTILLERY ));

		menuContent.getChildren().addAll(btn1, btn2, btn3);

		towerConstructionMenu.getContent().add(menuContent);
		towerConstructionMenu.hide();

		// Create and link SpriteViews to any new Entities on the GameMap.
		gameModel.map.getEntities().addListener(new ListChangeListener<Entity>()
		{
			@Override
			public void onChanged( Change<? extends Entity> change )
			{
				change.next();
				if ( change.wasAdded() )
				{
					for ( Entity entity : change.getAddedSubList() )
					{
						createSpriteView( entity );
					}
				}
				if ( change.wasRemoved() )
				{
					for ( Entity entity : change.getRemoved() )
					{
						entity.getSprite().unbind();
					}
				}
			}
		});
		staticTilesView.toBack();
	}

	// SpriteView factory method.
	private SpriteView createSpriteView( SpriteProvider provider )
	{
		SpriteView spriteView = new SpriteView();
		spriteView.setSpriteProvider(provider);

		ImageView tileImage = spriteView.getImageView();
		tileImage.setFitHeight(TILE_SIZE);
		tileImage.setFitWidth(TILE_SIZE);
		tileImage.setPreserveRatio(false);
		gameArea.getChildren().add(tileImage);

		return spriteView;
	}

	///  If tower can be constructed, open construction popup and await user input.
	private void onTileClicked( int x, int y , MouseEvent event )
	{
		clickedTileX = x;  clickedTileY = y;
		if ( gameModel.isValidConstructionLot(clickedTileX, clickedTileY) )
		{
			state = GUIState.TOWER_CONSTRUCTION;

			towerConstructionMenu.show(gameArea.getScene().getWindow(), event.getScreenX(), event.getScreenY());
		}
	}

	private void constructTower( GameModel.TowerType type )
	{
		gameModel.createTower(clickedTileX, clickedTileY, type);
		towerConstructionMenu.hide();
	}

	/// Must be relayed to Scene upon call. For handling tower construction popup closing when clicked anywhere on screen.
	public EventHandler<MouseEvent> getOnMouseClickedFilter()
	{
		return mouseEvent ->
		{
			if ( towerConstructionMenu.isShowing()
					&& !towerConstructionMenu.getContent().getFirst().contains(mouseEvent.getScreenX(), mouseEvent.getScreenY()))
			{
				towerConstructionMenu.hide();
			}
		};
	}

	/// Stitch static, non-interactable map tiles into a single Image.
	private void loadMapTiles()
	{
		int width = ( int ) (savedMapData.cols * TILE_SIZE);
		int height = ( int ) (savedMapData.rows * TILE_SIZE);

		staticTiles = new WritableImage(width, height);
		Canvas canvas = new Canvas(width, height);
		GraphicsContext gc = canvas.getGraphicsContext2D();

		for ( int y = 0; y < savedMapData.rows; y++ )
		{
			for ( int x = 0; x < savedMapData.cols; x++ )
			{
				int index = savedMapData.tiles[y][x];

				TileEnum tile;
				tile = TileEnum.fromFlatIndex(index);
				System.out.printf("Tile %s at %d:%d\n", tile.name(), x, y);
				double sourceX = tile.getCol() * TILE_SIZE;
				double sourceY = tile.getRow() * TILE_SIZE;

				if ( index > 14 )
				{
					gc.drawImage(ImageLoader.getImage("/com/example/assets/tiles/Tileset-64x64.png"),
							TileEnum.GRASS.getCol() * TILE_SIZE, TileEnum.GRASS.getRow() * TILE_SIZE, TILE_SIZE, TILE_SIZE,
							x * TILE_SIZE, y * TILE_SIZE, TILE_SIZE, TILE_SIZE);
				}

				switch (tile)
				{
					// Any interactable Tiles will be registered in GameMap
					case ARTILLERY_TOWER:
					{
						gameModel.createTower(x, y, GameModel.TowerType.ARTILLERY);
						break;
					}
					case ARCHERY_TOWER:
					{
						gameModel.createTower(x, y, GameModel.TowerType.ARCHER);
						break;
					}
					case MAGE_TOWER:
					{
						gameModel.createTower(x, y, GameModel.TowerType.MAGE);
						break;
					}
					case EMPTY_TOWER_TILE:
					{
						System.out.printf("empty lot at %d:%d\n", x, y);
						gameModel.map.setTile(x, y,
								new EmptyLotTile(x, y, ImageLoader.getImage("/com/example/assets/towers/TowerSlotwithoutbackground128.png")));
						break;
					}
					// Other non-interactable Tiles will be drawn onto the grass background.
					default:
					{
						gc.drawImage(ImageLoader.getImage("/com/example/assets/tiles/Tileset-64x64.png"),
								sourceX, sourceY, TILE_SIZE, TILE_SIZE,
								x * TILE_SIZE, y * TILE_SIZE, TILE_SIZE, TILE_SIZE);
						break;
					}
				}
			}
		}
		// Output
		canvas.snapshot(null, staticTiles);
	}

	/// For handling events from GameModel.
	@Override
	public void handle( GameEvent event )
	{
		switch (event.type)
		{
			case MESSAGE -> debugText.setText(((GameDataEvent<String>)event).data);
		}
	}
}