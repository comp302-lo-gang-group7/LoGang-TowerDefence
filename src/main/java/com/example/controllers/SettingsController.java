package com.example.controllers;

import com.example.main.Main;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;

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
        difficultyCombo.getItems().addAll("Easy", "Normal", "Hard", "Expert");
        difficultyCombo.setValue("Normal");
        
        gameSpeedCombo.getItems().addAll("Slow", "Normal", "Fast", "Turbo");
        gameSpeedCombo.setValue("Normal");
        
        // Set up slider listeners to update labels
        musicVolumeSlider.valueProperty().addListener((obs, oldVal, newVal) -> {
            int value = newVal.intValue();
            musicVolumeLabel.setText(value + "%");
        });
        
        sfxVolumeSlider.valueProperty().addListener((obs, oldVal, newVal) -> {
            int value = newVal.intValue();
            sfxVolumeLabel.setText(value + "%");
        });
        
        // Add wooden styling to all buttons
        applyButtonStyle(saveBtn);
        applyButtonStyle(resetBtn);
        applyButtonStyle(homeBtn);
    }
    
    private void applyButtonStyle(Button button) {
        String buttonCss = 
            "-fx-background-color: linear-gradient(#7d5a3c, #5d4228); " +
            "-fx-background-radius: 8; " +
            "-fx-text-fill: #e8d9b5; " +
            "-fx-font-size: 14px; " +
            "-fx-font-weight: bold; " +
            "-fx-font-family: 'Segoe UI'; " +
            "-fx-padding: 8 15 8 15; " +
            "-fx-border-color: linear-gradient(#a07748, #8a673c); " +
            "-fx-border-width: 2; " +
            "-fx-border-radius: 8; " +
            "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.6), 5, 0.0, 0, 2);";
        
        String hoverCss = 
            "-fx-background-color: linear-gradient(#94704c, #705236); " +
            "-fx-background-radius: 8; " +
            "-fx-text-fill: #f5ead9; " +
            "-fx-font-size: 14px; " +
            "-fx-font-weight: bold; " +
            "-fx-font-family: 'Segoe UI'; " +
            "-fx-padding: 8 15 8 15; " +
            "-fx-border-color: linear-gradient(#c6965f, #b88d5a); " +
            "-fx-border-width: 2; " +
            "-fx-border-radius: 8; " +
            "-fx-cursor: hand; " +
            "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.7), 6, 0.0, 0, 2);";
        
        String pressedCss = 
            "-fx-background-color: linear-gradient(#5d4228, #4e3822); " +
            "-fx-background-radius: 8; " +
            "-fx-text-fill: #d9c9a0; " +
            "-fx-font-size: 14px; " +
            "-fx-font-weight: bold; " +
            "-fx-font-family: 'Segoe UI'; " +
            "-fx-padding: 9 15 7 15; " + // Offset padding to simulate pressed effect
            "-fx-border-color: #7d5a3c; " +
            "-fx-border-width: 2; " +
            "-fx-border-radius: 8; " +
            "-fx-effect: innershadow(three-pass-box, rgba(0,0,0,0.4), 4, 0.0, 0, 1);";
        
        // Set initial style
        button.setStyle(buttonCss);
        
        // Add hover/exit listeners
        button.setOnMouseEntered(e -> {
            button.setStyle(hoverCss);
            button.setScaleX(1.05);
            button.setScaleY(1.05);
        });
        
        button.setOnMouseExited(e -> {
            button.setStyle(buttonCss);
            button.setScaleX(1.0);
            button.setScaleY(1.0);
        });
        
        // Add pressed/released listeners
        button.setOnMousePressed(e -> {
            button.setStyle(pressedCss);
            button.setScaleX(1.02);
            button.setScaleY(1.02);
        });
        
        button.setOnMouseReleased(e -> {
            if (button.isHover()) {
                button.setStyle(hoverCss);
                button.setScaleX(1.05);
                button.setScaleY(1.05);
            } else {
                button.setStyle(buttonCss);
                button.setScaleX(1.0);
                button.setScaleY(1.0);
            }
        });
    }
    
    @FXML
    public void saveSettings() {
        // This would actually save settings in a real implementation
        showMessage("Settings saved successfully!");
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
        
        showMessage("Settings reset to defaults.");
    }
    
    private void showMessage(String message) {
        // In a real implementation, you would show a proper dialog
        System.out.println(message);
    }
}