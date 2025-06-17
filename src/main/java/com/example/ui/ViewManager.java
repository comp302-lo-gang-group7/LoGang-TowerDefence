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
 * Class ViewManager
 */
public class ViewManager {
    private final Stage stage;
    private static Scene scene;

    /**
     * TODO
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
     * TODO
     */
    public void setCustomCursor(Image cursorImage) {
        if (scene != null && cursorImage != null) {
            ImageCursor customCursor = new ImageCursor(cursorImage, cursorImage.getWidth() / 2, cursorImage.getHeight() / 2);
            scene.setCursor(customCursor);
        }
    }

    /**
     * TODO
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
     * TODO
     */
    public void switchToGameScreen(String mapName, int startingGold) {
        switchToGameScreen(mapName, startingGold, null);
    }

    /**
     * TODO
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
     * TODO
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
     * TODO
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
     * TODO
     */
    public void resizeWindow(int width, int height) {
        stage.setWidth(width);
        stage.setHeight(height + 25);
    }

    /**
     * TODO
     */
    public void terminateApplication() {
        stage.close();
    }

    /**
     * TODO
     */
    public static Scene getScene() {
        return scene;
    }

    /**
     * TODO
     */
    public javafx.scene.Cursor getCustomCursor() {
        return scene.getCursor();
    }

    /**
     * TODO
     */
    public Stage getStage() {
        return stage;
    }

    /**
     * TODO
     */
    public void resizeWindowDefault() {
        this.resizeWindow(800, 600);
    }
}
