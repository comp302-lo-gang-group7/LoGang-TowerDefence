package com.example.utils;

import com.example.main.Main;

import javafx.animation.ScaleTransition;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.ImageCursor;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Label;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
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
                                                    "-fx-border-radius: 5; -fx-background-radius: 5;";
    
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
                                                       "-fx-border-radius: 5; -fx-background-radius: 5; ";
    
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
    public static void showInfoAlert(String title, String content, Object controller) {
        if (suppressDialogs) return;

        // Create a new stage for our custom dialog
        Stage dialogStage = new Stage();
        dialogStage.initModality(Modality.APPLICATION_MODAL);
        dialogStage.initStyle(StageStyle.UNDECORATED);
        dialogStage.setTitle(title);
        
        // Create the custom title bar
        HBox titleBar = createTitleBar(dialogStage, title);
        
        // Create content area
        VBox contentArea = new VBox(10);
        contentArea.setAlignment(Pos.CENTER);
        contentArea.setPadding(new Insets(20, 20, 20, 20));
        contentArea.setStyle("-fx-background-color: #5d4228;");
        
        // Create content text
        Text contentText = new Text(content);
        contentText.setFont(Font.font("Segoe UI", 14));
        contentText.setFill(Color.web("#e8d9b5"));
        contentText.setWrappingWidth(350);
        
        // Create button area
        HBox buttonBox = new HBox();
        buttonBox.setAlignment(Pos.CENTER);
        buttonBox.setPadding(new Insets(20, 0, 10, 0));
        buttonBox.setStyle("-fx-background-color: #5d4228;"); // Ensure consistent background
        
        // Create OK button (now with green styling)
        Button okButton = new Button("OK");
        okButton.setPrefWidth(100);
        okButton.setPrefHeight(30);
        okButton.setStyle(OK_BUTTON_NORMAL_STYLE);
        
        // OK button hover effect
        okButton.setOnMouseEntered(e -> okButton.setStyle(OK_BUTTON_HOVER_STYLE));
        okButton.setOnMouseExited(e -> okButton.setStyle(OK_BUTTON_NORMAL_STYLE));
        
        // OK button click action
        okButton.setOnAction(e -> dialogStage.close());
        
        // Add button to button area
        buttonBox.getChildren().add(okButton);
        
        // Build the content area
        contentArea.getChildren().addAll(contentText, buttonBox);
        
        // Create main container with title bar and content
        VBox root = new VBox();
        root.getChildren().addAll(titleBar, contentArea);
        root.setStyle("-fx-background-color: #5d4228; -fx-border-color: #8a673c; -fx-border-width: 2;");
        
        // Apply drop shadow effect
        root.setEffect(new DropShadow(15, Color.rgb(0, 0, 0, 0.5)));
        
        // Set up the scene with proper size to prevent button from being cut off
        Scene dialogScene = new Scene(root, 400, 200);
        // Ensure scene background is properly colored
        dialogScene.setFill(Color.web("#5d4228"));
        dialogStage.setScene(dialogScene);
        
        // Set custom cursor for the dialog scene
        Image customCursorImage = new Image(MapEditorUtils.class.getResourceAsStream("/com/example/assets/ui/01.png"));
        ImageCursor customCursor = new ImageCursor(customCursorImage, customCursorImage.getWidth() / 2, customCursorImage.getHeight() / 2);
        dialogScene.setCursor(customCursor);
        root.setCursor(customCursor);
        
        // Center on parent
        dialogStage.centerOnScreen();
        
        // Make the dialog draggable by the title bar
        setupDraggableStage(titleBar, dialogStage);
        
        // Set custom cursor for the OK button
        okButton.setCursor(customCursor);
        
        // Set main scene cursor to custom cursor before showing dialog
        if (Main.getViewManager() != null && Main.getViewManager().getScene() != null && Main.getViewManager().getCustomCursor() != null) {
            Main.getViewManager().getScene().setCursor(Main.getViewManager().getCustomCursor());
        }

        // Show dialog and wait for it to close
        dialogStage.showAndWait();

        // Restore custom cursor to main scene
        if (Main.getViewManager() != null && Main.getViewManager().getScene() != null && Main.getViewManager().getCustomCursor() != null) {
            Main.getViewManager().getScene().setCursor(Main.getViewManager().getCustomCursor());
        }
    }

    public static void showErrorAlert(String title, String header, String content, Object caller) {
        if (suppressDialogs) return;

        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle(title);
        dialog.setHeaderText(null);

        DialogPane dialogPane = dialog.getDialogPane();

        VBox contentBox = new VBox(15);
        contentBox.setAlignment(Pos.CENTER);
        contentBox.setPadding(new Insets(20, 20, 10, 20));

        // Use red ribbon for errors
        ImageView ribbonIcon = null;
        String ribbonPath = "/com/example/assets/ui/Ribbon_Red_3Slides.png";

        try {
            Image iconImage = new Image(caller.getClass().getResourceAsStream(ribbonPath));
            ribbonIcon = new ImageView(iconImage);
            ribbonIcon.setFitWidth(200);
            ribbonIcon.setFitHeight(40);
            ribbonIcon.setPreserveRatio(true);
        } catch (Exception e) {
            System.err.println("Could not load ribbon image for error alert: " + e.getMessage());
        }

        Label headerLabel = new Label(header);
        headerLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #C62828;");
        headerLabel.setAlignment(Pos.CENTER);
        headerLabel.setWrapText(true);

        Label contentLabel = new Label(content);
        contentLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #333333;");
        contentLabel.setWrapText(true);
        contentLabel.setAlignment(Pos.CENTER);

        if (ribbonIcon != null) {
            contentBox.getChildren().addAll(ribbonIcon, headerLabel, contentLabel);
        } else {
            contentBox.getChildren().addAll(headerLabel, contentLabel);
        }

        dialogPane.setStyle("-fx-background-color: #f4dede;");

        contentBox.setStyle("-fx-background-color: #fbeaea; -fx-background-radius: 5; " +
                "-fx-border-color: #c62828; -fx-border-width: 3; -fx-border-radius: 5;");

        dialogPane.setContent(contentBox);

        ButtonType okButtonType = new ButtonType("OK", ButtonBar.ButtonData.OK_DONE);
        dialogPane.getButtonTypes().add(okButtonType);

        Button okButton = (Button) dialogPane.lookupButton(okButtonType);
        okButton.setStyle("-fx-background-color: #c62828; -fx-text-fill: white; -fx-font-weight: bold;");

        // Set custom cursor for the OK button
        Image customCursorImage = new Image(MapEditorUtils.class.getResourceAsStream("/com/example/assets/ui/01.png"));
        ImageCursor customCursor = new ImageCursor(customCursorImage, customCursorImage.getWidth() / 2, customCursorImage.getHeight() / 2);
        okButton.setCursor(customCursor);

        okButton.setOnMouseEntered(e ->
                okButton.setStyle("-fx-background-color: #ef5350; -fx-text-fill: white; -fx-font-weight: bold;")
        );
        okButton.setOnMouseExited(e ->
                okButton.setStyle("-fx-background-color: #c62828; -fx-text-fill: white; -fx-font-weight: bold;")
        );

        // Set custom cursor for the dialog pane and its scene
        dialog.getDialogPane().setCursor(customCursor);
        dialog.getDialogPane().getScene().setCursor(customCursor);

        // Set main scene cursor to custom cursor before showing dialog
        if (Main.getViewManager() != null && Main.getViewManager().getScene() != null && Main.getViewManager().getCustomCursor() != null) {
            Main.getViewManager().getScene().setCursor(Main.getViewManager().getCustomCursor());
        }

        dialog.showAndWait();

        // Restore custom cursor to main scene
        if (Main.getViewManager() != null && Main.getViewManager().getScene() != null && Main.getViewManager().getCustomCursor() != null) {
            Main.getViewManager().getScene().setCursor(Main.getViewManager().getCustomCursor());
        }
    }


    /**
     * Shows a fully custom styled confirmation dialog with our custom title bar
     */
    public static boolean showCustomConfirmDialog(String title, String content, Object controller) {
        if (suppressDialogs) return true;

        // Create a new stage for our custom dialog
        Stage dialogStage = new Stage();
        dialogStage.initModality(Modality.APPLICATION_MODAL);
        dialogStage.initStyle(StageStyle.UNDECORATED);
        dialogStage.setTitle(title);
        
        // Reset dialog result
        dialogConfirmed = false;
        
        // Create the custom title bar
        HBox titleBar = createTitleBar(dialogStage, title);
        
        // Create content area
        VBox contentArea = new VBox(10);
        contentArea.setAlignment(Pos.CENTER);
        contentArea.setPadding(new Insets(20, 20, 20, 20));
        contentArea.setStyle("-fx-background-color: #5d4228;");
        
        // Create content text
        Text contentText = new Text(content);
        contentText.setFont(Font.font("Segoe UI", 14));
        contentText.setFill(Color.web("#e8d9b5"));
        contentText.setWrappingWidth(350);
        
        // Create button area
        HBox buttonBox = new HBox(20);
        buttonBox.setAlignment(Pos.CENTER);
        buttonBox.setPadding(new Insets(20, 0, 10, 0));
        buttonBox.setStyle("-fx-background-color: #5d4228;");
        
        // Create OK button
        Button okButton = new Button("OK");
        okButton.setPrefWidth(100);
        okButton.setPrefHeight(30);
        okButton.setStyle(MapEditorUtils.OK_BUTTON_NORMAL_STYLE);
        
        // Set custom cursor for the OK button
        Image customCursorImage = new Image(MapEditorUtils.class.getResourceAsStream("/com/example/assets/ui/01.png"));
        ImageCursor customCursor = new ImageCursor(customCursorImage, customCursorImage.getWidth() / 2, customCursorImage.getHeight() / 2);
        okButton.setCursor(customCursor);

        // OK button hover effect
        okButton.setOnMouseEntered(e -> okButton.setStyle(MapEditorUtils.OK_BUTTON_HOVER_STYLE));
        okButton.setOnMouseExited(e -> okButton.setStyle(MapEditorUtils.OK_BUTTON_NORMAL_STYLE));
        
        // OK button click action
        okButton.setOnAction(e -> {
            dialogConfirmed = true;
            dialogStage.close();
        });
        
        // Create Cancel button
        Button cancelButton = new Button("Cancel");
        cancelButton.setPrefWidth(100);
        cancelButton.setPrefHeight(30);
        cancelButton.setStyle(MapEditorUtils.BUTTON_NORMAL_STYLE);

        // Set custom cursor for the Cancel button
        cancelButton.setCursor(customCursor);
        
        // Cancel button hover effect
        cancelButton.setOnMouseEntered(e -> cancelButton.setStyle(MapEditorUtils.BUTTON_HOVER_STYLE));
        cancelButton.setOnMouseExited(e -> cancelButton.setStyle(MapEditorUtils.BUTTON_NORMAL_STYLE));
        
        // Cancel button click action
        cancelButton.setOnAction(e -> {
            dialogConfirmed = false;
            dialogStage.close();
        });
        
        // Add buttons to button area
        buttonBox.getChildren().addAll(okButton, cancelButton);
        
        // Build the content area
        contentArea.getChildren().addAll(contentText, buttonBox);
        
        // Create main container with title bar and content
        VBox root = new VBox();
        root.getChildren().addAll(titleBar, contentArea);
        root.setStyle("-fx-background-color: #5d4228; -fx-border-color: #8a673c; -fx-border-width: 2;");
        
        // Apply drop shadow effect
        root.setEffect(new DropShadow(15, Color.rgb(0, 0, 0, 0.5)));
        
        // Set up the scene
        Scene dialogScene = new Scene(root, 400, 200);
        dialogScene.setFill(Color.web("#5d4228"));
        dialogStage.setScene(dialogScene);

        // Set custom cursor for the dialog scene
        dialogScene.setCursor(customCursor);
        root.setCursor(customCursor);
        
        // Center on parent
        dialogStage.centerOnScreen();
        
        // Make the dialog draggable by the title bar
        setupDraggableStage(titleBar, dialogStage);
        
        // Set main scene cursor to custom cursor before showing dialog
        if (Main.getViewManager() != null && Main.getViewManager().getScene() != null && Main.getViewManager().getCustomCursor() != null) {
            Main.getViewManager().getScene().setCursor(Main.getViewManager().getCustomCursor());
        }

        // Show dialog and wait for it to close
        dialogStage.showAndWait();
        
        // Restore custom cursor to main scene
        if (Main.getViewManager() != null && Main.getViewManager().getScene() != null && Main.getViewManager().getCustomCursor() != null) {
            Main.getViewManager().getScene().setCursor(Main.getViewManager().getCustomCursor());
        }

        return dialogConfirmed;
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
        // Animate button scale
        ScaleTransition st = new ScaleTransition(Duration.millis(100), button);
        st.setFromX(1.0);
        st.setFromY(1.0);
        st.setToX(0.95);
        st.setToY(0.95);
        st.setCycleCount(2);
        st.setAutoReverse(true);
        st.play();
    }

    public static void setSuppressDialogs(boolean b) {
        suppressDialogs = b;
    }
}