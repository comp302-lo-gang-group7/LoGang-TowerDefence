package com.example.main;

import com.example.game.*;
import com.example.ui.*;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;

public class Main extends javafx.application.Application
{
	public static GameModel gameModel;
	public static ViewManager viewManager;

	@Override
	public void start(Stage stage) throws IOException {
		gameModel = new GameModel();
		viewManager = new ViewManager(stage);
		viewManager.switchTo("/com/example/fxml/home_page.fxml");
	}

	public static ViewManager getViewManager() {
		return viewManager;
	}

	public static void main( String[] args )
	{
		launch();
	}
}