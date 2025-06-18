package com.example.controllers;

import java.net.URL;
import java.util.ResourceBundle;

import com.example.main.Main;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

/**
 * Controller for managing a custom title bar in the application.
 * Provides functionality for window dragging, minimizing, and closing.
 */
public class CustomTitleBarController implements Initializable {
    
    /**
     * The title bar HBox used for dragging the window.
     */
    @FXML
    private HBox titleBar;
    
    /**
     * Button for minimizing the window.
     */
    @FXML
    private Button minimizeBtn;
    
    /**
     * Button for closing the window.
     */
    @FXML
    private Button closeBtn;
    
    /**
     * Initializes the controller and sets up the draggable window functionality.
     *
     * @param location  The location used to resolve relative paths for the root object, or null if unknown.
     * @param resources The resources used to localize the root object, or null if not applicable.
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setupDraggableWindow();
    }
    
    /**
     * Sets up the functionality to make the window draggable using the title bar.
     */
    private void setupDraggableWindow() {
        titleBar.setOnMousePressed(event -> {
            Main.setXOffset(event.getSceneX());
            Main.setYOffset(event.getSceneY());
        });
        
        titleBar.setOnMouseDragged(event -> {
            Stage stage = (Stage) titleBar.getScene().getWindow();
            stage.setX(event.getScreenX() - Main.getXOffset());
            stage.setY(event.getScreenY() - Main.getYOffset());
        });
        
        titleBar.getChildren().filtered(node -> node instanceof Button).forEach(button -> {
            button.setOnMouseEntered(e -> {
                if (button == minimizeBtn) {
                    button.setStyle("-fx-background-color: #756446; -fx-text-fill: #f5ead9; -fx-font-weight: bold; " +
                                   "-fx-padding: 0 8 5 8; -fx-background-radius: 0;");
                } else {
                    button.setStyle("-fx-background-color: #a05454; -fx-text-fill: #f5ead9; -fx-font-weight: bold; " +
                                   "-fx-font-size: 16px; -fx-padding: 0 8 0 8; -fx-background-radius: 0;");
                }
            });
            
            button.setOnMouseExited(e -> {
                if (button == minimizeBtn) {
                    button.setStyle("-fx-background-color: transparent; -fx-text-fill: #e8d9b5; -fx-font-weight: bold; " +
                                   "-fx-padding: 0 8 5 8; -fx-background-radius: 0;");
                } else {
                    button.setStyle("-fx-background-color: transparent; -fx-text-fill: #e8d9b5; -fx-font-weight: bold; " +
                                   "-fx-font-size: 16px; -fx-padding: 0 8 0 8; -fx-background-radius: 0;");
                }
            });
        });
    }
    
    /**
     * Minimizes the application window.
     */
    @FXML
    private void minimizeWindow() {
        Stage stage = (Stage) titleBar.getScene().getWindow();
        stage.setIconified(true);
    }
    
    /**
     * Closes the application window.
     */
    @FXML
    private void closeWindow() {
        Stage stage = (Stage) titleBar.getScene().getWindow();
        stage.close();
    }
}
