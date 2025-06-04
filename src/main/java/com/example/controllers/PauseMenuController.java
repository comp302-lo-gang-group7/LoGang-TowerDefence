package com.example.controllers;

import com.example.game.GameManager;
import com.example.main.Main;
import com.example.utils.StyleManager;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;

import java.net.URL;
import java.util.ResourceBundle;

public class PauseMenuController implements Initializable {
    @FXML private StackPane pauseRoot;
    @FXML private Button resumeBtn, settingsBtn, exitBtn;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Apply StyleManager to all buttons
        StyleManager.setupButtonWithCustomCursor(resumeBtn);
        StyleManager.setupButtonWithCustomCursor(settingsBtn);
        StyleManager.setupButtonWithCustomCursor(exitBtn);
    }

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
