package com.example.controllers;

import com.example.main.Main;

import javafx.fxml.FXML;

/**
 * Controller class responsible for handling app-wide navigation functionality.
 */
public class Controller {

    /**
     * Navigates to the home page by resizing the window to its default size and switching to the home page view.
     */
    @FXML
    public void goToHomePage() {
        Main.getViewManager().resizeWindowDefault();
        Main.getViewManager().switchTo("/com/example/fxml/home_page.fxml");
    }
}
