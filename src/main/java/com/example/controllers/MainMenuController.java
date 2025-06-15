package com.example.controllers;

import java.net.URL;
import java.util.ResourceBundle;

import com.example.main.Main;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.SVGPath;

/**
 * Controller for the main menu page. Uses ViewManager to handle navigation.
 */
public class MainMenuController extends Controller implements Initializable {
    
    @FXML private Button newGameBtn;
    @FXML private Button loadGameBtn;
    @FXML private Button mapEditorBtn;
    @FXML private Button quitBtn;
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Add SVG icons to buttons
        setupButtonIcons();
        
        // Add styling and animation
        setupButtonAnimations();
    }
    
    private void setupButtonIcons() {
        // Create SVG icons (no image files needed)
        addSvgIconToButton(newGameBtn, "M8,5.14V19.14L19,12.14L8,5.14Z", 20); // Play icon
        addSvgIconToButton(loadGameBtn, "M14,2H6A2,2 0 0,0 4,4V20A2,2 0 0,0 6,22H18A2,2 0 0,0 20,20V8L14,2M18,20H6V4H13V9H18V20Z", 20); // File icon
        addSvgIconToButton(mapEditorBtn, "M20.5,3L20.34,3.03L15,5.1L9,3L3.36,4.9C3.15,4.97 3,5.15 3,5.38V20.5A0.5,0.5 0 0,0 3.5,21L3.66,20.97L9,18.9L15,21L20.64,19.1C20.85,19.03 21,18.85 21,18.62V3.5A0.5,0.5 0 0,0 20.5,3M10,5.47L14,6.87V18.53L10,17.13V5.47M5,6.46L8,5.45V17.15L5,18.31V6.46M16,18.53V6.87L19,5.71V17.53L16,18.53Z", 20); // Map icon
        addSvgIconToButton(quitBtn, "M19,3H5C3.89,3 3,3.89 3,5V9H5V5H19V19H5V15H3V19A2,2 0 0,0 5,21H19A2,2 0 0,0 21,19V5C21,3.89 20.1,3 19,3M10.08,15.58L11.5,17L16.5,12L11.5,7L10.08,8.41L12.67,11H3V13H12.67L10.08,15.58Z", 20); // Exit icon
    }
    
    private void addSvgIconToButton(Button button, String svgPathContent, double size) {
        // Create SVG icon
        SVGPath svgPath = new SVGPath();
        svgPath.setContent(svgPathContent);
        svgPath.setStyle("-fx-fill: #d9c9a0;");
        
        // Create a region to hold the SVG with proper sizing
        Region iconRegion = new Region();
        iconRegion.setShape(svgPath);
        iconRegion.setMinSize(size, size);
        iconRegion.setPrefSize(size, size);
        iconRegion.setMaxSize(size, size);
        iconRegion.setStyle("-fx-background-color: #d9c9a0;");
        
        // Add to button with proper spacing
        StackPane iconContainer = new StackPane(iconRegion);
        iconContainer.setPrefWidth(30);
        button.setGraphic(iconContainer);
        button.setGraphicTextGap(15);
    }
    
    private void setupButtonAnimations() {
        // Add styling for all buttons
        String buttonCss = 
            "-fx-background-color: linear-gradient(#7d5a3c, #5d4228); " +
            "-fx-background-radius: 8; " +
            "-fx-text-fill: #e8d9b5; " +
            "-fx-font-size: 16px; " +
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
            "-fx-font-size: 16px; " +
            "-fx-font-weight: bold; " +
            "-fx-font-family: 'Segoe UI'; " +
            "-fx-padding: 8 15 8 15; " +
            "-fx-border-color: linear-gradient(#c6965f, #b88d5a); " +
            "-fx-border-width: 2; " +
            "-fx-border-radius: 8; " +
            "-fx-cursor: hand; " +
            "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.7), 6, 0.0, 0, 2);";
        
        String pressedCss = 
            "-fx-background-color: linear-gradient(#5d4228, #4e3822); " +
            "-fx-background-radius: 8; " +
            "-fx-text-fill: #d9c9a0; " +
            "-fx-font-size: 16px; " +
            "-fx-font-weight: bold; " +
            "-fx-font-family: 'Segoe UI'; " +
            "-fx-padding: 9 15 7 15; " + // Offset padding to simulate pressed effect
            "-fx-border-color: #7d5a3c; " +
            "-fx-border-width: 2; " +
            "-fx-border-radius: 8; " +
            "-fx-effect: innershadow(three-pass-box, rgba(0,0,0,0.4), 4, 0.0, 0, 1);";
        
        // Apply to all buttons
        setupButtonStyle(newGameBtn, buttonCss, hoverCss, pressedCss);
        setupButtonStyle(loadGameBtn, buttonCss, hoverCss, pressedCss);
        setupButtonStyle(mapEditorBtn, buttonCss, hoverCss, pressedCss);
        setupButtonStyle(quitBtn, buttonCss, hoverCss, pressedCss);
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
            button.setScaleX(1.02);
            button.setScaleY(1.02);
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
    public void goToNewGamePage() {
        Main.getViewManager().switchTo("/com/example/fxml/create_game_page.fxml");
    }

    @FXML
    public void goToLoadGamePage() {
        Main.getViewManager().switchTo("/com/example/fxml/load_game_page.fxml");
    }

    @FXML
    public void goToMapEditorPage() {
        Main.getViewManager().switchTo("/com/example/fxml/map_editor_page.fxml");
    }

    @FXML
    public void goToSettings() { 
        Main.getViewManager().switchTo("/com/example/fxml/settings.fxml");
    }

    @FXML
    public void terminateApplication() { 
        Main.getViewManager().terminateApplication();
    }
}

