package com.example.ui;

import java.io.IOException;
import java.util.List;

import com.example.controllers.GameScreenController;
import com.example.config.LevelConfig;

import javafx.fxml.FXMLLoader;
import javafx.scene.ImageCursor;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * Manages the application's views and handles screen transitions, custom cursor settings, and window resizing.
 */
public class ViewManager {
    private final Stage stage;
    private static Scene scene;

    /**
     * Constructs a ViewManager instance and initializes the stage with the default scene.
     *
     * @param stage The primary stage of the application.
     */
    public ViewManager(Stage stage) {
        this.stage = stage;

        try {
            FXMLLoader titleBarLoader = new FXMLLoader(getClass().getResource("/com/example/fxml/CustomTitleBar.fxml"));
            Parent titleBar = titleBarLoader.load();

            FXMLLoader contentLoader = new FXMLLoader(getClass().getResource("/com/example/fxml/home_page.fxml"));
            Parent content = contentLoader.load();

            VBox root = new VBox();
            root.getChildren().addAll(titleBar, content);

            this.scene = new Scene(root);
            stage.setScene(scene);

        } catch (IOException e) {
            System.out.printf("An IOException occurred during ViewManager initialization, error: %s%n", e);
            e.printStackTrace();
        } catch (Exception unexpectedError) {
            System.out.printf("An unexpected error occurred in ViewManager constructor, error: %s%n", unexpectedError);
            unexpectedError.printStackTrace();
        }
    }

    /**
     * Sets a custom cursor for the application scene.
     *
     * @param cursorImage The image to be used as the custom cursor.
     */
    public void setCustomCursor(Image cursorImage) {
        if (scene != null && cursorImage != null) {
            ImageCursor customCursor = new ImageCursor(cursorImage, cursorImage.getWidth() / 2, cursorImage.getHeight() / 2);
            scene.setCursor(customCursor);
        }
    }

    /**
     * Switches the current view to the specified FXML file.
     *
     * @param fxmlPath The path to the FXML file to load.
     */
    public void switchTo(String fxmlPath) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent content = fxmlLoader.load();

            VBox root = (VBox) scene.getRoot();
            root.getChildren().set(1, content);

            if (scene.getCursor() instanceof ImageCursor) {
                scene.setCursor(scene.getCursor());
            }

        } catch (IOException e) {
            System.out.printf("An IOException occurred during switch to FXML path %s, error: %s%n", fxmlPath, e);
            e.printStackTrace();
        }
    }

    /**
     * Switches to the game screen with the specified map name and starting gold.
     *
     * @param mapName      The name of the map to load.
     * @param startingGold The initial amount of gold for the game.
     */
    public void switchToGameScreen(String mapName, int startingGold) {
        switchToGameScreen(mapName, startingGold, null);
    }

    /**
     * Switches to the game screen with the specified map name, starting gold, and wave configurations.
     *
     * @param mapName      The name of the map to load.
     * @param startingGold The initial amount of gold for the game.
     * @param waves        The wave configurations for the game.
     */
    public void switchToGameScreen(String mapName, int startingGold, List<int[]> waves) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/example/fxml/game_screen_page.fxml"));
            Parent content = fxmlLoader.load();

            ((GameScreenController) fxmlLoader.getController()).init(mapName, startingGold, waves);

            VBox root = (VBox) scene.getRoot();
            root.getChildren().set(1, content);

            if (scene.getCursor() instanceof ImageCursor) {
                scene.setCursor(scene.getCursor());
            }

        } catch (IOException e) {
            System.out.printf("An IOException occurred during switch to FXML path com/example/fxml/game_screen_page.fxml, error: %s%n", e);
            e.printStackTrace();
        }
    }

    /**
     * Switches to the game screen with the specified level configuration.
     *
     * @param config The configuration for the level.
     */
    public void switchToGameScreen(LevelConfig config) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/example/fxml/game_screen_page.fxml"));
            Parent content = fxmlLoader.load();

            ((GameScreenController) fxmlLoader.getController()).init(config);

            VBox root = (VBox) scene.getRoot();
            root.getChildren().set(1, content);

            if (scene.getCursor() instanceof ImageCursor) {
                scene.setCursor(scene.getCursor());
            }

        } catch (IOException e) {
            System.out.printf("An IOException occurred during switch to FXML path com/example/fxml/game_screen_page.fxml, error: %s%n", e);
            e.printStackTrace();
        }
    }

    /**
     * Switches to the game screen with the specified level configuration and level ID.
     *
     * @param config  The configuration for the level.
     * @param levelId The ID of the level to load.
     */
    public void switchToGameScreen(LevelConfig config, String levelId) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/example/fxml/game_screen_page.fxml"));
            Parent content = fxmlLoader.load();

            ((GameScreenController) fxmlLoader.getController()).init(config, levelId);

            VBox root = (VBox) scene.getRoot();
            root.getChildren().set(1, content);

            if (scene.getCursor() instanceof ImageCursor) {
                scene.setCursor(scene.getCursor());
            }

        } catch (IOException e) {
            System.out.printf("An IOException occurred during switch to FXML path com/example/fxml/game_screen_page.fxml, error: %s%n", e);
            e.printStackTrace();
        }
    }

    /**
     * Resizes the application window to the specified width and height.
     *
     * @param width  The new width of the window.
     * @param height The new height of the window.
     */
    public void resizeWindow(int width, int height) {
        stage.setWidth(width);
        stage.setHeight(height + 25);
    }

    /**
     * Terminates the application by closing the stage.
     */
    public void terminateApplication() {
        stage.close();
    }

    /**
     * Retrieves the current scene of the application.
     *
     * @return The current scene.
     */
    public static Scene getScene() {
        return scene;
    }

    /**
     * Retrieves the custom cursor currently set for the scene.
     *
     * @return The custom cursor of the scene.
     */
    public javafx.scene.Cursor getCustomCursor() {
        return scene.getCursor();
    }

    /**
     * Retrieves the primary stage of the application.
     *
     * @return The primary stage.
     */
    public Stage getStage() {
        return stage;
    }

    /**
     * Resizes the application window to the default dimensions.
     */
    public void resizeWindowDefault() {
        this.resizeWindow(800, 600);
    }
}
