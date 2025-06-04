package com.example.controllers;

import com.example.main.Main;
import com.example.utils.MapEditorUtils;
import com.example.utils.StyleManager;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;

import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.ResourceBundle;

/**
 * Controller for the load game page. It displays saved games and provides functionality 
 * to load or delete them, or return to the home page.
 */
public class LoadGameController extends Controller implements Initializable {
    
    @FXML private ListView<String> savedGamesListView;
    @FXML private Button loadBtn;
    @FXML private Button deleteBtn;
    @FXML private Button homeBtn;

    private final ObservableList<String> savedGames = FXCollections.observableArrayList();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Add example saved games with date/time
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        
        // Populate with example data
        savedGames.addAll(
            "Desert Map - " + dateFormat.format(new Date()),
            "Mountain Pass - Level 7 - " + dateFormat.format(new Date(System.currentTimeMillis() - 86400000)),
            "Tutorial Map - " + dateFormat.format(new Date(System.currentTimeMillis() - 172800000)),
            "Custom Map 1 - " + dateFormat.format(new Date(System.currentTimeMillis() - 432000000))
        );
        
        savedGamesListView.setItems(savedGames);
        
        // Select first item by default
        if (!savedGames.isEmpty()) {
            savedGamesListView.getSelectionModel().selectFirst();
        }
        
        // Disable buttons if no selection
        loadBtn.setDisable(savedGames.isEmpty());
        deleteBtn.setDisable(savedGames.isEmpty());
        
        // Add listener for selection changes
        savedGamesListView.getSelectionModel().selectedItemProperty().addListener(
            (observable, oldValue, newValue) -> {
                boolean noSelection = (newValue == null);
                loadBtn.setDisable(noSelection);
                deleteBtn.setDisable(noSelection);
            }
        );
        
        // Setup buttons with StyleManager
        StyleManager.setupButtonWithCustomCursor(loadBtn);
        StyleManager.setupButtonWithCustomCursor(deleteBtn);
        StyleManager.setupButtonWithCustomCursor(homeBtn);
        
        // Style list cells
        setupCustomListView();
    }
    
    private void setupCustomListView() {
        savedGamesListView.setCellFactory(list -> new ListCell<String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setStyle("-fx-background-color: transparent;");
                } else {
                    setText(item);
                    setStyle("-fx-text-fill: #e8d9b5; -fx-font-family: 'Segoe UI'; -fx-font-size: 14px;");
                    
                    // Add hover effect
                    setOnMouseEntered(e -> {
                        if (!isSelected()) {
                            setStyle("-fx-background-color: #604631; -fx-text-fill: #f5ead9; -fx-font-family: 'Segoe UI'; -fx-font-size: 14px;");
                        }
                    });
                    
                    setOnMouseExited(e -> {
                        if (!isSelected()) {
                            setStyle("-fx-background-color: transparent; -fx-text-fill: #e8d9b5; -fx-font-family: 'Segoe UI'; -fx-font-size: 14px;");
                        }
                    });

                    // Set custom cursor
                    setCursor(StyleManager.getCustomCursor());
                }
            }
        });
    }
    
    @FXML
    private void loadSelectedGame() {
        String selectedGame = savedGamesListView.getSelectionModel().getSelectedItem();
        if (selectedGame == null) {
            MapEditorUtils.showInfoAlert("No Game Selected", "Please select a game to load.", this);
            return;
        }
        
        // Show confirmation dialog
        boolean confirmed = MapEditorUtils.showCustomConfirmDialog(
            "Load Game",
            "Are you sure you want to load: " + selectedGame + "?\nAny unsaved progress will be lost.",
            this
        );
        
        if (confirmed) {
            // Show loading notification
            MapEditorUtils.showInfoAlert(
                "Game Loading",
                "Loading saved game: " + selectedGame,
                this
            );
            
            // In a real implementation, you would load the actual game state here
            // For now, we'll just go to the game screen
            Main.getViewManager().switchTo("/com/example/fxml/game_screen_page.fxml");
        }
    }
    
    @FXML
    private void deleteSelectedGame() {
        String selectedGame = savedGamesListView.getSelectionModel().getSelectedItem();
        if (selectedGame == null) {
            MapEditorUtils.showInfoAlert("No Game Selected", "Please select a game to delete.", this);
            return;
        }
        
        // Show confirmation dialog
        boolean confirmed = MapEditorUtils.showCustomConfirmDialog(
            "Delete Game",
            "Are you sure you want to delete: " + selectedGame + "?\nThis action cannot be undone.",
            this
        );
        
        if (confirmed) {
            // Remove the selected game from the list
            savedGames.remove(selectedGame);
            
            // Show success notification
            MapEditorUtils.showInfoAlert(
                "Game Deleted",
                "Successfully deleted: " + selectedGame,
                this
            );
        }
    }
    
    @FXML
    private void goToHomePage() {
        Main.getViewManager().switchTo("/com/example/fxml/home_page.fxml");
    }
}