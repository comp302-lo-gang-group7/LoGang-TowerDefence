package com.example.utils;

import javafx.scene.ImageCursor;
import javafx.scene.control.Button;
import javafx.scene.control.DialogPane;
import javafx.scene.image.Image;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.stage.Popup;
import javafx.stage.Window;
import javafx.stage.Stage;

public class StyleManager {
    private static ImageCursor customCursor;
    
    static {
        try {
            Image cursorImage = new Image(StyleManager.class.getResourceAsStream("/com/example/assets/ui/01.png"));
            customCursor = new ImageCursor(cursorImage);
        } catch (Exception e) {
            System.out.println("Failed to load cursor image: " + e.getMessage());
            customCursor = null;
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

    public static void setupButtonWithCustomCursor(Button button) {
        // Set initial style
        button.setStyle(BUTTON_NORMAL_STYLE);
        if (customCursor != null) {
            button.setCursor(customCursor);
        }

        // Add hover/exit listeners
        button.setOnMouseEntered(e -> {
            button.setStyle(BUTTON_HOVER_STYLE);
            if (customCursor != null) {
                button.setCursor(customCursor);
            }
            button.setScaleX(1.05);
            button.setScaleY(1.05);
        });

        button.setOnMouseExited(e -> {
            button.setStyle(BUTTON_NORMAL_STYLE);
            if (customCursor != null) {
                button.setCursor(customCursor);
            }
            button.setScaleX(1.0);
            button.setScaleY(1.0);
        });

        // Add pressed/released listeners
        button.setOnMousePressed(e -> {
            button.setStyle(BUTTON_PRESSED_STYLE);
            if (customCursor != null) {
                button.setCursor(customCursor);
            }
            button.setScaleX(1.02);
            button.setScaleY(1.02);
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
            if (customCursor != null) {
                button.setCursor(customCursor);
            }
        });
    }

    public static ImageCursor getCustomCursor() {
        return customCursor;
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

    /**
     * Recursively apply custom cursor to a node and all its children
     */
    public static void applyCustomCursorRecursively(Node node) {
        if (customCursor != null) {
            node.setCursor(customCursor);
            
            if (node instanceof Parent) {
                Parent parent = (Parent) node;
                parent.getChildrenUnmodifiable().forEach(StyleManager::applyCustomCursorRecursively);
            }
        }
    }
} 