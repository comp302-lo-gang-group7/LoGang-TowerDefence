package com.example.controllers;

import com.example.main.Main;
import javafx.fxml.FXML;

public class MainMenuController {

    @FXML
    public void goToNewGamePage() {
        Main.getViewManager().switchTo("/com/example/fxml/create_game_page.fxml");
    }

    @FXML
    public void goToLoadGamePage() {
        Main.getViewManager().switchTo("/com/example/fxml/load_game_page.fxml");
    }
}
