package com.example.controllers;

import com.example.main.Main;
import com.example.utils.StyleManager;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;

import java.net.URL;
import java.util.ResourceBundle;

public class CreateGameController extends Controller implements Initializable {

    @FXML private Button defaultGameBtn;
    @FXML private Button customGameBtn;
    @FXML private Button backBtn;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setupButtonStyles();
    }
    
    private void setupButtonStyles() {
        // Apply StyleManager to all buttons
        StyleManager.setupButtonWithCustomCursor(defaultGameBtn);
        StyleManager.setupButtonWithCustomCursor(customGameBtn);
        StyleManager.setupButtonWithCustomCursor(backBtn);
    }

    @FXML
    public void goToDefaultGamePage() {
        Main.getViewManager().switchTo("/com/example/fxml/game_config_page.fxml");
    }

    @FXML
    public void goToHomePage() {
        Main.getViewManager().switchTo("/com/example/fxml/home_page.fxml");
    }
}