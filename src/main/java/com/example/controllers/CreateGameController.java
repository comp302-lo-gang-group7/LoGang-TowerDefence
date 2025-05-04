package com.example.controllers;

import javafx.fxml.FXML;
import com.example.main.Main;

/**
 * This is a controller for the create game page. It uses the ViewManager to request changes based on button click actions taken in its respective view (FXML file).
 */
public class CreateGameController extends Controller {
    @FXML
    public void goToDefaultGamePage() { Main.getViewManager().switchTo("/com/example/fxml/game_screen_page.fxml");}
}
