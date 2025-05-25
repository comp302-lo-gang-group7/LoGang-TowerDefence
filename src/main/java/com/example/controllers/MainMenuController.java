package com.example.controllers;

import com.example.main.Main;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.layout.Region;
import javafx.scene.shape.SVGPath;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.animation.TranslateTransition;
import javafx.util.Duration;
import javafx.scene.text.Text;
import javafx.scene.layout.HBox;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Controller for the main menu page. Uses ViewManager to handle navigation.
 */
public class MainMenuController extends Controller implements Initializable {
    
    @FXML private Button newGameBtn;
    @FXML private Button defaultGameBtn;
    @FXML private Button customGameBtn;
    @FXML private Button loadGameBtn;
    @FXML private Button mapEditorBtn;
    @FXML private Button settingsBtn;
    @FXML private Button quitBtn;
    @FXML private VBox newGameOptions;
    
    private boolean newGameOptionsVisible = false;
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Add SVG icons to buttons
        setupButtonIcons();
        
        // Add styling and animation
        setupButtonAnimations();
        
        // Add sub-menu button styling
        setupSubMenuButtonStyle();
    }
    
    private void setupButtonIcons() {
        // Create SVG icons (no image files needed)
        addSvgIconToButton(newGameBtn, "M8,5.14V19.14L19,12.14L8,5.14Z", 20); // Play icon
        addSvgIconToButton(loadGameBtn, "M14,2H6A2,2 0 0,0 4,4V20A2,2 0 0,0 6,22H18A2,2 0 0,0 20,20V8L14,2M18,20H6V4H13V9H18V20Z", 20); // File icon
        addSvgIconToButton(mapEditorBtn, "M20.5,3L20.34,3.03L15,5.1L9,3L3.36,4.9C3.15,4.97 3,5.15 3,5.38V20.5A0.5,0.5 0 0,0 3.5,21L3.66,20.97L9,18.9L15,21L20.64,19.1C20.85,19.03 21,18.85 21,18.62V3.5A0.5,0.5 0 0,0 20.5,3M10,5.47L14,6.87V18.53L10,17.13V5.47M5,6.46L8,5.45V17.15L5,18.31V6.46M16,18.53V6.87L19,5.71V17.53L16,18.53Z", 20); // Map icon
        addSvgIconToButton(quitBtn, "M19,3H5C3.89,3 3,3.89 3,5V9H5V5H19V19H5V15H3V19A2,2 0 0,0 5,21H19A2,2 0 0,0 21,19V5C21,3.89 20.1,3 19,3M10.08,15.58L11.5,17L16.5,12L11.5,7L10.08,8.41L12.67,11H3V13H12.67L10.08,15.58Z", 20); // Exit icon
        
        // Add settings icon with special styling
        SVGPath settingsIcon = new SVGPath();
        settingsIcon.setContent("M12,15.5A3.5,3.5 0 0,1 8.5,12A3.5,3.5 0 0,1 12,8.5A3.5,3.5 0 0,1 15.5,12A3.5,3.5 0 0,1 12,15.5M19.43,12.97C19.47,12.65 19.5,12.33 19.5,12C19.5,11.67 19.47,11.34 19.43,11L21.54,9.37C21.73,9.22 21.78,8.95 21.66,8.73L19.66,5.27C19.54,5.05 19.27,4.96 19.05,5.05L16.56,6.05C16.04,5.66 15.5,5.32 14.87,5.07L14.5,2.42C14.46,2.18 14.25,2 14,2H10C9.75,2 9.54,2.18 9.5,2.42L9.13,5.07C8.5,5.32 7.96,5.66 7.44,6.05L4.95,5.05C4.73,4.96 4.46,5.05 4.34,5.27L2.34,8.73C2.21,8.95 2.27,9.22 2.46,9.37L4.57,11C4.53,11.34 4.5,11.67 4.5,12C4.5,12.33 4.53,12.65 4.57,12.97L2.46,14.63C2.27,14.78 2.21,15.05 2.34,15.27L4.34,18.73C4.46,18.95 4.73,19.03 4.95,18.95L7.44,17.94C7.96,18.34 8.5,18.68 9.13,18.93L9.5,21.58C9.54,21.82 9.75,22 10,22H14C14.25,22 14.46,21.82 14.5,21.58L14.87,18.93C15.5,18.67 16.04,18.34 16.56,17.94L19.05,18.95C19.27,19.03 19.54,18.95 19.66,18.73L21.66,15.27C21.78,15.05 21.73,14.78 21.54,14.63L19.43,12.97Z");
        settingsIcon.setStyle("-fx-fill: #d9c9a0;");
        
        Region iconRegion = new Region();
        iconRegion.setShape(settingsIcon);
        iconRegion.setMinSize(18, 18);
        iconRegion.setPrefSize(18, 18);
        iconRegion.setMaxSize(18, 18);
        iconRegion.setStyle("-fx-background-color: #d9c9a0;");
        
        StackPane iconContainer = new StackPane(iconRegion);
        iconContainer.setPrefWidth(24);
        
        // Create text for settings button
        Text settingsText = new Text("Settings");
        settingsText.setStyle("-fx-font-family: 'Segoe UI'; -fx-font-size: 14px; -fx-font-weight: bold;");
        settingsText.setFill(javafx.scene.paint.Color.web("#e8d9b5"));
        
        // Create HBox for icon and text
        HBox content = new HBox(10);
        content.setAlignment(javafx.geometry.Pos.CENTER);
        content.getChildren().addAll(iconContainer, settingsText);
        
        // Set the HBox as the button's graphic
        settingsBtn.setGraphic(content);
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
        
        String settingsButtonCss = 
            "-fx-background-color: linear-gradient(#7d5a3c, #5d4228); " +
            "-fx-background-radius: 6; " +
            "-fx-text-fill: #e8d9b5; " +
            "-fx-font-size: 14px; " +
            "-fx-font-weight: bold; " +
            "-fx-font-family: 'Segoe UI'; " +
            "-fx-padding: 6 12 6 12; " +
            "-fx-border-color: linear-gradient(#a07748, #8a673c); " +
            "-fx-border-width: 1.5; " +
            "-fx-border-radius: 6; " +
            "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.5), 4, 0.0, 0, 1);";
        
        String settingsHoverCss = 
            "-fx-background-color: linear-gradient(#94704c, #705236); " +
            "-fx-background-radius: 6; " +
            "-fx-text-fill: #f5ead9; " +
            "-fx-font-size: 14px; " +
            "-fx-font-weight: bold; " +
            "-fx-font-family: 'Segoe UI'; " +
            "-fx-padding: 6 12 6 12; " +
            "-fx-border-color: linear-gradient(#c6965f, #b88d5a); " +
            "-fx-border-width: 1.5; " +
            "-fx-border-radius: 6; " +
            "-fx-cursor: hand; " +
            "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.6), 5, 0.0, 0, 1);";
        
        String settingsPressedCss = 
            "-fx-background-color: linear-gradient(#5d4228, #4e3822); " +
            "-fx-background-radius: 6; " +
            "-fx-text-fill: #d9c9a0; " +
            "-fx-font-size: 14px; " +
            "-fx-font-weight: bold; " +
            "-fx-font-family: 'Segoe UI'; " +
            "-fx-padding: 7 12 5 12; " +
            "-fx-border-color: #7d5a3c; " +
            "-fx-border-width: 1.5; " +
            "-fx-border-radius: 6; " +
            "-fx-effect: innershadow(three-pass-box, rgba(0,0,0,0.3), 3, 0.0, 0, 1);";
        
        // Apply to main menu buttons
        setupButtonStyle(newGameBtn, buttonCss, hoverCss, pressedCss);
        setupButtonStyle(loadGameBtn, buttonCss, hoverCss, pressedCss);
        setupButtonStyle(mapEditorBtn, buttonCss, hoverCss, pressedCss);
        setupButtonStyle(quitBtn, buttonCss, hoverCss, pressedCss);
        
        // Apply special styling to settings button
        setupButtonStyle(settingsBtn, settingsButtonCss, settingsHoverCss, settingsPressedCss);
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

    private void setupSubMenuButtonStyle() {
        String subMenuButtonCss = 
            "-fx-background-color: linear-gradient(#6b4c2e, #4e331f); " +
            "-fx-background-radius: 6; " +
            "-fx-text-fill: #e8d9b5; " +
            "-fx-font-size: 14px; " +
            "-fx-font-weight: bold; " +
            "-fx-font-family: 'Segoe UI'; " +
            "-fx-padding: 6 12 6 12; " +
            "-fx-border-color: linear-gradient(#8a673c, #705236); " +
            "-fx-border-width: 1.5; " +
            "-fx-border-radius: 6; " +
            "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.4), 4, 0.0, 0, 1);";
        
        String subMenuHoverCss = 
            "-fx-background-color: linear-gradient(#7d5a3c, #5d4228); " +
            "-fx-background-radius: 6; " +
            "-fx-text-fill: #f5ead9; " +
            "-fx-font-size: 14px; " +
            "-fx-font-weight: bold; " +
            "-fx-font-family: 'Segoe UI'; " +
            "-fx-padding: 6 12 6 12; " +
            "-fx-border-color: linear-gradient(#a07748, #8a673c); " +
            "-fx-border-width: 1.5; " +
            "-fx-border-radius: 6; " +
            "-fx-cursor: hand; " +
            "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.5), 5, 0.0, 0, 1);";
        
        String subMenuPressedCss = 
            "-fx-background-color: linear-gradient(#4e331f, #3d2819); " +
            "-fx-background-radius: 6; " +
            "-fx-text-fill: #d9c9a0; " +
            "-fx-font-size: 14px; " +
            "-fx-font-weight: bold; " +
            "-fx-font-family: 'Segoe UI'; " +
            "-fx-padding: 7 12 5 12; " +
            "-fx-border-color: #6b4c2e; " +
            "-fx-border-width: 1.5; " +
            "-fx-border-radius: 6; " +
            "-fx-effect: innershadow(three-pass-box, rgba(0,0,0,0.3), 3, 0.0, 0, 1);";
        
        setupButtonStyle(defaultGameBtn, subMenuButtonCss, subMenuHoverCss, subMenuPressedCss);
        setupButtonStyle(customGameBtn, subMenuButtonCss, subMenuHoverCss, subMenuPressedCss);
    }
    
    @FXML
    public void toggleNewGameOptions() {
        newGameOptionsVisible = !newGameOptionsVisible;
        newGameOptions.setVisible(newGameOptionsVisible);
        newGameOptions.setManaged(newGameOptionsVisible);
        
        // Animate the transition
        TranslateTransition tt = new TranslateTransition(Duration.millis(200), newGameOptions);
        if (newGameOptionsVisible) {
            newGameOptions.setTranslateY(-10);
            tt.setToY(0);
        } else {
            tt.setToY(-10);
        }
        tt.play();
    }

    @FXML
    public void goToDefaultGamePage() {
        Main.getViewManager().switchTo("/com/example/fxml/game_screen_page.fxml");
    }

    @FXML
    public void goToCustomGamePage() {
        Main.getViewManager().switchTo("/com/example/fxml/create_game_page.fxml");
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

