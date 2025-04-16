package com.example.game;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class Application extends javafx.application.Application
{
	public static GameModel gameModel;

	@Override
	public void start( Stage stage ) throws IOException
	{
		gameModel = new GameModel();
		FXMLLoader fxmlLoader = new FXMLLoader(Application.class.getResource("gameScreenView.fxml"));
		Scene scene = new Scene(fxmlLoader.load(), 640, 420);
		stage.setTitle("Hello!");
		stage.setScene(scene);
		stage.show();
	}

	public static void main( String[] args )
	{
		launch();
	}
}