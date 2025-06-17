package com.example.controllers;

import com.example.main.Main;

import javafx.fxml.FXML;



/**
 * Class Controller
 */
public class Controller {
    @FXML
    /**
     * TODO
     */
    public void goToHomePage() {
        Main.getViewManager().resizeWindowDefault();
        Main.getViewManager().switchTo("/com/example/fxml/home_page.fxml");
    }
}
