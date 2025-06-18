package com.example.controllers;

import com.example.game.GameManager;
import com.example.main.Main;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Controller for the Pause Menu in the game. Handles button interactions and menu functionality.
 */
public class PauseMenuController implements Initializable {
    @FXML private StackPane pauseRoot;
    @FXML private Button resumeBtn, settingsBtn, exitBtn;
    
    private static final String BUTTON_NORMAL_STYLE = "-fx-background-color: linear-gradient(#6b4c2e, #4e331f); " +
                                                     "-fx-text-fill: #e8d9b5; -fx-font-family: 'Segoe UI'; " +
                                                     "-fx-font-size: 14px; -fx-font-weight: bold; " +
                                                     "-fx-border-color: #8a673c; -fx-border-width: 2; " +
                                                     "-fx-border-radius: 5; -fx-background-radius: 5;";
    
    private static final String BUTTON_HOVER_STYLE = "-fx-background-color: linear-gradient(#7d5a3c, #5d4228); " +
                                                    "-fx-text-fill: #f5ead9; -fx-font-family: 'Segoe UI'; " +
                                                    "-fx-font-size: 14px; -fx-font-weight: bold; " + 
                                                    "-fx-border-color: #a07748; -fx-border-width: 2; " +
                                                    "-fx-border-radius: 5; -fx-background-radius: 5;";
    
    private static final String BUTTON_PRESSED_STYLE = "-fx-background-color: linear-gradient(#422c17, #6b4c2e); " +
                                                      "-fx-text-fill: #d9c9a0; -fx-font-family: 'Segoe UI'; " +
                                                      "-fx-font-size: 14px; -fx-font-weight: bold; " +
                                                      "-fx-border-color: #8a673c; -fx-border-width: 2; " +
                                                      "-fx-border-radius: 5; -fx-background-radius: 5;";

    /**
     * Initializes the Pause Menu controller and applies button styling.
     *
     * @param location The location used to resolve relative paths for the root object, or null if unknown.
     * @param resources The resources used to localize the root object, or null if not applicable.
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setupButtonEffects(resumeBtn);
        setupButtonEffects(exitBtn);
    }

    /**
     * Sets up visual effects for a button, including hover and click effects.
     *
     * @param button The button to apply effects to.
     */
    private void setupButtonEffects(Button button) {
        button.setStyle(BUTTON_NORMAL_STYLE);
        button.setOnMouseEntered(e -> {
            button.setStyle(BUTTON_HOVER_STYLE);
            button.setScaleX(1.05);
            button.setScaleY(1.05);
        });
        button.setOnMouseExited(e -> {
            button.setStyle(BUTTON_NORMAL_STYLE);
            button.setScaleX(1.0);
            button.setScaleY(1.0);
        });
        button.setOnMousePressed(e -> {
            button.setStyle(BUTTON_PRESSED_STYLE);
            animateButtonClick(button);
        });
        button.setOnMouseReleased(e -> {
            if (button.isHover()) {
                button.setStyle(BUTTON_HOVER_STYLE);
                button.setScaleX(1.05);
                button.setScaleY(1.05);
            } else {
                button.setStyle(BUTTON_NORMAL_STYLE);
                button.setScaleX(1.0);
                button.setScaleY(1.0);
            }
        });
    }

    /**
     * Animates the button click by scaling it down slightly.
     *
     * @param button The button to animate.
     */
    private void animateButtonClick(Button button) {
        button.setScaleX(0.95);
        button.setScaleY(0.95);
    }

    /**
     * Handles the Resume button click event. Resumes the game and removes the pause menu.
     *
     * @param e The action event triggered by the button click.
     */
    @FXML
    public void handleResume(ActionEvent e) {
        GameManager.getInstance().resume();
        ((Pane)pauseRoot.getParent()).getChildren().remove(pauseRoot);
    }

    /**
     * Handles the Settings button click event. Switches to the settings view.
     *
     * @param e The action event triggered by the button click.
     */
    @FXML
    public void handleSettings(ActionEvent e) {
        Main.getViewManager().switchTo("/com/example/fxml/settings.fxml");
    }

    /**
     * Handles the Exit button click event. Stops the game and switches to the home page view.
     *
     * @param e The action event triggered by the button click.
     */
    @FXML
    public void handleExit(ActionEvent e) {
        GameManager.getInstance().stop();
        Main.getViewManager().switchTo("/com/example/fxml/home_page.fxml");
    }
}
