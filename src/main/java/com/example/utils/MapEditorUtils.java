package com.example.utils;

import com.example.controllers.MapEditorController;
import javafx.animation.ScaleTransition;
import javafx.animation.Timeline;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelReader;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;

/**
 * Utility class for the map editor with reusable methods for UI interactions
 */
public class MapEditorUtils {

    // Button styling constants
    public static final String BUTTON_NORMAL_STYLE = "-fx-background-color: linear-gradient(#6b4c2e, #4e331f); " +
                                                     "-fx-text-fill: #e8d9b5; -fx-font-family: 'Segoe UI'; " +
                                                     "-fx-font-size: 14px; -fx-font-weight: bold; " +
                                                     "-fx-border-color: #8a673c; -fx-border-width: 2; " +
                                                     "-fx-border-radius: 5; -fx-background-radius: 5;";
    
    public static final String BUTTON_HOVER_STYLE = "-fx-background-color: linear-gradient(#7d5a3c, #5d4228); " +
                                                    "-fx-text-fill: #f5ead9; -fx-font-family: 'Segoe UI'; " +
                                                    "-fx-font-size: 14px; -fx-font-weight: bold; " + 
                                                    "-fx-border-color: #a07748; -fx-border-width: 2; " +
                                                    "-fx-border-radius: 5; -fx-background-radius: 5; " +
                                                    "-fx-cursor: hand;";
    
    public static final String BUTTON_PRESSED_STYLE = "-fx-background-color: linear-gradient(#422c17, #6b4c2e); " +
                                                      "-fx-text-fill: #d9c9a0; -fx-font-family: 'Segoe UI'; " +
                                                      "-fx-font-size: 14px; -fx-font-weight: bold; " +
                                                      "-fx-border-color: #8a673c; -fx-border-width: 2; " +
                                                      "-fx-border-radius: 5; -fx-background-radius: 5;";
    
    public static final String OK_BUTTON_NORMAL_STYLE = "-fx-background-color: linear-gradient(#447240, #2e5a23); " +
                                                        "-fx-text-fill: #e8f4d9; -fx-font-family: 'Segoe UI'; " +
                                                        "-fx-font-size: 14px; -fx-font-weight: bold; " +
                                                        "-fx-border-color: #5d7542; -fx-border-width: 2; " +
                                                        "-fx-border-radius: 5; -fx-background-radius: 5;";
    
    public static final String OK_BUTTON_HOVER_STYLE = "-fx-background-color: linear-gradient(#548e4f, #3b6e2c); " +
                                                       "-fx-text-fill: #f5ffe9; -fx-font-family: 'Segoe UI'; " +
                                                       "-fx-font-size: 14px; -fx-font-weight: bold; " + 
                                                       "-fx-border-color: #6a894d; -fx-border-width: 2; " +
                                                       "-fx-border-radius: 5; -fx-background-radius: 5; " +
                                                       "-fx-cursor: hand;";
    
    public static final String OK_BUTTON_PRESSED_STYLE = "-fx-background-color: linear-gradient(#2e5a23, #447240); " +
                                                         "-fx-text-fill: #d9f0c0; -fx-font-family: 'Segoe UI'; " +
                                                         "-fx-font-size: 14px; -fx-font-weight: bold; " +
                                                         "-fx-border-color: #5d7542; -fx-border-width: 2; " +
                                                         "-fx-border-radius: 5; -fx-background-radius: 5;";
    
    // Title bar constants
    public static final String TITLE_BAR_STYLE = "-fx-background-color: #5d4228; -fx-border-color: #8a673c; -fx-border-width: 0 0 1 0;";
    public static final String BUTTON_TRANSPARENT_STYLE = "-fx-background-color: transparent; -fx-text-fill: #e8d9b5; -fx-font-weight: bold;";
    public static final String CLOSE_BUTTON_HOVER = "-fx-background-color: #a05454; -fx-text-fill: #f5ead9;";

    // Track dialog confirmation status
    public static boolean dialogConfirmed = false;

    // For dialog suppression
    public static boolean suppressDialogs = false;

    /**
     * Shows a wood-styled info alert with custom title bar
     */
    public static void showInfoAlert(String title, String content, MapEditorController controller) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        
        // Apply custom styling
        DialogPane dialogPane = alert.getDialogPane();
        dialogPane.setStyle("-fx-background-color: #5d4228;");
        dialogPane.lookup(".content.label").setStyle("-fx-text-fill: #e8d9b5;");
        
        // Apply custom cursor to the entire dialog
        StyleManager.applyCustomCursorToDialog(dialogPane);
        
