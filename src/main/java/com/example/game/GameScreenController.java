package com.example.game;

import com.example.main.Main;
import com.example.map.Tile;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;

public class GameScreenController implements GameEventListener
{
	public GridPane imageGrid;
	private GameModel gameModel;
	private ImageView[][] tilesGrid;

	@FXML
	private Label welcomeText;

	@FXML
	public void initialize()
	{
		gameModel = Main.gameModel;
		if ( gameModel == null )
		{
			throw new RuntimeException("Game Model is not initialized");
		}

		tilesGrid = new ImageView[gameModel.map.getHeight()][gameModel.map.getWidth()];
		for ( int y = 0; y < tilesGrid.length; y++ )
		{
			for ( int x = 0; x < tilesGrid[y].length; x++ )
			{
				ImageView tileImage = new ImageView();
				tileImage.setFitHeight(100);
				tileImage.setFitWidth(100);
				tileImage.setPreserveRatio(true);
				tileImage.setImage(gameModel.map.getTile(x, y).getSprite());
				final int tileY = y, tileX = x;
				tileImage.setOnMouseClicked(_ -> gameModel.onTileClicked(tileX, tileY));
				imageGrid.add(tileImage, y, x);
				tilesGrid[y][x] = tileImage;
			}
		}

		gameModel.addListener(this);
	}

	public void repaintTile( Tile tile )
	{
		tilesGrid[tile.getY()][tile.getX()].setImage(tile.getSprite());
	}

	@Override
	public void handle( GameEvent event )
	{
		switch (event.type)
		{
			case MESSAGE -> welcomeText.setText(((GameDataEvent<String>)event).data);
			case REPAINT -> repaintTile(((GameDataEvent<Tile>)event).data);
		}
	}
}