package com.example.controllers;

import com.example.main.Main;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Controller for the How To Play page.
 * Provides information on game mechanics and strategy.
 */
public class HowToPlayController extends Controller implements Initializable {
    
    @FXML private Button homeBtn;
    @FXML private StackPane headerBar;
    
    private double xOffset = 0;
    private double yOffset = 0;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Apply wooden button styling to the home button
        applyButtonStyle(homeBtn);
    }
    
    /**
     * Go back to the home page
     */
    @FXML
    public void goToHomePage() {
        Main.getViewManager().switchTo("/com/example/fxml/home_page.fxml");
    }
    
    /**
     * Make window draggable - handle mouse press
     */
    @FXML
    private void onHeaderBarPressed(MouseEvent event) {
        Stage stage = (Stage)((Node) event.getSource()).getScene().getWindow();
        xOffset = event.getSceneX();
        yOffset = event.getSceneY();
    }

    /**
     * Make window draggable - handle mouse drag
     */
    @FXML
    private void onHeaderBarDragged(MouseEvent event) {
        Stage stage = (Stage)((Node) event.getSource()).getScene().getWindow();
        stage.setX(event.getScreenX() - xOffset);
        stage.setY(event.getScreenY() - yOffset);
    }
    
    /**
     * Apply wooden styling to buttons with hover and press effects
     */
    private void applyButtonStyle(Button button) {
        String buttonCss = 
            "-fx-background-color: linear-gradient(#7d5a3c, #5d4228); " +
            "-fx-background-radius: 8; " +
            "-fx-text-fill: #e8d9b5; " +
            "-fx-font-size: 14px; " +
            "-fx-font-weight: bold; " +
            "-fx-font-family: 'Segoe UI'; " +
            "-fx-padding: 8 15 8 15; " +
            "-fx-border-color: linear-gradient(#a07748, #8a673c); " +
            "-fx-border-width: 2; " +
            "-fx-border-radius: 8; " +
            "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.6), 5, 0.0, 0, 2);";
        
        String hoverCss = 
            "-fx-background-color: linear-gradient(#94704c, #705236); " +
            "-fx-background-radius: 8; " +
            "-fx-text-fill: #f5ead9; " +
            "-fx-font-size: 14px; " +
            "-fx-font-weight: bold; " +
            "-fx-font-family: 'Segoe UI'; " +
            "-fx-padding: 8 15 8 15; " +
            "-fx-border-color: linear-gradient(#c6965f, #b88d5a); " +
            "-fx-border-width: 2; " +
            "-fx-border-radius: 8; " +
            "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.7), 6, 0.0, 0, 2);";
        
        String pressedCss = 
            "-fx-background-color: linear-gradient(#5d4228, #4e3822); " +
            "-fx-background-radius: 8; " +
            "-fx-text-fill: #d9c9a0; " +
            "-fx-font-size: 14px; " +
            "-fx-font-weight: bold; " +
            "-fx-font-family: 'Segoe UI'; " +
            "-fx-padding: 9 15 7 15; " +
            "-fx-border-color: #7d5a3c; " +
            "-fx-border-width: 2; " +
            "-fx-border-radius: 8; " +
            "-fx-effect: innershadow(three-pass-box, rgba(0,0,0,0.4), 4, 0.0, 0, 1);";
        
        // Set initial style
        button.setStyle(buttonCss);
        
        // Add hover/exit listeners
        button.setOnMouseEntered(e -> {
            button.setStyle(hoverCss);
            button.setScaleX(1.05);
            button.setScaleY(1.05);
        });
        
        button.setOnMouseExited(e -> {
            button.setStyle(buttonCss);
            button.setScaleX(1.0);
            button.setScaleY(1.0);
        });
        
        // Add pressed/released listeners
        button.setOnMousePressed(e -> {
            button.setStyle(pressedCss);
            button.setScaleX(1.02);
            button.setScaleY(1.02);
        });
        
        button.setOnMouseReleased(e -> {
            if (button.isHover()) {
                button.setStyle(hoverCss);
                button.setScaleX(1.05);
                button.setScaleY(1.05);
            } else {
                button.setStyle(buttonCss);
                button.setScaleX(1.0);
                button.setScaleY(1.0);
            }
        });
    }
}