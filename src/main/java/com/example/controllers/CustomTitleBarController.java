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
 * Class CustomTitleBarController
 */
public class CustomTitleBarController implements Initializable {

    @FXML
    private HBox titleBar;

    @FXML
    private Button minimizeBtn;

    @FXML
    private Button closeBtn;

    @Override
    /**
     * TODO
     */
    public void initialize(URL location, ResourceBundle resources) {

        setupDraggableWindow();
    }

    /**
     * TODO
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

    @FXML
    /**
     * TODO
     */
    private void minimizeWindow() {
        Stage stage = (Stage) titleBar.getScene().getWindow();
        stage.setIconified(true);
    }

    @FXML
    /**
     * TODO
     */
    private void closeWindow() {
        Stage stage = (Stage) titleBar.getScene().getWindow();
        stage.close();
    }
}