package com.example.controllers;

import com.example.main.Main;
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
        // Apply consistent wooden button styling to match the home page
        setupButtonStyles();
    }
    
    private void setupButtonStyles() {
        // Style for normal state - rich wood texture
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
        
        // Style for hover state - slightly lighter brown
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
                           "-fx-cursor: hand; " +
                           "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.7), 6, 0.0, 0, 2);";
        
        // Style for pressed state - darker brown
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
        
        // Apply styles to all buttons
        setupButtonStyle(defaultGameBtn, normalStyle, hoverStyle, pressedStyle);
        setupButtonStyle(customGameBtn, normalStyle, hoverStyle, pressedStyle);
        setupButtonStyle(backBtn, normalStyle, hoverStyle, pressedStyle);
    }
    
    private void setupButtonStyle(Button button, String normalStyle, String hoverStyle, String pressedStyle) {
        // Set initial style
        button.setStyle(normalStyle);
        
        // Add hover/exit listeners
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
        
        // Add pressed/released listeners
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

    @FXML
    public void goToDefaultGamePage() {
        Main.getViewManager().switchTo("/com/example/fxml/game_screen_page.fxml");
    }

    @FXML
    public void goToHomePage() {
        Main.getViewManager().switchTo("/com/example/fxml/home_page.fxml");
    }
}