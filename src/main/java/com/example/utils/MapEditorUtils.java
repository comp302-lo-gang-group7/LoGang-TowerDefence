package com.example.utils;

import javafx.animation.ScaleTransition;
import javafx.animation.Timeline;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
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
    private static final String BUTTON_NORMAL_STYLE = "-fx-background-color: linear-gradient(#6b4c2e, #4e331f); " +
                                                     "-fx-text-fill: #e8d9b5; -fx-font-family: 'Segoe UI'; " +
                                                     "-fx-font-size: 14px; -fx-font-weight: bold; " +
                                                     "-fx-border-color: #8a673c; -fx-border-width: 2; " +
                                                     "-fx-border-radius: 5; -fx-background-radius: 5;";
    
    private static final String BUTTON_HOVER_STYLE = "-fx-background-color: linear-gradient(#7d5a3c, #5d4228); " +
                                                    "-fx-text-fill: #f5ead9; -fx-font-family: 'Segoe UI'; " +
                                                    "-fx-font-size: 14px; -fx-font-weight: bold; " + 
                                                    "-fx-border-color: #a07748; -fx-border-width: 2; " +
                                                    "-fx-border-radius: 5; -fx-background-radius: 5; " +
                                                    "-fx-cursor: hand;";
    
    private static final String BUTTON_PRESSED_STYLE = "-fx-background-color: linear-gradient(#422c17, #6b4c2e); " +
                                                      "-fx-text-fill: #d9c9a0; -fx-font-family: 'Segoe UI'; " +
                                                      "-fx-font-size: 14px; -fx-font-weight: bold; " +
                                                      "-fx-border-color: #8a673c; -fx-border-width: 2; " +
                                                      "-fx-border-radius: 5; -fx-background-radius: 5;";
    
    // Green OK button styling
    private static final String OK_BUTTON_NORMAL_STYLE = "-fx-background-color: linear-gradient(#447240, #2e5a23); " +
                                                        "-fx-text-fill: #e8f4d9; -fx-font-family: 'Segoe UI'; " +
                                                        "-fx-font-size: 14px; -fx-font-weight: bold; " +
                                                        "-fx-border-color: #5d7542; -fx-border-width: 2; " +
                                                        "-fx-border-radius: 5; -fx-background-radius: 5;";
    
    private static final String OK_BUTTON_HOVER_STYLE = "-fx-background-color: linear-gradient(#548e4f, #3b6e2c); " +
                                                       "-fx-text-fill: #f5ffe9; -fx-font-family: 'Segoe UI'; " +
                                                       "-fx-font-size: 14px; -fx-font-weight: bold; " + 
                                                       "-fx-border-color: #6a894d; -fx-border-width: 2; " +
                                                       "-fx-border-radius: 5; -fx-background-radius: 5; " +
                                                       "-fx-cursor: hand;";
    
    private static final String OK_BUTTON_PRESSED_STYLE = "-fx-background-color: linear-gradient(#2e5a23, #447240); " +
                                                         "-fx-text-fill: #d9f0c0; -fx-font-family: 'Segoe UI'; " +
                                                         "-fx-font-size: 14px; -fx-font-weight: bold; " +
                                                         "-fx-border-color: #5d7542; -fx-border-width: 2; " +
                                                         "-fx-border-radius: 5; -fx-background-radius: 5;";
    
    // Title bar constants
    private static final String TITLE_BAR_STYLE = "-fx-background-color: #5d4228; -fx-border-color: #8a673c; -fx-border-width: 0 0 1 0;";
    private static final String BUTTON_TRANSPARENT_STYLE = "-fx-background-color: transparent; -fx-text-fill: #e8d9b5; -fx-font-weight: bold;";
    private static final String CLOSE_BUTTON_HOVER = "-fx-background-color: #a05454; -fx-text-fill: #f5ead9;";

    // Track dialog confirmation status
    private static boolean dialogConfirmed = false;

    /**
     * Shows a wood-styled info alert with custom title bar
     */
    public static void showInfoAlert(String title, String content, Object controller) {
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
        
        // Center on parent
        dialogStage.centerOnScreen();
        
        // Make the dialog draggable by the title bar
        setupDraggableStage(titleBar, dialogStage);
        
        // Show dialog and wait for it to close
        dialogStage.showAndWait();
    }

    public static void showErrorAlert(String title, String header, String content, Object caller) {
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

        okButton.setOnMouseEntered(e ->
                okButton.setStyle("-fx-background-color: #ef5350; -fx-text-fill: white; -fx-font-weight: bold;")
        );
        okButton.setOnMouseExited(e ->
                okButton.setStyle("-fx-background-color: #c62828; -fx-text-fill: white; -fx-font-weight: bold;")
        );

        dialog.showAndWait();
    }


    /**
     * Shows a fully custom styled confirmation dialog with our custom title bar
     */
    public static boolean showCustomConfirmDialog(String title, String content, Object controller) {
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
        buttonBox.setStyle("-fx-background-color: #5d4228;"); // Ensure consistent background
        
        // Create OK button (now green)
        Button okButton = new Button("OK");
        okButton.setPrefWidth(100);
        okButton.setPrefHeight(30);
        okButton.setStyle(OK_BUTTON_NORMAL_STYLE);
        
        // OK button hover effect
        okButton.setOnMouseEntered(e -> okButton.setStyle(OK_BUTTON_HOVER_STYLE));
        okButton.setOnMouseExited(e -> okButton.setStyle(OK_BUTTON_NORMAL_STYLE));
        
        // Add pressed style for feedback
        okButton.setOnMousePressed(e -> okButton.setStyle(OK_BUTTON_PRESSED_STYLE));
        okButton.setOnMouseReleased(e -> okButton.setStyle(OK_BUTTON_HOVER_STYLE));
        
        // OK button click action
        okButton.setOnAction(e -> {
            dialogConfirmed = true;
            dialogStage.close();
        });
        
        // Create Cancel button (standard wood color)
        Button cancelButton = new Button("Cancel");
        cancelButton.setPrefWidth(100);
        cancelButton.setPrefHeight(30);
        cancelButton.setStyle(BUTTON_NORMAL_STYLE);
        
        // Cancel button hover effect
        cancelButton.setOnMouseEntered(e -> cancelButton.setStyle(BUTTON_HOVER_STYLE));
        cancelButton.setOnMouseExited(e -> cancelButton.setStyle(BUTTON_NORMAL_STYLE));
        
        // Add pressed style for feedback
        cancelButton.setOnMousePressed(e -> cancelButton.setStyle(BUTTON_PRESSED_STYLE));
        cancelButton.setOnMouseReleased(e -> cancelButton.setStyle(BUTTON_HOVER_STYLE));
        
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
        
        // Set up the scene with proper size to prevent buttons from being cut off
        Scene dialogScene = new Scene(root, 400, 220);
        // Ensure scene background is properly colored
        dialogScene.setFill(Color.web("#5d4228"));
        dialogStage.setScene(dialogScene);
        
        // Center on parent
        dialogStage.centerOnScreen();
        
        // Make the dialog draggable by the title bar
        setupDraggableStage(titleBar, dialogStage);
        
        // Show dialog and wait for it to close
        dialogStage.showAndWait();
        
        // Return result
        return dialogConfirmed;
    }

    /**
     * Creates a custom title bar for dialogs
     */
    private static HBox createTitleBar(Stage stage, String title) {
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
    private static void setupDraggableStage(HBox titleBar, Stage stage) {
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

    /**
     * Creates a composite tile image from a base image and an overlay
     */
    public static Image compositeTile(Image base, Image overlay, int tileSize, double whiteThreshold) {
        WritableImage result = new WritableImage(tileSize, tileSize);
        
        PixelReader baseReader = base.getPixelReader();
        PixelReader overlayReader = overlay.getPixelReader();
        PixelWriter resultWriter = result.getPixelWriter();
        
        for (int y = 0; y < tileSize; y++) {
            for (int x = 0; x < tileSize; x++) {
                Color overlayColor = overlayReader.getColor(x, y);
                
                // If the overlay pixel is close to white, use the base color
                if (isCloseToWhite(overlayColor, whiteThreshold)) {
                    resultWriter.setColor(x, y, baseReader.getColor(x, y));
                } else {
                    resultWriter.setColor(x, y, overlayColor);
                }
            }
        }
        
        return result;
    }
    
    private static boolean isCloseToWhite(Color color, double threshold) {
        return color.getRed() > threshold && color.getGreen() > threshold && color.getBlue() > threshold;
    }
}