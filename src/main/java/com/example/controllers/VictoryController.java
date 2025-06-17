package com.example.controllers;

import com.example.game.GameManager;
import com.example.main.Main;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;

import java.net.URL;
import java.util.Objects;
import java.util.ResourceBundle;


/**
 * Class VictoryController
 */
public class VictoryController implements Initializable {
    @FXML private StackPane victoryRoot;
    @FXML private HBox starsBox;
    @FXML private Button exitBtn;


    /**
     * TODO
     */
    private static final String BUTTON_NORMAL_STYLE = "-fx-background-color: linear-gradient(#6b4c2e, #4e331f); " +
                                                     "-fx-text-fill: #e8d9b5; -fx-font-family: 'Segoe UI'; " +
                                                     "-fx-font-size: 14px; -fx-font-weight: bold; " +
                                                     "-fx-border-color: #8a673c; -fx-border-width: 2; " +
                                                     "-fx-border-radius: 5; -fx-background-radius: 5;";

    /**
     * TODO
     */
    private static final String BUTTON_HOVER_STYLE = "-fx-background-color: linear-gradient(#7d5a3c, #5d4228); " +
                                                    "-fx-text-fill: #f5ead9; -fx-font-family: 'Segoe UI'; " +
                                                    "-fx-font-size: 14px; -fx-font-weight: bold; " +
                                                    "-fx-border-color: #a07748; -fx-border-width: 2; " +
                                                    "-fx-border-radius: 5; -fx-background-radius: 5;";

    /**
     * TODO
     */
    private static final String BUTTON_PRESSED_STYLE = "-fx-background-color: linear-gradient(#422c17, #6b4c2e); " +
                                                      "-fx-text-fill: #d9c9a0; -fx-font-family: 'Segoe UI'; " +
                                                      "-fx-font-size: 14px; -fx-font-weight: bold; " +
                                                      "-fx-border-color: #8a673c; -fx-border-width: 2; " +
                                                      "-fx-border-radius: 5; -fx-background-radius: 5;";

    @Override
    /**
     * TODO
     */
    public void initialize(URL location, ResourceBundle resources) {

        setupButtonEffects(exitBtn);
    }

    /**
     * TODO
     */
    private void setupButtonEffects(Button button) {

        button.setStyle(BUTTON_NORMAL_STYLE);


        button.setOnMouseEntered(e -> {
            button.setStyle(BUTTON_HOVER_STYLE);
            button.setScaleX(1.05);
            button.setScaleY(1.05);
        });

        button.setOnMouseExited(e -> {
            button.setStyle(BUTTON_NORMAL_STYLE);
            button.setScaleX(1.0);
            button.setScaleY(1.0);
        });


        button.setOnMousePressed(e -> {
            button.setStyle(BUTTON_PRESSED_STYLE);
            animateButtonClick(button);
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
        });
    }

    /**
     * TODO
     */
    private void animateButtonClick(Button button) {
        button.setScaleX(0.95);
        button.setScaleY(0.95);
    }


    /**
     * TODO
     */
    public void init(int stars) {
        starsBox.getChildren().clear();
        Image starImg = new Image(Objects.requireNonNull(getClass().getResourceAsStream(
                "/com/example/assets/ui/star.png")));


        for (int i = 0; i < stars; i++) {
            final int starIndex = i;
            ImageView iv = new ImageView(starImg);
            iv.setFitWidth(64);
            iv.setFitHeight(64);


            iv.setScaleX(0.1);
            iv.setScaleY(0.1);

            starsBox.getChildren().add(iv);


            javafx.animation.Timeline timeline = new javafx.animation.Timeline(
                new javafx.animation.KeyFrame(
                    javafx.util.Duration.millis(300 + starIndex * 200),
                    new javafx.animation.KeyValue(iv.scaleXProperty(), 1.0, javafx.animation.Interpolator.EASE_OUT),
                    new javafx.animation.KeyValue(iv.scaleYProperty(), 1.0, javafx.animation.Interpolator.EASE_OUT)
                ),
                new javafx.animation.KeyFrame(
                    javafx.util.Duration.millis(500 + starIndex * 200),
                    new javafx.animation.KeyValue(iv.rotateProperty(), 360, javafx.animation.Interpolator.EASE_OUT)
                )
            );
            timeline.play();
        }
    }

    @FXML
    /**
     * TODO
     */
    public void handleExit(ActionEvent e) {
        GameManager.getInstance().stop();
        if (victoryRoot.getParent() instanceof Pane parent) {
            parent.getChildren().remove(victoryRoot);
        }
        Main.getViewManager().resizeWindowDefault();
        Main.getViewManager().switchTo("/com/example/fxml/home_page.fxml");
    }
}