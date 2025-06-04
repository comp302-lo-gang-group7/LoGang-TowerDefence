package com.example.ui;

import com.example.controllers.GameScreenController;
import com.example.utils.StyleManager;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.scene.Node;

import java.io.IOException;

/**
 * The view manager is used to request screen changes. Whenever a screen is to be loaded into the scene, the view manager handles that process.
 */
public class ViewManager {
    private final Stage stage;
    private static Scene scene;
    private static Parent titleBar;

    public ViewManager(Stage stage) {
        this.stage = stage;

        try {
            // Load custom title bar
            FXMLLoader titleBarLoader = new FXMLLoader(getClass().getResource("/com/example/fxml/CustomTitleBar.fxml"));
            titleBar = titleBarLoader.load();
            
            // Load initial content (home page)
            FXMLLoader contentLoader = new FXMLLoader(getClass().getResource("/com/example/fxml/home_page.fxml"));
            Parent content = contentLoader.load();
            
            // Create root container with title bar at top and content area
            VBox root = new VBox();
            root.getChildren().addAll(titleBar, content);
            
            // Create scene
            scene = new Scene(root);

            // Set cursor using StyleManager
            scene.setCursor(StyleManager.getCustomCursor());
            applyCustomCursorToAll(root);
            
            stage.setScene(scene);
            stage.show();
            
        } catch (IOException e) {
            System.out.printf("An IOException occurred during ViewManager initialization, error: %s%n", e);
            e.printStackTrace();
        } catch (Exception unexpectedError) {
            System.out.printf("An unexpected error occurred in ViewManager constructor, error: %s%n", unexpectedError);
            unexpectedError.printStackTrace();
        }
    }

    private void applyCustomCursorToAll(Node node) {
        // Set cursor on the node itself
        node.setCursor(StyleManager.getCustomCursor());
        
        // If the node has children (is a Parent), recursively apply to all children
        if (node instanceof Parent) {
            for (Node child : ((Parent) node).getChildrenUnmodifiable()) {
                applyCustomCursorToAll(child);
            }
        }
    }

    public void switchTo(String fxmlPath) {
        try {
            // Load new fxml page
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent content = fxmlLoader.load();
            
            // Create new root container
            VBox root = new VBox();
            
            // Add title bar and new content
            root.getChildren().addAll(titleBar, content);
            
            // Set the new root to the scene
            scene.setRoot(root);
            
            // Ensure cursor is set on new content
            applyCustomCursorToAll(root);
            
        } catch (IOException e) {
            System.out.printf("An IOException occurred during switch to FXML path %s, error: %s%n", fxmlPath, e);
            e.printStackTrace();
        }
    }

    public void switchToGameScreen(String mapName, int startingGold) {
        try {
            // Load new fxml page
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/example/fxml/game_screen_page.fxml"));
            Parent content = fxmlLoader.load();

            // Initialize the controller with map data
            GameScreenController controller = fxmlLoader.getController();
            controller.init(mapName, startingGold);

            // Create new root container
            VBox root = new VBox();
            
            // Add title bar and new content
            root.getChildren().addAll(titleBar, content);
            
            // Set the new root to the scene
            scene.setRoot(root);
            
            // Ensure cursor is set on new content
            applyCustomCursorToAll(root);

        } catch (IOException e) {
            System.out.printf("An IOException occurred during switch to game screen, error: %s%n", e);
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
        stage.setHeight(height + 25); // Add height for title bar
    }

    public void terminateApplication() {
        stage.close();
    }

    public static Scene getScene() {
        return scene;
    }

    public Stage getStage() {
        return stage;
    }

    public void resizeWindowDefault() {
        this.resizeWindow(640, 450);
    }
}
