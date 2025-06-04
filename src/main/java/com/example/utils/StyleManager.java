package com.example.utils;

import javafx.scene.Cursor;
import javafx.scene.ImageCursor;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.DialogPane;
import javafx.scene.image.Image;
import javafx.scene.layout.Region;
import javafx.scene.control.Alert;
import javafx.stage.Popup;
import javafx.stage.Window;
import javafx.stage.Stage;

/**
 * Utility class to manage styles and cursor behavior consistently across the application.
 */
public class StyleManager {
    private static ImageCursor customCursor;
    
    static {
        try {
            Image cursorImage = new Image(StyleManager.class.getResourceAsStream("/com/example/assets/ui/01.png"));
            customCursor = new ImageCursor(cursorImage);
        } catch (Exception e) {
            System.err.println("Failed to load custom cursor: " + e.getMessage());
        }
    }

    public static final String BUTTON_NORMAL_STYLE = "-fx-background-color: linear-gradient(#7d5a3c, #5d4228); " +
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

    public static final String BUTTON_HOVER_STYLE = "-fx-background-color: linear-gradient(#94704c, #705236); " +
            "-fx-background-radius: 8; " +
            "-fx-text-fill: #f5ead9; " +
            "-fx-font-size: 16px; " +
            "-fx-font-weight: bold; " +
            "-fx-font-family: 'Segoe UI'; " +
            "-fx-padding: 8 15 8 15; " +
            "-fx-border-color: linear-gradient(#c6965f, #b88d5a); " +
            "-fx-border-width: 2; " +
            "-fx-border-radius: 8; " +
            "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.7), 6, 0.0, 0, 2);";

    public static final String BUTTON_PRESSED_STYLE = "-fx-background-color: linear-gradient(#5d4228, #4e3822); " +
            "-fx-background-radius: 8; " +
            "-fx-text-fill: #d9c9a0; " +
            "-fx-font-size: 16px; " +
            "-fx-font-weight: bold; " +
            "-fx-font-family: 'Segoe UI'; " +
            "-fx-padding: 9 15 7 15; " +
            "-fx-border-color: #7d5a3c; " +
            "-fx-border-width: 2; " +
            "-fx-border-radius: 8; " +
            "-fx-effect: innershadow(three-pass-box, rgba(0,0,0,0.4), 4, 0.0, 0, 1);";

    /**
     * Get the custom cursor instance.
     */
    public static Cursor getCustomCursor() {
        return customCursor;
    }

    /**
     * Apply the custom cursor to a button and set up its hover behavior.
     */
    public static void setupButtonWithCustomCursor(Button button) {
        if (button == null) return;
        
        button.setCursor(customCursor);
        button.setOnMouseEntered(e -> button.setCursor(customCursor));
        button.setOnMouseExited(e -> button.setCursor(customCursor));
    }

    /**
     * Apply the custom cursor to a scene.
     */
    public static void applyCustomCursorToScene(Scene scene) {
        if (scene == null) return;
        scene.setCursor(customCursor);
    }

    /**
     * Apply the custom cursor to a node and all its children recursively.
     */
    public static void applyCustomCursorRecursively(Node node) {
        if (node == null) return;
        
        node.setCursor(customCursor);
        
        if (node instanceof Region) {
            ((Region) node).getChildrenUnmodifiable().forEach(StyleManager::applyCustomCursorRecursively);
        }
    }

    /**
     * Apply custom cursor to an Alert dialog
     */
    public static void setupAlertWithCustomCursor(Alert alert) {
        if (customCursor != null) {
            // Get the dialog pane
            DialogPane dialogPane = alert.getDialogPane();
            
            // Set cursor for the dialog pane
            dialogPane.setCursor(customCursor);
            
            // Set cursor for all buttons in the dialog
            dialogPane.getButtonTypes().forEach(buttonType -> {
                Button button = (Button) dialogPane.lookupButton(buttonType);
                setupButtonWithCustomCursor(button);
            });
            
            // Set cursor for the dialog window when it's shown
            dialogPane.sceneProperty().addListener((observable, oldScene, newScene) -> {
                if (newScene != null) {
                    // Apply to scene
                    newScene.setCursor(customCursor);
                    
                    // Apply to window when it's shown
                    newScene.windowProperty().addListener((obs, oldWindow, newWindow) -> {
                        if (newWindow != null) {
                            applyCustomCursorToWindow(newWindow);
                        }
                    });
                }
            });
        }
    }

    /**
     * Apply custom cursor to any Popup
     */
    public static void setupPopupWithCustomCursor(Popup popup) {
        if (customCursor != null) {
            popup.getContent().forEach(node -> applyCustomCursorRecursively(node));
            
            // Ensure cursor is set when popup is shown
            popup.setOnShown(e -> popup.getScene().setCursor(customCursor));
        }
    }

    /**
     * Apply custom cursor to any window
     */
    public static void applyCustomCursorToWindow(Window window) {
        if (customCursor != null && window instanceof Stage) {
            Stage stage = (Stage) window;
            stage.getScene().setCursor(customCursor);
            applyCustomCursorRecursively(stage.getScene().getRoot());
        }
    }
} 