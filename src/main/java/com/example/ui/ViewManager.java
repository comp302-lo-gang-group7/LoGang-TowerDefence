package com.example.ui;

import javafx.fxml.FXMLLoader;
import javafx.scene.ImageCursor;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;
import java.io.InputStream;

/**
 * The view manager is used to request screen changes. Whenever a screen is to be loaded into the scene, the view manager handles that process.
 */
public class ViewManager {
    private final Stage stage;
    private static Scene scene;

    public ViewManager(Stage stage) {
        this.stage = stage; // Ensure it is initialized outside the scope of failure.

        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/example/fxml/home_page.fxml")); // Initially set to home page.
            Parent root = fxmlLoader.load();
            this.scene = new Scene(root); // Set initial scene in view manager.

            // This is a check for the 01.png cursor image in /assets/ui. In case the image resource is not found, we default to a normal cursor.
            InputStream stream = getClass().getResourceAsStream("/com/example/assets/ui/01.png");
            if (stream != null) {
                Image image = new Image(stream);
                scene.setCursor(new ImageCursor(image));
            } else {
                System.err.println("Custom cursor image not found!");
            }

        } catch (IOException e) {
            System.out.printf("An IOException occurred during switch to FXML home page in ViewManager constructor, error: %s%n", e);
            e.printStackTrace();
        } catch (Exception unexpectedError) {
            System.out.printf("An unexpected error occured in ViewManager constructor, error: %s%n", unexpectedError);
            unexpectedError.printStackTrace();
        }
    }

    public void switchTo(String fxmlPath) {
        try {
            // Load new fxml page
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent root = fxmlLoader.load();

            // Get original scene with correct setup
            Scene scene = ViewManager.getScene();
            scene.setRoot(root);

            // Load a new fxml onto the scene
            stage.setScene(ViewManager.getScene());
            stage.show();
        } catch (IOException e) {
            System.out.printf("An IOException occurred during switch to FXML path %s, error: %s%n", fxmlPath, e);
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
        stage.setHeight(height);
    }


    public void terminateApplication() {
        // NOTE: WE NEED TO HAVE SOME CLEANUP STEP HERE DOWN THE LINE
        stage.close();
    }

    public static Scene getScene() {
        return scene;
    }
}
