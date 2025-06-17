package com.example.main;

import java.io.IOException;

import com.example.ui.ViewManager;


import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

/**
 * Class Main
 */
public class Main extends javafx.application.Application {
    public static ViewManager viewManager;
    private static double xOffset = 0;
    private static double yOffset = 0;

    @Override
    /**
     * TODO
     */
    public void start(Stage stage) throws IOException {

        stage.initStyle(StageStyle.UNDECORATED);


        stage.setResizable(false);


        stage.setWidth(800);
        stage.setHeight(620);

        viewManager = new ViewManager(stage);
        viewManager.switchTo("/com/example/fxml/home_page.fxml");


        Image customCursorImage = new Image(getClass().getResourceAsStream("/com/example/assets/ui/01.png"));
        viewManager.setCustomCursor(customCursorImage);

        stage.show();
    }

    /**
     * TODO
     */
    public static ViewManager getViewManager() {
        return viewManager;
    }

    /**
     * TODO
     */
    public static double getXOffset() {
        return xOffset;
    }

    /**
     * TODO
     */
    public static double getYOffset() {
        return yOffset;
    }

    /**
     * TODO
     */
    public static void setXOffset(double x) {
        xOffset = x;
    }

    /**
     * TODO
     */
    public static void setYOffset(double y) {
        yOffset = y;
    }

    /**
     * TODO
     */
    public static void main(String[] args) {
        launch();
    }
}