        // Show the dialog
        alert.showAndWait();
    }

    public static void showErrorAlert(String title, String header, String content, MapEditorController controller) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        
        // Apply custom styling
        DialogPane dialogPane = alert.getDialogPane();
        dialogPane.setStyle("-fx-background-color: #5d4228;");
        dialogPane.lookup(".content.label").setStyle("-fx-text-fill: #e8d9b5;");
        dialogPane.lookup(".header-panel").setStyle("-fx-background-color: #4e331f;");
        dialogPane.lookup(".header-panel .label").setStyle("-fx-text-fill: #e8d9b5;");
        
        // Apply custom cursor to the entire dialog
        StyleManager.applyCustomCursorToDialog(dialogPane);
        
        // Show the dialog
        alert.showAndWait();
    }

    /**
     * Shows a fully custom styled confirmation dialog with our custom title bar
     */
    public static boolean showCustomConfirmDialog(String title, String message, MapEditorController controller) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        
        // Apply custom styling
        DialogPane dialogPane = alert.getDialogPane();
        dialogPane.setStyle("-fx-background-color: #5d4228;");
        dialogPane.lookup(".content.label").setStyle("-fx-text-fill: #e8d9b5;");
        
        // Style the buttons
        alert.getButtonTypes().forEach(buttonType -> {
            Button button = (Button) dialogPane.lookupButton(buttonType);
            StyleManager.setupButtonWithCustomCursor(button);
        });
        
        // Apply custom cursor to the entire dialog
        StyleManager.applyCustomCursorToDialog(dialogPane);
        
        // Show the dialog and return the result
        return alert.showAndWait().filter(t -> t == ButtonType.OK).isPresent();
    }

    /**
     * Creates a custom title bar for dialogs
     */
    public static HBox createTitleBar(Stage stage, String title) {
        // Create the title bar
        HBox titleBar = new HBox();
        titleBar.setAlignment(Pos.CENTER_RIGHT);
        titleBar.setPrefHeight(25);
        titleBar.setStyle(TITLE_BAR_STYLE);
        titleBar.setPadding(new Insets(0, 5, 0, 10));
        
        // Title text on the left
        Label titleLabel = new Label(title);
        titleLabel.setTextFill(Color.web("#e8d9b5"));
        titleLabel.setFont(Font.font("Segoe UI", FontWeight.BOLD, 14));
        HBox.setHgrow(titleLabel, Priority.ALWAYS);
        titleLabel.setAlignment(Pos.CENTER_LEFT);
        
        // Close button on the right
        Button closeButton = new Button("×");
        closeButton.setStyle(BUTTON_TRANSPARENT_STYLE + "-fx-font-size: 16px;");
        closeButton.setOnAction(e -> stage.close());
        
        // Hover effect for close button
        closeButton.setOnMouseEntered(e -> closeButton.setStyle(CLOSE_BUTTON_HOVER + "-fx-font-size: 16px;"));
        closeButton.setOnMouseExited(e -> closeButton.setStyle(BUTTON_TRANSPARENT_STYLE + "-fx-font-size: 16px;"));
        
        // Add components to title bar
        titleBar.getChildren().addAll(titleLabel, closeButton);
        
        return titleBar;
    }
    
    /**
     * Makes a stage draggable by a node
     */
    public static void setupDraggableStage(HBox titleBar, Stage stage) {
        final double[] xOffset = {0};
        final double[] yOffset = {0};
        
        titleBar.setOnMousePressed(event -> {
            xOffset[0] = event.getSceneX();
            yOffset[0] = event.getSceneY();
        });
        
        titleBar.setOnMouseDragged(event -> {
            stage.setX(event.getScreenX() - xOffset[0]);
            stage.setY(event.getScreenY() - yOffset[0]);
        });
    }

    /**
     * Animates a button click with a visual feedback
     */
    public static void animateButtonClick(Button button, ImageView imageView, String pressedImagePath, Object controller) {
        // Store original image
        Image originalImage = imageView.getImage();

        // Load pressed state image
        Image pressedImage = new Image(MapEditorUtils.class.getResourceAsStream(pressedImagePath));
        imageView.setImage(pressedImage);

        // Create scale animation
        ScaleTransition st = new ScaleTransition(Duration.millis(100), button);
        st.setFromX(1.0);
        st.setFromY(1.0);
        st.setToX(0.9);
        st.setToY(0.9);
        st.setCycleCount(2);
        st.setAutoReverse(true);

        // Reset image after animation
        st.setOnFinished(e -> imageView.setImage(originalImage));

        st.play();
    }

    public static void setSuppressDialogs(boolean b) {
        suppressDialogs = b;
    }
}