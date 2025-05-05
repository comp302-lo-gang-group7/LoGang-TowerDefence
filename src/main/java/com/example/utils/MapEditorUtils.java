package com.example.utils;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelReader;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

import java.util.Optional;

/**
 * Utility class with helper methods for the Map Editor
 */
public class MapEditorUtils {

    /**
     * Shows a custom-styled game-themed dialog instead of the standard alert
     */
    public static void showInfoAlert(String title, String header, String content, Object caller) {
        // Create a custom dialog instead of the standard alert
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle(title);
        
        // Remove the header area completely
        dialog.setHeaderText(null);
        
        // Create a custom dialog pane with game-themed styling
        DialogPane dialogPane = dialog.getDialogPane();
        
        // Create a VBox for the content with proper padding and alignment
        VBox contentBox = new VBox(15);
        contentBox.setAlignment(Pos.CENTER);
        contentBox.setPadding(new Insets(20, 20, 10, 20));
        
        // Add a decorative ribbon image - select based on action type
        ImageView ribbonIcon = null;
        String ribbonPath = "/com/example/assets/ui/Ribbon_Blue_3Slides.png"; // Default blue
        
        // Change ribbon color based on the dialog title/action
        if (title.contains("Delete") || title.contains("Clear")) {
            ribbonPath = "/com/example/assets/ui/Ribbon_Red_3Slides.png";
        } else if (title.contains("Save")) {
            ribbonPath = "/com/example/assets/ui/Ribbon_Yellow_3Slides.png";
        }
        
        try {
            Image iconImage = new Image(caller.getClass().getResourceAsStream(ribbonPath));
            ribbonIcon = new ImageView(iconImage);
            ribbonIcon.setFitWidth(200);  // Make it wider to match the ribbon style
            ribbonIcon.setFitHeight(40);  // Keep a reasonable height
            ribbonIcon.setPreserveRatio(true);
        } catch (Exception e) {
            System.err.println("Could not load ribbon image: " + e.getMessage());
        }
        
        // Add header text with custom styling - adjust color based on action type
        Label headerLabel = new Label(header);
        String textColor = "#1565C0"; // Default blue
        
        if (title.contains("Delete") || title.contains("Clear")) {
            textColor = "#C62828"; // Red for destructive actions
        } else if (title.contains("Save")) {
            textColor = "#F57F17"; // Dark yellow for save actions
        }
        
        headerLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: " + textColor + ";");
        headerLabel.setAlignment(Pos.CENTER);
        headerLabel.setWrapText(true);
        
        // Add content text with custom styling
        Label contentLabel = new Label(content);
        contentLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #333333;");
        contentLabel.setWrapText(true);
        contentLabel.setAlignment(Pos.CENTER);
        
        // Add all elements to the content box
        if (ribbonIcon != null) {
            contentBox.getChildren().addAll(ribbonIcon, headerLabel, contentLabel);
        } else {
            contentBox.getChildren().addAll(headerLabel, contentLabel);
        }
        
        // Set the background to match the grass texture
        String backgroundStyle = "-fx-background-color: #9dc183;"; // Same green as the map
        dialogPane.setStyle(backgroundStyle);
        
        // Add a border to make it look like a game panel
        contentBox.setStyle("-fx-background-color: #e9f5e3; -fx-background-radius: 5; " +
                          "-fx-border-color: #5d7542; -fx-border-width: 3; -fx-border-radius: 5;");
        
        // Set the content
        dialogPane.setContent(contentBox);
        
        // Add OK button but style it to match the game
        ButtonType okButtonType = new ButtonType("OK", ButtonBar.ButtonData.OK_DONE);
        dialogPane.getButtonTypes().add(okButtonType);
        
        // Style the OK button to match the action type
        Button okButton = (Button) dialogPane.lookupButton(okButtonType);
        String buttonColor = "#4CAF50"; // Default green
        String hoverColor = "#66BB6A"; // Default hover green
        
        if (title.contains("Delete") || title.contains("Clear")) {
            buttonColor = "#d32f2f"; // Red button for destructive actions
            hoverColor = "#ef5350"; // Red hover
        } else if (title.contains("Save")) {
            buttonColor = "#FFA000"; // Yellow/orange for save
            hoverColor = "#FFB74D"; // Lighter yellow/orange for hover
        }
        
        okButton.setStyle("-fx-background-color: " + buttonColor + "; -fx-text-fill: white; -fx-font-weight: bold;");
        
        // Add hover effect
        final String finalButtonColor = buttonColor;
        final String finalHoverColor = hoverColor;
        
        okButton.setOnMouseEntered(e -> 
            okButton.setStyle("-fx-background-color: " + finalHoverColor + "; -fx-text-fill: white; -fx-font-weight: bold;")
        );
        okButton.setOnMouseExited(e -> 
            okButton.setStyle("-fx-background-color: " + finalButtonColor + "; -fx-text-fill: white; -fx-font-weight: bold;")
        );
        
        // Show the dialog and wait for user response
        dialog.showAndWait();
    }

