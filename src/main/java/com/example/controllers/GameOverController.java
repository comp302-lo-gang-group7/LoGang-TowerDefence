package com.example.controllers;

import com.example.game.GameManager;
import com.example.main.Main;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;

/**
 * Controller for the game over overlay. Provides a button to return to the main menu.
 */
public class GameOverController {
    @FXML private StackPane gameOverRoot;

    @FXML
    public void handleExit(ActionEvent e) {
        // Ensure the game loop is stopped before leaving the game screen
        GameManager.getInstance().stop();
        // Remove this overlay in case the scene is reused
        if (gameOverRoot.getParent() instanceof Pane parent) {
            parent.getChildren().remove(gameOverRoot);
        }
        Main.getViewManager().resizeWindowDefault();
        Main.getViewManager().switchTo("/com/example/fxml/home_page.fxml");
    }
}