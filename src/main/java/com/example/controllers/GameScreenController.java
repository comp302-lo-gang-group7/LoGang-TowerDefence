package com.example.controllers;

import com.example.game.GameDataEvent;
import com.example.game.GameEvent;
import com.example.game.GameEventListener;
import com.example.game.GameModel;
import com.example.ui.SpriteView;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;

public class GameScreenController implements GameEventListener
{
	public GridPane imageGrid;
	private GameModel gameModel;
	private SpriteView[][] tileGrid;

	@FXML
	private Label debugText;

	@FXML
	public void initialize()
	{
		gameModel = new GameModel();

		tileGrid = new SpriteView[gameModel.map.getHeight()][gameModel.map.getWidth()];
		for ( int y = 0; y < tileGrid.length; y++ )
		{
			for ( int x = 0; x < tileGrid[y].length; x++ )
			{
				SpriteView spriteView = new SpriteView();
				ImageView tileImage = spriteView.getImageView();
				tileImage.setFitHeight(50);
				tileImage.setFitWidth(50);
				tileImage.setPreserveRatio(true);
				final int tileY = y, tileX = x;
				tileImage.setOnMouseClicked(_ -> gameModel.onTileClicked(tileX, tileY));

				spriteView.spriteProviderProperty().bindBidirectional(gameModel.map.getTileProperty(x, y));

				imageGrid.add(tileImage, y, x);
				tileGrid[y][x] = spriteView;
			}
		}

		debugText.textProperty().bindBidirectional(gameModel.debugMessage);
		gameModel.addListener(this);
	}

	@Override
	public void handle( GameEvent event )
	{
		switch (event.type)
		{
			case MESSAGE -> debugText.setText(((GameDataEvent<String>)event).data);
		}
	}
}