    /**
     * Shows a custom-styled confirmation dialog that matches the game theme
     * 
     * @return true if user confirmed, false otherwise
     */
    public static boolean showCustomConfirmDialog(String title, String header, String content, Object caller) {
        // Create a custom dialog
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle(title);
        dialog.setHeaderText(null);
        
        DialogPane dialogPane = dialog.getDialogPane();
        
        // Create content layout
        VBox contentBox = new VBox(15);
        contentBox.setAlignment(Pos.CENTER);
        contentBox.setPadding(new Insets(20, 20, 10, 20));
        
        // For confirmation dialogs, use the ribbon color based on the action being confirmed
        ImageView ribbonIcon = null;
        String ribbonPath = "/com/example/assets/ui/Ribbon_Blue_3Slides.png"; // Default
        
        // For clear map action (which is the primary use case for confirmations)
        if (title.contains("Clear")) {
            ribbonPath = "/com/example/assets/ui/Ribbon_Red_3Slides.png";
        }
        
        try {
            Image iconImage = new Image(caller.getClass().getResourceAsStream(ribbonPath));
            ribbonIcon = new ImageView(iconImage);
            ribbonIcon.setFitWidth(200);  // Make it wider to match the ribbon style
            ribbonIcon.setFitHeight(40);  // Keep a reasonable height
            ribbonIcon.setPreserveRatio(true);
        } catch (Exception e) {
            System.err.println("Could not load ribbon image: " + e.getMessage());
        }
        
        // Add header text with custom styling (red for warning is appropriate for confirmation dialogs)
        Label headerLabel = new Label(header);
        headerLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #C62828;"); // Red for warning
        headerLabel.setAlignment(Pos.CENTER);
        headerLabel.setWrapText(true);
        
        // Add content text with custom styling
        Label contentLabel = new Label(content);
        contentLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #333333;");
        contentLabel.setWrapText(true);
        contentLabel.setAlignment(Pos.CENTER);
        
        // Add all elements to the content box
        if (ribbonIcon != null) {
            contentBox.getChildren().addAll(ribbonIcon, headerLabel, contentLabel);
        } else {
            contentBox.getChildren().addAll(headerLabel, contentLabel);
        }
        
        // Set the background to match the grass texture
        String backgroundStyle = "-fx-background-color: #9dc183;"; // Same green as the map
        dialogPane.setStyle(backgroundStyle);
        
        // Add a border to make it look like a game panel
        contentBox.setStyle("-fx-background-color: #e9f5e3; -fx-background-radius: 5; " +
                           "-fx-border-color: #5d7542; -fx-border-width: 3; -fx-border-radius: 5;");
        
        // Set the content
        dialogPane.setContent(contentBox);
        
        // Add OK and Cancel buttons
        ButtonType confirmButtonType = new ButtonType("OK", ButtonBar.ButtonData.OK_DONE);
        ButtonType cancelButtonType = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
        dialogPane.getButtonTypes().addAll(confirmButtonType, cancelButtonType);
        
        // Style the buttons - keep red for the confirm button in confirmation dialogs
        // as it represents a potentially destructive action
        Button confirmButton = (Button) dialogPane.lookupButton(confirmButtonType);
        confirmButton.setStyle("-fx-background-color: #d32f2f; -fx-text-fill: white; -fx-font-weight: bold;");
        
        Button cancelButton = (Button) dialogPane.lookupButton(cancelButtonType);
        cancelButton.setStyle("-fx-background-color: #757575; -fx-text-fill: white; -fx-font-weight: bold;");
        
        // Add hover effects
        confirmButton.setOnMouseEntered(e -> 
            confirmButton.setStyle("-fx-background-color: #ef5350; -fx-text-fill: white; -fx-font-weight: bold;")
        );
        confirmButton.setOnMouseExited(e -> 
            confirmButton.setStyle("-fx-background-color: #d32f2f; -fx-text-fill: white; -fx-font-weight: bold;")
        );
        
        cancelButton.setOnMouseEntered(e -> 
            cancelButton.setStyle("-fx-background-color: #9e9e9e; -fx-text-fill: white; -fx-font-weight: bold;")
        );
        cancelButton.setOnMouseExited(e -> 
            cancelButton.setStyle("-fx-background-color: #757575; -fx-text-fill: white; -fx-font-weight: bold;")
        );
        
        // Show the dialog and process the result
        Optional<ButtonType> result = dialog.showAndWait();
        return result.isPresent() && result.get() == confirmButtonType;
    }
    
