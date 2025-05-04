package com.example.ui;

import com.example.controllers.GameScreenController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * The view manager is used to request screen changes. Whenever a screen is to be loaded into the scene, the view manager handles that process.
 */
public class ViewManager {
    private final Stage stage;

    public ViewManager(Stage stage) {
        this.stage = stage;
    }

    public void switchTo(String fxmlPath) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent root = fxmlLoader.load();

            Scene scene = new Scene(root);
            if ( fxmlLoader.getController() instanceof GameScreenController )
            {
                GameScreenController gameScreenController = fxmlLoader.getController();
                scene.addEventFilter(MouseEvent.MOUSE_CLICKED, gameScreenController.getOnMouseClickedFilter());
            }

            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            System.out.printf("An IOException occurred during switch to FXML path %s, error: %s%n", fxmlPath, e);
            e.printStackTrace();
        }
    }

    public void terminateApplication() {
        // NOTE: WE NEED TO HAVE SOME CLEANUP STEP HERE DOWN THE LINE
        stage.close();
    }

}
