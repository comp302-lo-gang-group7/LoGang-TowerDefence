package com.example.main;

import java.io.IOException;

import com.example.ui.ViewManager;

import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

/**
 * The Main class serves as the entry point for the JavaFX application.
 * It initializes the primary stage and manages the application's view.
 */
public class Main extends javafx.application.Application {
    /**
     * The ViewManager instance responsible for managing the application's views.
     */
    public static ViewManager viewManager;

    /**
     * The horizontal offset used for positioning elements.
     */
    private static double xOffset = 0;

    /**
     * The vertical offset used for positioning elements.
     */
    private static double yOffset = 0;

    /**
     * Starts the JavaFX application by initializing the primary stage and setting up the view manager.
     *
     * @param stage The primary stage for the application.
     * @throws IOException If an I/O error occurs during initialization.
     */
    @Override
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
     * Retrieves the ViewManager instance.
     *
     * @return The ViewManager instance.
     */
    public static ViewManager getViewManager() {
        return viewManager;
    }

    /**
     * Retrieves the horizontal offset value.
     *
     * @return The horizontal offset value.
     */
    public static double getXOffset() {
        return xOffset;
    }

    /**
     * Retrieves the vertical offset value.
     *
     * @return The vertical offset value.
     */
    public static double getYOffset() {
        return yOffset;
    }

    /**
     * Sets the horizontal offset value.
     *
     * @param x The new horizontal offset value.
     */
    public static void setXOffset(double x) {
        xOffset = x;
    }

    /**
     * Sets the vertical offset value.
     *
     * @param y The new vertical offset value.
     */
    public static void setYOffset(double y) {
        yOffset = y;
    }

    /**
     * The main method that launches the JavaFX application.
     *
     * @param args The command-line arguments passed to the application.
     */
    public static void main(String[] args) {
        launch();
    }
}