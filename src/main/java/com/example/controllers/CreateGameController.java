package com.example.controllers;

import java.net.URL;
import java.util.ResourceBundle;

import com.example.main.Main;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;

/**
 * Controller class for the Create Game page. Handles button interactions and applies consistent styling.
 */
public class CreateGameController extends Controller implements Initializable {

    @FXML private Button defaultGameBtn;
    @FXML private Button customGameBtn;
    @FXML private Button backBtn;

    /**
     * Initializes the controller and sets up button styles.
     *
     * @param location The location used to resolve relative paths for the root object, or null if not known.
     * @param resources The resources used to localize the root object, or null if not applicable.
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setupButtonStyles();
    }
    
    /**
     * Configures the styles for all buttons on the page.
     */
    private void setupButtonStyles() {
        String normalStyle = "-fx-background-color: linear-gradient(#7d5a3c, #5d4228); " +
                            "-fx-background-radius: 8; " +
                            "-fx-text-fill: #e8d9b5; " +
                            "-fx-font-size: 16px; " +
                            "-fx-font-weight: bold; " +
                            "-fx-font-family: 'Segoe UI'; " +
                            "-fx-padding: 12 15 12 15; " +
                            "-fx-border-color: linear-gradient(#a07748, #8a673c); " +
                            "-fx-border-width: 2; " +
                            "-fx-border-radius: 8; " +
                            "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.6), 5, 0.0, 0, 1);";
        
        String hoverStyle = "-fx-background-color: linear-gradient(#94704c, #705236); " +
                           "-fx-border-color: linear-gradient(#c6965f, #b88d5a); " +
                           "-fx-background-radius: 8; " +
                           "-fx-text-fill: #f5ead9; " +
                           "-fx-font-size: 16px; " +
                           "-fx-font-weight: bold; " +
                           "-fx-font-family: 'Segoe UI'; " +
                           "-fx-padding: 12 15 12 15; " +
                           "-fx-border-width: 2; " +
                           "-fx-border-radius: 8; " +
                           "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.7), 6, 0.0, 0, 2);";
        
        String pressedStyle = "-fx-background-color: linear-gradient(#5d4228, #4e3822); " +
                             "-fx-border-color: #7d5a3c; " +
                             "-fx-background-radius: 8; " +
                             "-fx-text-fill: #d9c9a0; " +
                             "-fx-font-size: 16px; " +
                             "-fx-font-weight: bold; " +
                             "-fx-font-family: 'Segoe UI'; " +
                             "-fx-padding: 12 15 12 15; " +
                             "-fx-border-width: 2; " +
                             "-fx-border-radius: 8; " +
                             "-fx-effect: innershadow(three-pass-box, rgba(0,0,0,0.4), 4, 0.0, 0, 1);";
        
        setupButtonStyle(defaultGameBtn, normalStyle, hoverStyle, pressedStyle);
        setupButtonStyle(customGameBtn, normalStyle, hoverStyle, pressedStyle);
        setupButtonStyle(backBtn, normalStyle, hoverStyle, pressedStyle);
    }
    
    /**
     * Applies the specified styles to a button and sets up event listeners for hover and press actions.
     *
     * @param button The button to style.
     * @param normalStyle The style to apply when the button is in its normal state.
     * @param hoverStyle The style to apply when the button is hovered over.
     * @param pressedStyle The style to apply when the button is pressed.
     */
    private void setupButtonStyle(Button button, String normalStyle, String hoverStyle, String pressedStyle) {
        button.setStyle(normalStyle);
        
        button.setOnMouseEntered(e -> {
            button.setStyle(hoverStyle);
            button.setScaleX(1.05);
            button.setScaleY(1.05);
        });
        
        button.setOnMouseExited(e -> {
            button.setStyle(normalStyle);
            button.setScaleX(1.0);
            button.setScaleY(1.0);
        });
        
        button.setOnMousePressed(e -> {
            button.setStyle(pressedStyle);
        });
        
        button.setOnMouseReleased(e -> {
            if (button.isHover()) {
                button.setStyle(hoverStyle);
                button.setScaleX(1.05);
                button.setScaleY(1.05);
            } else {
                button.setStyle(normalStyle);
                button.setScaleX(1.0);
                button.setScaleY(1.0);
            }
        });
    }

    /**
     * Navigates to the default game configuration page.
     */
    @FXML
    public void goToDefaultGamePage() {
        Main.getViewManager().switchTo("/com/example/fxml/game_config_page.fxml");
    }

    /**
     * Navigates to the custom game configuration page.
     */
    @FXML
    public void goToCustomGamePage() {
        Main.getViewManager().switchTo("/com/example/fxml/custom_game_page.fxml");
    }

    /**
     * Navigates to the home page.
     */
    @FXML
    public void goToHomePage() {
        Main.getViewManager().switchTo("/com/example/fxml/home_page.fxml");
    }
}