    /**
     * Animates a button click with a pressed image
     */
    public static void animateButtonClick(Button button, ImageView imageView, String pressedImagePath, Object caller) {
        Image originalImage = imageView.getImage();
        
        // Set the pressed button image
        imageView.setImage(new Image(caller.getClass().getResourceAsStream(pressedImagePath)));
        
        // Reset after a short delay
        new Thread(() -> {
            try {
                Thread.sleep(200);
                javafx.application.Platform.runLater(() -> {
                    imageView.setImage(originalImage);
                });
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }
    
    /**
     * Creates a composite tile by blending a base image with an overlay
     * 
     * @param base Base image (usually grass)
     * @param overlay Overlay image to be placed on top
     * @param tileSize Size of the tile
     * @param whiteThreshold Threshold for white pixels to be considered transparent
     * @return Composited image
     */
    public static Image compositeTile(Image base, Image overlay, int tileSize, double whiteThreshold) {
        int width = tileSize;
        int height = tileSize;

        WritableImage result = new WritableImage(width, height);
        PixelReader baseReader = base.getPixelReader();
        PixelReader overlayReader = overlay.getPixelReader();
        PixelWriter writer = result.getPixelWriter();

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                Color overlayColor = overlayReader.getColor(x, y);
                // Ignore fully transparent or near-white pixels
                if (overlayColor.getOpacity() == 0 ||
                        (overlayColor.getRed() > whiteThreshold && overlayColor.getGreen() > whiteThreshold && overlayColor.getBlue() > whiteThreshold)) {
                    // Use base tile pixel
                    writer.setColor(x, y, baseReader.getColor(x, y));
                } else {
                    Color baseColor = baseReader.getColor(x, y);
                    double alpha = overlayColor.getOpacity();
                    Color blended = new Color(
                            overlayColor.getRed() * alpha + baseColor.getRed() * (1 - alpha),
                            overlayColor.getGreen() * alpha + baseColor.getGreen() * (1 - alpha),
                            overlayColor.getBlue() * alpha + baseColor.getBlue() * (1 - alpha),
                            1.0
                    );
                    writer.setColor(x, y, blended);
                }
            }
        }

        return result;
    }
}