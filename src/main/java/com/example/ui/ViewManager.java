package com.example.ui;

import com.example.controllers.GameScreenController;
import com.example.utils.StyleManager;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;

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
            if (StyleManager.getCustomCursor() != null) {
                scene.setCursor(StyleManager.getCustomCursor());
            }
            applyCustomCursorToAll(root);
            
            // REMOVING AGGRESSIVE MOUSE_MOVED LISTENER
            /*
            scene.addEventFilter(MouseEvent.MOUSE_MOVED, event -> {
                System.out.println("ViewManager: MOUSE_MOVED on main scene. Active dialog context? Cursor: " + (scene.getCursor() != null ? scene.getCursor().toString() : "null")); // DIAGNOSTIC PRINT
                if (StyleManager.getCustomCursor() != null) {
                    // Ensure the scene itself has the cursor
                    if (scene.getCursor() != StyleManager.getCustomCursor()) {
                        scene.setCursor(StyleManager.getCustomCursor());
                        System.out.println("ViewManager: MOUSE_MOVED - Set main scene cursor."); // DIAGNOSTIC
                    }
                    // Aggressively re-apply to all nodes in the root of the scene
                    if (scene.getRoot() != null) {
                        applyCustomCursorToAll(scene.getRoot()); // This should print from within if changes are made
                    }
                }
            });
            */

            // Add listener for stage focus changes to re-apply cursor
            this.stage.focusedProperty().addListener((obs, oldVal, newVal) -> {
                if (newVal) { // Main stage gained focus
                    System.out.println("ViewManager: Main stage gained focus. Refreshing cursor.");
                    refreshMainSceneCursor(); // This uses static scene and StyleManager.getCustomCursor()
                } else { // Main stage lost focus
                    System.out.println("ViewManager: Main stage lost focus.");
                    // Consider if anything needs to be done when focus is lost.
                    // For now, primarily concerned with regaining focus.
                }
            });

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

    private static void applyCustomCursorToAll(Node node) {
        if (StyleManager.getCustomCursor() != null) {
            boolean changed = false;
            if (node.getCursor() != StyleManager.getCustomCursor()) {
                node.setCursor(null); // Force reset on the node
                node.setCursor(StyleManager.getCustomCursor());
                changed = true;
            }
            // System.out.println("applyCustomCursorToAll for node: " + node + (changed ? " - CURSOR SET" : " - cursor already set")); // Optional detailed logging

            if (node instanceof Parent) {
                for (Node child : ((Parent) node).getChildrenUnmodifiable()) {
                    applyCustomCursorToAll(child);
                }
            }
        }
        // Reinforce on the main scene after recursion - this part might be redundant if called from scene's MOUSE_MOVED
        // but let's keep it for now.
        if (scene != null && StyleManager.getCustomCursor() != null) {
            if (scene.getCursor() != StyleManager.getCustomCursor()) {
                // scene.setCursor(StyleManager.getCustomCursor()); // Already handled by the MOUSE_MOVED listener directly on scene
                // System.out.println("ViewManager: applyCustomCursorToAll - Reinforced main scene cursor."); // DIAGNOSTIC
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
            if (titleBar == null) { // Defensive check in case titleBar wasn't loaded
                 FXMLLoader titleBarLoader = new FXMLLoader(getClass().getResource("/com/example/fxml/CustomTitleBar.fxml"));
                 titleBar = titleBarLoader.load();
            }
            root.getChildren().addAll(titleBar, content);
            
            // Set the new root to the scene
            scene.setRoot(root);
            
            // Ensure cursor is set on new content
            if (StyleManager.getCustomCursor() != null) {
                scene.setCursor(StyleManager.getCustomCursor());
            }
            applyCustomCursorToAll(root);
            
            refreshMainSceneCursor(); // Use the full refresh logic
            
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
            if (titleBar == null) { // Defensive check
                 FXMLLoader titleBarLoader = new FXMLLoader(getClass().getResource("/com/example/fxml/CustomTitleBar.fxml"));
                 titleBar = titleBarLoader.load();
            }
            root.getChildren().addAll(titleBar, content);
            
            // Set the new root to the scene
            scene.setRoot(root);
            
            // Ensure cursor is set on new content
            if (StyleManager.getCustomCursor() != null) {
                scene.setCursor(StyleManager.getCustomCursor());
            }
            applyCustomCursorToAll(root);

            refreshMainSceneCursor(); // Use the full refresh logic

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

    /**
     * Re-applies the custom cursor to the main application scene and all its nodes.
     * This is useful after modal dialogs close or other focus changes.
     */
    public static void refreshMainSceneCursor() {
        if (scene != null && StyleManager.getCustomCursor() != null) {
            System.out.println("ViewManager: Refreshing main scene cursor (force attempt).");
            scene.setCursor(null); 
            scene.setCursor(StyleManager.getCustomCursor());
            if (scene.getRoot() != null) {
                applyCustomCursorToAll(scene.getRoot());
            }
        } else if (scene == null) {
            System.err.println("ViewManager.refreshMainSceneCursor: Main scene is null.");
        } else {
            System.err.println("ViewManager.refreshMainSceneCursor: Custom cursor is null, cannot refresh.");
        }
    }
}
