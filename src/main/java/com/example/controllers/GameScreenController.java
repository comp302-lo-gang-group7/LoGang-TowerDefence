package com.example.controllers;

import com.example.game.GameDataEvent;
import com.example.game.GameEvent;
import com.example.game.GameEventListener;
import com.example.game.GameModel;
import com.example.map.Entity;
import com.example.map.Tile;
import com.example.ui.ImageLoader;
import com.example.ui.SpriteView;
import com.example.utils.Point;
import javafx.collections.ListChangeListener;
import javafx.collections.MapChangeListener;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Popup;

import java.security.SecureRandom;

public class GameScreenController implements GameEventListener
{
	public static final double TILE_SIZE = 64.0;
	@FXML
	public GridPane imageGrid;

	private GameModel gameModel;
	private SpriteView[][] gridSprites;

	@FXML
	private Label debugText;

	@FXML
	private Pane testPane;

	private Popup towerConstructionMenu;

	private enum GUIState
	{
		NONE,
		TOWER_CONSTRUCTION
	}
	private GUIState state;
	private int clickedTileX, clickedTileY;

	private Entity testEntity;
	private SecureRandom random = new SecureRandom();

	@FXML
	public void initialize()
	{
		state = GUIState.NONE;

		gameModel = new GameModel();

		gridSprites = new SpriteView[gameModel.map.getHeight()][gameModel.map.getWidth()];

		for ( int y = 0; y < gameModel.map.getHeight(); y++ )
		{
			for ( int x = 0; x < gameModel.map.getWidth(); x++ )
			{
				SpriteView gridSprite = new SpriteView();
				gridSprite.setSpriteProvider(gameModel.map.getTile(x, y));

				ImageView tileImage = gridSprite.getImageView();
				tileImage.setFitHeight(TILE_SIZE);
				tileImage.setFitWidth(TILE_SIZE);
				tileImage.setPreserveRatio(true);

				final int tileY = y, tileX = x;
				tileImage.setOnMouseClicked(event -> onTileClicked(tileX, tileY, event));

				testPane.getChildren().add(tileImage);
				gridSprites[y][x] = gridSprite;
			}
		}

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

		// Each grid sprite on the game map will listen to changed tiles.
		gameModel.map.getTiles().addListener(new MapChangeListener<>()
		{
			@Override
			public void onChanged( Change<? extends Point, ? extends Tile> change )
			{
				SpriteView spriteView = gridSprites[change.getKey().y()][change.getKey().x()];
				spriteView.replaceSpriteProvider(change.getValueAdded(), change.getValueRemoved());

				if ( change.wasAdded() )
				{
					System.out.printf("added %s at %s\n", change.getValueAdded(), change.getKey());
				}
				if ( change.wasRemoved() )
				{
					System.out.printf("removed %s from %s\n", change.getValueRemoved(), change.getKey());
				}
			}
		});

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
						SpriteView spriteView = new SpriteView();
						spriteView.setSpriteProvider(entity);

						ImageView tileImage = spriteView.getImageView();
						tileImage.setFitHeight(TILE_SIZE);
						tileImage.setFitWidth(TILE_SIZE);
						tileImage.setPreserveRatio(true);
						testPane.getChildren().add(tileImage);
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

		testEntity = new Entity(50.0, 50.0, ImageLoader.getImage("/tower_archer.png"));
		gameModel.map.getEntities().add(testEntity);
	}

	///  If tower can be constructed, open construction popup and await user input.
	private void onTileClicked( int x, int y , MouseEvent event )
	{
		clickedTileX = x;  clickedTileY = y;
		if ( gameModel.isValidConstructionLot(clickedTileX, clickedTileY) )
		{
			state = GUIState.TOWER_CONSTRUCTION;

			towerConstructionMenu.show(testPane.getScene().getWindow(), event.getScreenX(), event.getScreenY());
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