package com.example.controllers;

import com.example.main.Main;
import com.example.utils.StyleManager;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.paint.Color;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.util.Callback;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * This is a controller for the settings page. It handles game preferences and options.
 */
public class SettingsController extends Controller implements Initializable {
    
    @FXML private Slider musicVolumeSlider;
    @FXML private Slider sfxVolumeSlider;
    @FXML private Label musicVolumeLabel;
    @FXML private Label sfxVolumeLabel;
    @FXML private ComboBox<String> difficultyCombo;
    @FXML private ComboBox<String> gameSpeedCombo;
    @FXML private CheckBox showHintsCb;
    @FXML private CheckBox autoSaveCb;
    @FXML private CheckBox fullscreenCb;
    @FXML private CheckBox showFpsCb;
    @FXML private Button saveBtn;
    @FXML private Button resetBtn;
    @FXML private Button homeBtn;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Set up the ComboBoxes
        setupComboBoxes();
        
        // Set up slider listeners to update labels
        setupSliders();
        
        // Apply wooden style to checkboxes
        setupCheckboxes();
        
        // Apply StyleManager to buttons
        StyleManager.setupButtonWithCustomCursor(saveBtn);
        StyleManager.setupButtonWithCustomCursor(resetBtn);
        StyleManager.setupButtonWithCustomCursor(homeBtn);
    }
    
    private void setupComboBoxes() {
        // Configure difficulty combo box
        difficultyCombo.getItems().addAll("Easy", "Normal", "Hard", "Expert");
        difficultyCombo.setValue("Normal");
        styleComboBox(difficultyCombo);
        
        // Configure game speed combo box
        gameSpeedCombo.getItems().addAll("Slow", "Normal", "Fast", "Turbo");
        gameSpeedCombo.setValue("Normal");
        styleComboBox(gameSpeedCombo);
    }
    
    private void styleComboBox(ComboBox<String> comboBox) {
        // Set custom cell factory for items in dropdown
        comboBox.setCellFactory(new Callback<ListView<String>, ListCell<String>>() {
            @Override
            public ListCell<String> call(ListView<String> param) {
                return new ListCell<String>() {
                    @Override
                    protected void updateItem(String item, boolean empty) {
                        super.updateItem(item, empty);
                        
                        if (item != null && !empty) {
                            setText(item);
                            setTextFill(Color.web("#e8d9b5"));
                            setStyle("-fx-background-color: #5d4228; -fx-font-family: 'Segoe UI'; -fx-font-weight: bold; -fx-font-size: 13px;");
                            setCursor(StyleManager.getCustomCursor());
                        } else {
                            setText(null);
                        }
                    }
                };
            }
        });
        
        // Set custom button cell (what's shown when closed)
        comboBox.setButtonCell(new ListCell<String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                
                if (item != null && !empty) {
                    setText(item);
                    setTextFill(Color.web("#e8d9b5"));
                    setStyle("-fx-font-family: 'Segoe UI'; -fx-font-weight: bold; -fx-font-size: 13px;");
                    setCursor(StyleManager.getCustomCursor());
                } else {
                    setText(null);
                }
            }
        });

        // Set custom cursor for the ComboBox itself
        comboBox.setCursor(StyleManager.getCustomCursor());
    }
    
    private void setupSliders() {
        // Format music volume slider
        musicVolumeSlider.valueProperty().addListener((obs, oldVal, newVal) -> {
            int value = newVal.intValue();
            musicVolumeLabel.setText(value + "%");
        });
        
        // Format sound effects slider
        sfxVolumeSlider.valueProperty().addListener((obs, oldVal, newVal) -> {
            int value = newVal.intValue();
            sfxVolumeLabel.setText(value + "%");
        });

        // Set custom cursor for sliders
        musicVolumeSlider.setCursor(StyleManager.getCustomCursor());
        sfxVolumeSlider.setCursor(StyleManager.getCustomCursor());
    }
    
    private void setupCheckboxes() {
        // Apply wooden style to all checkboxes
        String checkboxStyle = "-fx-text-fill: #e8d9b5; -fx-font-family: 'Segoe UI';" +
                           "-fx-font-size: 13px; -fx-background-color: transparent;" +
                           "-fx-border-color: transparent;" +
                           "-fx-mark-color: #a07748;";
                           
        showHintsCb.setStyle(checkboxStyle);
        autoSaveCb.setStyle(checkboxStyle);
        fullscreenCb.setStyle(checkboxStyle);
        showFpsCb.setStyle(checkboxStyle);

        // Set custom cursor for checkboxes
        showHintsCb.setCursor(StyleManager.getCustomCursor());
        autoSaveCb.setCursor(StyleManager.getCustomCursor());
        fullscreenCb.setCursor(StyleManager.getCustomCursor());
        showFpsCb.setCursor(StyleManager.getCustomCursor());
    }
    
    @FXML
    public void saveSettings() {
        // This would actually save settings in a real implementation
        MapEditorUtils.showInfoAlert("Settings Saved", "Settings saved successfully!", this);
    }
    
    @FXML
    public void resetSettings() {
        // Reset all settings to default
        musicVolumeSlider.setValue(75);
        sfxVolumeSlider.setValue(100);
        difficultyCombo.setValue("Normal");
        gameSpeedCombo.setValue("Normal");
        showHintsCb.setSelected(true);
        autoSaveCb.setSelected(true);
        fullscreenCb.setSelected(false);
        showFpsCb.setSelected(false);
        
        MapEditorUtils.showInfoAlert("Settings Reset", "Settings reset to defaults.", this);
    }
}