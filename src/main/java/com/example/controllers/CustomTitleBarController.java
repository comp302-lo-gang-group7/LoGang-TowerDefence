package com.example.controllers;

import com.example.main.Main;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import javafx.scene.control.Button; // Added this import for Button
import java.net.URL;
import java.util.ResourceBundle;

public class CustomTitleBarController implements Initializable {
    
    @FXML
    private HBox titleBar;
    
    @FXML
    private Button minimizeBtn;
    
    @FXML
    private Button closeBtn;
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Make the window draggable by the title bar
        setupDraggableWindow();
    }
    
    private void setupDraggableWindow() {
        titleBar.setOnMousePressed(event -> {
            // Store initial mouse position
            Main.setXOffset(event.getSceneX());
            Main.setYOffset(event.getSceneY());
        });
        
        titleBar.setOnMouseDragged(event -> {
            // Get the stage from the title bar
            Stage stage = (Stage) titleBar.getScene().getWindow();
            
            // Move the window by the difference between current mouse position and initial position
            stage.setX(event.getScreenX() - Main.getXOffset());
            stage.setY(event.getScreenY() - Main.getYOffset());
        });
        
        // Add hover effects for buttons
        titleBar.getChildren().filtered(node -> node instanceof Button).forEach(button -> {
            button.setOnMouseEntered(e -> {
                if (button == minimizeBtn) {
                    button.setStyle("-fx-background-color: #756446; -fx-text-fill: #f5ead9; -fx-font-weight: bold; " +
                                   "-fx-padding: 0 8 5 8; -fx-background-radius: 0; -fx-cursor: hand;");
                } else {
                    button.setStyle("-fx-background-color: #a05454; -fx-text-fill: #f5ead9; -fx-font-weight: bold; " +
                                   "-fx-font-size: 16px; -fx-padding: 0 8 0 8; -fx-background-radius: 0; -fx-cursor: hand;");
                }
            });
            
            button.setOnMouseExited(e -> {
                if (button == minimizeBtn) {
                    button.setStyle("-fx-background-color: transparent; -fx-text-fill: #e8d9b5; -fx-font-weight: bold; " +
                                   "-fx-padding: 0 8 5 8; -fx-background-radius: 0; -fx-cursor: hand;");
                } else {
                    button.setStyle("-fx-background-color: transparent; -fx-text-fill: #e8d9b5; -fx-font-weight: bold; " +
                                   "-fx-font-size: 16px; -fx-padding: 0 8 0 8; -fx-background-radius: 0; -fx-cursor: hand;");
                }
            });
        });
    }
    
    @FXML
    private void minimizeWindow() {
        Stage stage = (Stage) titleBar.getScene().getWindow();
        stage.setIconified(true);
    }
    
    @FXML
    private void closeWindow() {
        Stage stage = (Stage) titleBar.getScene().getWindow();
        stage.close();
    }
}