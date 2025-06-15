package com.example.main;

import java.io.IOException;

import com.example.ui.ViewManager;

import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class Main extends javafx.application.Application {
    public static ViewManager viewManager;
    private static double xOffset = 0;
    private static double yOffset = 0;

    @Override
    public void start(Stage stage) throws IOException {
        // Set stage to undecorated (removes the default window decoration)
        stage.initStyle(StageStyle.UNDECORATED);
        
        // Disable window resizing
        stage.setResizable(false);
        
        viewManager = new ViewManager(stage);
        viewManager.switchTo("/com/example/fxml/home_page.fxml");

        // Set custom cursor
        Image customCursorImage = new Image(getClass().getResourceAsStream("/com/example/assets/ui/01.png"));
        viewManager.setCustomCursor(customCursorImage);
        
        stage.show();
    }

    public static ViewManager getViewManager() {
        return viewManager;
    }
    
    public static double getXOffset() {
        return xOffset;
    }
    
    public static double getYOffset() {
        return yOffset;
    }
    
    public static void setXOffset(double x) {
        xOffset = x;
    }
    
    public static void setYOffset(double y) {
        yOffset = y;
    }

    public static void main(String[] args) {
        launch();
    }
}