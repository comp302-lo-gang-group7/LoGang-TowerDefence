package com.example.controllers;

import com.example.main.Main;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;

import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.ResourceBundle;

/**
 * Controller for the load game page. It displays saved games and provides functionality 
 * to load or delete them, or return to the home page.
 */
public class LoadGameController implements Initializable {
    
    @FXML
    private ListView<String> savedGamesListView;
    
    @FXML
    private Button loadBtn;
    
    @FXML
    private Button deleteBtn;
    
    private ObservableList<String> savedGames = FXCollections.observableArrayList();
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Add example saved games with date/time
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        
        // Populate with example data
        savedGames.addAll(
            "Forest Campaign - Level 3 - " + dateFormat.format(new Date(System.currentTimeMillis() - 86400000)), // Yesterday
            "Desert Map - " + dateFormat.format(new Date()), // Today
            "Mountain Pass - Level 7 - " + dateFormat.format(new Date(System.currentTimeMillis() - 172800000)), // 2 days ago
            "Tutorial Map - " + dateFormat.format(new Date(System.currentTimeMillis() - 259200000)), // 3 days ago
            "Custom Map 1 - " + dateFormat.format(new Date(System.currentTimeMillis() - 432000000)) // 5 days ago
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
    }
    
    @FXML
    private void loadSelectedGame() {
        String selectedGame = savedGamesListView.getSelectionModel().getSelectedItem();
        if (selectedGame != null) {
            // Show confirmation dialog
            Alert alert = new Alert(AlertType.INFORMATION);
            alert.setTitle("Game Loading");
            alert.setHeaderText("Loading Game");
            alert.setContentText("Loading saved game: " + selectedGame);
            alert.showAndWait();
            
            // In a real implementation, you would load the actual game state here
            // For now, we'll just go to the game screen
            Main.getViewManager().switchTo("/com/example/fxml/game_screen.fxml");
        }
    }
    
    @FXML
    private void deleteSelectedGame() {
        String selectedGame = savedGamesListView.getSelectionModel().getSelectedItem();
        if (selectedGame != null) {
            // Show confirmation dialog
            Alert alert = new Alert(AlertType.CONFIRMATION);
            alert.setTitle("Delete Saved Game");
            alert.setHeaderText("Confirm Deletion");
            alert.setContentText("Are you sure you want to delete the saved game: " + selectedGame + "?");
            
            // Process the result
            alert.showAndWait().ifPresent(result -> {
                if (result == ButtonType.OK) {
                    // Remove the selected game from the list
                    savedGames.remove(selectedGame);
                    
                    // Disable buttons if list is now empty
                    if (savedGames.isEmpty()) {
                        loadBtn.setDisable(true);
                        deleteBtn.setDisable(true);
                    }
                }
            });
        }
    }
    
    @FXML
    private void goToHomePage() {
        Main.getViewManager().switchTo("/com/example/fxml/home_page.fxml");
    }
}