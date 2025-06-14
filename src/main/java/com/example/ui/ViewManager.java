package com.example.ui;

import java.io.IOException;
import java.util.List;

import com.example.controllers.GameScreenController;

import javafx.fxml.FXMLLoader;
import javafx.scene.ImageCursor;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * The view manager is used to request screen changes. Whenever a screen is to be loaded into the scene, the view manager handles that process.
 */
public class ViewManager {
    private final Stage primaryStage;
    private static Scene scene;
    private ImageCursor customCursor;
    private javafx.event.EventHandler<MouseEvent> mouseMovedEventHandler;

    public ViewManager(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }

    public void showScene(String fxmlPath, int width, int height) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(fxmlPath));
            scene = new Scene(fxmlLoader.load(), width, height);
            primaryStage.setScene(scene);
            applyCustomCursorToCurrentScene();
        } catch (IOException e) {
            System.out.printf("An IOException occurred during ViewManager initialization, error: %s%n", e);
        }
    }

    public void setScene(String fxmlPath) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(fxmlPath));
            scene.setRoot(fxmlLoader.load());
            resizeWindow(scene.getWidth(), scene.getHeight());
            applyCustomCursorToCurrentScene();
        } catch (IOException e) {
            System.err.println("Failed to load FXML: " + fxmlPath + " error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void setCustomCursor(ImageCursor cursor) {
        this.customCursor = cursor;
        applyCustomCursorToCurrentScene();
    }

    private void applyCustomCursorToCurrentScene() {
        if (customCursor != null && primaryStage.getScene() != null) {
            Scene currentScene = primaryStage.getScene();
            currentScene.setCursor(customCursor);

            if (currentScene.getRoot() != null) {
                // Remove existing event filter if present to prevent multiple attachments
                if (mouseMovedEventHandler != null) {
                    currentScene.getRoot().removeEventFilter(MouseEvent.MOUSE_MOVED, mouseMovedEventHandler);
                }

                // Create and add new event filter
                mouseMovedEventHandler = event -> {
                    if (!currentScene.getCursor().equals(customCursor)) {
                        currentScene.setCursor(customCursor);
                    }
                };
                currentScene.getRoot().addEventFilter(MouseEvent.MOUSE_MOVED, mouseMovedEventHandler);
            }
        }
    }

    public ImageCursor getCustomCursor() {
        return customCursor;
    }

    public Scene getScene() {
        return primaryStage.getScene();
    }

    public void resizeWindow(double width, double height) {
        primaryStage.setWidth(width);
        primaryStage.setHeight(height);
        primaryStage.centerOnScreen();
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
    public void resizeWindowDefault() {
        this.resizeWindow(640, 450);
    }

    public void terminateApplication() {
        primaryStage.close();
    }
}

