package com.example.controllers;

import javafx.fxml.FXML;
import com.example.main.Main;

public class CreateGameController extends Controller {
    @FXML
    public void goToDefaultGamePage() { Main.getViewManager().switchTo("/com/example/fxml/game_screen_page.fxml");}
}
