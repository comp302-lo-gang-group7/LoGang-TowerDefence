package com.example.ui;

import java.io.IOException;
import java.util.List;

import com.example.controllers.GameScreenController;

import javafx.fxml.FXMLLoader;
import javafx.scene.ImageCursor;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * The view manager is used to request screen changes. Whenever a screen is to be loaded into the scene, the view manager handles that process.
 */
public class ViewManager {
    private final Stage stage;
    private static Scene scene;

    public ViewManager(Stage stage) {
        this.stage = stage;

        try {
            // Load custom title bar
            FXMLLoader titleBarLoader = new FXMLLoader(getClass().getResource("/com/example/fxml/CustomTitleBar.fxml"));
            Parent titleBar = titleBarLoader.load();
            
            // Load initial content (home page)
            FXMLLoader contentLoader = new FXMLLoader(getClass().getResource("/com/example/fxml/home_page.fxml"));
            Parent content = contentLoader.load();
            
            // Create root container with title bar at top and content area
            VBox root = new VBox();
            root.getChildren().addAll(titleBar, content);
            
            // Create scene
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

    public void setCustomCursor(Image cursorImage) {
        if (scene != null && cursorImage != null) {
            ImageCursor customCursor = new ImageCursor(cursorImage, cursorImage.getWidth() / 2, cursorImage.getHeight() / 2);
            scene.setCursor(customCursor);
        }
    }

    public void switchTo(String fxmlPath) {
        try {
            // Load new fxml page
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent content = fxmlLoader.load();
            
            // Get the current root layout which contains the title bar
            VBox root = (VBox) scene.getRoot();
            
            // Keep the title bar and replace the content
            root.getChildren().set(1, content);
            
            // Reapply custom cursor after switching content
            if (scene.getCursor() instanceof ImageCursor) {
                scene.setCursor(scene.getCursor()); // Reapply the existing ImageCursor
            }
            
//            // If this is the GameScreen scene, inject mouse click event filter
//            if (fxmlLoader.getController() instanceof GameScreenController) {
//                GameScreenController gameScreenController = fxmlLoader.getController();
//                scene.addEventFilter(MouseEvent.MOUSE_CLICKED, gameScreenController.getOnMouseClickedFilter());
//            }
            
        } catch (IOException e) {
            System.out.printf("An IOException occurred during switch to FXML path %s, error: %s%n", fxmlPath, e);
            e.printStackTrace();
        }
    }

    public void switchToGameScreen(String mapName, int startingGold) {
        switchToGameScreen(mapName, startingGold, null);
    }

    public void switchToGameScreen(String mapName, int startingGold, List<int[]> waves) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/example/fxml/game_screen_page.fxml"));
            Parent content = fxmlLoader.load();

            ((GameScreenController) fxmlLoader.getController()).init(mapName, startingGold, waves);

            VBox root = (VBox) scene.getRoot();
            root.getChildren().set(1, content);

            // Reapply custom cursor after switching content
            if (scene.getCursor() instanceof ImageCursor) {
                scene.setCursor(scene.getCursor()); // Reapply the existing ImageCursor
            }

        } catch (IOException e) {
            System.out.printf("An IOException occurred during switch to FXML path com/example/fxml/game_screen_page.fxml, error: %s%n", e);
            e.printStackTrace();
        }
    }

    /**
     * This method allows different parts of the project to resize the window.
     * @param width
     * @param height
     */
    public void resizeWindow(int width, int height) {
        stage.setWidth(width);
        stage.setHeight(height + 25); // Updated from 30 to 25 for smaller title bar
    }

    public void terminateApplication() {
        stage.close();
    }

    public static Scene getScene() {
        return scene;
    }

    public javafx.scene.Cursor getCustomCursor() {
        return scene.getCursor();
    }

    public Stage getStage() {
        return stage;
    }

    public void resizeWindowDefault() {
        this.resizeWindow(640, 450);
    }
}
