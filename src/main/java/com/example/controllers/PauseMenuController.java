package com.example.controllers;

import com.example.game.GameManager;
import com.example.main.Main;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;

public class PauseMenuController {
    @FXML private StackPane pauseRoot;
    @FXML private Button resumeBtn, settingsBtn, exitBtn;

    @FXML
    public void handleResume(ActionEvent e) {
        GameManager.getInstance().resume();
        ((Pane)pauseRoot.getParent()).getChildren().remove(pauseRoot);
    }

    @FXML
    public void handleSettings(ActionEvent e) {
        Main.getViewManager().resizeWindowDefault();
        Main.getViewManager().switchTo("/com/example/fxml/settings.fxml");
    }

    @FXML
    public void handleExit(ActionEvent e) {
        GameManager.getInstance().stop();
        Main.getViewManager().resizeWindowDefault();
        Main.getViewManager().switchTo("/com/example/fxml/home_page.fxml");
    }
}
