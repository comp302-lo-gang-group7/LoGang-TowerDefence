package com.example.controllers;

import com.example.main.Main;
import javafx.fxml.FXML;


/**
 * This is a controller with some methods that are generally used app-wide.
 */
public class Controller {
    @FXML
    public void goToHomePage() {
        Main.getViewManager().switchTo("/com/example/fxml/home_page.fxml");
        Main.getViewManager().resizeWindowDefault();
    }
}
