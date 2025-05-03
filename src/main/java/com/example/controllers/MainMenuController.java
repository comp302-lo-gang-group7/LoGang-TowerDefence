package com.example.controllers;

import com.example.main.Main;
import javafx.fxml.FXML;


/**
 * This is a controller for the main menu page. It uses the ViewManager to request changes based on button click actions taken in its respective view (FXML file).
 */
public class MainMenuController extends Controller {

    @FXML
    public void goToNewGamePage() {
        Main.getViewManager().switchTo("/com/example/fxml/create_game_page.fxml");
    }

    @FXML
    public void goToLoadGamePage() {
        Main.getViewManager().switchTo("/com/example/fxml/load_game_page.fxml");
    }

    @FXML
    public void goToMapEditorPage() {
        Main.getViewManager().switchTo("/com/example/fxml/map_editor_page.fxml");
    }

    @FXML
    public void goToSettings() { Main.getViewManager().switchTo("/com/example/fxml/settings.fxml"); }

    @FXML
    public void terminateApplication() { Main.getViewManager().terminateApplication();}

}
