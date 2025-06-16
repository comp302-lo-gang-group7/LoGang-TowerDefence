package com.example.controllers;

import com.example.game.GameManager;
import com.example.main.Main;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;

import java.util.Objects;

/** Controller for the victory overlay that displays earned stars. */
public class VictoryController {
    @FXML private StackPane victoryRoot;
    @FXML private HBox starsBox;

    /** Populate the overlay with the given star count. */
    public void init(int stars) {
        starsBox.getChildren().clear();
        Image starImg = new Image(Objects.requireNonNull(getClass().getResourceAsStream(
                "/com/example/assets/buttons/Star_Button.png")));
        for (int i = 0; i < stars; i++) {
            ImageView iv = new ImageView(starImg);
            iv.setFitWidth(32);
            iv.setFitHeight(32);
            starsBox.getChildren().add(iv);
        }
    }

    @FXML
    public void handleExit(ActionEvent e) {
        GameManager.getInstance().stop();
        if (victoryRoot.getParent() instanceof Pane parent) {
            parent.getChildren().remove(victoryRoot);
        }
        Main.getViewManager().resizeWindowDefault();
        Main.getViewManager().switchTo("/com/example/fxml/home_page.fxml");
    }
}